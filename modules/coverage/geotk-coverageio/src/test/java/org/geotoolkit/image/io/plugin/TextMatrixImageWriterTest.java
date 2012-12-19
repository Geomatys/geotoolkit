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

import java.util.Locale;
import java.util.Iterator;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;

import org.geotoolkit.image.io.TextImageWriterTestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link TextMatrixImageWriter}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 *
 * @todo The constructor uses a quite large tolerance threshold for floating point values because
 *       of the precision lost when formating the numbers. We should probably increase the writer
 *       accuracy, then revisit this threshold.
 *
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-232">GEOTK-232</a>
 */
public final strictfp class TextMatrixImageWriterTest extends TextImageWriterTestBase {
    /**
     * Creates a new test suite.
     */
    public TextMatrixImageWriterTest() {
        sampleToleranceThreshold = 1E-2;
    }

    /**
     * Creates a writer using the {@link Locale#CANADA}.
     */
    @Override
    protected void prepareImageWriter(final boolean optionallySetOutput) {
        if (writer == null) {
            writer = new TextMatrixImageWriter(new TextMatrixImageWriter.Spi() {{
                locale  = Locale.CANADA;
                charset = Charset.forName("UTF-8");
            }});
        }
        if (optionallySetOutput && reader == null) {
            // Reader is used only after the output has been set.
            // We need a reader using the same locale and encoding.
            reader = new TextMatrixImageReader(new TextMatrixImageReader.Spi() {{
                locale  = Locale.CANADA;
                charset = Charset.forName("UTF-8");
            }});
        }
    }

    /**
     * Ignored for now.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-232">GEOTK-232</a>
     */
    @Override
    @Ignore("The TextMatrixImageWriter doesn't allocate enough space for the 'int' type.")
    public void testOneIntBand() {
    }

    @Override
    @Ignore("Fail randomly - reason not yet identifier.")
    public void testOneFloatBand() {
    }

    /**
     * Tests the number format.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testCreateNumberFormat() throws IOException {
        prepareImageWriter(false);
        testCreateNumberFormat((TextMatrixImageWriter) writer);
    }

    /**
     * Tests the write operation.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testWrite() throws IOException {
        prepareImageWriter(false);
        final IIOImage image = createImage(false);
        final TextMatrixImageWriter writer = (TextMatrixImageWriter) this.writer;
        try (StringWriter out = new StringWriter()) {
            writer.setOutput(out);
            writer.write(image);
            assertMultilinesEquals(
                "   0.00   0.01   0.02   0.03   0.04   0.05   0.06   0.07\n" +
                "   0.10   0.11   0.12   0.13   0.14   0.15   0.16   0.17\n" +
                "   0.20   0.21   0.22   0.23   0.24   0.25   0.26   0.27\n" +
                "   0.30   0.31   0.32   0.33   0.34   0.35   0.36   0.37\n" +
                "   0.40   0.41   0.42   0.43   0.44   0.45   0.46   0.47\n" +
                "  88.50  88.51  88.52  88.53  88.54  88.55  88.56  88.57\n" +
                "  88.60  88.61  88.62  88.63  88.64  88.65  88.66  88.67\n" +
                "  88.70  88.71  88.72  88.73  88.74  88.75  88.76  88.77\n" +
                "  88.80  88.81  88.82  88.83  88.84  88.85  88.86  88.87\n" +
                "  88.90  88.91  88.92  88.93  88.94  88.95  88.96  88.97\n", out.toString());
            /*
             * Writes the same image, but only a sub-area of it. Note that the columns are more
             * narrow by one character since we have one less digit (all numbers are smaller than 10).
             */
            final ImageWriteParam param = writer.getDefaultWriteParam();
            param.setSourceRegion(new Rectangle(2, 1, 4, 3));
            out.getBuffer().setLength(0);
            writer.write(null, image, param);
            assertMultilinesEquals(
                "   0.12  0.13  0.14  0.15\n" +
                "   0.22  0.23  0.24  0.25\n" +
                "   0.32  0.33  0.34  0.35\n", out.toString());
            /*
             * Adds a subsampling of (2,3).
             */
            param.setSourceSubsampling(2, 3, 0, 1);
            out.getBuffer().setLength(0);
            writer.write(null, image, param);
            assertMultilinesEquals(
                "   0.22  0.24\n", out.toString());
        }
        writer.dispose();
    }

    /**
     * Tests the registration of the image writer in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("matrix");
        assertTrue("Expected a writer.", it.hasNext());
        assertTrue(it.next() instanceof TextMatrixImageWriter);
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
            if (it.next() instanceof TextMatrixImageWriter) {
                return;
            }
        }
        fail("Writer not found.");
    }
}
