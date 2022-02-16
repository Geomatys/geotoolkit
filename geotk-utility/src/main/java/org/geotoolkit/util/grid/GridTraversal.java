/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.grid;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.util.ArgumentChecks;

/**
 * Find all intersections between a regular grid (whose cells are aligned with 0 origin, and size is 1 on each of their
 * axes) and a polyline. It returns all points of the polyline, adding intersection points on the fly.
 *
 * To acquire a stream of all points in the populated line string, use {@link #stream(double[], int, boolean, boolean)}
 * or the {@link Builder} class.
 *
 * @implNote
 * This algorithm complexity is roughly O(d*n), with d the number of dimensions of the polyline, and n is the total
 * number of intersection point found.
 *
 * It is partially splittable : each segment of input polyline will be treated in a separate thread if using a parallel
 * stream. Also, if one of the polyline segment is aligned on an reference axis (meaning only one of the dimension is
 * changing along the segment), it can be splitted too.
 *
 * TODO : make compliant with future ISO-19107 API.
 *
 * @author Alexis Manin (Geomatys)
 */
public class GridTraversal implements Spliterator<double[]> {

    /**
     * Authorized rounding when testing if two values are close. Note that changing that will impact your unit tests
     * because:
     * <ul>
     *     <li>A test check the rounding tolerance</li>
     *     <li>
     *         Another test checks this algorithm tolerance to big values.
     *         However, as we restrict to double precision, there could be rounding errors too big for this delta.
     *     </li>
     * </ul>
     */
    static final double EPSILON = 1e-8;

    /**
     * The epsilon to use when checking for colinearity. It is less precise than default epsilon, because it's used as
     * a check at the end of the operation chain, where there's more rounding errors.
     */
    static final double COLINEAR_EPSILON = 1e-5;

    private final PointList trajectory;

    private int startPoint;
    private final int endPoint;

    private Spliterator<double[]> intersectionEvaluator;

    /**
     * Create a new traversal analyzer for given polyline.
     *
     * @param trajectory The polyline coordinates. We expect coordinates of each points are given in order. Example: for
     * a 3 dimension polyline composed of points a and b, we expect the array to be: [a0, a1, a2, b0, b1, b2].
     */
    private GridTraversal(PointList trajectory) {
        this.trajectory = trajectory;
        startPoint = 0;
        endPoint = this.trajectory.size() - 1;

        prepareSegment();
    }

    private GridTraversal(final PointList trajectory, final int startPoint, final int endPoint) {
        this.trajectory = trajectory;
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        prepareSegment();
    }

    @Override
    public boolean tryAdvance(Consumer<? super double[]> action) {
        while (intersectionEvaluator != null) {
            boolean pushedForward = intersectionEvaluator.tryAdvance(action);
            if (pushedForward) {
                return true;
            }

            intersectionEvaluator = null;
            startPoint++;
            prepareSegment();
        }

        return false;
    }

    /**
     * Prepare analysis for the polyline segment denoted by currently configured start and end point.
     */
    private void prepareSegment() {
        if (startPoint >= endPoint)
            return;
        final double[] start = trajectory.getPoint(startPoint);
        final double[] end = trajectory.getPoint(startPoint+1);

        double[] vec = toVector(start, end);
        int nonZeroCount = 0; int lastNonZero = 0;
        for (int i = 0 ; i < vec.length ; i++) {
            if (!isNearZero(vec[i])) {
                nonZeroCount++;
                lastNonZero = i;
            }
        }

        switch (nonZeroCount) {
            case 0:
                startPoint++;
                prepareSegment();
                break;
            case 1:
                intersectionEvaluator = new MonoDimensionMove(lastNonZero, start, vec[lastNonZero]);
                break;
            default:
                intersectionEvaluator = new MultiDimensionMove(start, end);
                break;
        }
    }

    @Override
    public Spliterator<double[]> trySplit() {
        final int span = endPoint - startPoint;
        if (span > 1) {
            final int splitPoint = startPoint + span / 2;
            // At least two segments remain
            final Spliterator<double[]> prefix = new GridTraversal(trajectory, startPoint, splitPoint);
            this.startPoint = splitPoint;
            prepareSegment();
            return prefix;
        }

        // Last hope : maybe our inner spliterator is splittable
        if (intersectionEvaluator == null) {
            prepareSegment();
        }

        if (intersectionEvaluator != null) {
            return intersectionEvaluator.trySplit();
        }

        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        // We cannot mark it immutable, because input trajectory is a double array which could be modified externally.
        return ORDERED | NONNULL | DISTINCT;
    }

    static double ceilOrIncrement(final double source) {
        double upper = Math.ceil(source);
        if (isNearZero(source - upper)) {
            upper += 1;
        }

        return upper;
    }

