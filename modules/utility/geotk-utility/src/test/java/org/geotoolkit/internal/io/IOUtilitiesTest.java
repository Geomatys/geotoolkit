/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.io;

import java.io.File;
import java.io.IOException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link IOUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public final strictfp class IOUtilitiesTest {
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
     * Tests the {@link IOUtilities#changeExtension} method.
     *
     * @throws IOException Should not happen.
     *
     * @since 3.07
     */
    @Test
    public void testChangeExtension() throws IOException {
        assertEquals("Picture.tiff",
                IOUtilities.changeExtension("Picture.png", "tiff"));
        assertEquals("Users/name/Picture.tiff",
                IOUtilities.changeExtension("Users/name/Picture.png", "tiff"));
        assertEquals(new File("Picture.tiff"),
                IOUtilities.changeExtension(new File("Picture.png"), "tiff"));
        assertEquals(new File("Users/name/Picture.tiff"),
                IOUtilities.changeExtension(new File("Users/name/Picture.png"), "tiff"));

        final File file = new File("Users/name/Picture.png");
        assertSame(file, IOUtilities.changeExtension(file, "png"));
    }
}
