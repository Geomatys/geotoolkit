/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;

import org.geotoolkit.image.io.PaletteFactory;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.assertMultilinesEquals;


/**
 * Tests {@link TextMatrixImageWriter}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.4
 */
public final class TextMatrixImageWriterTest {
    /**
     * The image to test.
     */
    private static IIOImage image;

    /**
     * The writer to test.
     */
    private static TextMatrixImageWriter writer;

    /**
     * Creates the image to test.
     *
     * @throws IOException Should never happen.
     */
    @Before
    public void createImage() throws IOException {
        final int width  = 8;
        final int height = 10;
        final ColorModel cm = PaletteFactory.getDefault().getContinuousPalette(
                "grayscale", 0f, 1f, DataBuffer.TYPE_FLOAT, 1, 0).getColorModel();
        final WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                double value = (10*y + x) / 100.0;
                if (y >= 5) {
                    value += 88;
                }
                raster.setSample(x, y, 0, value);
            }
        }
        image = new IIOImage(new BufferedImage(cm, raster, false, null), null, null);
    }

    /**
     * Creates the writer to test.
     */
    @Before
    public void createWriter() {
        TextMatrixImageWriter.Spi spi = new TextMatrixImageWriter.Spi();
        spi.locale = Locale.CANADA;
        writer = new TextMatrixImageWriter(spi);
    }

    /**
     * Tests the number format.
     */
    @Test
    public void testCreateNumberFormat() {
        assertEquals(Locale.CANADA, writer.getDataLocale(null));

        final NumberFormat format = writer.createNumberFormat(image, null);
        assertEquals(2, format.getMinimumFractionDigits());
        assertEquals(2, format.getMaximumFractionDigits());
        assertEquals(1, format.getMinimumIntegerDigits());
        assertEquals( "0.12", format.format( 0.1216));
        assertEquals("-0.30", format.format(-0.2978));

        final FieldPosition pos = writer.getExpectedFractionPosition(format);
        assertEquals("Field type", NumberFormat.FRACTION_FIELD, pos.getField());
        assertEquals("Fraction width", 2, pos.getEndIndex() - pos.getBeginIndex());
        assertEquals("Total width (including sign)", 6, pos.getEndIndex());
    }

    /**
     * Tests the write operation.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testWrite() throws IOException {
        final StringWriter out = new StringWriter();
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
        out.close();
    }
}