    static double floorOrDecrement(final double source) {
        double lower = Math.floor(source);
        if (isNearZero(source - lower)) {
            lower -= 1;
        }

        return lower;
    }

    static boolean isNearZero(double ordinate) {
        return -EPSILON <= ordinate && ordinate <= EPSILON;
    }

    static double[] toVector(double[] start, double[] end) {
        double[] vec = new double[start.length];
        for (int i = 0; i < vec.length; i++) {
            vec[i] = end[i] - start[i];
        }

        return vec;
    }

    /**
     * A simple abstraction allowing to access input polyline points, whatever structure it is in. For now, we only
     * manage 1D array of value, but in the future, we could simply add a new PointList using a JTS polyline as input,
     * or any other structure.
     *
     * TODO : replace with true {@link List} interface.
     */
    interface PointList {
        int size();
        boolean isEmpty();
        double[] getPoint(int index);

        /**
         *
         * @return Number of coordinates in a single point (length of a point vector).
         */
        int getDimension();
    }

    private static class ContiguousArrayPoint implements PointList {

        final double[] coordinates;
        final int dimension;

        final int size;

        public ContiguousArrayPoint(int dimension, double... coordinates) {
            ArgumentChecks.ensureStrictlyPositive("dimension", dimension);
            if (coordinates.length % dimension != 0) {
                throw new IllegalArgumentException(String.format(
                        "Given array size (%d) is not a multiple the number of dimensions (%d) specified",
                        coordinates.length, dimension
                ));
            }
            this.coordinates = coordinates;
            this.dimension = dimension;
            size = coordinates.length / dimension;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return coordinates.length <= 0;
        }

        @Override
        public double[] getPoint(int index) {
            index *= dimension;
            return Arrays.copyOfRange(coordinates, index, index + dimension);
        }

        @Override
        public int getDimension() {
            return dimension;
        }
    }

    private static class Point2DList implements PointList {

        final List<Point2D> source;

        Point2DList(final List<Point2D> source) {
            this.source = source;
        }

        @Override
        public int size() {
            return source.size();
        }

        @Override
        public boolean isEmpty() {
            return source.isEmpty();
        }

        @Override
        public double[] getPoint(int index) {
            final Point2D pt2d = source.get(index);
            return new double[]{pt2d.getX(), pt2d.getY()};
        }

        @Override
        public int getDimension() {
            return 2;
        }
    }

    /**
     * Return a new polyline, populated with points intersecting a grid of reference - origin on (0, 0) and cell size
     * is 1.
     *
     * @param trajectory The polyline coordinates. We expect coordinates of each points are given in order. Example: for
     * a 3 dimension polyline composed of points a and b, we expect the array to be: [a0, a1, a2, b0, b1, b2].
     * @param dimension Number of dimension of the polyline.
     * @param includeStart True if we want result stream to give back start point of given trajectory on first move.
     * False if we want it to omit the first point of input polyline.
     * @param parallel True if we want returned stream to use multi-threading, false otherwise.
     * @return A stream giving back in order points composing given polyline, adding points intersecting virual grid in
     * the same time.
     */
    public static Stream<double[]> stream(final double[] trajectory, final int dimension, final boolean includeStart, final boolean parallel) {
        return new GridTraversal.Builder()
                .setPolyline(trajectory, dimension)
                .setIncludeStart(includeStart)
                .setParallel(parallel)
                .stream();

    }

    /**
     * Prepare a grid traversal tool. You have to specify at least the polyline to study using one of the
     * setPolyline methods (see {@link #setPolyline(double[], int)}, {@link #setPolyline(java.awt.geom.Point2D...) } or
     * {@link #setPolyline(java.util.List) }.
     *
     * You can optionally specify if you want returned point stream to be evaluated with multi-threading using
     * {@link #setParallel(boolean) } method.
     *
     * IMPORTANT: There's a method allowwing to configure resulting point stream to omit the polyline first point. That
     * can be useful for ray analysis. By default, result stream return the given polyline start point as first element.
     * Configure this behavior with {@link #setIncludeStart(boolean) }.
     */
    public static class Builder {
        PointList polyline;
        boolean includeStart = true;
        boolean parallel = false;

        public Builder setPolyline(Point2D... coordinates) {
            if (coordinates == null || coordinates.length == 0) {
                polyline = null;
            }

            return setPolyline(Arrays.asList(coordinates));
        }

        public Builder setPolyline(final List<Point2D> coordinates) {
            polyline = coordinates == null? null : new Point2DList(coordinates);
            return this;
        }

        /**
         *
         * @param coordinates
         * @param dimension
         * @return
         * @deprecated Use {@link #setPolyline(int, double...)} instead.
         */
        @Deprecated
        public Builder setPolyline(final double[] coordinates, final int dimension) {
            return setPolyline(dimension, coordinates);
        }

