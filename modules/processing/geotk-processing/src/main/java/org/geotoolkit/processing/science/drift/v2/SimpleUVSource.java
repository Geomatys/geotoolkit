/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import javax.vecmath.Vector2d;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.DirectPosition1D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import static org.geotoolkit.processing.science.drift.v2.Utilities.*;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * TODO : add logic to re-use previously queried Calibration2D and Snapshot objects when same slices are used multiple
 * times in a row (Example: user queries times separated by a few seconds, but data granularity is at minute).
 *
 * @author Alexis Manin (Geomatys)
 */
class SimpleUVSource implements UVSource {

    private static final Function<CoordinateReferenceSystem, VerticalCRS> VERTICAL_EXTRACTOR = inCrs -> CRS.getVerticalComponent(inCrs, false);

    final GridCoverageResource source;
    private final GridGeometry sourceGeometry;
    final SingleCRS sourceCrs2d;
    final int horizontalAxisIdx;

    final Point uvIndices;

    final TransferFunction native2geophysic;

    public SimpleUVSource(GridCoverageResource source) throws DataStoreException {
        this(source, new Point(0, 1));
    }

    public SimpleUVSource(GridCoverageResource source, final Point uvIndices) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Source coverage", source);
        ArgumentChecks.ensureNonNull("UV indices", uvIndices);
        ArgumentChecks.ensurePositive("U indice", uvIndices.x);
        ArgumentChecks.ensurePositive("V indice", uvIndices.y);

