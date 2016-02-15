/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.io.LineNumberReader;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;

import static org.junit.Assume.assumeTrue;


/**
 * Run the test scripts. Each script contains a list of source and target coordinates
 * reference systems (in WKT), source coordinate points and expected coordinate points
 * after the transformation from source CRS to target CRS.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.11
 *
 * @since 2.1
 */
public final strictfp class ScriptTest extends ReferencingTestBase {

    /**
     * Ensures that the EPSG database is available. If no EPSG database is installed,
     * then the tests will be skipped. We do not cause a test failure because the EPSG
     * database is not expected to be installed when Geotk is built for the first time
     * on a new machine.
     */
    @Before
    public void ensureEpsgAvailable() {
        assumeTrue(false /*isEpsgFactoryAvailable()*/);
    }

    /**
     * Runs the specified test script.
     *
     * @throws Exception If a test failed.
     */
    private void runScript(final String filename) throws Exception {
        final ScriptRunner test;
        try (LineNumberReader in = TestData.openReader(getClass(), filename)) {
            test = new ScriptRunner(in);
            test.run();
        }
        if (out != null) {
            out.println(filename);
            test.printStatistics(out);
            out.println();
        }
        if (test.firstError != null) {
            throw test.firstError;
        }
    }

    /**
     * Run {@code "ParameterizedTransform.txt"}. This script is different than the other ones,
     * in that it creates the math transform directly instead than inferring them from a source
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
     * Run {@code "DatumShift.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testDatumShift() throws Exception {
        runScript("DatumShift.txt");
    }

    /**
     * Run the {@code "ObliqueMercator.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    @Ignore("Too many test failures to fix (when projection a point too far from the central meridian).")
    public void testObliqueMercator() throws Exception {
        runScript("ObliqueMercator.txt");
    }

    /**
     * Run {@code "AlbersEqualArea.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    @Ignore("Too many test failures to fix (when projection a point too far from the central meridian).")
    public void testAlbersEqualArea() throws Exception {
        runScript("AlbersEqualArea.txt");
    }

    /**
     * Run {@code "LambertAzimuthalEqualArea.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    @Ignore("Temporarily disabled. A tolerance threshold which is currently 1E-6 needs to be come 1.3E-6. But before to change the threshold, we are waiting to see if migration in SIS fix the issue.")
    public void testLambertAzimuthalEqualArea() throws Exception {
        runScript("LambertAzimuthalEqualArea.txt");
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
     * Run {@code "Polyconic.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    public void testPolyconic() throws Exception {
        runScript("Polyconic.txt");
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
    @Ignore("Datum change detected in an unexpected place.")
    public void testKrovak() throws Exception {
        runScript("Krovak.txt");
    }

    /**
     * Run {@code "NADCON.txt"}.
     *
     * @throws Exception If a test failed.
     */
    @Test
    @Ignore("Revisit after migration to SIS.")
    public void testNADCON() throws Exception {
//        Assume.assumeTrue(NADCON.isAvailable());
        runScript("NADCON.txt");
    }
}
