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

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;
import org.junit.Assert;
import org.junit.Test;

import static org.geotoolkit.util.grid.GridTraversal.EPSILON;
import static org.geotoolkit.util.grid.GridTraversalTest.checkSegment;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MultiDimensionMoveTest {

    @Test
    public void test2DSequential() {
        test2D(false);
    }

    @Test
    public void test3DSequential() {
        test3D(false);
    }

    @Test
    public void monkeyTesting() {
        final Random rand = new Random();
        final long seed = rand.nextLong();
        rand.setSeed(seed);

        for (int i = 0 ; i < 13 ; i++) {
            final int dimension = rand.nextInt(49)+2;
            final boolean parallel = false; // TODO : randomize
            try {
                double[] start = rand.doubles(dimension, -2000, 2000).toArray();
                double[] end = rand.doubles(dimension, -2000, 2000).toArray();
                check(start, end, parallel);
            } catch (AssertionError|RuntimeException e) {
                String msg = String.format(
                        "Test failed for seed %d, iteration %d, %d dimensions, %s mode",
                        seed, i, dimension, parallel? "parallel" : "sequential"
                );
                throw new AssertionError(msg, e);
            }
        }
    }

    private void test2D(final boolean parallel) {
        // first, test a mainly horizontal move
        final double[] start = {2.3,4.6};
        final double[] end = {6.6, 5.0};
        MultiDimensionMove move = new MultiDimensionMove(start, end);

        double[] result = StreamSupport.stream(move, parallel)
                .flatMapToDouble(DoubleStream::of)
                .toArray();

        Assert.assertEquals("Number of coordinates", 10, result.length);

        double expectedXVal = 3.0;
        for (int i = 0 ; i < result.length - 2 ; i+=2) {
            Assert.assertEquals(expectedXVal, result[i], EPSILON);
            assertTrue("Y coordinate should never reach end point value before last point", result[i+1] < end[1]);
            expectedXVal++;
        }

        // Ensure that last point is equal to endpoint
        Assert.assertEquals("X coordinate of end point", end[0], result[result.length - 2], EPSILON);
        Assert.assertEquals("Y coordinate of end point", end[1], result[result.length - 1], EPSILON);

        // Now, test a mainly vertical move
        end[0] = 2.5;
        end[1] = -9.2;
        move = new MultiDimensionMove(start, end);
        result = StreamSupport.stream(move, parallel)
                .flatMapToDouble(DoubleStream::of)
                .toArray();
        Assert.assertEquals("Number of coordinates", 30, result.length);

        double expectedYVal = 4.0;
        for (int i = 0 ; i < result.length - 2 ; i+=2) {
            Assert.assertEquals(expectedYVal, result[i+1], EPSILON);
            assertTrue("Y coordinate should never reach end point value before last point", result[i] < end[0]);
            expectedYVal--;
        }

        // Ensure that last point is equal to endpoint
        Assert.assertEquals("X coordinate of end point", end[0], result[result.length - 2], EPSILON);
        Assert.assertEquals("Y coordinate of end point", end[1], result[result.length - 1], EPSILON);

        // Test a move in which both axis move a lot
        start[0] = -3.0;
        start[1] = -7.0;
        end[0] = -8.5;
        end[1] = 2;

        check(start, end, parallel);
    }

    private void test3D(boolean parallel) {
        final double[] start = {2.3, 4.6, 100033.3};
        final double[] end = {5.6, 5.0, 100036.2};
        check(start, end, parallel);

        // We'll test with a null translation on one of the axes
        end[1] = start[1];
        check(start, end, parallel);
    }

    /**
     * we must ensure that in the segment AB, for each computed point X, all the following conditions are met:
     * <ul>
     * <li>AB and AX are colinear</li>
     * <li>AB and AX have the same direction</li>
     * <li>||ABX-1|| &lt; ||ABX|| &lt; ||ABX+1|| </li>
     * </ul>
     * @param start Start of the segment to analyze
     * @param end End of the segment to analyze
     * @param parallel True if we want to execute the analysis in parallel, false otherwise.
     */
    private void check(final double[] start, final double[] end, boolean parallel) {
        final MultiDimensionMove move = new MultiDimensionMove(start, end);
        final Iterator<double[]> points = StreamSupport.stream(move, parallel)
                .iterator();

        checkSegment(start, end, points);
    }
}
