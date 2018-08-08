package org.geotoolkit.geometry.jts.distance;

import java.util.Spliterator;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.IntStream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.exceptions.IllegalCrsException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DistanceSpliterator implements Spliterator.OfDouble {

    private final CoordinateSequence sequence;
    private final CoordinateReferenceSystem crs;

    private final OfInt idxSpliterator;
    private final Supplier<ToDoubleBiFunction<Coordinate, Coordinate>> engineProvider;

    private final ToDoubleBiFunction<Coordinate, Coordinate> distanceEngine;
    private final IntConsumer pointConsumer;
    private double resultBuffer = 0;

    private DistanceSpliterator(
            CoordinateSequence polyline,
            CoordinateReferenceSystem polylineCrs,
            final OfInt coordinateIndexSpliterator,
            final Supplier<ToDoubleBiFunction<Coordinate, Coordinate>> distanceCalculatorSupplier
    ) {
        sequence = polyline;
        crs = polylineCrs;

        idxSpliterator = coordinateIndexSpliterator;

        distanceEngine = distanceCalculatorSupplier.get();
        engineProvider = distanceCalculatorSupplier;
        pointConsumer = idx -> {
            resultBuffer = distanceEngine.applyAsDouble(
                    sequence.getCoordinate(idx - 1),
                    sequence.getCoordinate(idx)
            );
        };
    }

    @Override
    public OfDouble trySplit() {
        OfInt idxSplitted = idxSpliterator.trySplit();
        if (idxSplitted != null) {
            return new DistanceSpliterator(sequence, crs, idxSplitted, engineProvider);
        }

        return null;
    }

    @Override
    public boolean tryAdvance(DoubleConsumer action) {
        if (idxSpliterator.tryAdvance(pointConsumer)) {
            action.accept(resultBuffer);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long estimateSize() {
        return idxSpliterator.estimateSize();
    }

    @Override
    public int characteristics() {
        /* We inherit wrapped spliterator characteristics concerning browsing
         * and splitting. However, as we override the returned value, we cannot
         * ensure value related characteristics (because two different segments
         * could have the same length, and segments length are arbitrary).
         */
        return idxSpliterator.characteristics() - DISTINCT - SORTED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CoordinateSequence polyline;
        private CoordinateReferenceSystem crs;
        private int fromInclusive;
        private int toExclusive = -1;

        public Builder setpolyline(final LineString polyline) throws FactoryException {
            ArgumentChecks.ensureNonNull("Polyline to use for distance computing", polyline);
            final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(polyline);
            if (crs != null) {
                this.crs = crs;
            }

            this.polyline = polyline.getCoordinateSequence();

            return this;
        }

        public Builder setPolyline(CoordinateSequence polyline) {
            this.polyline = polyline;
            return this;
        }

        public Builder setCrs(CoordinateReferenceSystem crs) {
            this.crs = crs;
            return this;
        }

        public Builder setRange(final int startInclusive, final int endExclusive) {
            fromInclusive = startInclusive;
            toExclusive = endExclusive;
            return this;
        }

        public OfDouble buildOrthodromic() {
            return buildCustom(() -> new OrthodromicEngine(crs));
        }

        public OfDouble buildLoxodromic() {
            return buildCustom(() -> new LoxodromicEngine(crs));
        }

        public OfDouble buildCustom(final Supplier<ToDoubleBiFunction<Coordinate, Coordinate>> segmentLengthComputerProvider) {
            ArgumentChecks.ensureNonNull("Supplier of segment length computing engine", segmentLengthComputerProvider);
            checkValues();

            return new DistanceSpliterator(
                    polyline,
                    crs,
                    IntStream.range(fromInclusive + 1, toExclusive).spliterator(),
                    segmentLengthComputerProvider
            );
        }

        private void checkValues() {
            if (polyline == null) {
                throw new IllegalStateException("No polyline provided for the distance measure.");
            }

            final int ptNumber = polyline.size();
            if (ptNumber < 1) {
                throw new IllegalStateException("An empty polyline has been given for distance measure.");
            } else if (ptNumber < 2) {
                throw new IllegalStateException("The polyline to measure contains only one point. There no segment to compute length for.");
            }

            if (crs == null) {
                throw new IllegalStateException("No coordinate reference system given for the polyline. We need one to be able to represent the polyline on an earth ellipsoid");
            }

            int crsDimension = crs.getCoordinateSystem().getDimension();
            if (crsDimension < 2) {
                throw new IllegalCrsException(crs, String.format(
                        "Given coordinate reference system has not enough dimension.%nExpected: 2, but found: %d%nGiven coordinate reference system: %s",
                        crsDimension, crs.getName().getCode()
                ));
            } else if (crsDimension > 2) {
                final SingleCRS horizontalCpt = CRS.getHorizontalComponent(crs);
                if (horizontalCpt == null) {
                    throw new IllegalCrsException(crs, String.format(
                            "No horizontal component can be found in given coordinate reference system [%s]. Impossible to project the polyline on an ellispoid",
                            crs.getName().getCode()
                    ));
                }

                crs = horizontalCpt;
            }

            if (fromInclusive < 0) {
                fromInclusive = 0;
            } else if (fromInclusive < 0 || fromInclusive > ptNumber -2) {
                throw new IllegalStateException(String.format(
                        "Invalid start position (from parameter). Given value: %d, authorized interval: [%d..%d]",
                        fromInclusive, 0, ptNumber - 2
                ));
            }

            if (toExclusive < 0) {
                toExclusive = ptNumber;
            } else if (toExclusive < 1 || toExclusive > ptNumber) {
                throw new IllegalStateException(String.format(
                        "Invalid end position (to parameter). Given value: %d, authorized interval: [%d..%d]",
                        toExclusive, 1, ptNumber
                ));
            }
        }
    }
}
