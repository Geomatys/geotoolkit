/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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


import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.Commons;
import org.opengis.test.CalculationType;
import org.opengis.test.referencing.TransformTestCase;
import org.apache.sis.internal.system.DataDirectory;
import org.junit.Test;

import static org.apache.sis.test.Assume.*;
import static java.lang.StrictMath.*;


/**
 * Tests {@link NTv2Transform} with RGF93 data.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class RGF93TransformTest extends TransformTestCase {
    /**
     * The test points, as (Easting, Northing) ordinates.
     * <ol>
     *   <li>The two first columns are the source points expressed in the EPSG:27572 projected CRS.</li>
     *   <li>The two next columns are the expected points expressed in the EPSG:2154 projected CRS,
     *       as computed by IGN using the NTv2 grid.</li>
     *   <li>The two last columns are the same expected points, but computed using CIRCE (the
     *       reference software) instead than the NTv2 grid.</li>
     * </ol>
     */
    private static final double[] POINTS = {
    //   EPSG:27572                     EPSG:2154 using NTv2           EPSG:2154 using CIRCE
    //   -------------------------      -------------------------      -------------------------
         565767.9060, 2669005.7300,     619119.4605, 7102502.9796,     619119.4610, 7102502.9800,  // [ 1]
         586916.3540, 2685313.9090,     640394.2192, 7118626.5017,     640394.2190, 7118626.5010,  // [ 2]
         586809.9010, 2640699.4610,     639914.1894, 7074034.8467,     639914.1890, 7074034.8460,  // [ 3]
         523456.3190, 2558732.7240,     575909.3038, 6992641.1077,     575909.3040, 6992641.1070,  // [ 4]
         595226.2980, 2558214.3740,     647634.5864, 6991523.7515,     647634.5870, 6991523.7510,  // [ 5]
         731608.0030, 2558633.7960,     783942.7495, 6990790.2512,     783942.7500, 6990790.2510,  // [ 6]
         305468.5890, 2525760.9710,     357778.7906, 6961484.3559,     357778.7910, 6961484.3560,  // [ 7]
         435389.8080, 2492660.2280,     487347.6920, 6927337.7626,     487347.6920, 6927337.7620,  // [ 8]
         579906.7800, 2488093.9460,     631736.5407, 6921573.1749,     631736.5410, 6921573.1750,  // [ 9]
         696204.1960, 2486642.3370,     747948.8340, 6919142.1958,     747948.8340, 6919142.1960,  // [10]
         784124.9120, 2466527.7250,     835641.6226, 6898291.5327,     835641.6230, 6898291.5320,  // [11]
         856998.3680, 2470446.3390,     908500.6340, 6901582.5244,     908500.6340, 6901582.5240,  // [12]
         929226.5610, 2485290.9830,     980808.8597, 6915794.5771,     980808.8600, 6915794.5770,  // [13]
          55824.4970, 2394454.2120,     107242.8306, 6832277.1818,     107242.8310, 6832277.1820,  // [14]
         207039.4390, 2376909.4780,     258206.9875, 6813532.5659,     258206.9870, 6813532.5660,  // [15]
         347675.8820, 2364112.2710,     398636.7673, 6799599.5673,     398636.7680, 6799599.5670,  // [16]
         472921.7110, 2347471.9370,     523653.0866, 6781938.7375,     523653.0870, 6781938.7370,  // [17]
         615208.4710, 2410068.4940,     666360.5365, 6843302.7284,     666360.5360, 6843302.7280,  // [18]
         774255.1110, 2336781.9170,     824671.2851, 6768721.5295,     824671.2850, 6768721.5290,  // [19]
         885696.8330, 2354069.8220,     936176.9432, 6785041.1104,     936176.9430, 6785041.1100,  // [20]
        1023838.2710, 2347294.3570,    1074154.7208, 6777074.0950,    1074154.7210, 6777074.0950,  // [21]
         245930.1080, 2231287.2200,     295884.5626, 6667708.0689,     295884.5620, 6667708.0690,  // [22]
         482536.2450, 2234288.2340,     532320.5569, 6668763.5434,     532320.5570, 6668763.5430,  // [23]
         571772.5460, 2243869.7100,     621562.9560, 6677593.5695,     621562.9560, 6677593.5690,  // [24]
         808477.9260, 2213860.5740,     857816.7872, 6645607.2826,     857816.7870, 6645607.2820,  // [25]
         962154.8710, 2238672.8090,    1011577.0899, 6669076.0321,    1011577.0900, 6669076.0320,  // [26]
         335739.3040, 2178572.4910,     385188.6949, 6614304.9039,     385188.6950, 6614304.9030,  // [27]
         710424.8050, 2124204.1250,     759087.4114, 6556864.5922,     759087.4110, 6556864.5920,  // [28]
         804996.0320, 2099634.2940,     853362.4078, 6531513.0948,     853362.4080, 6531513.0940,  // [29]
         917801.8930, 2141421.2140,     966422.5862, 6572293.1866,     966422.5860, 6572293.1870,  // [30]
         311927.5700, 2013416.1660,     360048.3633, 6449497.6594,     360048.3630, 6449497.6590,  // [31]
         443131.7830, 2000469.2590,     491014.8595, 6435484.9121,     491014.8590, 6435484.9120,  // [32]
         589087.0090, 2008609.2550,     636891.9741, 6442402.4429,     636891.9740, 6442402.4430,  // [33]
         712291.5080, 1982263.3390,     759750.0629, 6415045.6523,     759750.0630, 6415045.6520,  // [34]
         816457.4380, 2009601.8830,     864043.4959, 6441470.2490,     864043.4960, 6441470.2490,  // [35]
         951557.8200, 2000101.9410,     998925.0808, 6430818.4617,     998925.0810, 6430818.4620,  // [36]
         335588.3720, 1925554.7010,     382965.7020, 6361532.7761,     382965.7020, 6361532.7760,  // [37]
         466953.3130, 1895363.0580,     513941.6034, 6330291.1939,     513941.6030, 6330291.1930,  // [38]
         591523.5580, 1888624.7270,     638321.3307, 6322522.3676,     638321.3300, 6322522.3680,  // [39]
         716320.1410, 1914162.4660,     763198.2973, 6346981.7959,     763198.2970, 6346981.7960,  // [40]
         814149.5290, 1887019.7680,     860690.8052, 6319036.8495,     860690.8040, 6319036.8490,  // [41]
         969601.4270, 1903012.3120,    1016109.8027, 6333675.7251,    1016109.8030, 6333675.7250,  // [42]
         278246.3650, 1832443.1700,     324925.5421, 6268991.6779,     324925.5420, 6268991.6780,  // [43]
         418920.7800, 1814754.9160,     465295.7475, 6250169.6075,     465295.7470, 6250169.6080,  // [44]
         590413.4980, 1791602.8840,     636400.3102, 6225618.8552,     636400.3100, 6225618.8550,  // [45]
        1162613.7590, 1765373.2060,    1207701.1362, 6194512.5764,    1207701.1360, 6194512.5760   // [46]
    };

    /**
     * Returns source or expected test points.
     *
     * @param  type 0 for the source points, 1 for the expected destination points (NTv2),
     *         or 2 for the expected destination points (CIRCE).
     * @return The test points.
     */
    private static double[] getPoints(final int type) {
        final double[] points = new double[POINTS.length / 3];
        int srcOff = type*2;
        int dstOff = 0;
        while (dstOff < points.length) {
            System.arraycopy(POINTS, srcOff, points, dstOff, 2);
            srcOff += 6;
            dstOff += 2;
        }
        return points;
    }

    /**
     * Test the transformation of some points givens in the test set of
     * <a href="http://lambert93.ign.fr/index.php?id=30">"Notice explicative"</a>
     * from IGN lambert93 website.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    @org.junit.Ignore
    public void testTransform() throws FactoryException, TransformException {
        assumeDataExists(DataDirectory.DATUM_CHANGES, "gr3df97a.txt");
        assumeTrue(Commons.isEpsgFactoryAvailable());
        /*
         * Get the transform, which will use the NTv2 grid since
         * we are transforming between two-dimensional CRS.
         */
        final ProjectedCRS sourceCRS = (ProjectedCRS) CRS.decode("EPSG:27572", true);       // Lambert zone II etendu
        final ProjectedCRS targetCRS = (ProjectedCRS) CRS.decode("EPSG:2154",  true);       // RGF93 / lambert93
        transform = CRS.findMathTransform(sourceCRS, targetCRS);
        /*
         * Test the transform. It is normal to have a difference compared with CIRCE, since the
         * NTv2 grid is an approximation. However the difference compared to the expected values
         * for the NTv2 grid is more complex. In a previous version (were we performed all steps
         * by hand, including the conversion from grad to degrees and the longitude shift), we
         * had the same result (tolerance = 1E-4). By letting the referencing module doing the
         * transform concatenation by itself, we have a slight difference (less than 4E-4 metre).
         * It may be related to different rounding errors, since the referencing module performs
         * more aggressive usage and concatenation of affine transforms.
         */
        final double[] sourcePts   = getPoints(0);
        final double[] expectedPts = getPoints(1);
        final double[] actualPts   = new double[sourcePts.length];
        transform.transform(sourcePts, 0, actualPts, 0, sourcePts.length / 2);
        tolerance = 4E-4;
        assertCoordinatesEqual("RGF93 transform (compared to NTv2)",
                2, expectedPts, 0, actualPts, 0, expectedPts.length/2, CalculationType.DIRECT_TRANSFORM);

        tolerance = 2E-3;
        final double[] circePts = getPoints(2);
        assertCoordinatesEqual("RGF93 transform (compared to CIRCE)",
                2, circePts, 0, actualPts, 0, circePts.length/2, CalculationType.DIRECT_TRANSFORM);
        /*
         * Optional statistics for comparing our errors with the ones reported by IGN. We perform
         * equal or better than IGN in 42% of cases, and we perform worst in the remaining 58% of
         * cases. The differences however are very small: 0.5 millimetre.
         */
        if (false) {
            int pass = 0;
            double s1=0, s2=0;
            for (int i=0; i<circePts.length; i++) {
                final double d1 = abs(actualPts  [i] - circePts[i]);
                final double d2 = abs(expectedPts[i] - circePts[i]);
                s1 += d1;
                s2 += d2;
                if (d1 <= d2) {
                    pass++;
                }
            }
            System.out.println("Mean error (Geotk): " + s1 / circePts.length);
            System.out.println("Mean error (IGN):   " + s2 / circePts.length);
            System.out.println("No regression for " + round(pass*100f / circePts.length) + "% of ordinates.");
        }
    }
}
