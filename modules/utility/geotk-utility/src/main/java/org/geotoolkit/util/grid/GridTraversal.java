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
 * To acquire a stream of all points in the populated line string, use {@link #stream(double[], int, boolean) }.
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
     * @param trajectory The polyline ordinates. We expect ordinates of each points are given in order. Example: for
     * a 3 dimension polyline composed of points a and b, we expect the array to be: [a0, a1, a2, b0, b1, b2].
     * @param dimension Number of dimension of the polyline.
     */
    private GridTraversal(double[] trajectory, int dimension) {
        ArgumentChecks.ensureNonNull("Trajetory over the grid", trajectory);
        ArgumentChecks.ensureStrictlyPositive("Number of points in given trajectory", trajectory.length);
        ArgumentChecks.ensureStrictlyPositive("dimension", dimension);

        this.trajectory = new ContiguousArrayPoint(trajectory, dimension);
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
    }

    private static class ContiguousArrayPoint implements PointList {

        final double[] ordinates;
        final int dimension;

        final int size;

        public ContiguousArrayPoint(double[] ordinates, int dimension) {
            if (ordinates.length % dimension != 0) {
                throw new IllegalArgumentException(String.format(
                        "Given array size (%d) is not a multiple the number of dimensions (%d) specified",
                        ordinates.length, dimension
                ));
            }
            this.ordinates = ordinates;
            this.dimension = dimension;
            size = ordinates.length / dimension;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return ordinates.length <= 0;
        }

        @Override
        public double[] getPoint(int index) {
            index *= dimension;
            return Arrays.copyOfRange(ordinates, index, index + dimension);
        }
    }

    /**
     * Return a new polyline, populated with points intersecting a grid of reference - origin on (0, 0) and cell size
     * is 1.
     *
     * @param trajectory The polyline ordinates. We expect ordinates of each points are given in order. Example: for
     * a 3 dimension polyline composed of points a and b, we expect the array to be: [a0, a1, a2, b0, b1, b2].
     * @param dimension Number of dimension of the polyline.
     * @param parallel True if we want returned stream to use multi-threading, false otherwise.
     * @return A stream giving back in order points composing given polyline, adding points intersecting virual grid in
     * the same time.
     */
    public static Stream<double[]> stream(final double[] trajectory, final int dimension, final boolean parallel) {
        if (trajectory.length < dimension) {
            return Stream.empty();
        } else if (trajectory.length == dimension) {
            return Stream.of(trajectory);
        } else {
            // Spliterator omit the very first point, so we must add it manually.
            double[] firstPoint = Arrays.copyOf(trajectory, dimension);
            final Stream<double[]> startPoint = Stream.of(firstPoint);
            return Stream.concat(startPoint, StreamSupport.stream(new GridTraversal(trajectory, dimension), parallel));
        }
    }
}
