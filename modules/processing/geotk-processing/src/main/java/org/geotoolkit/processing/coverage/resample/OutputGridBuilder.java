package org.geotoolkit.processing.coverage.resample;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.Utilities;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.stream.IntStream;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

/**
 * TODO: Cache intermediate information, as math-transforms, etc.
 * TODO: check CRS dimension: {@link #compatibleSourceCRS(CoordinateReferenceSystem, CoordinateReferenceSystem, CoordinateReferenceSystem)}.
 *
 final boolean force2D = (sourceCRS != compatibleSourceCRS);
 final GridGeometry sourceGGCopy;
 if (force2D) {
 final int xAxis = AxisDirections.indexOfColinear(sourceCRS.getCoordinateSystem(), compatibleSourceCRS.getCoordinateSystem());
 sourceGGCopy = sourceGG.reduce(xAxis, xAxis+1);
 } else sourceGGCopy = sourceGG;
 */
class OutputGridBuilder {

    @Nonnull
    final GridGeometry source;
    @Nonnull GridGeometry target;

    @Nonnull
    final MathTransformFactory mtFactory;

    /**
     *
     * If the target GridGeometry is incomplete, provides default values for the missing fields. Three cases
     * may occurs:
     *
     * - User provided no GridGeometry at all. Then, constructs an image of the same size
     *   than the source image and set an envelope big enough to contains the projected
     *   coordinates. The transform will derive from the grid and georeferenced envelopes.
     *
     * - User provided only a grid envelope. Then, set an envelope big enough to contains
     *   the projected coordinates. The transform will derive from the grid and georeferenced
     *   envelopes.
     *
     * - User provided only a "grid to CRS" transform. Then, transform the projected
     *   envelope to "grid units" using the specified transform and create a grid envelope
     *   big enough to hold the result.
     *
     * @param source Grid geometry of the souce coverage to resample. It MUST define extent, grid to crs and
     *               envelope, because it will be used for target geometry completion, if needed.
     * @param target A geometry representing wanted output. If there's missing information, they will be inferred
     *               from given source.
     * @throws FactoryException If a conversion is needed between source and target CRS, but SIS registry fails to
     * provide one.
     */
    OutputGridBuilder(@Nonnull GridGeometry source, @Nonnull GridGeometry target) throws FactoryException {
        ensureNonNull("Source grid geometry", source);
        if (!source.isDefined(GridGeometry.EXTENT|GridGeometry.GRID_TO_CRS))
            throw new IllegalArgumentException("Source grid geometry is incomplete.");
        this.source = source;

        mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);

        if (target == null) {
            target = source;
        } else if (!target.isDefined(GridGeometry.GRID_TO_CRS)) {
            final CoordinateReferenceSystem targetCrs;
            if (target.isDefined(GridGeometry.CRS)) {
                targetCrs = target.getCoordinateReferenceSystem();
            } else if (source.isDefined(GridGeometry.CRS)) {
                targetCrs = source.getCoordinateReferenceSystem();
            } else targetCrs = null;

            final MathTransform grid2Crs = concatenateGrid2Crs(source, targetCrs, PixelInCell.CELL_CENTER);

            if (target.isDefined(GridGeometry.EXTENT)) {
                target = new GridGeometry(target.getExtent(), PixelInCell.CELL_CENTER, grid2Crs, targetCrs);
            } else if (target.isDefined(GridGeometry.ENVELOPE)) {
                final GeneralEnvelope targetEnvelope = new GeneralEnvelope(target.getEnvelope());
                targetEnvelope.setCoordinateReferenceSystem(targetCrs);
                // TODO: check rounding mode
                target = new GridGeometry(PixelInCell.CELL_CENTER, grid2Crs, targetEnvelope, GridRoundingMode.ENCLOSING);
            } else {
                target = new GridGeometry(source.getExtent(), PixelInCell.CELL_CENTER, grid2Crs, targetCrs);
            }
        } else if (!target.isDefined(GridGeometry.EXTENT)) {
            final PixelInCell cellCorner = PixelInCell.CELL_CORNER;
            target = new GridGeometry(
                    cellCorner, target.getGridToCRS(cellCorner), source.getEnvelope(), GridRoundingMode.ENCLOSING
            );
        }

