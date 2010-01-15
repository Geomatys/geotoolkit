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
package org.geotoolkit.image.io;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageReader;
import java.awt.image.RenderedImage;
import javax.imageio.ImageWriter;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.internal.io.TemporaryFile;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link XImageIO}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class XImageIOTest {
    /**
     * Tests the {@link XImageIO#getReaderBySuffix(Object, Boolean, Boolean)} method,
     * followed by {@link XImageIO#getWriterBySuffix(Object, RenderedImage)}.
     *
     * @throws IOException If an I/O error occured while writing or reading the image.
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
}
