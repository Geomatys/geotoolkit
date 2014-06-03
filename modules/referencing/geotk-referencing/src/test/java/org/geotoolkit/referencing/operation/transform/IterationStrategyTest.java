/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.util.Random;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.operation.transform.IterationStrategy;
import static org.apache.sis.referencing.operation.transform.IterationStrategy.*;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link IterationStrategy} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class IterationStrategyTest {
    /**
     * Maximum number of dimension tested. The referencing module should be able to handle high
     * numbers, but we stick to low one in order to avoid making the test to long to execute.
     */
    private static final int MAX_DIMENSION = 6;

    /**
     * Maximum offset to test. The referencing module should be able to handle high numbers,
     * but we stick to low one in order to avoid making the test to long to execute.
     */
    private static final int MAX_OFFSET = 20;

    /**
     * Tests a few cases, comparing the computed value with the expected value.
     */
    @Test
    public void testExpected() {
        assertEquals("Target replace source.", ASCENDING,  suggest(0, 1, 0, 1, 240));
        assertEquals("Target before source.",  ASCENDING,  suggest(1, 1, 0, 1, 239));
        assertEquals("Target after source.",   DESCENDING, suggest(0, 1, 1, 1, 239));
        assertEquals("Overlaps.",           BUFFER_TARGET, suggest(0, 2, 1, 1, 120));
        assertEquals("Overlaps.",           BUFFER_SOURCE, suggest(1, 1, 0, 2, 120));
    }

    /**
     * An empirical test making sure that the target subarray didn't overwrote the
     * source subarray while a transformation was in progress.
     *
     * @throws TransformException Should never occur.
     */
    @Test
    public void empiricalTest() throws TransformException {
        final int[] statistics = new int[4];
        /*
         * The length of the above array is hard-coded on purpose, as a reminder that if we
         * need to augment this value, then we need to augment the statistic checks at the
         * end of this method as well.
         */
        final int length = (2*MAX_OFFSET) * MAX_DIMENSION;
        final double[] sourcePts = new double[length];
        final double[] targetPts = new double[length];
        final double[] sharedPts = new double[length];
        final Random random = new Random(650268926);
        for (int i=0; i<length; i++) {
            sourcePts[i] = random.nextDouble();
        }
        final int checksum = Arrays.hashCode(sourcePts);
        for (int sourceDimension=1; sourceDimension<=MAX_DIMENSION; sourceDimension++) {
            for (int targetDimension=1; targetDimension<=MAX_DIMENSION; targetDimension++) {
                final PseudoTransform tr = new PseudoTransform(sourceDimension, targetDimension);
                for (int srcOff=0; srcOff<=MAX_OFFSET; srcOff++) {
                    for (int dstOff=0; dstOff<=MAX_OFFSET; dstOff++) {
                        final int numPts = min((length-srcOff) / sourceDimension,
                                               (length-dstOff) / targetDimension);
                        final IterationStrategy strategy = IterationStrategy.suggest(
                                srcOff, sourceDimension, dstOff, targetDimension, numPts);
                        statistics[strategy.ordinal()]++;
                        Arrays.fill(targetPts, Double.NaN);
                        System.arraycopy(sourcePts, 0, sharedPts, 0, length);
                        tr.transform(sharedPts, srcOff, sharedPts, dstOff, numPts);
                        tr.transform(sourcePts, srcOff, targetPts, dstOff, numPts);
                        assertEquals("Source points have been modified.", checksum, Arrays.hashCode(sourcePts));
                        final int stop = dstOff + numPts * targetDimension;
                        for (int i=dstOff; i<stop; i++) {
                            final double expected = targetPts[i];
                            final double actual   = sharedPts[i];
                            if (actual != expected) {
                                final int index = i - dstOff;
                                fail("Transform" +
                                     "(srcOff=" + srcOff + " srcDim=" + sourceDimension +
                                     " dstOff=" + dstOff + " dstDim=" + targetDimension +
                                     " numPts=" + numPts + ") using strategy " + strategy +
                                     ": for point " + (index / targetDimension) +
                                     " at dimension " + (index % targetDimension) +
                                     ", expected " + expected + " but got " + actual);
                            }
                        }
                    }
                }
            }
        }
        int sum = 0;
        for (int i=0; i<statistics.length; i++) {
            sum += statistics[i];
        }
        assertEquals(MAX_DIMENSION * MAX_DIMENSION * (MAX_OFFSET+1) * (MAX_OFFSET+1), sum);
        /*
         * The following statistics were determined empirically at the time we wrote this test,
         * right after having debugged IterationStrategy. They depend on the value of MAX_OFFSET
         * and MAX_DIMENSION constants, but do not depend on the random number generation. The
         * sum checked just before this comment verifies partially that assertion.
         *
         * If we assume that the calculation done by IterationStrategy was optimal at the time
         * we wrote it, then the statistics below should not change for current MAX_* setting.
         * If those values change because of some algorithm change, then ASCENDING + DESCENDING
         * count should increase while the BUFFER_SOURCE + BUFFER_TARGET count should decrease,
         * otherwise it would not be an improvement.
         */
        assertEquals(4851, statistics[ASCENDING    .ordinal()]);
        assertEquals(4410, statistics[DESCENDING   .ordinal()]);
        assertEquals(3465, statistics[BUFFER_SOURCE.ordinal()]);
        assertEquals(3150, statistics[BUFFER_TARGET.ordinal()]);
    }
}
