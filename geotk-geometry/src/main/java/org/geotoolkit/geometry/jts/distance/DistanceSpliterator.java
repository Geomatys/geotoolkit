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
 * Aims to browse segments of a polyline, and for each one, compute its length.
 * To get a new instance, use the {@link #builder() } method to obtain a builder
 * which will help you configure a spliterator.
 *
 * @author Alexis Manin (Geomatys)
 */
public class DistanceSpliterator implements Spliterator.OfDouble {

    /**
     * Polyline to iterate on.
     */
    private final CoordinateSequence sequence;
    /**
     * Coordinate reference system in which the polyline is expressed.
     */
    private final CoordinateReferenceSystem crs;

    /**
     * Browse the polyline using its coordinate indices.
     */
    private final OfInt idxSpliterator;
    /**
     * A provider of segment length computing.
     */
    private final Supplier<ToDoubleBiFunction<Coordinate, Coordinate>> engineProvider;

    /**
     * The segment length computer used by this spliterator.
     */
    private final ToDoubleBiFunction<Coordinate, Coordinate> distanceEngine;

    private final IntConsumer pointConsumer;
    /**
     * Held temporary results (segment length).
     */
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

    /**
     *
     * @return an helper to configure and create new distance spliterators.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CoordinateSequence polyline;
        private CoordinateReferenceSystem crs;
        private int fromInclusive;
        private int toExclusive = -1;

        /**
         * Specify the polyline to measure. If available in the geometry, we'll
         * also configure source coordinate reference system.
         *
         * @param polyline The polyline to measure.
         * @return this builder, for further configuration.
         * @throws FactoryException If an SRID is found in the given polyline,
         * but cannot be translated into {@link CoordinateReferenceSystem}.
         */
        public Builder setpolyline(final LineString polyline) throws FactoryException {
            ArgumentChecks.ensureNonNull("Polyline to use for distance computing", polyline);
            final CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(polyline);
            if (crs != null) {
                this.crs = crs;
            }

            this.polyline = polyline.getCoordinateSequence();

            return this;
        }

        /**
         * Specify the segments to measure using a list of points. We consider
         * the given coordinate sequence as a jointed suite of segments (meaning
         * the first segment is constitued from points 0 and 1, the second is
         * built from points 1 and 2, etc.)
         *
         * Important: if giving the polyline through this method, you have to
         * configure the point CRS using {@link #setCrs(org.opengis.referencing.crs.CoordinateReferenceSystem) }.
         *
         * @param polyline The sequence of points to use as a polyline.
         * @return this builder, for further configuration.
         */
        public Builder setPolyline(CoordinateSequence polyline) {
            this.polyline = polyline;
            return this;
        }

        /**
         * Tell what coordinate system is used by configured polyline. It will
         * allow the distance computer to find adequat ellipsoid, and perform
         * needed coordinate transformations.
         *
         * @param crs The coordinate reference system to consider for the
         * measured polyline.
         *
         * @return this builder, for further configuration.
         */
        public Builder setCrs(CoordinateReferenceSystem crs) {
            this.crs = crs;
            return this;
        }

        /**
         * Allow user to specify only a subset of the polyline to measure.
         * The given numbers represent an interval, whose start is the segment
         * index to start measure on, and end is the segment indice to stop
         * measures before.
         *
         * @param startInclusive Index of the segment (not polyline point) to
         * measure first. Allowed values are from 0 (first polyline segment) to
         * the number of points in the polyline  minus 2 (last segment).
         * @param endExclusive index of the segment to stop measure before.
         * Allowed values are from 1 (second segment in the polyline) to the
         * number of points in the polyline  minus 1 (void : a segment which
         * would be found after the polyline).
         *
         * @return this builder, for further configuration.
         */
        public Builder setRange(final int startInclusive, final int endExclusive) {
            fromInclusive = startInclusive;
            toExclusive = endExclusive;
            return this;
        }

        /**
         * Build a new spliterator from information previously configured. Not
         * that before calling this method, the polyline and CRS must have been
         * configured using {@link #setPolyline(org.locationtech.jts.geom.CoordinateSequence) }
         * and {@link #setCrs(org.opengis.referencing.crs.CoordinateReferenceSystem) }.
         *
         * Returned spliterator uses orthodromic distances, which is the shortest
         * distance between two points on an ellipsoid.
         *
         * @return A spliterator giving orthodromic lengths of segments.
         */
        public OfDouble buildOrthodromic() {
            return buildCustom(() -> new OrthodromicEngine(crs));
        }

        /**
         * Build a new spliterator from information previously configured. Not
         * that before calling this method, the polyline and CRS must have been
         * configured using {@link #setPolyline(org.locationtech.jts.geom.CoordinateSequence) }
         * and {@link #setCrs(org.opengis.referencing.crs.CoordinateReferenceSystem) }.
         *
         * This method returns a spliterator giving loxodromic distances, which
         * is the distance between two point on a sphere, when using a path with
         * constant heading.
         *
         * @return A spliterator giving loxodromic lengths of segments.
         */
        public OfDouble buildLoxodromic() {
            return buildCustom(() -> new LoxodromicEngine(crs));
        }

        /**
         * Create a new spliterator using a provided method for segment length
         * computing. If you aim is to compute either orthodromic or loxodromic
         * distance, we recommend to use {@link #buildOrthodromic() } or {@link #buildLoxodromic() }
         * respectively, instead of this method.
         *
         * @param segmentLengthComputerProvider Provide the engine to use for
         * segment length computing. Note that if the provided engine is not
         * thread-safe, you MUST provide a new and independant engine on each
         * supply. It is IMPORTANT, because the supplier will be called each
         * time the spliterator is splitted, meaning that values returned will
         * be used in parallel in different threads.
         *
         * @return A new spliterator using provided distance computing strategy.
         */
        public OfDouble buildCustom(final Supplier<ToDoubleBiFunction<Coordinate, Coordinate>> segmentLengthComputerProvider) {
            ArgumentChecks.ensureNonNull("Supplier of segment length computing engine", segmentLengthComputerProvider);
            checkValues();

            return new DistanceSpliterator(
                    polyline,
                    crs,
                    IntStream.range(fromInclusive + 1, toExclusive + 1).spliterator(),
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
                toExclusive = ptNumber - 1;
            } else if (toExclusive < 1 || toExclusive >= ptNumber) {
                throw new IllegalStateException(String.format(
                        "Invalid end position (to parameter). Given value: %d, authorized interval: [%d..%d]",
                        toExclusive, 1, ptNumber
                ));
            }
        }
    }
}
