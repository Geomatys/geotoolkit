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
package org.geotoolkit.image.io;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.image.SampleModels;
import org.geotoolkit.internal.io.TemporaryFile;
import org.apache.sis.util.Classes;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.test.TestData;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link XImageIO}. Also ensure that every plugins are correctly registered.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class XImageIOTest extends ImageTestBase {
    /**
     * Creates a new test suite.
     */
    public XImageIOTest() {
        super(XImageIO.class);
    }

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
        image = reader.read(0);
        XImageIO.close(reader);
        reader.dispose();

        final Path tmp = TemporaryFile.createTempFile("TEST", ".png", null);
        try {
            final ImageWriter writer = XImageIO.getWriterBySuffix(tmp, image);
            writer.write(image);
            XImageIO.close(writer);
            writer.dispose();
            assertTrue("The created file should not be empty.", Files.size(tmp) > 0);
        } finally {
            assertTrue(TemporaryFile.delete(tmp));
        }
        showCurrentImage("testGetBySuffix()");
    }

    /**
     * Tests the {@link XImageIO#getReaderBySuffix(String, Object, Boolean, Boolean)} method
     * with a suffix having mismatched cases. We use the TIFF format, since its suffix was
     * declared only in lower-cases as of JDK7 and Geot 3.20.
     *
     * @throws IOException If an I/O error occurred while fetching the reader.
     *
     * @since 3.20
     */
    @Test
    public void testGetBySuffixIgnoreCase() throws IOException {
        final ImageReader reader = XImageIO.getReaderBySuffix("PnG", null, null, null);
        reader.dispose();
    }

    /**
     * Tests that every plugins are correctly registered. In particular,
     * we need to ensure that the MIME type is declared for each plugin.
     *
     * <p>Issue: {@link ImageReaderSpi#getMIMETypes()} javadoc said that the MIME type array can be null.
     * However if we let it be null, then we get a {@link NullPointerException} when invoking
     * {@link javax.imageio.ImageIO#getReaderMIMETypes()}. There is a bug in Image I/O code,
     * which appear to not check if the MIME type array is null. Stack trace is:</p>
     *
     * {@preformat text
     *   java.lang.NullPointerException
     *      at java.util.Collections.addAll(Collections.java:5400)
     *      at javax.imageio.ImageIO.getReaderWriterInfo(ImageIO.java:468)
     *      at javax.imageio.ImageIO.getReaderMIMETypes(ImageIO.java:496)
     * }
     *
     * @since 3.10
     */
    @Test
    public void testPluginsRegistration() {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Iterator<ImageReaderSpi> it = registry.getServiceProviders(ImageReaderSpi.class, false);
        while (it.hasNext()) {
            final ImageReaderSpi spi = it.next();
            final String name = Classes.getShortClassName(spi);
            assertNotNull(name, spi.getFormatNames());
            assertNotNull(name, spi.getInputTypes());
            assertNotNull(name, spi.getMIMETypes()); // See method javadoc.
        }
    }
}
