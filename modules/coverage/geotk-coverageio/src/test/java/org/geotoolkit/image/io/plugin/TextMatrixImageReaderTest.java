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

import java.util.Locale;
import java.util.Iterator;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.TextImageReaderTestBase;
import org.geotoolkit.image.io.metadata.SpatialMetadata;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link TextMatrixImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.06
 */
public final strictfp class TextMatrixImageReaderTest extends TextImageReaderTestBase {
    /**
     * The provider for the format to be tested.
     */
    public static final strictfp class Spi extends TextMatrixImageReader.Spi {
        public Spi() {
            padValue = -9999;
            locale   = Locale.CANADA;
            charset  = Charset.forName("UTF-8");
        }
    }

    /**
     * Creates a reader and sets its input if needed.
     */
    @Override
    protected void prepareImageReader(final boolean setInput) throws IOException {
        if (reader == null) {
            reader = new TextMatrixImageReader(new Spi());
        }
        if (setInput) {
            reader.setInput(TestData.file(this, "matrix.txt"));
        }
    }

    @Override
    @Ignore("This test randomly fail. Need more investigation.")
    public void testReadAsRenderedImage() {
    }

    /**
     * Tests the metadata of the {@link "matrix.txt"} file.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        prepareImageReader(true);
        assertEquals(20, reader.getWidth (0));
        assertEquals(42, reader.getHeight(0));
        assertNull(reader.getStreamMetadata());
        final SpatialMetadata metadata = (SpatialMetadata) reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "└───ImageDescription\n" +
            "    └───Dimensions\n" +
            "        └───Dimension\n" +
            "            ├───minValue=“-1.893”\n" +
            "            ├───maxValue=“31.14”\n" +
            "            └───fillSampleValues=“-9999.0”\n"), metadata.toString());
    }

    /**
     * Tests the registration of the image reader in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("matrix");
        assertTrue("Expected a reader.", it.hasNext());
        assertTrue(it.next() instanceof TextMatrixImageReader);
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
            if (it.next() instanceof TextMatrixImageReader) {
                return;
            }
        }
        fail("Reader not found.");
    }

    @Test
    @Override
    @Ignore("This test fails randomly.")
    public void testReadAsBufferedImage() {
    }
}