        this.target = target;
    }

    OutputGridBuilder setTargetCrs(final CoordinateReferenceSystem targetCrs) throws FactoryException {
        if (targetCrs == null) return this;

        final PixelInCell inCell = PixelInCell.CELL_CENTER;
        final MathTransform newG2C = concatenateGrid2Crs(target, targetCrs, inCell);
        // TODO: Should we check if the operation is inversible ? Resample will need it, but prehaps it's automatically checked when build the geom.
        target = new GridGeometry(target.getExtent(), inCell, newG2C, targetCrs);

        return this;
    }

    Dimension getTargetImageDimension() {
        final Point imageAxes = getImageAxis(target);
        final GridExtent baseExtent = target.getExtent();
        return new Dimension(
                Math.toIntExact(baseExtent.getSize(imageAxes.x)),
                Math.toIntExact(baseExtent.getSize(imageAxes.y))
        );
    }

    /**
     * Computes the math transform from [Target Grid] to [Source Grid].
     * @implNote
     * The transform will be computed using the following path:
     *
     *      Target Grid --> Target CRS --> Source CRS --> Source Grid
     *                   ^              ^              ^
     *                 step 1         step 2         step 3
     *
     * If source and target CRS are equal, a shorter path is used. This special
     * case is needed because 'sourceCRS' and 'targetCRS' may be null.
     *
     *      Target Grid --> Common CRS --> Source Grid
     *                   ^              ^
     *                 step 1         step 3
     * @param inCell The pixel in cell to use for grid transforms. Used through {@link GridGeometry#getGridToCRS(PixelInCell)}.
     * @return A math transform to get source pixel coordinates from target pixel coordinates.
     * @throws NoninvertibleTransformException If we cannot inverse source grid transform.
     * @throws FactoryException If we cannot find a conversion between source and target CRS.
     */
    MathTransform createBridge(PixelInCell inCell) throws NoninvertibleTransformException, FactoryException {
        final Point sourceXY = getImageAxis(source);
        final Point targetXY = getImageAxis(target);
        final GridGeometry source = this.source.reduce(new int[]{sourceXY.x, sourceXY.y});
        final GridGeometry target = this.target.reduce(new int[]{targetXY.x, targetXY.y});
        final MathTransform step1 = target.getGridToCRS(inCell);
        final MathTransform step3 = source.getGridToCRS(inCell).inverse();

        final CoordinateOperation inverseOp = targetToSourceCrs(source, target);
        if (inverseOp != null) {
            final MathTransform step2 = WraparoundTransform.create(mtFactory, inverseOp);
            return mtFactory.createConcatenatedTransform(
                    mtFactory.createConcatenatedTransform(step1, step2),
                    step3
            );
        }

        return mtFactory.createConcatenatedTransform(step1, step3);
    }

    private static CoordinateOperation targetToSourceCrs(final GridGeometry source, final GridGeometry target) throws FactoryException {
        final CoordinateReferenceSystem sourceCrs = source.isDefined(GridGeometry.CRS) ? source.getCoordinateReferenceSystem() : null;
        final CoordinateReferenceSystem targetCrs = target.isDefined(GridGeometry.CRS) ? target.getCoordinateReferenceSystem() : null;
        if (targetCrs != null && sourceCrs != null && !Utilities.equalsIgnoreMetadata(sourceCrs, targetCrs)) {
            return CRS.findOperation(targetCrs, sourceCrs, tryGetRoi(source, target));
        }
        return null;
    }

    static boolean isSameCrs() {
        return false;
    }

    private static MathTransform concatenateGrid2Crs(final GridGeometry source, CoordinateReferenceSystem target, PixelInCell inCell) throws FactoryException {
        MathTransform g2c = source.getGridToCRS(inCell);
        if (target != null) {
            if (!source.isDefined(GridGeometry.CRS))
                throw new IllegalArgumentException("Source geometry has no defined CRS. Cannot define coordinate operation to target CRS.");

            final CoordinateReferenceSystem sourceCrs = source.getCoordinateReferenceSystem();
            if (!Utilities.equalsIgnoreMetadata(sourceCrs, target)) {
                final CoordinateOperation op = CRS.findOperation(
                        sourceCrs,
                        target,
                        tryExtractGeoBbox(source)
                );

                g2c = MathTransforms.concatenate(g2c, op.getMathTransform());
            }
        }

        return g2c;
    }

    /**
     * Build a geographic extent by analyzing input geometries. It is a pseudo-safe operation returning null if no area
     * of interest can be deduced, in order to avoid failure of entire resample.
     * @param target Primary grid geometry to extract geographic bbox from.
     * @param fallback If we cannot deduce a bbox from first parameter, we'll try to get one from this one.
     * @return null if we cannot compute any geographic extent.
     */
    private static GeographicBoundingBox tryGetRoi(GridGeometry target, GridGeometry fallback) {
        GeographicBoundingBox geoBbox = tryExtractGeoBbox(target);
        if (geoBbox == null)
            geoBbox = tryExtractGeoBbox(fallback);

        return geoBbox;
    }

    private static GeographicBoundingBox tryExtractGeoBbox(GridGeometry target) {
        try {
            if (target != null && target.isDefined(GridGeometry.ENVELOPE)) {
                return toGeoBbox(target.getEnvelope());
            }
        } catch (RuntimeException | TransformException e) {
            // TODO: log or raise warning
        }

        return null;
    }

    private static GeographicBoundingBox toGeoBbox(Envelope baseEnvelope) throws TransformException {
        final Envelope geoEnv = Envelopes.transform(baseEnvelope, CommonCRS.defaultGeographic());
        return new DefaultGeographicBoundingBox(
                geoEnv.getMinimum(0), geoEnv.getMaximum(0),
                geoEnv.getMinimum(1), geoEnv.getMaximum(1)
        );
    }

    private static Point getImageAxis(GridGeometry sourceGG) {
        if (sourceGG.isDefined(GridGeometry.EXTENT)) {
            final GridExtent extent = sourceGG.getExtent();
            final int[] dims = IntStream.range(0, sourceGG.getDimension())
                    .filter(i -> extent.getSize(i) > 1)
                    .limit(2)
                    .toArray();
            if (dims.length == 2) {
                return new Point(dims[0], dims[1]);
            }
        }

        // TODO: better strategy:
        // Cannot safely deduce grid dimensions. Check horizontal axis in source geometry. It's not very safe, cause
        // we don't care about possible axis shifts in grid to CRS transform.
        if (sourceGG.isDefined(GridGeometry.CRS)) {
            final CoordinateReferenceSystem crs = sourceGG.getCoordinateReferenceSystem();
            final SingleCRS hCrs = CRS.getHorizontalComponent(crs);
            if (hCrs != null) {
                final int xAxis = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), hCrs.getCoordinateSystem());
                if (xAxis >= 0) return new Point(xAxis, xAxis+1);
            }
        }

        // TODO: Warn user
        return new Point(0, 1);
    }
}
