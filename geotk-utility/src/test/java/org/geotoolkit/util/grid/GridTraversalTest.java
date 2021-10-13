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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.sis.util.ArgumentChecks.ensurePositive;
import static org.geotoolkit.util.grid.GridTraversal.EPSILON;
import static org.geotoolkit.util.grid.GridTraversal.areColinear;
import static org.geotoolkit.util.grid.GridTraversal.isNearZero;
import static org.geotoolkit.util.grid.GridTraversal.toVector;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Alexis Manin (Geomatys)
 *
 */
public class GridTraversalTest {

    @Test
    public void testZeroCheck() {
        assertTrue( isNearZero(  1e-8));
        assertTrue( isNearZero(- 1e-8));
        assertTrue( isNearZero(  1e-9));
        assertTrue( isNearZero(- 1e-9));
        assertTrue( isNearZero( 42e-10));
        assertTrue( isNearZero(-22e-10));
        assertFalse(isNearZero(  1e-7));
        assertFalse(isNearZero(- 1e-7));
        assertFalse(isNearZero(  1e-4));
        assertFalse(isNearZero(- 1e-4));
    }

    @Test
    public void testColinearityDetection() {
        // No move = colinear
        assertTrue(areColinear(new double[] {0, 0}, new double[]{0, 0}));
        // Move along a single dimension = colinear
        assertTrue(areColinear(new double[] {0.29, 0.123, 3.2}, new double[]{0.34, 0.123, 3.2}));
        // Proportionals = colinear
        assertTrue(areColinear(new double[] {0.2, 1230, 345.454, 2.3}, new double[]{0.4000000002, 2460, 690.908000003, 4.6}));
        assertTrue(areColinear(new double[] {42, 0, 33.2}, new double[]{12600, 0, 9960}));
        // Otherwise, they're not
        assertFalse(areColinear(new double[] {0, 0}, new double[]{120, 10}));
        assertFalse(areColinear(new double[] {0, 20, 43}, new double[]{120, 10, 22}));
        assertFalse(areColinear(new double[] {0, 0, 12, 30}, new double[]{0, 0, 11, 10}));
        assertFalse(areColinear(new double[] {3.2, Double.NaN}, new double[]{3.2, 0}));
        assertFalse(areColinear(new double[] {Double.NaN, 0.1}, new double[]{0.1, 0.1}));
    }

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
        assertFalse("No point should be returned", builder.stream().findFirst().isPresent());
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
                double[] generatedVector = toVector(start, end);
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
        new MonkeyProducer().run();
    }

    /**
     * Test behavior with big numbers. Restrict looping / segment / dimensions for easier debug.
     */
    @Test
    public void bigMonkeyTest() {
        final Random rand = new Random();
        new MonkeyProducer(rand.nextLong(),
                range(2, 1),
                range(2, 2),
                range(20_000d, 200d),
                range(2, 4),
                Parallelization.random
        ).run();
        new MonkeyProducer(rand.nextLong(),
                range(2, 1),
                range(2, 2),
                range(200_000d, 200d),
                range(2, 4),
                Parallelization.random
        ).run();
        new MonkeyProducer(rand.nextLong(),
                range(2, 1),
                range(2, 2),
                range(2_000_000d, 200d),
                range(2, 4),
                Parallelization.random
        ).run();
    }

    /**
     * This test purpose is to keep track of past failed random tests, and run them each time to ensure no regression is
     * introduced. That mostly check corner cases.
     */
    @Test
    public void monkeyTestCornerCases() {
        // Big numbers --> Overflow end of segment
        new Monkey("OX8iX7Iz0eQAAAACAAAAA0E+hIAAAAAAQFkAAAAAAAAB").run();
        new Monkey("aFFVR40RZDwAAAACAAAAAkEIagAAAAAAQFkAAAAAAAAB").run();

        // Test polyline is entirely contained in one single pixel
        new Monkey("kEQw8rBokdoAAAACAAAAAkEIagAAAAAAQBAAAAAAAAAB").run();
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

    private static void test(final double[] inPolyline, final int dimension, final boolean parallel) {
        final List<double[]> points = GridTraversal.stream(inPolyline, dimension, true, parallel)
                .collect(Collectors.toList());

        Assert.assertArrayEquals(
                "First extracted point should be the polyline start point",
                Arrays.copyOf(inPolyline, dimension),
                points.get(0),
                EPSILON);

        double[] start = new double[dimension];
        double[] end = new double[dimension];
        int idx = 0;
        int segmentIdx = 1;
        try {
            for (int i = dimension; i < inPolyline.length; i += dimension, segmentIdx = i / dimension) {
                System.arraycopy(inPolyline, i - dimension, start, 0, dimension);
                System.arraycopy(inPolyline, i, end, 0, dimension);

                final double[] segmentVector = toVector(start, end);

                assertTrue("Generated polyline has diverged from input one", idx < points.size());

                double[] previousPt = start;
                while (++idx < points.size()) {
                    final double[] currentPt = points.get(idx);

                    checkStep(segmentVector, previousPt, currentPt);

                    if (almostEqual(currentPt, end)) break;
                    else previousPt = currentPt;
                }
            }
        } catch (AssertionError | RuntimeException e) {
            throw new AssertionError(String.format(
                    "Error while evaluating point %d against segment %d(%s to %s)",
                    idx, segmentIdx, Arrays.toString(start), Arrays.toString(end)
            ), e);
        }
    }

    private static boolean almostEqual(double[] first, double[] second) {
        for (int c = 0 ; c < first.length ; c++) {
            if (Math.abs(first[c] - second[c]) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    static void checkSegment(final double[] start, final double[] end, final Iterator<double[]> generatedPoints) {
        double[] segmentVector = toVector(start, end);

        double[] previousPt = start;
        while (generatedPoints.hasNext()) {
            final double[] pt = generatedPoints.next();
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

    private static void checkStep(final double[] segmentVector, double[] previousPt, double[] currentPoint) {
        checkNotEqual(previousPt, currentPoint); // We don't want doublons
        assertTrue(
                "Next grid intersection is not on source segment",
                areColinear(segmentVector, toVector(previousPt, currentPoint))
        );
        // to know if our point is in a reasonable distance from the previous point, we could use distance.
        // However, it is not a reliable measure in high-dimension spaces. Instead, we just ensure that no ordinate
        // has grown too far
        for (int i = 0; i < segmentVector.length; i++) {
            if (GridTraversal.isNearZero(segmentVector[i])) {
                Assert.assertEquals("Ordinate at index "+i+" should be constant ->", previousPt[i], currentPoint[i], EPSILON);
            } else {
                final boolean increment = segmentVector[i] >= 0;
                final double lowerBound = increment ? previousPt[i] : GridTraversal.floorOrDecrement(previousPt[i]);
                final double upperBound = increment ? GridTraversal.ceilOrIncrement(previousPt[i]) : previousPt[i];

                if (currentPoint[i] < lowerBound - EPSILON || currentPoint[i] > upperBound + EPSILON) {
                    Assert.fail(String.format(
                            "Ordinate at index %d is %f, but we expect a value in [%f..%f]",
                            i, currentPoint[i], lowerBound, upperBound
                    ));
                }
            }
        }
    }

    /**
     * See {@link IntClamp}
     */
    private static IntClamp range(int min, int size) {
        return new IntClamp(min, size);
    }

    /**
     * See {@link IntClamp}
     */
    private static DoubleClamp range(double min, double size) {
        return new DoubleClamp(min, size);
    }

    /**
     * A monkey represents a single random test. Using its {@link #encode() text format}, it is possible to recreate
     * an exact copy, to reproduce the same test behavior.
     */
    private static class Monkey implements Runnable {

        /**
         * Seed of the random number generator used for test case generation.
         */
        final long seed;

        final int dimension;
        final int nbSegments;

        final DoubleClamp coordClamp;

        private boolean parallel;

        Monkey(String b64Info) {
            final byte[] decodedInfo = Base64.getDecoder().decode(b64Info);
            final ByteBuffer buffer = ByteBuffer.wrap(decodedInfo);
            this.seed = buffer.getLong();
            this.dimension = buffer.getInt();
            this.nbSegments = buffer.getInt();
            this.coordClamp = range(buffer.getDouble(), buffer.getDouble());
            this.parallel = buffer.get() == 0;
        }

        Monkey(long seed, int dimension, int nbSegments, DoubleClamp coordClamp, boolean parallel) {
            this.seed = seed;
            this.dimension = dimension;
            this.nbSegments = nbSegments;
            this.coordClamp = coordClamp;
            this.parallel = parallel;
        }

        @Override
        public void run() {
            final Random rand = new Random(seed);

            final int nbOrdinates = dimension * (nbSegments + 1);
            final double[] trajectory = rand.doubles(nbOrdinates, coordClamp.min, coordClamp.min+coordClamp.size)
                    .toArray();
            test(trajectory, dimension, parallel);
        }

        /**
         * Allow to deactivate parallel computation, to ease debug.
         * @param parallel True to activate concurrency, false to run computing sequentially.
         * @return This monkey, to make it run.
         */
        public Monkey setParallel(boolean parallel) {
            this.parallel = parallel;
            return this;
        }

        /**
         *
         * @return The text id allowing to recreate this monkey.
         */
        String encode() {
            final byte[] info = new byte[8+4+4+8*2+1];
            final ByteBuffer buffer = ByteBuffer.wrap(info);
            buffer.putLong(seed)
                    .putInt(dimension)
                    .putInt(nbSegments)
                    .putDouble(coordClamp.min)
                    .putDouble(coordClamp.size)
                    .put((byte) (parallel ? 0 : 1));
            return Base64.getEncoder()
                    .withoutPadding()
                    .encodeToString(info);
        }
    }

    /**
     * Setup class useful for monkey testing parameterization/reproduction (not monkey reproduction).
     * Each number range field represents a range of values to bound random generated numbers.
     */
    private static class MonkeyProducer implements Runnable {
        /**
         * Seed of the random number generator used for test case generation.
         */
        final long seed;
        /**
         * What number of dimensions are authorized in target grid/polylines.
         */
        final IntClamp dimClamp;
        /**
         * How many segments to generate for each loop.
         */
        final IntClamp nbSegmentsClamp;
        /**
         * Generation boundaries for coordinate values.
         */
        final DoubleClamp coordClamp;

        /**
         * How many times to perform a test with a fresh dataset generated by this monkey.
         */
        final IntClamp loopClamp;

        /**
         * Do we authorize parallel processing or not.
         */
        final Parallelization parallelization;

        /**
         * Create a totally random dataset for test.
         */
        MonkeyProducer() {
            this(new Random().nextLong());
        }

        /**
         * It's necessary for retro-compatibility on past identified corner-cases.
         * However, please do not use it for new failed cases. Use {@link #MonkeyProducer(String)} instead.
         *
         * @param seed The seed to use for random number generation.
         */
        MonkeyProducer(final long seed) {
            this(seed, range(2, 5), range(1, 50), range(-200., 400.), range(51, 0), Parallelization.random);
        }

        /**
         * Reproduce a test-case from its encoded information.
         * When a monkey test fails, it should report the text to use here. It allows for consistent test reproduction.
         *
         * @param b64Info A text produced through {@link #encode()} method.
         */
        MonkeyProducer(String b64Info) {
            final byte[] decoded = Base64.getDecoder().decode(b64Info);
            final ByteBuffer buffer = ByteBuffer.wrap(decoded);
            this.seed = buffer.getLong();
            this.dimClamp = range(buffer.getInt(), buffer.getInt());
            this.nbSegmentsClamp = range(buffer.getInt(), buffer.getInt());
            this.coordClamp = range(buffer.getDouble(), buffer.getDouble());
            this.loopClamp = range(buffer.getInt(), buffer.getInt());
            this.parallelization = Parallelization.values()[buffer.getInt()];
        }

        MonkeyProducer(long seed, IntClamp dimClamp, IntClamp nbSegmentsClamp, DoubleClamp coordClamp, IntClamp loopClamp, Parallelization parallelization) {
            this.seed = seed;
            this.dimClamp = dimClamp;
            this.nbSegmentsClamp = nbSegmentsClamp;
            this.coordClamp = coordClamp;
            this.loopClamp = loopClamp;
            this.parallelization = parallelization;
        }

        @Override
        public void run() {
            final Random rand = new Random(seed);
            final int nbLoops = loopClamp.next(rand);
            for (int i = 0 ; i < nbLoops ; i++) {
                final int dimension = dimClamp.next(rand);
                final int segmentNumber = nbSegmentsClamp.next(rand);
                final boolean parallel;
                switch (parallelization) {
                    case force: parallel = true; break;
                    case forbid: parallel = false; break;
                    default: parallel = rand.nextBoolean();
                }

                final Monkey marcel = new Monkey(rand.nextLong(), dimension, segmentNumber, coordClamp, parallel);
                try {
                    marcel.run();
                } catch (AssertionError|RuntimeException e) {
                    String msg = String.format(
                            "To reproduce this failing case, run:%n%nnew Monkey(\"%s\").run();%n%n" +
                            "Please add above code in method [monkeyTestCornerCases] for non-regression purposes.%n" +
                            "Alternatively, you can execute again the whole test collection using:%n%n" +
                            "new MonkeyProducer(\"%s\").run();%n%n" +
                            "Failed case encountered at iteration %d/%d",
                            marcel.encode(), encode(), i, nbLoops
                    );
                    throw new AssertionError(msg, e);
                }
            }
        }

        /**
         *
         * @return A text containing all needed information to clone/rerun this exact monkey.
         */
        private String encode() {
            final byte[] info = new byte[52];
            final ByteBuffer buffer = ByteBuffer.wrap(info);
            buffer.putLong(seed);
            buffer.putInt(dimClamp.min);
            buffer.putInt(dimClamp.size);
            buffer.putInt(nbSegmentsClamp.min);
            buffer.putInt(nbSegmentsClamp.size);
            buffer.putDouble(coordClamp.min);
            buffer.putDouble(coordClamp.size);
            buffer.putInt(loopClamp.min);
            buffer.putInt(loopClamp.size);
            buffer.putInt(parallelization.ordinal());
            return Base64.getEncoder()
                    .withoutPadding()
                    .encodeToString(info);
        }
    }

    /**
     * An interval of values for random generation. Defined by lower bound accepted + value span.
     */
    private static class IntClamp {
        final int min;
        final int size;

        public IntClamp(int min, int size) {
            this.min = min;
            ensurePositive("Clamping size", size);
            this.size = size;
        }

        int next(Random rand) {
            if (size <= 0) return min;
            else return min + rand.nextInt(size);
        }
    }

    /**
     * See {@link IntClamp}
     */
    private static class DoubleClamp {
        final double min;
        final double size;

        public DoubleClamp(double min, double size) {
            this.min = min;
            this.size = size;
        }

        double next(Random rand) {
            if (size == 0) return min;
            return min + rand.nextDouble() * size;
        }
    }

    private enum Parallelization {
        forbid, force, random
    }
}
