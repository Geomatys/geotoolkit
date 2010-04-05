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
package org.geotoolkit.image.io.plugin;

import java.util.Iterator;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.DataBuffer;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.geotoolkit.test.image.ImageReaderTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link NetcdfImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 */
public final class NetcdfImageReaderTest extends ImageReaderTestBase {
    /**
     * Creates a reader.
     */
    @Override
    protected NetcdfImageReader createImageReader() throws IOException {
        NetcdfImageReader.Spi spi = new NetcdfImageReader.Spi();
        final NetcdfImageReader reader = new NetcdfImageReader(spi);
        reader.setInput(NetcdfTestBase.getTestFile());
        return reader;
    }

    /**
     * Tests the metadata.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        final NetcdfImageReader reader = createImageReader();
        assertArrayEquals(new String[] {"temperature", "pct_variance"}, reader.getImageNames().toArray());
        assertEquals(  2, reader.getNumImages(false));
        assertEquals(  1, reader.getNumBands (0));
        assertEquals(  4, reader.getDimension(0));
        assertEquals(720, reader.getWidth    (0));
        assertEquals(499, reader.getHeight   (0));
        assertEquals(DataBuffer.TYPE_SHORT, reader.getRawDataType(0));
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        if (false) assertMultilinesEquals(decodeQuotes(
            SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "└───ImageDescription\n" +
            "    └───Dimensions\n" +
            "        └───Dimension\n" +
            "            ├───minValue=“-1.893”\n" +
            "            ├───maxValue=“31.14”\n" +
            "            └───fillSampleValues=“-9999.0”\n"), metadata.toString());
        reader.dispose();
    }

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
