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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import static org.geotoolkit.util.grid.GridTraversal.EPSILON;

/**
 *
 * @author Alexis Manin (Geomatys)
 *
 * @todo test failure has been reported for random seed 1695120540846454768.
 */
public class GridTraversalTest {

    @Test
    public void testIncludeStart() {
        final GridTraversal.Builder builder = new GridTraversal.Builder()
                .setPolyline(2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7)
                .setIncludeStart(false);

        double[] firstPoint = getFirstPoint(builder);
        Assert.assertArrayEquals("Should be equal to second point", new double[]{4, 4}, firstPoint, 1e-9);

        builder.setIncludeStart(true);

        firstPoint = getFirstPoint(builder);
        Assert.assertArrayEquals("Should be equal to first point", new double[]{3, 3}, firstPoint, 1e-9);

        builder.setPolyline(2, 3, 3, 2, 2);

        firstPoint = getFirstPoint(builder);
        Assert.assertArrayEquals("Should be equal to first point", new double[]{3, 3}, firstPoint, 1e-9);

        builder.setIncludeStart(false);

        firstPoint = getFirstPoint(builder);
        Assert.assertArrayEquals("Should be equal to second point", new double[]{2, 2}, firstPoint, 1e-9);

        // Now we check for single dimension move
        builder.setPolyline(1, 3, 2, 1);
        firstPoint = getFirstPoint(builder);
        Assert.assertArrayEquals("Should be equal to second point", new double[]{2}, firstPoint, 1e-9);

        builder.setIncludeStart(true);
        Assert.assertArrayEquals("Should be equal to first point", new double[]{2}, firstPoint, 1e-9);

        // Check behavior when putting only one point
        builder.setIncludeStart(false)
            .setPolyline(1, 1);
        Assert.assertFalse("No point should be returned", builder.stream().findFirst().isPresent());
    }

    private static double[] getFirstPoint(final GridTraversal.Builder source) {
        return source.stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Grid traversal returned no result !"));
    }

    @Test
    public void testVectorCreation() {
        final Random rand = new Random();
        final long seed = rand.nextLong();
        rand.setSeed(seed);

        for (int i = 0 ; i < 51 ; i++) {
            final int dimension = rand.nextInt(40)+2;
            try {
                double[] start = rand.doubles(dimension, -20000, 20000).toArray();
                double[] end = rand.doubles(dimension, -20000, 20000).toArray();
                double[] generatedVector = GridTraversal.toVector(start, end);
                double[] expectedVector = IntStream.range(0, start.length)
                        .mapToDouble(x -> end[x] - start[x])
                        .toArray();
                Assert.assertArrayEquals("Vector generated", expectedVector, generatedVector, EPSILON);
            } catch (AssertionError|RuntimeException e) {
                String msg = String.format(
                        "Test failed for seed %d, iteration %d, %d dimensions",
                        seed, i, dimension
                );
                throw new AssertionError(msg, e);
            }
        }
    }

    @Test
    public void test2DSequential() {
        test2D(false);
    }

    @Test
    public void test2DParallel() {
        test2D(true);
    }

    @Test
    public void monkeyTest() {
        final Random rand = new Random();
        final long seed = rand.nextLong();
        rand.setSeed(seed);

        for (int i = 0 ; i < 51 ; i++) {
            final int dimension = rand.nextInt(5)+2;
            final int segmentNumber = rand.nextInt(50) + 1;
            final boolean parallel = rand.nextBoolean();
            try {
                final double[] trajectory = Stream.generate(() -> rand.doubles(dimension, -200, 200))
                        .limit(segmentNumber + 1)
                        .flatMapToDouble(in -> in)
                        .toArray();
                test(trajectory, dimension, true);
            } catch (AssertionError|RuntimeException e) {
                String msg = String.format(
                        "Test failed for seed %d, iteration %d, %d dimensions and %d segments in %s mode",
                        seed, i, dimension, segmentNumber, parallel? "parallel" : "sequential"
                );
                throw new AssertionError(msg, e);
            }
        }
    }

    private void test2D(boolean parallel) {
        double[] inPolyline = {
            -1.2, 3.4,
            -1.1, 3.45,
            -2.4, 3.6,
            -0.7, 2.8,
            1204, 2.8,
            4044.3, -2459.4
        };

        test(inPolyline, 2, parallel);
    }

