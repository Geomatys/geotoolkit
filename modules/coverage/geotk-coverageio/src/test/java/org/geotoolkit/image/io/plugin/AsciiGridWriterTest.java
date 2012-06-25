/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;

import org.geotoolkit.image.io.TextImageWriterTestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link AsciiGridWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.07
 */
public final strictfp class AsciiGridWriterTest extends TextImageWriterTestBase {
    /**
     * Creates a new test suite.
     */
    public AsciiGridWriterTest() {
    }

    /**
     * Creates a writer.
     */
    @Override
    protected void prepareImageWriter(final boolean optionallySetOutput) {
        if (writer == null) {
            writer = new AsciiGridWriter(new AsciiGridWriter.Spi());
        }
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneByteBand() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testThreeByteBands() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneShortBand() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneUnsignedShortBand() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneIntBand() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneFloatBand() throws IOException {
    }

    /**
     * @todo Can not run because spatial metadata are missing.
     */
    @Override
    @Ignore("Can not run because spatial metadata are missing")
    public void testOneDoubleBand() throws IOException {
    }

    /**
     * Tests the write operation.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testWrite() throws IOException {
        prepareImageWriter(false);
        final IIOImage image = createImage(true);
        final AsciiGridWriter writer = (AsciiGridWriter) this.writer;
        final StringWriter out = new StringWriter();
        try {
            writer.setOutput(out);
            writer.write(image);
            assertMultilinesEquals(
                "NCOLS         8\n" +
                "NROWS         10\n" +
                "XLLCORNER     -500.0\n" +
                "YLLCORNER     400.0\n" +
                "CELLSIZE      100.0\n" +
                "NODATA_VALUE  -9998\n" +
                "0.0 0.01 0.02 0.03 0.04 0.05 0.06 0.07\n" +
                "0.1 0.11 0.12 0.13 0.14 0.15 0.16 0.17\n" +
                "0.2 0.21 0.22 0.23 0.24 0.25 0.26 0.27\n" +
                "0.3 0.31 -9998 0.33 0.34 0.35 0.36 0.37\n" +
                "0.4 0.41 0.42 0.43 0.44 0.45 0.46 0.47\n" +
                "88.5 88.51 88.52 88.53 88.54 88.55 88.56 88.57\n" +
                "88.6 88.61 88.62 88.63 88.64 88.65 88.66 88.67\n" +
                "88.7 88.71 88.72 88.73 88.74 88.75 -9998 88.77\n" +
                "88.8 88.81 88.82 88.83 88.84 88.85 88.86 88.87\n" +
                "88.9 88.91 88.92 88.93 88.94 88.95 88.96 88.97\n", out.toString());
            /*
             * Writes the same image, but only a sub-area of it.
             */
            final ImageWriteParam param = writer.getDefaultWriteParam();
            param.setSourceRegion(new Rectangle(2, 1, 4, 3));
            out.getBuffer().setLength(0);
            writer.write(null, image, param);
            assertMultilinesEquals(
                "NCOLS         4\n" +
                "NROWS         3\n" +
                "XLLCORNER     -300.0\n" +
                "YLLCORNER     300.0\n" +
                "CELLSIZE      100.0\n" +
                "NODATA_VALUE  -9998\n" +
                "0.12 0.13 0.14 0.15\n" +
                "0.22 0.23 0.24 0.25\n" +
                "-9998 0.33 0.34 0.35\n", out.toString());
            /*
             * Adds a subsampling of (2,3).
             */
            param.setSourceSubsampling(2, 2, 0, 1);
            out.getBuffer().setLength(0);
            writer.write(null, image, param);
            assertMultilinesEquals(
                "NCOLS         2\n" +
                "NROWS         1\n" +
                "XLLCORNER     -100.0\n" +
                "YLLCORNER     0.0\n" +
                "CELLSIZE      200.0\n" +
                "NODATA_VALUE  -9998\n" +
                "0.22 0.24\n", out.toString());
        } finally {
            out.close();
        }
        writer.dispose();
    }

    /**
     * Tests the registration of the image writer in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("ascii-grid");
        assertTrue("Expected a writer.", it.hasNext());
        assertTrue(it.next() instanceof AsciiGridWriter);
        assertFalse("Expected no more writer.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByMIMEType("text/plain");
        while (it.hasNext()) {
            if (it.next() instanceof AsciiGridWriter) {
                return;
            }
        }
        fail("Writer not found.");
    }
}
