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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.util.ArgumentChecks;

/**
 * Find all intersections between a regular grid (whose cells are aligned with 0 origin, and size is 1 on each of their
 * axes) and a polyline. It returns all points of the polyline, adding intersection points on the fly.
 *
 * To acquire a stream of all points in the populated line string, use {@link #stream(double[], int, boolean) } or the
 * {@link Builder} class.
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

    static final double EPSILON = 1e-7;

    private final PointList trajectory;

    private int startPoint;
    private final int endPoint;

    private Spliterator<double[]> intersectionEvaluator;

    /**
     * Create a new traversal analyzer for given polyline.
     *
     * @param trajectory The polyline coordinates. We expect coordinates of each points are given in order. Example: for
     * a 3 dimension polyline composed of points a and b, we expect the array to be: [a0, a1, a2, b0, b1, b2].
     * @param dimension Number of dimension of the polyline.
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
        if (source - EPSILON < upper && upper < source + EPSILON) {
            upper += 1;
        }

        return upper;
    }

    static double floorOrDecrement(final double source) {
        double lower = Math.floor(source);
        if (source - EPSILON < lower && lower < source + EPSILON) {
            lower -= 1;
        }

        return lower;
    }

    static boolean isNearZero(double ordinate) {
        return -EPSILON < ordinate && ordinate < EPSILON;
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
    static interface PointList {
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

        public ContiguousArrayPoint(double[] coordinates, int dimension) {
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

        public Builder setPolyline(final double[] coordinates, final int dimension) {
            polyline = coordinates == null? null : new ContiguousArrayPoint(coordinates, dimension);
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
}
