/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.io.IOException;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.ReferencingTestCase;

import org.junit.*;
import static org.junit.Assume.*;


/**
 * Tests {@link NTv2Transform} with RGF93 data.
 *
 * @author Simon Reynard (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 */
public class RGF93TransformTest extends TransformTestCase {
    /**
     * The data file.
     */
    private static final String FILE = NTv2Transform.RGF93;

    /**
     * Source coordinates of the test points published in the IGN document.
     * The CRS is {@code EPSG:27572} except for axis order which is (North, East).
     */
    private static final double[] SOURCE_POINTS = {
        2669005.7300 ,  565767.9060,
        2685313.9090 ,  586916.3540,
        2640699.4610 ,  586809.9010,
        2558732.7240 ,  523456.3190,
        2558214.3740 ,  595226.2980,
        2558633.7960 ,  731608.0030,
        2525760.9710 ,  305468.5890,
        2492660.2280 ,  435389.8080,
        2488093.9460 ,  579906.7800,
        2486642.3370 ,  696204.1960,
        2466527.7250 ,  784124.9120,
        2470446.3390 ,  856998.3680,
        2485290.9830 ,  929226.5610,
        2394454.2120 ,   55824.4970,
        2376909.4780 ,  207039.4390,
        2364112.2710 ,  347675.8820,
        2347471.9370 ,  472921.7110,
        2410068.4940 ,  615208.4710,
        2336781.9170 ,  774255.1110,
        2354069.8220 ,  885696.8330,
        2347294.3570 , 1023838.2710,
        2231287.2200 ,  245930.1080,
        2234288.2340 ,  482536.2450,
        2243869.7100 ,  571772.5460,
        2213860.5740 ,  808477.9260,
        2238672.8090 ,  962154.8710,
        2178572.4910 ,  335739.3040,
        2124204.1250 ,  710424.8050,
        2099634.2940 ,  804996.0320,
        2141421.2140 ,  917801.8930,
        2013416.1660 ,  311927.5700,
        2000469.2590 ,  443131.7830,
        2008609.2550 ,  589087.0090,
        1982263.3390 ,  712291.5080,
        2009601.8830 ,  816457.4380,
        2000101.9410 ,  951557.8200,
        1925554.7010 ,  335588.3720,
        1895363.0580 ,  466953.3130,
        1888624.7270 ,  591523.5580,
        1914162.4660 ,  716320.1410,
        1887019.7680 ,  814149.5290,
        1903012.3120 ,  969601.4270,
        1832443.1700 ,  278246.3650,
        1814754.9160 ,  418920.7800,
        1791602.8840 ,  590413.4980,
        1765373.2060 , 1162613.7590
    };

    /**
     * Expected target coordinates of the test points published in the IGN document.
     * The CRS is {@code EPSG:2154} except for axis order which is (North, East).
     */
    private static final double[] EXPECTED_POINTS = {
        7102502.9796 ,  619119.4605,
        7118626.5017 ,  640394.2192,
        7074034.8467 ,  639914.1894,
        6992641.1077 ,  575909.3038,
        6991523.7515 ,  647634.5864,
        6990790.2512 ,  783942.7495,
        6961484.3559 ,  357778.7906,
        6927337.7626 ,  487347.6920,
        6921573.1749 ,  631736.5407,
        6919142.1958 ,  747948.8340,
        6898291.5327 ,  835641.6226,
        6901582.5244 ,  908500.6340,
        6915794.5771 ,  980808.8597,
        6832277.1818 ,  107242.8306,
        6813532.5659 ,  258206.9875,
        6799599.5673 ,  398636.7673,
        6781938.7375 ,  523653.0866,
        6843302.7284 ,  666360.5365,
        6768721.5295 ,  824671.2851,
        6785041.1104 ,  936176.9432,
        6777074.0950 , 1074154.7208,
        6667708.0689 ,  295884.5626,
        6668763.5434 ,  532320.5569,
        6677593.5695 ,  621562.9560,
        6645607.2826 ,  857816.7872,
        6669076.0321 , 1011577.0899,
        6614304.9039 ,  385188.6949,
        6556864.5922 ,  759087.4114,
        6531513.0948 ,  853362.4078,
        6572293.1866 ,  966422.5862,
        6449497.6594 ,  360048.3633,
        6435484.9121 ,  491014.8595,
        6442402.4429 ,  636891.9741,
        6415045.6523 ,  759750.0629,
        6441470.2490 ,  864043.4959,
        6430818.4617 ,  998925.0808,
        6361532.7761 ,  382965.7020,
        6330291.1939 ,  513941.6034,
        6322522.3676 ,  638321.3307,
        6346981.7959 ,  763198.2973,
        6319036.8495 ,  860690.8052,
        6333675.7251 , 1016109.8027,
        6268991.6779 ,  324925.5421,
        6250169.6075 ,  465295.7475,
        6225618.8552 ,  636400.3102,
        6194512.5764 , 1207701.1362
    };

