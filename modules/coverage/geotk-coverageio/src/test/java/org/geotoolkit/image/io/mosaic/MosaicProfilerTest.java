/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.io.IOException;

import org.apache.sis.math.Statistics;

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
public final strictfp class MosaicProfilerTest extends MosaicTestBase {
    /**
     * Small value for floating point comparisons.
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
     * @param  profiler The profiler for which to run {@link MosaicProfiler#estimateEfficiency(int)}.
     * @return The efficiency estimation.
     * @throws IOException Should not happen.
     */
    private static double single(final MosaicProfiler profiler) throws IOException {
        final Statistics stats = profiler.estimateEfficiency(1);
        assertEquals(1, stats.count());
        return stats.mean();
    }

    /**
     * Tests {@link MosaicProfiler#estimateEfficiency(int)} with some pre-defined regions
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
        assertEquals("Asked a translated images requerying 2 tiles.", 0.5, single(profiler), EPS);

        region.translate(0, TARGET_SIZE*3/4);
        profiler.setQueryRegion(region);
        assertEquals("Asked a translated images requerying 4 tiles.", 0.25, single(profiler), EPS);

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
        assertEquals("Asking for an exact tile should have no cost.", 0.25, single(profiler), EPS);

        region.translate(TARGET_SIZE, TARGET_SIZE);
        profiler.setQueryRegion(region);
        assertEquals("Cost should stay the same.", 0.25, single(profiler), EPS);

        region.translate(TARGET_SIZE/4, TARGET_SIZE*3/4);
        profiler.setQueryRegion(region);
        assertEquals("Asked an images requerying more tiles.", 1./9, single(profiler), EPS);

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
        assertEquals("Asked a translated images requerying 4 tiles.", 0.25, single(profiler), EPS);
    }

    /**
     * Tests {@link MosaicProfiler#estimateEfficiency(int)} for a constant image size and
     * using increasing subsampling values.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testEfficiencyAtConstantSubsampling() throws IOException {
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
            strict[i] = profiler.estimateEfficiency(50).mean();
        }
        profiler.setSeed(897026254);
        profiler.setMaxSubsampling(1);
        profiler.setSubsamplingChangeAllowed(true);
        for (int i=0; i<better.length; i++) {
            profiler.setMinSubsampling(i + 1);
            better[i] = profiler.estimateEfficiency(50).mean();
        }
        /*
         * Empirical values determined from previous run of this test. This test is performed only
         * in order to avoid regression; it doesn't not guarantee that the values are correct (this
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
                        "%6.3f, %6.3f,  // %6.2f  [%2d]",
                        strict[i], better[i], better[i] / strict[i], i+1));
            }
        }
        final double[] expected = {
            0.203,  0.203,  //   1.00  [ 1]
            0.088,  0.088,  //   1.00  [ 2]
            0.180,  0.180,  //   1.00  [ 3]
            0.036,  0.270,  //   7.54  [ 4]
            0.206,  0.206,  //   1.00  [ 5]
            0.088,  0.240,  //   2.73  [ 6]
            0.014,  0.267,  //  18.86  [ 7]
            0.011,  0.291,  //  25.59  [ 8]
            0.214,  0.214,  //   1.00  [ 9]
            0.100,  0.219,  //   2.19  [10]
            0.007,  0.249,  //  38.18  [11]
            0.036,  0.269,  //   7.54  [12]
            0.005,  0.279,  //  58.06  [13]
            0.004,  0.321,  //  75.49  [14]
            0.247,  0.247,  //   1.00  [15]
            0.003,  0.261,  //  78.99  [16]
            0.003,  0.258,  //  86.74  [17]
            0.099,  0.255,  //   2.57  [18]
            0.002,  0.291,  // 121.63  [19]
            0.036,  0.281,  //   7.77  [20]
            0.015,  0.286,  //  19.51  [21]
            0.002,  0.263,  // 142.85  [22]
            0.002,  0.294,  // 175.34  [23]
            0.011,  0.282,  //  25.15  [24]
            0.025,  0.313,  //  12.78  [25]
            0.001,  0.333,  // 249.32  [26]
            0.054,  0.368,  //   6.79  [27]
            0.001,  0.367,  // 318.13  [28]
            0.001,  0.354,  // 327.02  [29]
            0.101,  0.101,  //   1.00  [30]
            0.001,  0.102,  // 106.91  [31]
            0.001,  0.107,  // 119.17  [32]
            0.007,  0.111,  //  16.84  [33]
            0.001,  0.102,  // 127.94  [34]
            0.015,  0.106,  //   7.20  [35]
            0.037,  0.115,  //   3.12  [36]
            0.001,  0.118,  // 173.21  [37]
            0.001,  0.117,  // 180.79  [38]
            0.005,  0.126,  //  25.91  [39]
            0.012,  0.132,  //  11.52  [40]
            0.001,  0.113,  // 203.96  [41]
            0.004,  0.119,  //  28.25  [42]
            0.001,  0.122,  // 239.92  [43]
            0.000,  0.126,  // 258.68  [44]
            0.286,  0.286,  //   1.00  [45]
            0.000,  0.321,  // 715.46  [46]
            0.000,  0.352,  // 824.85  [47]
            0.003,  0.356,  // 107.57  [48]
            0.000,  0.358,  // 913.88  [49]
            0.008,  0.354,  //  45.91  [50]
        };
        for (int i=0,j=0; j<expected.length; i++) {
            assertEquals("strict", expected[j++], strict[i], 0.001);
            assertEquals("better", expected[j++], better[i], 0.001);
        }
    }
}
