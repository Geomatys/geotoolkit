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
package org.geotoolkit.internal.image.io;

import javax.imageio.spi.ImageReaderSpi;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link Formats}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.10
 */
public final strictfp class FormatsTest {
    /**
     * Tests {@link Formats#simplify(String[])}.
     */
    @Test
    public void testSimplify() {
        assertArrayEquals(new String[] {
            "ascii-grid", "bmp", "gif", "jpeg", "jpeg 2000", "jpeg2000", "jpg",
            "matrix", "netcdf", "png", "pnm", "raw", "records", "tif", "tiff"
        }, Formats.simplify(
            "raw", "tif", "jpeg", "WBMP", "PNM", "JPG", "wbmp", "JPEG", "PNG",
            "jpeg 2000", "ascii-grid", "tiff", "BMP", "JPEG2000", "RAW", "netcdf",
            "matrix", "jpeg2000", "GIF", "TIF", "TIFF", "jpg", "bmp", "pnm", "png",
            "NetCDF", "records", "JPEG 2000", "gif"
        ));
    }

    /**
     * Test {@link Formats#getDisplayName}.
     */
    @Test
    public void testGetDisplayName() {
        final ImageReaderSpi spi = Formats.getReaderByFormatName("png", null);
        assertNotNull("Not allowed to return null.", spi);
        assertEquals("PNG", Formats.getDisplayName(spi));
    }
}
