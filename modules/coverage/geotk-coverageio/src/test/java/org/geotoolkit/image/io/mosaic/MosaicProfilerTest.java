/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.io.IOException;

import org.geotoolkit.math.Statistics;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MosaicProfiler} using the Blue Marble mosaic.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class MosaicProfilerTest extends TestBase {
    /**
     * Small value for floating point comparaisons.
     */
    private static final double EPS = 1E-10;

    /**
     * Ensures that the {@code min} size is not greater than {@code max}.
     */
    private static void assertNotGreater(final Dimension min, final Dimension max) {
        assertTrue("Width is out of bounds.",  min.width  <= max.width);
        assertTrue("Height is out of bounds.", min.height <= max.height);
    }

    /**
     * Run a single sample using the given profiler, and returns the estimated cost.
     *
     * @param  profiler The profiler for which to run {@link MosaicProfiler#costSampling(int)}.
     * @return The cost estimation.
     * @throws IOException Should not happen.
     */
    private static double single(final MosaicProfiler profiler) throws IOException {
        final Statistics stats = profiler.costSampling(1);
        assertEquals(1, stats.count());
        return stats.mean();
    }

    /**
     * Tests {@link MosaicProfiler#costSampling(int)} with some pre-defined regions
     * and subsampling values. Note that because we leave no room for random values,
     * the random seed should have no effect.
     * <p>
     * The pyramid used have subsamplings of 1,3,5,9,15,45,90. It has no subsampling
     * of 2, which is useful for this test.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testExactTileSize() throws IOException {
        final MosaicProfiler profiler = new MosaicProfiler(manager);
        profiler.setMinSize(TARGET_SIZE);
        profiler.setMaxSize(TARGET_SIZE);
        /*
         * Tests images having exactly the size of tiles with no subsampling.
         */
        profiler.setMaxSubsampling(1);
        final Rectangle region = new Rectangle(2*TARGET_SIZE, 3*TARGET_SIZE, TARGET_SIZE, TARGET_SIZE);
        profiler.setQueryRegion(region);
        assertEquals("Asking for an exact tile should have no cost.", 1, single(profiler), EPS);

        region.translate(TARGET_SIZE/4, 0);
        profiler.setQueryRegion(region);
        assertEquals("Asked a translated images requerying 2 tiles.", 2, single(profiler), EPS);

        region.translate(0, TARGET_SIZE*3/4);
        profiler.setQueryRegion(region);
        assertEquals("Asked a translated images requerying 4 tiles.", 4, single(profiler), EPS);

        /*
         * Tests images twice bigger, with a subsampling of 2. Because there is no such subsampling
         * in the pyramid used for this test, it should fallback on the subsampling of 1.
         */
        profiler.setMinSubsampling(2);
        assertEquals("MaxSubsampling should have been adjusted automatically.",
                new Dimension(2, 2), profiler.getMaxSubsampling());
        region.x = region.y = TARGET_SIZE*4;
        region.width = region.height = TARGET_SIZE*2;
        profiler.setQueryRegion(region);
        assertEquals("Asking for an exact tile should have no cost.", 4, single(profiler), EPS);

        region.translate(TARGET_SIZE, TARGET_SIZE);
        profiler.setQueryRegion(region);
        assertEquals("Cost should stay the same.", 4, single(profiler), EPS);

        region.translate(TARGET_SIZE/4, TARGET_SIZE*3/4);
        profiler.setQueryRegion(region);
        assertEquals("Asked an images requerying more tiles.", 9, single(profiler), EPS);

        /*
         * Tests images with a subsampling of 3. We should be back on a subsampling
         * handled by the pyramid.
         */
        profiler.setMinSubsampling(3);
        assertEquals("MaxSubsampling should have been adjusted automatically.",
                new Dimension(3, 3), profiler.getMaxSubsampling());
        region.x = region.y = TARGET_SIZE*6;
        region.width = region.height = TARGET_SIZE*3;
        profiler.setQueryRegion(region);
        assertEquals("Asking for an exact tile should have no cost.", 1, single(profiler), EPS);

        region.translate(TARGET_SIZE, TARGET_SIZE);
        profiler.setQueryRegion(region);
        assertEquals("Asked a translated images requerying 4 tiles.", 4, single(profiler), EPS);
    }

    /**
     * Tests {@link MosaicProfiler#costSampling(int)} for a constant image size and
     * using increasing subsampling values.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testCostAtConstantSubsampling() throws IOException {
        final MosaicProfiler profiler = new MosaicProfiler(manager);
        assertNotGreater(profiler.getMaxSize(), profiler.getMosaicSize());

        profiler.setSeed(897026254);
        profiler.setMinSize(new Dimension(800, 600));
        profiler.setMaxSize(new Dimension(800, 600));
        profiler.setMaxSubsampling(1);

        final double[] strict = new double[50];
        final double[] better = new double[strict.length];
        for (int i=0; i<strict.length; i++) {
            profiler.setMinSubsampling(i + 1);
            strict[i] = profiler.costSampling(50).mean();
        }
        profiler.setSeed(897026254);
        profiler.setMaxSubsampling(1);
        profiler.setSubsamplingChangeAllowed(true);
        for (int i=0; i<better.length; i++) {
            profiler.setMinSubsampling(i + 1);
            better[i] = profiler.costSampling(50).mean();
        }
        /*
         * Empirical values determined from previous run of this test. This test is performed only
         * in order to avoid regression; it doesn't not garantee that the values are correct (this
         * was the job of the previous test methods). For updating the empirical values, set the
         * "if (false)" statement to "if (true)" in the block below. The columns are:
         *
         *   - cost when subsampling change is not allowed
         *   - cost when subsampling changes are allowed.
         *   - Ratio betwen column 1 and column 2
         *   - The subsampling value.
         */
        if (false) {
            for (int i=0; i<strict.length; i++) {
                System.out.println(String.format(java.util.Locale.US,
                        "%7.2f, %6.2f,  // %6.2f  [%2d]",
                        strict[i], better[i], strict[i] / better[i], i+1));
            }
        }
        final double[] expected = {
               5.88,   5.88,  //   1.00  [ 1]
              11.90,  11.90,  //   1.00  [ 2]
               6.34,   6.34,  //   1.00  [ 3]
              28.80,   4.04,  //   7.13  [ 4]
               5.68,   5.68,  //   1.00  [ 5]
              12.25,   4.59,  //   2.67  [ 6]
              71.54,   4.06,  //  17.64  [ 7]
              88.24,   3.57,  //  24.72  [ 8]
               5.68,   5.68,  //   1.00  [ 9]
              10.56,   5.19,  //   2.03  [10]
             153.83,   4.42,  //  34.79  [11]
              28.68,   4.00,  //   7.18  [12]
             208.67,   3.83,  //  54.51  [13]
             235.78,   3.28,  //  71.77  [14]
               5.03,   5.03,  //   1.00  [15]
             303.28,   4.59,  //  66.07  [16]
             337.73,   4.45,  //  75.82  [17]
              10.68,   4.37,  //   2.44  [18]
             418.25,   3.88,  // 107.87  [19]
              28.49,   3.91,  //   7.29  [20]
              69.04,   3.80,  //  18.17  [21]
             543.44,   3.93,  // 138.38  [22]
             597.47,   3.66,  // 163.31  [23]
              89.40,   3.69,  //  24.23  [24]
              41.13,   3.32,  //  12.40  [25]
             749.61,   3.17,  // 236.49  [26]
              19.20,   2.82,  //   6.81  [27]
             867.84,   2.85,  // 304.05  [28]
             925.06,   3.00,  // 308.37  [29]
              10.48,  10.48,  //   1.00  [30]
            1046.09,  10.47,  //  99.96  [31]
            1114.21,   9.96,  // 111.91  [32]
             151.95,   9.30,  //  16.34  [33]
            1252.61,  10.31,  // 121.45  [34]
              68.85,   9.85,  //   6.99  [35]
              28.15,   8.96,  //   3.14  [36]
            1468.69,   8.81,  // 166.70  [37]
            1552.36,   9.02,  // 172.05  [38]
             205.52,   8.25,  //  24.92  [39]
              87.32,   7.93,  //  11.02  [40]
            1798.20,   9.09,  // 197.88  [41]
             238.92,   8.88,  //  26.92  [42]
            1964.54,   8.64,  // 227.50  [43]
            2058.16,   8.30,  // 247.95  [44]
               3.65,   3.65,  //   1.00  [45]
            2228.89,   3.34,  // 666.51  [46]
            2342.63,   3.10,  // 756.24  [47]
             302.86,   3.04,  //  99.71  [48]
            2552.26,   2.98,  // 856.59  [49]
             130.91,   2.95   //  44.30  [50]
        };
        for (int i=0,j=0; j<expected.length; i++) {
            assertEquals("strict", expected[j++], strict[i], 0.01);
            assertEquals("better", expected[j++], better[i], 0.01);
        }
    }
}
