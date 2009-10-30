/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.internal.io;

import java.net.URL;
import java.io.File;
import java.io.IOException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link IOUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 */
public class IOUtilitiesTest {
    /**
     * Tests the {@link IOUtilities#commonParent} method.
     */
    @Test
    public void testCommonParent() {
        final File root  = new File("home/root/subdirectory");
        final File other = new File("home/root/data/other");
        assertEquals(new File("home/root"), IOUtilities.commonParent(root, other));
    }

    /**
     * Tests the {@link IOUtilities#toFile} method.
     *
     * @throws IOException Should not happen.
     */
    @Test
    public void testToFile() throws IOException {
        assertEquals("Unix absolute path.", new File("/Users/name/Picture.png"),
                IOUtilities.toFile(new URL("file:/Users/name/Picture.png"), null));
        /*
         * Do not test a Windows-specific path (e.g. "file:///C:/some/path/Picture.png").
         * The result is different on Windows or Unix platforms.
         */
    }
}
