/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.image.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageReader;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.internal.io.TemporaryFile;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.util.converter.Classes;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link XImageIO}. Also ensure that every plugins are correctly registered.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.07
 */
public final class XImageIOTest {
    /**
     * Tests the {@link XImageIO#getReaderBySuffix(Object, Boolean, Boolean)} method,
     * followed by {@link XImageIO#getWriterBySuffix(Object, RenderedImage)}.
     *
     * @throws IOException If an I/O error occurred while writing or reading the image.
     */
    @Test
    public void testGetBySuffix() throws IOException {
        /*
         * Use a very small file (about 4 kb), since the purpose
         * of this method is not to test the PNG reader or writer.
         */
        final File file = TestData.file(SampleModels.class, "Contour.png");
        final ImageReader reader = XImageIO.getReaderBySuffix(file, true, true);
        assertTrue(reader.isSeekForwardOnly());
        assertTrue(reader.isIgnoringMetadata());
        final RenderedImage image = reader.read(0);
        XImageIO.close(reader);
        reader.dispose();

        final File tmp = TemporaryFile.createTempFile("TEST", ".png", null);
        try {
            final ImageWriter writer = XImageIO.getWriterBySuffix(tmp, image);
            writer.write(image);
            XImageIO.close(writer);
            writer.dispose();
            assertTrue("The created file should not be empty.", tmp.length() > 0);
        } finally {
            assertTrue(TemporaryFile.delete(tmp));
        }
    }

    /**
     * Tests that every plugins are correctly registered. In particular,
     * we need to ensure that the MIME type is declared for each plugin.
     *
     * @since 3.10
     */
    @Test
    public void testPluginsRegistration() {
        Registry.setDefaultCodecPreferences();
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        WorldFileImageReader.Spi.registerDefaults(registry);
        WorldFileImageWriter.Spi.registerDefaults(registry);
        try {
            final Iterator<ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, false);
            while (it.hasNext()) {
                final ImageReaderSpi spi = it.next();
                final String name = Classes.getShortClassName(spi);
                assertNotNull(name, spi.getFormatNames());
                assertNotNull(name, spi.getInputTypes());
                assertNotNull(name, spi.getMIMETypes());
            }
            // Following line was used to throw a NullPointerException
            // if a plugin declare a null array of MIME types.
            final String[] types = ImageIO.getReaderMIMETypes();
            assertTrue(types.length > 16); // Arbitrary threshold.
        } finally {
            WorldFileImageReader.Spi.unregisterDefaults(registry);
            WorldFileImageWriter.Spi.unregisterDefaults(registry);
        }
    }

    /**
     * Tests {@link XImageIO#getFormatNamesByMimeType}.
     *
     * @since 3.14
     */
    @Test
    public void testGetFormatNamesByMimeType() {
        assertArrayEquals("Geotk has no RAW writer at this time.", new String[0],
                XImageIO.getFormatNamesByMimeType("image/x-raw", false, true));
        assertArrayEquals("The RAW reader should has been found.", new String[] {"raw"},
                XImageIO.getFormatNamesByMimeType("image/x-raw", true, false));
        assertArrayEquals("Geotk has no RAW writer at this time.", new String[0],
                XImageIO.getFormatNamesByMimeType("image/x-raw", true, true));
        assertArrayEquals("The RAW reader should has been found.", new String[] {"raw"},
                XImageIO.getFormatNamesByMimeType("image/x-raw", false, false));
    }
}
