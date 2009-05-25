/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.console;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link CommandLine}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class CommandLineTest {
    /**
     * Tests with all arguments set.
     */
    @Test
    public void testAll() {
        final Main main = new Main(new String[] {
            "--string",  "Hello world",
            "--flag",
            "--integer", "12",
            "--real",    "10.25",
            "--file",    "org/geotoolkit/Main.java",
            "--renamed", "Hello again"
        });
        main.initialize();
        assertEquals("Hello world", main.string);
        assertTrue  (main.flag);
        assertEquals(12, main.integer);
        assertEquals(10.25, main.real, 0.0);
        assertEquals(new File("org/geotoolkit/Main.java"), main.file);
        assertEquals("Hello again", main.dummy);
    }

    /**
     * Tests with some arguments left to default value.
     */
    @Test
    public void testSome() {
        final Main main = new Main(new String[] {
            "--integer", "15",
            "--file",    "org/geotoolkit/Main.java"
        });
        main.initialize();
        assertNull  (main.string);
        assertFalse (main.flag);
        assertEquals(15, main.integer);
        assertEquals(23.0, main.real, 0.0);
        assertEquals(new File("org/geotoolkit/Main.java"), main.file);
        assertNull  (main.dummy);
    }

    /**
     * Tests using the {@code =} separator between arguments and their values.
     */
    @Test
    public void testSeparators() {
        final Main main = new Main(new String[] {
            "--string=", "Hello world",
            "--integer", "=", "12",
            "--real=10.25",
            "--file = org/geotoolkit/Main.java",
            "--renamed", "=Hello again"
        });
        main.initialize();
        assertEquals("Hello world", main.string);
        assertFalse (main.flag);
        assertEquals(12, main.integer);
        assertEquals(10.25, main.real, 0.0);
        assertEquals(new File("org/geotoolkit/Main.java"), main.file);
        assertEquals("Hello again", main.dummy);
    }

    /**
     * Tests with unknown parameters.
     */
    @Test
    public void testUnknown() {
        final Main main = new Main(new String[] {
            "--integer", "15",
            "--dummy",   "org/geotoolkit/Main.java"
        });
        assertEquals(0, main.error.length());
        try {
            main.run();
            fail("Expected an exception");
        } catch (IllegalStateException e) {
            // This is the expected exception.
        }
        assertTrue(main.error.indexOf("--dummy") >= 0);
    }
}