    private void test(final double[] inPolyline, final int dimension, final boolean parallel) {
        final List<double[]> points = GridTraversal.stream(inPolyline, dimension, true, parallel)
                .collect(Collectors.toList());

        // First two points should be the first segment of the polyline, as we do not cross any pixel.
        Assert.assertArrayEquals(
                "First extracted point should be the polyline start point",
                Arrays.copyOf(inPolyline, dimension),
                points.get(0),
                EPSILON);

        double[] start = new double[dimension];
        double[] end = new double[dimension];
        int idx = 1;
        int segmentIdx = 1;
        try {
            for (int i = dimension; i < inPolyline.length; i += dimension, segmentIdx = i / dimension) {
                System.arraycopy(inPolyline, i - dimension, start, 0, dimension);
                System.arraycopy(inPolyline, i, end, 0, dimension);

                final double[] segmentVector = GridTraversal.toVector(start, end);

                Assert.assertTrue("Generated polyline has diverged from input one", idx < points.size());

                double[] previousPt = start;
                while (idx < points.size()) {
                    final double[] currentPt = points.get(idx);
                    checkNotEqual(previousPt, currentPt); // We don't want doublons

                    try {
                        checkColinearity(segmentVector, previousPt, currentPt);
                    } catch (AssertionError e) {
                    /* If there's an error, it could be because we're gone to the next segment. However, if it is not
                     * the case, then there's a real error to give back.
                     * To be sure that we did not jump too early to the segment's endpoint, we ensure that two last
                     * points (which should previous segment end and the point preceding it) are close enough (in
                     * adjacent cells).
                     */
                        try {
                            Assert.assertArrayEquals(end, previousPt, EPSILON);
                            checkStep(segmentVector, points.get(idx - 2), previousPt);
                            break;
                        } catch (AssertionError bis) {
                            e.addSuppressed(bis);
                            throw e;
                        }
                    }

                    checkStep(segmentVector, previousPt, currentPt);
                    previousPt = currentPt;
                    idx++;
                }
            }
        } catch (AssertionError | RuntimeException e) {
            throw new AssertionError(String.format(
                    "Error while evaluating point %d against segment %d(%s to %s)",
                    idx, segmentIdx, Arrays.toString(start), Arrays.toString(end)
            ), e);
        }
    }


    static void checkSegment(final double[] start, final double[] end, final Iterator<double[]> generatedPoints) {
        double[] segmentVector = GridTraversal.toVector(start, end);

        double[] previousPt = start;
        while (generatedPoints.hasNext()) {
            final double[] pt = generatedPoints.next();
            checkNotEqual(previousPt, pt);
            checkColinearity(segmentVector, previousPt, pt);
            checkStep(segmentVector, previousPt, pt);
            previousPt = pt;
        }

        // check that last point is equal to given endpoint.
        Assert.assertArrayEquals(
                "Spliterator did not stop on expected segment end.",
                end,
                previousPt,
                EPSILON
        );
    }

    private static void checkNotEqual(final double[]pt1, final double[] pt2) {
        boolean doublon = true;
        try {
            Assert.assertArrayEquals(pt1, pt2, 1e-9);
        } catch (AssertionError e) {
            // Expected result. We do not want given points to be the same.
            doublon = false;
        }
        if (doublon) {
            Assert.fail("We've found two consecutive points that are equal.");
        }
    }

    private static void checkColinearity(final double[] segmentVector, double[] previousPt, double[] point) {
        // First, check that our vector is still colinear with input vector
        int i = 0;
        while (GridTraversal.isNearZero(segmentVector[i])) {
            i++;
        }

        double moveRatio = (point[i] - previousPt[i]) / (segmentVector[i]);
        for (; i < segmentVector.length; i++) {
            final double ordinate = point[i] - previousPt[i];
            if (GridTraversal.isNearZero(segmentVector[i])) {
                Assert.assertTrue("Ordinate at index " + i + " should be approximately 0", GridTraversal.isNearZero(ordinate));
            } else {
                Assert.assertEquals("Vectors are not colinear, not proportional.", moveRatio, (ordinate) / (segmentVector[i]), EPSILON);
            }
        }
    }

    private static void checkStep(final double[] segmentVector, double[] previousPt, double[] point) {
        // to know if our point is in a reasonable distance from the previous point, we could use distance.
        // However, it is not a reliable measure in high-dimension spaces. Instead, we just ensure that no ordinate
        // has grown too far
        for (int i = 0; i < segmentVector.length; i++) {
            if (GridTraversal.isNearZero(segmentVector[i])) {
                Assert.assertEquals("Ordinate at index "+i+" should be constant ->", previousPt[i], point[i], EPSILON);
            } else {
                final boolean increment = segmentVector[i] >= 0;
                final double lowerBound = increment ? previousPt[i] : GridTraversal.floorOrDecrement(previousPt[i]);
                final double upperBound = increment ? GridTraversal.ceilOrIncrement(previousPt[i]) : previousPt[i];

                if (point[i] < lowerBound - EPSILON || point[i] > upperBound + EPSILON) {
                    Assert.fail(String.format(
                            "Ordinate at index %d is %f, but we expect a value in [%f..%f]",
                            i, point[i], lowerBound, upperBound
                    ));
                }
            }
        }
    }
}
