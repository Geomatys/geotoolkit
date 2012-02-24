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
package org.geotoolkit.image.io.plugin;

import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests that do not require any particular {@link NetcdfImageReader} instance.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.08
 */
public final strictfp class NetcdfImageReaderTest {
    /**
     * Tests the registration of the image reader in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("NetCDF");
        assertTrue("Expected a reader.", it.hasNext());
        assertTrue(it.next() instanceof NetcdfImageReader);
        assertFalse("Expected no more reader.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType("application/netcdf");
        while (it.hasNext()) {
            if (it.next() instanceof NetcdfImageReader) {
                return;
            }
        }
        fail("Reader not found.");
    }
}