        public Builder setPolyline(final int dimension, final double... coordinates) {
            polyline = coordinates == null? null : new ContiguousArrayPoint(dimension, coordinates);
            return this;
        }

        /**
         *
         * @param includeStart True if you want the first point of result stream to be input polyline start point. False
         * to omit it.
         *
         * @return this builder for further configuration.
         */
        public Builder setIncludeStart(boolean includeStart) {
            this.includeStart = includeStart;
            return this;
        }

        public Builder setParallel(boolean parallel) {
            this.parallel = parallel;
            return this;
        }

        public Stream<double[]> stream() {
            ArgumentChecks.ensureNonNull("Polyline", polyline);
            if (polyline.isEmpty()) {
                return Stream.empty();
            } else if (polyline.size() == 1) {
                return includeStart ? Stream.of(polyline.getPoint(0)) : Stream.empty();
            } else {
                Stream<double[]> pointStream = StreamSupport.stream(new GridTraversal(polyline), parallel);
                if (includeStart) {
                    // Spliterator omit the very first point, so we must add it manually.
                    double[] firstPoint = polyline.getPoint(0);
                    final Stream<double[]> startPoint = Stream.of(firstPoint);
                    return Stream.concat(startPoint, pointStream);
                } else {
                    return pointStream;
                }
            }
        }
    }

    /**
     * Ensure that vector encoded by first parameter is proportional (u = kv) to the second given one.
     * Note that any non finite value in any of the source vectors will cause a detection failure (return false).
     *
     * <em>Note on precision</em>: this code rely on {@link GridTraversal#COLINEAR_EPSILON}, so it's required precision
     * (and therefore limit) is around this magnitude.
     *
     * @param u First vector to compare (should be avector representing a segment, or a move along a segment).
     * @param v Second vector in the comparison (should be avector representing a segment, or a move along a segment).
     * @return True if we detect that the two vectors are colinear. False if not.
     */
    static boolean areColinear(final double[] u, final double[] v) {
        return Double.isFinite(colinearityFactor(u, v, COLINEAR_EPSILON));
    }

    /**
     * Tries to compute a scalar value K that satisfies <pre>u = Kv</pre>. This is possible only if both vectors are
     * colinears, so {@link Double#NaN} will be returned if inputs are not colinear vectors.
     *
     * WARNING: inputs won't be validated, but they <em>must</em> follow these rules:
     * <ul>
     *     <li>u and v vectors must have the same number of dimensions</li>
     *     <li>Dimension of input vector must be strictly positive (greater or equal to 1)</li>
     *     <li>Given tolerance must be positive (greater or equal to 0)</li>
     * </ul>
     *
     * Notes:
     * <ul>
     *  <li>Be careful to choose a tolerance that matches your vectors magnitude.</li>
     *  <li>as a tolerance is accepted for K value, you must know that returned K value precision is not more than
     *  provided tolerance. This is because K is computed from first dimension of input vectors. The computation of K on
     *  other dimensions is only used as control values, so if there's a difference regarding input tolerance, you should
     *  keep in mind that returned K value is not averaged along all dimensions, it is the K of the first dimension.</li>
     *  <li>This code would be a perfect candidate for vectorization</li>
     *  <li>Maybe branchless optimisation would be possible</li>
     *  <li>OptionalDouble instead of double would make return value semantic clearer, however, there's currently a high
     *  cost for such construct (but in the future, Valhalla will open doors to a brand new world of optimisations).</li>
     * </ul>
     *
     * @param u First vector to compare (linear vector: represent a segment, or a move along one)
     * @param v Second vector to compare (linear vector: represent a segment, or a move along one)
     * @param tolerance An epsilon that represents the accepted shift for K coefficient between dimensions.
     * @return NaN if vectors are <em>not</em> colinear. Otherwise, return the scalar K representing proportionality
     * between input colinear vectors.
     */
    static double colinearityFactor(final double[] u, final double[] v, double tolerance) {
        // Ensure that u = kv for any moving/non null dimension.
        double coef = 1.0;
        boolean coefInitialized = false;
        for (int i = 0 ; i < u.length ; i++) {
            double uv = v[i] - u[i];
            if (!Double.isFinite(uv)) {
                return Double.NaN;
            } else if (isNearZero(uv)) {
                // No need to take into account. No move along this dimension
                continue;
            } else if (isNearZero(u[i])) {
                return Double.NaN;
            } else if (!coefInitialized) {
                coef = v[i] / u[i];
                coefInitialized = true;
            } else if (Math.abs(coef - (v[i] / u[i])) > tolerance) {
                return Double.NaN;
            }
        }

        return coef;
    }
}