     /**
     * Creates a new test suite.
     */
    public RGF93TransformTest() {
        super(NTv2Transform.class, null);
    }

    /**
     * Checks if the RGF93 data are available.
     */
    private static void assumeAvailable() {
        try {
            assumeTrue(Installation.NTv2.exists(NTv2Transform.class, FILE));
        } catch (IOException e) {
            throw new AssertionError(e); // Cause JUnit test failure.
        }
    }

    /**
     * Loads an binary file.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testBinary() throws FactoryException, TransformException {
        assumeAvailable();
        final NTv2Transform rgf = new NTv2Transform(FILE);
        transform = rgf;
        tolerance = 1E-10;
        stress(CoordinateDomain.GEOGRAPHIC, 426005043);
    }
    
    /**
     * Ensures that the cache works properly.
     * 
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testCache() throws FactoryException {
        assumeAvailable();
        final NTv2Transform rgf = new NTv2Transform(FILE);
        assertSame(rgf.grid, new NTv2Transform(FILE).grid);
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
    public void testTransform() throws FactoryException, TransformException {
        assumeTrue(ReferencingTestCase.isEpsgFactoryAvailable());
        assumeAvailable();
        assertEquals(SOURCE_POINTS.length, EXPECTED_POINTS.length);
        final double [] resPts = new double[SOURCE_POINTS.length];
        final NTv2Transform rgfTransform = new NTv2Transform(FILE);
        final ProjectedCRS sourceCRS = (ProjectedCRS) CRS.decode("EPSG:27572", true); // Lambert zone II etendu
        final ProjectedCRS targetCRS = (ProjectedCRS) CRS.decode("EPSG:2154",  true); // RGF93 / lambert93

        final MathTransform sourceToGeo = CRS.findMathTransform(sourceCRS, sourceCRS.getBaseCRS());
        final MathTransform geoToTarget = CRS.findMathTransform(targetCRS.getBaseCRS(), targetCRS);

        // Transform the coordinates.
        swapOrdinates(SOURCE_POINTS);
        sourceToGeo.transform(SOURCE_POINTS, 0, resPts, 0, SOURCE_POINTS.length/2);
        // Changement du meridien pour Greenwich
        for (int i=0; i<resPts.length; i+= 2) {
            resPts[i] += 2.596921297;
        }
        // Transformation coordonnées grades -> degrés
        for (int i=0; i<resPts.length; i++) {
            resPts[i] = resPts[i]*360/400;
        }
        // Transformation ntf->rgf93 avec la grille
        rgfTransform.transform(resPts, 0, resPts, 0, resPts.length/2);

        // Changement des valeurs vers projection RGF93/lambert93
        geoToTarget.transform(resPts, 0, resPts, 0, resPts.length/2);
        swapOrdinates(resPts);

        // Verify values
        for (int i=0; i<EXPECTED_POINTS.length; i++) {
            assertEquals(EXPECTED_POINTS[  i], resPts[i], 1E-4);
            assertEquals(EXPECTED_POINTS[++i], resPts[i], 1E-4);
        }
    }

    /**
     * Switch order of ordinates in a table. This is done because the IGN
     * document puts Northing before Easting.
     *
     * @param points table of coordinates.
     */
    private static void swapOrdinates(final double[] points) {
        for (int i=0; i<points.length; i++) {
            final double d = points[i];
            points[i] = points[++i];
            points[i] = d;
        }
    }
}
