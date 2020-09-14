package org.geotoolkit.processing.coverage.resample;

import java.awt.*;
import java.awt.image.Raster;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import javax.annotation.Nonnull;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.internal.referencing.WraparoundTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.collection.BackingStoreException;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Helps define a complete Grid geometry for resampling operation. Also provides math transform to go from
 * {@link #target target grid} to {@link #source source grid}. Note that for conversion between pixel coordinates in
 * coverage renderings, you should use {@link #forDefaultRendering()} or {@link #forRendering(GridExtent, GridExtent)}
 * methods, which include shifts between {@link GridGeometry#getExtent() coverage grid extents} and
 * {@link Raster#getBounds() rendering boundaries}.
 *
 * /!\ Not THREAD-SAFE !
 * @implNote This object uses an internal {@link #cache} to avoid re-computing information. However, if you add methods
 * which impacts target grid geometry, you should ALWAYS call {@link Cache#clear()} after alteration.
 */
public final class OutputGridBuilder {

    @Nonnull
    final GridGeometry source;
    @Nonnull
    GridGeometry target;

    @Nonnull
    final MathTransformFactory mtFactory;

    @Nonnull
    final Cache cache;

    @Nonnull
    final Point sourceXY;

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
    public OutputGridBuilder(@Nonnull GridGeometry source, @Nonnull GridGeometry target) throws FactoryException {
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
        sourceXY = findImageAxes(source);
        cache = new Cache();
    }

    private MathTransform createBridge(PixelInCell inCell) throws NoninvertibleTransformException, FactoryException {
        final Point targetXY = cache.getOrComputeImageAxes();
        final GridGeometry source = this.source.reduce(new int[]{sourceXY.x, sourceXY.y});
        final GridGeometry target = this.target.reduce(new int[]{targetXY.x, targetXY.y});
        final MathTransform step1 = target.getGridToCRS(inCell);
        final MathTransform step3 = source.getGridToCRS(inCell).inverse();

        // Note: Before applying wrap-around, we must ensure it is really needed. Fore more information, see the
        // following unit test : ResampleTest#resampleFalse0_360
        final CoordinateOperation inverseOp = targetToSourceCrs(source, target);
        if (inverseOp != null) {
            final MathTransform withoutWrap = mtFactory.createConcatenatedTransform(
                    mtFactory.createConcatenatedTransform(step1, inverseOp.getMathTransform()),
                    step3
            );

            final MathTransform step2 = WraparoundTransform.forTargetCRS(mtFactory, inverseOp);
            final MathTransform withWrap = mtFactory.createConcatenatedTransform(
                    mtFactory.createConcatenatedTransform(step1, step2),
                    step3
            );

            return checkIfWrapAroundIsNeeded(withWrap, withoutWrap, source.getExtent(), target.getExtent());
        }

        return mtFactory.createConcatenatedTransform(step1, step3);
    }

    /**
     * Check if we need to include a wrap-around workaround in resample transform. This is a necessary check, because
     * even if a CRS of the workflow use different axis convention, it does not mean the attached grid geometry follows
     * the convention. For more information check {@code ResampleTest#resampleFalse0_360()} unit test.
     *
     * TODO: It's total heuristic here. We should think about a better approach.
     *
     * @implNote
     * Here, we compare extents obtained after transformation with given source one. We keep the transform that give us
     * the extent closest to given source.
     *
     * @param with A version of the resample transform using wrap-around.
     * @param without A version of the resample transform NOT using wrap-around.
     * @param sourceExtent Extent of source grid geometry of the resampling.
     * @param targetExtent Extent of target grid geometry og the resampling.
     * @return The Chosen math-transform.
     */
    private MathTransform checkIfWrapAroundIsNeeded(final MathTransform with, final MathTransform without, final GridExtent sourceExtent, final GridExtent targetExtent) {
        final Envelope extentWith = new GridGeometry(targetExtent, PixelInCell.CELL_CENTER, with, null).getEnvelope();
        final Envelope extentWithout = new GridGeometry(targetExtent, PixelInCell.CELL_CENTER, without, null).getEnvelope();

        final GeneralEnvelope sourceExtentAsEnvelope = new GeneralEnvelope(
                LongStream.of(sourceExtent.getLow().getCoordinateValues()).mapToDouble(val -> (double)val).toArray(),
                LongStream.of(sourceExtent.getHigh().getCoordinateValues()).mapToDouble(val -> (double)val).toArray()
        );

        final GeneralEnvelope intersectWith = new GeneralEnvelope(extentWith);
        intersectWith.intersect(sourceExtentAsEnvelope);
        if (intersectWith.isEmpty())
            return without;

        final GeneralEnvelope intersectWithout = new GeneralEnvelope(extentWithout);
        intersectWithout.intersect(sourceExtentAsEnvelope);
        if (intersectWithout.isEmpty())
            return with;

        double nbPxWith = 1, nbPxWithout = 1;
        for (int i = 0 ; i < sourceExtent.getDimension() ; i++) {
            nbPxWith *= intersectWith.getSpan(i);
            nbPxWithout *= intersectWithout.getSpan(i);
        }

        return nbPxWith > nbPxWithout? with : without;
    }

    private static CoordinateOperation targetToSourceCrs(final GridGeometry source, final GridGeometry target) throws FactoryException {
        final CoordinateReferenceSystem sourceCrs = source.isDefined(GridGeometry.CRS) ? source.getCoordinateReferenceSystem() : null;
        final CoordinateReferenceSystem targetCrs = target.isDefined(GridGeometry.CRS) ? target.getCoordinateReferenceSystem() : null;
        if (targetCrs != null && sourceCrs != null && !Utilities.equalsIgnoreMetadata(sourceCrs, targetCrs)) {
            return CRS.findOperation(targetCrs, sourceCrs, tryGetRoi(source, target));
        }
        return null;
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

    private static Point findImageAxes(GridGeometry sourceGG) {
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

    /**
     * Create a math transform concatenating {@link #createBridge(PixelInCell) cell center bridge} with translations
     * to take into account {@link org.apache.sis.coverage.grid.GridCoverage#render(GridExtent) grid coverage rendering}
     * shifts.
     *
     * This method is a shortcut for <code>{@link #forRendering(GridExtent, GridExtent) forRendering}(source.getExtent(), target.getExent())</code>.
     *
     * @return A math transform which can project target default rendering (null extent) to source one.
     * @throws FactoryException If we cannot find any valid transform between source and target CRSs.
     * @throws NoninvertibleTransformException If we cannot invert transform between source and target CRSs.
     */
    public MathTransform forDefaultRendering() throws FactoryException, NoninvertibleTransformException {
        return forRendering(source.getExtent(), target.getExtent());
    }

    /**
     * Create a math transform concatenating {@link #createBridge(PixelInCell) cell center bridge} with translations
     * to take into account {@link org.apache.sis.coverage.grid.GridCoverage#render(GridExtent) grid coverage rendering}
     * shifts.
     *
     * @implNote
     * Creates a math transform using the following path:
     *
     *      Target rendering --> Target Grid --> Target CRS --> Source CRS --> Source Grid --> Source rendering
     *
     * @param sourceRendering The grid extent used for source coverage rendering.
     * @param targetRendering The grid extent used for target coverage rendering.
     * @return A math transform which can project target specified rendering to source one.
     * @throws FactoryException If we cannot find any valid transform between source and target CRSs.
     * @throws NoninvertibleTransformException If we cannot invert transform between source and target CRSs.
     */
    public MathTransform forRendering(final GridExtent sourceRendering, final GridExtent targetRendering) throws FactoryException, NoninvertibleTransformException {
        final MathTransform bridge = cache.getOrCreateBridge2D(PixelInCell.CELL_CENTER);
        final AffineTransform2D source = new AffineTransform2D(
                1, 0, 0, 1,
                -sourceRendering.getLow(sourceXY.x),
                -sourceRendering.getLow(sourceXY.y)
        );
        final Point targetXY = cache.getOrComputeImageAxes();
        final AffineTransform2D target = new AffineTransform2D(
                1, 0, 0, 1,
                targetRendering.getLow(targetXY.x),
                targetRendering.getLow(targetXY.y)
        );

        // Don't use factory, because there should not be any caching needs for this.
        return MathTransforms.concatenate(target, bridge, source);
    }

    private class Cache {

        private Point targetXY;
        private final Map<PixelInCell, MathTransform> bridges = new TreeMap<>();

        private void clear() {
            targetXY = null;
            bridges.clear();
        }

        Point getOrComputeImageAxes() {
            if (targetXY == null) {
                targetXY = findImageAxes(target);
            }

            return targetXY;
        }

        MathTransform getOrCreateBridge2D(final PixelInCell inCell) throws NoninvertibleTransformException, FactoryException {
            try {
                return bridges.computeIfAbsent(inCell, this::createBridgeBackingError);
            } catch (BackingStoreException e) {
                try {
                    throw e.unwrapOrRethrow(FactoryException.class);
                } catch (BackingStoreException still) {
                    throw e.unwrapOrRethrow(NoninvertibleTransformException.class);
                }
            }
        }

        private MathTransform createBridgeBackingError(final PixelInCell inCell) {
            try {
                return createBridge(inCell);
            } catch (FactoryException | NoninvertibleTransformException e) {
                throw new BackingStoreException(e);
            }
        }
    }
}