        final List<SampleDimension> sampleDims = source.getSampleDimensions();
        if (sampleDims == null) {
            throw new DataStoreException("No sample dimension available for given coverage: "+source.getIdentifier());
        }
        final int expectedNbBands = Math.max(uvIndices.x, uvIndices.y) + 1;
        final int actualNbBands = sampleDims.size();
        if (actualNbBands < expectedNbBands) {
            throw new IllegalArgumentException(String.format(
                    "Given coverage should contain at least %d bands as U and V are located in bands %d and %d "
                            + "respectively, but only %d bands are available in input coverage %s.",
                    expectedNbBands, uvIndices.x, uvIndices.y, actualNbBands, source.getIdentifier()
            ));
        }
        this.source = source;
        this.sourceGeometry = source.getGridGeometry();
        final CoordinateReferenceSystem crs = sourceGeometry.getCoordinateReferenceSystem();
        this.sourceCrs2d = CRS.getHorizontalComponent(crs);
        this.horizontalAxisIdx = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), sourceCrs2d.getCoordinateSystem());
        this.uvIndices = uvIndices;

        MathTransform1D uTransfer = sampleDims.get(uvIndices.x).getTransferFunction()
                .filter(tr -> !tr.isIdentity())
                .orElse(null);
        MathTransform1D vTransfer = sampleDims.get(uvIndices.y).getTransferFunction()
                .filter(tr -> !tr.isIdentity())
                .orElse(null);
        if (uTransfer == null && vTransfer == null) {
            native2geophysic = d -> {};
        } else if (uTransfer == null) {
            native2geophysic = d -> d[1] = vTransfer.transform(d[1]);
        } else if (vTransfer == null) {
            native2geophysic = d -> d[0] = uTransfer.transform(d[0]);
        } else {
            native2geophysic = d -> {
                d[0] = uTransfer.transform(d[0]);
                d[1] = vTransfer.transform(d[1]);
            };
        }
    }

    final GridGeometry getSourceGeometry() {
        return sourceGeometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getSourceGeometry().getCoordinateReferenceSystem();
    }

    @Override
    public Optional<UVSource.TimeSet> atOrigin(DirectPosition origin) {
        final Instant time = getTime(origin)
                .orElseThrow(() -> new IllegalArgumentException("Given position should contain time value"));

        final GridGeometry gg = getSourceGeometry();
        final GeneralEnvelope env = new GeneralEnvelope(gg.getEnvelope());
        boolean pointCrossData = subEnvelope(env, CRS::getHorizontalComponent)
                .filter(subEnv -> {
                    try {
                        final DirectPosition geoloc = CRS.findOperation(
                                origin.getCoordinateReferenceSystem(),
                                subEnv.getCoordinateReferenceSystem(),
                                null
                        )
                                .getMathTransform()
                                .transform(origin, null);

                        return subEnv.contains(geoloc);
                    } catch (FactoryException | TransformException e) {
                        throw new BackingStoreException("Cannot project source point in grid coverage horizontal system", e);
                    }
                })
                .isPresent();

        // Short-circuit : no horizontal matching. Stop now
        if (!pointCrossData) {
            LOGGER.log(Level.FINE, "[SimpleUVSource] -> Short-circuit: Given origin {0} does not horizontally intersect envelope {1} of data {2}", new Object[]{origin, env, source});
            return Optional.empty();
        }

        final GeneralEnvelope timeEnv = subEnvelope(env, CRS::getTemporalComponent)
                .orElseThrow(() -> new IllegalStateException("Source dataset has no temporal axis"));

        final DefaultTemporalCRS timeCrs = DefaultTemporalCRS.castOrCopy((TemporalCRS) timeEnv.getCoordinateReferenceSystem());
        timeEnv.getLowerCorner().setOrdinate(0, timeCrs.toValue(time));

        final GridDerivation subgrid = gg.derive().subgrid(env);
        // Note: If input data has an elevation, we'll try to freeze it on a slice : either the one specified at origin, or an arbitrary one.
        final GridGeometry subGrid = freezeElevation(env, origin)
                .map(subgrid::slice)
                .orElse(subgrid)
                .build();

        return Optional.of(new TimeSet(subGrid));
    }

    private static Optional<DirectPosition> freezeElevation(final GeneralEnvelope source, final DirectPosition userOrigin) {
        return subEnvelope(source, VERTICAL_EXTRACTOR)
                .map(env
                        -> getSubCrs(userOrigin::getCoordinateReferenceSystem, VERTICAL_EXTRACTOR)
                        .map(component -> {
                            final DirectPosition1D elevation = new DirectPosition1D(component.crs);
                            elevation.coordinate = userOrigin.getOrdinate(component.idx);
                            return (DirectPosition) elevation;
                        })
                        .orElseGet(() -> {
                            LOGGER.log(Level.WARNING, "UV data has an elevation axis, but specified origin has not. We'll freeze data arbitrarily at its lowest value");
                            return env.getLowerCorner();
                        })
                );
    }

    class TimeSet implements UVSource.TimeSet {

        final GridGeometry atOrigin;

        Calibration2D lastQueried;

        public TimeSet(GridGeometry atOrigin) {
            this.atOrigin = atOrigin;
        }

        @Override
        public Optional<UVSource.Calibration2D> setTime(Instant target) {
            final DirectPosition1D timePoint = new DirectPosition1D(DEFAULT_TEMPORAL_CRS);
            timePoint.coordinate = target.getEpochSecond();
            try {
                final GridGeometry geom2d = atOrigin.derive()
                        .slice(timePoint)
                        .build();
                if (lastQueried == null || !lastQueried.sliceGeom.getExtent().equals(geom2d.getExtent())) {
                    lastQueried = new Calibration2D(geom2d);
                }

                return Optional.of(lastQueried);
            } catch (PointOutsideCoverageException e) {
                LOGGER.log(Level.FINE, "Queried time outside input data envelope");
                return Optional.empty();
            }
        }
    }

    class Calibration2D implements UVSource.Calibration2D {

        final GridGeometry sliceGeom;

        GeneralEnvelope lastQueried;
        Snapshot lastSnapshot;

        public Calibration2D(GridGeometry sliceGeom) {
            this.sliceGeom = sliceGeom;
        }

        @Override
        public UVSource.Snapshot setHorizontalComponent(Envelope target) {
            ArgumentChecks.ensureDimensionMatches("Horizontal crs", 2, target);
            if (lastQueried != null) {
                if (target.equals(lastQueried) || lastQueried.contains(target)) {
                    return lastSnapshot;
                }
            }

            final CoordinateOperation op;
            try {
                final CoordinateReferenceSystem targetCrs = target.getCoordinateReferenceSystem();
                // TODO : improve transform precision by providing a geographic envelope
                op = CRS.findOperation(targetCrs, sourceCrs2d, null);
                LOGGER.log(Level.FINE, () -> String.format(
                        "Created an operation between %s and %s. Approximate accuracy: %f",
                        targetCrs.getName(), sourceCrs2d, CRS.getLinearAccuracy(op)
                ));
            } catch (FactoryException ex) {
                throw new BackingStoreException("Cannot set horizontal conversion", ex);
            }

            final MathTransform target2Data = op.getMathTransform();
            if (target2Data instanceof MathTransform2D) {
                final MathTransform dataCrs2Grid;
                final RenderedImage snapshotImg;
                try {
                    GridGeometry snapshotGeom = sliceGeom.derive()
                            .subgrid(target)
                            .build()
                            .reduce(horizontalAxisIdx, horizontalAxisIdx+1);
                    final GridCoverage snapshot = source.read(snapshotGeom, uvIndices.x, uvIndices.y);
                    snapshotGeom = snapshot.getGridGeometry();
                    dataCrs2Grid = snapshotGeom.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                    snapshotImg = snapshot.render(snapshotGeom.getExtent());
                } catch (TransformException e) {
                    throw new BackingStoreException("Cannot project input envelope: " + target, e);
                } catch (DataStoreException e) {
                    throw new BackingStoreException("Error while reading source UV", e);
                }

                if (dataCrs2Grid instanceof MathTransform2D) {
                    final MathTransform2D targetCrs2Grid = MathTransforms.concatenate(
                            (MathTransform2D) target2Data, (MathTransform2D)dataCrs2Grid
                    );

                    lastSnapshot = new Snapshot(snapshotImg, sourceCrs2d, targetCrs2Grid);
                    lastQueried = new GeneralEnvelope(target); // use a new envelope to defend against external modifications
                    return lastSnapshot;
                }
                throw new IllegalStateException("Only 2D slices supported for now, bu built snapshot is not.");
            }

            throw new IllegalStateException("Expected a 2D transform, but got: " + (target2Data == null ? "null" : target2Data.getClass().getCanonicalName()));
        }
    }

    class Snapshot implements UVSource.Snapshot {

        final Raster slice;
        final MathTransform2D pointConverter;
        final double[] buffer;

        public Snapshot(RenderedImage slice, SingleCRS crs2d, MathTransform2D pointConverter) {
            if (slice.getNumXTiles() > 1 || slice.getNumYTiles() > 1) {
                throw new UnsupportedOperationException("Only single tile images are accepted");
            }
            this.slice = slice.getTile(0, 0);
            this.pointConverter = pointConverter;
            buffer = new double[slice.getSampleModel().getNumBands()];
        }

        @Override
        public Optional<Vector2d> evaluate(Point2D.Double location) {
                final Point2D tmpLoc;
            try {
                tmpLoc = pointConverter.transform(location, null);
            } catch (TransformException e) {
                throw new BackingStoreException("Cannot project input point: " + location, e);
            }

            // TODO: interpolation. Here we only pick nearest neighbor;
            final int x = (int) tmpLoc.getX();
            final int y = (int) tmpLoc.getY();
            if (x < 0 || y < 0 || x >= slice.getWidth() || y >= slice.getHeight()) {
                LOGGER.log(Level.FINE, "Following point is outside data envelope: {0}", location);
                return Optional.empty();
            }

            slice.getPixel(x, y, buffer);
            try {
                native2geophysic.transform(buffer);
            } catch (TransformException ex) {
                throw new BackingStoreException("Cannot transform pixel values from native to geophysic", ex);
            }

            if (Double.isFinite(buffer[0]) && Double.isFinite(buffer[1]))
                return Optional.of(new Vector2d(buffer));
            else
                return Optional.empty();
        }
    }

    @FunctionalInterface
    private static interface TransferFunction {
        void transform(final double[] nativePixel) throws TransformException;
    }
}
