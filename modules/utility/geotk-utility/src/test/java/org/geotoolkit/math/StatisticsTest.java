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
package org.geotoolkit.math;

import java.util.Random;
import java.util.Locale;
import java.io.IOException;
import java.io.StringWriter;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static java.lang.StrictMath.*;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link Statistics}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class StatisticsTest extends TestBase {
    /**
     * For floating point comparisons.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests the statistics over a large range of gaussian values.
     * Means should be close to 0, RMS and standard deviation should be close to 1.
     */
    @Test
    public void testGaussian() {
        final Random random = new Random(317780561);
        final Statistics statistics = new Statistics();
        for (int i=0; i<10000; i++) {
            statistics.add(random.nextGaussian());
        }
        assertEquals(0, statistics.countNaN());
        assertEquals(10000, statistics.count());
        assertEquals(0, statistics.mean(), 0.01);
        assertEquals(1, statistics.rms (), 0.01);
        assertEquals(1, statistics.standardDeviation(false), 0.01);
    }

    /**
     * Tests the statistics over a large range of values distributed between 0 and 1.
     * Means should be close to 0, minimum and maximum close to -1 and +1 respectively.
     */
    @Test
    public void testUniform() {
        // Theorical values for uniform distribution.
        final double lower  = -1;
        final double upper  = +1;
        final double range  = upper - lower;
        final double stdDev = sqrt(range*range / 12);

        // Now tests.
        final Random random = new Random(309080660);
        final Statistics statistics = new Statistics();
        for (int i=0; i<10000; i++) {
            statistics.add(random.nextDouble()*range + lower);
        }
        assertEquals(0,      statistics.countNaN());
        assertEquals(10000,  statistics.count());
        assertEquals(0.0,    statistics.mean(),    0.01);
        assertEquals(lower,  statistics.minimum(), 0.01);
        assertEquals(upper,  statistics.maximum(), 0.01);
        assertEquals(stdDev, statistics.rms(),     0.01);
        assertEquals(stdDev, statistics.standardDeviation(false), 0.01);
    }

    /**
     * Same than {@link #testUniform}, but on integer values.
     * Used for testing {@link Statistics#add(long)}.
     */
    @Test
    public void testUniformUsingIntegers() {
        // Theorical values for uniform distribution.
        final int    lower  = -1000;
        final int    upper  = +1000;
        final int    range  = upper - lower;
        final double stdDev = sqrt(range*range / 12.0);

        // Now tests.
        final Random random = new Random(309080660);
        final Statistics statistics = new Statistics();
        for (int i=0; i<10000; i++) {
            statistics.add(random.nextInt(range) + lower);
        }
        assertEquals(0,      statistics.countNaN());
        assertEquals(10000,  statistics.count());
        assertEquals(0.0,    statistics.mean(),    10.0);
        assertEquals(lower,  statistics.minimum(), 10.0);
        assertEquals(upper,  statistics.maximum(), 10.0);
        assertEquals(stdDev, statistics.rms(),     10.0);
        assertEquals(stdDev, statistics.standardDeviation(false), 10.0);
    }

    /**
     * Tests the statistics starting with a number big enough to make the code fails if we were
     * not using the <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">Kahan
     * summation algorithm</a>.
     */
    @Test
    public void testKahanAlgorithm() {
        final double[] offsetAndTolerancePairs = {
            // Starting with a reasonably small value, the result should be accurate
            // with or without Kahan algorithm. So we use that for testing the test.
            1000, 0.002,

            // First power of 10 for which the summation
            // fails if we don't use the Kahan algorithm.
            1E+16, 0.003,

            // Kahan algorithm still good here.
            1E+18, 0.003,

            // Last power of 10 before the summation fails
            // using the Kahan algorithm. Quality is lower.
            1E+19, 0.125,

            // Starting with this number fails in all case using our algorithm.
            // We test this value only in order to test our test method...
            1E+20, 0
        };
        final Random random = new Random(223386491);
        final Statistics statistics = new Statistics();
        for (int k=0; k<offsetAndTolerancePairs.length; k++) {
            final double offset = offsetAndTolerancePairs[k];
            final double tolerance = offsetAndTolerancePairs[++k];
            assertTrue("Possible misorder in offsetAndTolerancePairs", offset > 10);
            assertTrue("Possible misorder in offsetAndTolerancePairs", tolerance < 0.2);
            statistics.reset();
            statistics.add(offset);
            for (int i=0; i<10000; i++) {
                statistics.add(random.nextDouble());
            }
            final double r = statistics.mean() - offset / statistics.count();
            final double expected = (tolerance != 0) ? 0.5 : 0;
            assertEquals(expected, r, tolerance);

            statistics.add(-offset); // Accuracy will be better than in previous test.
            assertEquals(expected, statistics.mean(), min(tolerance, 0.1));
        }
    }

    /**
     * Tests the concatenation of many {@link Statistics} objects.
     */
    @Test
    public void testConcatenation() {
        final Random random = new Random(429323868);
        final Statistics global = new Statistics();
        final Statistics byBlock = new Statistics();
        for (int i=0; i<10; i++) {
            final Statistics block = new Statistics();
            for (int j=0; j<1000; j++) {
                final double value;
                if (random.nextInt(800) == 0) {
                    value = Double.NaN;
                } else {
                    value = random.nextGaussian() + 10*random.nextDouble();
                }
                global.add(value);
                block.add(value);
            }
            byBlock.add(block);
            if (i == 0) {
                assertEquals("Adding for the first time; should have the same amount of data.",
                        block, byBlock);
                assertEquals("Adding for the first time; should have got exactly the same data.",
                        global, byBlock);
            } else {
                assertFalse("Should have more data that the block we just computed.",
                        byBlock.equals(block));
            }
            assertEquals(global.count(),    byBlock.count());
            assertEquals(global.countNaN(), byBlock.countNaN());
            assertEquals(global.minimum(),  byBlock.minimum(), 0.0);
            assertEquals(global.maximum(),  byBlock.maximum(), 0.0);
            assertEquals(global.mean(),     byBlock.mean(),    1E-15);
            assertEquals(global.rms(),      byBlock.rms(),     1E-15);
        }
    }

    /**
     * Tests the formatting of {@link Statistics}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testFormatting() throws IOException {
        final Statistics statistics = new Statistics();
        assertEquals(0,  statistics.count());
        assertEquals(0,  statistics.countNaN());
        assertTrue(isNaN(statistics.minimum()));
        assertTrue(isNaN(statistics.maximum()));
        assertTrue(isNaN(statistics.range()));
        assertTrue(isNaN(statistics.mean()));
        assertTrue(isNaN(statistics.rms()));
        assertTrue(isNaN(statistics.standardDeviation(true)));
        assertTrue(isNaN(statistics.standardDeviation(false)));

        statistics.add(40);
        statistics.add(10);
        statistics.add(NaN);
        statistics.add(20);

        assertEquals(3,  statistics.count());
        assertEquals(1,  statistics.countNaN());
        assertEquals(10, statistics.minimum(), 0.0);
        assertEquals(40, statistics.maximum(), 0.0);
        assertEquals(30, statistics.range(), 0.0);
        assertEquals(23.333333333333333, statistics.mean(), EPS);
        assertEquals(26.457513110645905, statistics.rms(), EPS);
        assertEquals(12.472191289246473, statistics.standardDeviation(true), EPS);
        assertEquals(15.275252316519468, statistics.standardDeviation(false), EPS);

        assertNotSame(statistics, assertSerializable(statistics));

        Statistics other = statistics.clone();
        other.add(60);
        assertEquals(4,  other.count());
        assertEquals(1,  other.countNaN());
        assertEquals(10, other.minimum(), 0.0);
        assertEquals(60, other.maximum(), 0.0);

        final Statistics[] multi = new Statistics[] {statistics, other};
        final StringWriter buffer = new StringWriter();
        Statistics.writeTable(buffer, new String[] {"first", "second"}, multi, Locale.ENGLISH);
        assertMultilinesEquals(
                "┌─────────────────────┬───────┬────────┐\n" +
                "│                     │ first │ second │\n" +
                "├─────────────────────┼───────┼────────┤\n" +
                "│ Number of values:   │     3 │      4 │\n" +
                "│ Number of ‘NaN’:    │     1 │      1 │\n" +
                "│ Minimum value:      │  10.0 │   10.0 │\n" +
                "│ Maximum value:      │  40.0 │   60.0 │\n" +
                "│ Mean value:         │  23.3 │   32.5 │\n" +
                "│ Root Mean Square:   │  26.5 │   37.7 │\n" +
                "│ Standard deviation: │  15.3 │   22.2 │\n" +
                "└─────────────────────┴───────┴────────┘\n",
                buffer.toString());
    }
}
