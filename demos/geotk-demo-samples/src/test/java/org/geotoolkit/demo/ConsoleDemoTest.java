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
package org.geotoolkit.demo;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.demo.referencing.CoordinateConversion;


/**
 * Ensures that no exception is thrown while running the demos.
 * The demos results are not verified however.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.03
 */
public final strictfp class ConsoleDemoTest extends TestBase {
    /**
     * The output and error streams to use instead of the standard ones.
     * The {@link System#out} and {@link System#err} streams will be
     * redirected there in order to verify their content.
     */
    private ByteArrayOutputStream out, err;

    /**
     * Shutdown the logging. This is necessary in order to avoid a test
     * failure in {@link #verifyContent()}.
     */
    @BeforeClass
    public static void shutdownLogging() {
        Logger.getLogger("org.geotoolkit").setLevel(Level.OFF);
    }

    /**
     * Redirects the standard output and error streams before to run the tests.
     */
    @Before
    public void redirectOutputStreams() {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
    }

    /**
     * Ensures that the output stream is non-empty and that the error stream is empty.
     * Note that the error stream is used the the loggers, so we need to shutdown the
     * logging before the test is run.
     */
    @After
    public void verifyContent() {
        String content;
        content = out.toString(); assertTrue(content, !content.isEmpty());
        content = err.toString(); assertTrue(content,  content.isEmpty());
    }

    /**
     * Tests {@link CoordinateConversion}.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testCoordinateConversion() throws Exception {
        Installation.allowSystemPreferences = false; // For avoiding warning messages in stderr.
        CoordinateConversion.main(CharSequences.EMPTY_ARRAY);
    }
}
