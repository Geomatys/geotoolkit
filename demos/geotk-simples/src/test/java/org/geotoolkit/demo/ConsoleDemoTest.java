/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.junit.*;
import static org.junit.Assert.*;

import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.demo.referencing.CoordinateConversion;


/**
 * Ensures that no exception is thrown while running the demos.
 * The remos results are not verified however.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public class ConsoleDemoTest {
    /**
     * The output and error sreams to use instead of the standard ones.
     * The {@link System#out} and {@link System#err} streams will be
     * redirected there in order to verify their content.
     */
    private ByteArrayOutputStream out, err;

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
     */
    @After
    public void verifyContent() {
        String content;
        content = out.toString(); assertTrue(content, content.length() != 0);
        content = err.toString(); assertTrue(content, content.length() == 0);
    }

    /**
     * Tests {@link CoordinateConversion}.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testCoordinateConversion() throws Exception {
        Installation.allowSystemPreferences = false; // For avoiding warning messages in stderr.
        CoordinateConversion.main(new String[0]);
    }
}
