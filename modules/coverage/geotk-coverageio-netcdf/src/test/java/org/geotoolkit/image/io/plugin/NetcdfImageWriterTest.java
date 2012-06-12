/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
import javax.imageio.ImageWriter;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Test the {@link NetcdfImageWriter} implementation.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfImageWriterTest {
    /**
     * Tests the registration of the image writer in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("NetCDF");
        assertTrue("Expected a writer.", it.hasNext());
        assertTrue(it.next() instanceof NetcdfImageWriter);
        assertFalse("Expected no more writer.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        final Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType("application/netcdf");
        while (it.hasNext()) {
            if (it.next() instanceof NetcdfImageWriter) {
                return;
            }
        }
        fail("Writer not found.");
    }
}
