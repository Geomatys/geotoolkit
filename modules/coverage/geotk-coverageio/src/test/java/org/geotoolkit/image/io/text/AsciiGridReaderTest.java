/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.text;

import java.util.Iterator;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link AsciiGridReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class AsciiGridReaderTest extends TextImageReaderTestBase {
    /**
     * Creates a reader.
     */
    @Override
    protected AsciiGridReader createImageReader() throws IOException {
        AsciiGridReader.Spi spi = new AsciiGridReader.Spi();
        final AsciiGridReader reader = new AsciiGridReader(spi);
        reader.setInput(TestData.file(this, "grid.asc"));
//      reader.setInput("/Users/desruisseaux/Documents/Données/BRGM/geol_1m_asc.asc");
        return reader;
    }

    /**
     * Tests the metadata of the {@link "matrix.txt"} file.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        final TextImageReader reader = createImageReader();
        assertEquals(20, reader.getWidth (0));
        assertEquals(42, reader.getHeight(0));
        assertNull(reader.getStreamMetadata());
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "├───RectifiedGridDomain\n" +
            "│   ├───OffsetVectors\n" +
            "│   │   ├───origin=“-9500.0 20500.0”\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“1000.0 0.0”\n" +
            "│   │   └───OffsetVector\n" +
            "│   │       └───values=“0.0 -1000.0”\n" +
            "│   └───Limits\n" +
            "│       ├───low=“0 0”\n" +
            "│       └───high=“19 41”\n" +
            "├───SpatialRepresentation\n" +
            "│   ├───numberOfDimensions=“2”\n" +
            "│   ├───centerPoint=“0.0 0.0”\n" +
            "│   └───pointInPixel=“center”\n" +
            "└───ImageDescription\n" +
            "    └───Dimensions\n" +
            "        └───Dimension\n" +
            "            ├───minValue=“-1.893”\n" +
            "            ├───maxValue=“31.14”\n" +
            "            └───fillSampleValues=“-9999.0”\n"), metadata.toString());
    }

    /**
     * Disables this test, since attempts to read the image as byte values when
     * the data are actually float throw an {@link IIOException}.
     */
    @Test
    @Ignore
    @Override
    public void testByteType() throws IOException {
    }

    /**
     * Tests the registration of the image reader in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("ascii-grid");
        assertTrue("Expected a reader.", it.hasNext());
        assertTrue(it.next() instanceof AsciiGridReader);
        assertFalse("Expected no more reader.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType("text/plain");
        while (it.hasNext()) {
            if (it.next() instanceof AsciiGridReader) {
                return;
            }
        }
        fail("Reader not found.");
    }
}
