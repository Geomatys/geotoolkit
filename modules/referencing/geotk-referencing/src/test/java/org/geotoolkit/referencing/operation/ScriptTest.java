/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.io.LineNumberReader;
import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.referencing.operation.provider.NADCON;
import org.geotoolkit.referencing.operation.projection.KrovakTest;
import org.geotoolkit.referencing.operation.projection.MercatorTest;
import org.geotoolkit.referencing.operation.projection.OrthographicTest;
import org.geotoolkit.referencing.operation.projection.ObliqueMercatorTest;
import org.geotoolkit.referencing.operation.projection.EquirectangularTest;
import org.geotoolkit.referencing.operation.projection.AlbersEqualAreaTest;
import org.geotoolkit.referencing.operation.projection.LambertConformalTest;
import org.geotoolkit.referencing.operation.projection.TransverseMercatorTest;
import org.geotoolkit.referencing.operation.projection.PolarStereographicTest;
import org.geotoolkit.referencing.operation.projection.ObliqueStereographicTest;
import org.geotoolkit.referencing.operation.transform.NadconTransformTest;

import org.junit.*;


/**
 * Run the test scripts. Each script contains a list of source and target coordinates
 * reference systems (in WKT), source coordinate points and expected coordinate points
 * after the transformation from source CRS to target CRS.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
@Depend({
    DefaultMathTransformFactoryTest.class, EquirectangularTest.class, MercatorTest.class,
    TransverseMercatorTest.class, ObliqueMercatorTest.class, LambertConformalTest.class,
    AlbersEqualAreaTest.class, OrthographicTest.class, ObliqueStereographicTest.class,
    PolarStereographicTest.class, KrovakTest.class, NadconTransformTest.class
})
public final class ScriptTest {
    /**
     * Sets to {@code true} for displaying statistics.
     */
    private static final boolean VERBOSE = false;

    /**
     * Runs the specified test script.
     *
     * @throws Exception If a test failed.
     */
    private void runScript(final String filename) throws Exception {
        final LineNumberReader in = TestData.openReader(this, filename);
        final ScriptRunner test = new ScriptRunner(in);
        test.run();
        in.close();
        if (VERBOSE) {
            System.out.println(filename);
            test.printStatistics();
            System.out.println();
        }
        if (test.firstError != null) {
            throw test.firstError;
        }
    }

    /**
     * Run {@code "ParameterizedTransform.txt"}. This script is different than the other ones,
     * in that it creates the math transform directly instead than infering them from a source
     * and a target CRS. Consequently it is a more direct test of projection implementations
     * than the other scripts.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testParameterizedTransform() throws Exception {
        runScript("ParameterizedTransform.txt");
    }

    /**
     * Run {@code "AbridgedMolodensky.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testAbridgedMolodesky() throws Exception {
        runScript("AbridgedMolodensky.txt");
    }

    /**
     * Run {@code "Molodensky.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testMolodesky() throws Exception {
        runScript("Molodensky.txt");
    }

    /**
     * Run {@code "DatumShift.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testDatumShift() throws Exception {
        runScript("DatumShift.txt");
    }

    /**
     * Run {@code "Equirectangular.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testEquirectangular() throws Exception {
        runScript("Equirectangular.txt");
    }

    /**
     * Run {@code "Mercator.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testMercator() throws Exception {
        runScript("Mercator.txt");
    }

    /**
     * Run the {@code "ObliqueMercator.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testObliqueMercator() throws Exception {
        runScript("ObliqueMercator.txt");
    }

    /**
     * Run {@code "TransverseMercator.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testTransverseMercator() throws Exception {
        runScript("TransverseMercator.txt");
    }

    /**
     * Run {@code "AlbersEqualArea.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testAlbersEqualArea() throws Exception {
        runScript("AlbersEqualArea.txt");
    }

    /**
     * Run {@code "LambertAzimuthalEqualArea.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testLambertAzimuthalEqualArea() throws Exception {
        runScript("LambertAzimuthalEqualArea.txt");
    }

    /**
     * Run {@code "LambertConic.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testLambertConic() throws Exception {
        runScript("LambertConic.txt");
    }

    /**
     * Run {@code "Stereographic.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testStereographic() throws Exception {
        runScript("Stereographic.txt");
    }

    /**
     * Run {@code "Orthographic.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testOrthographic() throws Exception {
        runScript("Orthographic.txt");
    }

    /**
     * Run {@code "NZMG.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testNZMG() throws Exception {
        runScript("NZMG.txt");
    }

    /**
     * Run {@code "Krovak.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testKrovak() throws Exception {
        runScript("Krovak.txt");
    }

    /**
     * Run {@code "NADCON.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testNADCON() throws Exception {
        Assume.assumeTrue(NADCON.isAvailable());
        runScript("NADCON.txt");
    }
}
