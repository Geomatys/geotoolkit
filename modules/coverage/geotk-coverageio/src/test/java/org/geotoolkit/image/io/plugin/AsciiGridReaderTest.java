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

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.util.Iterator;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.TextImageReaderTestBase;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.internal.image.io.DimensionAccessor;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link AsciiGridReader}.
 * <p>
 * This class provides also a {@link #verify} static method for manual testings.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.07
 */
public strictfp class AsciiGridReaderTest extends TextImageReaderTestBase {
    /**
     * Creates a reader and sets its input if needed.
     */
    @Override
    protected void prepareImageReader(final boolean setInput) throws IOException {
        if (reader == null) {
            AsciiGridReader.Spi spi = new AsciiGridReader.Spi();
            reader = new AsciiGridReader(spi);
        }
        if (setInput) {
            reader.setInput(TestData.file(this, "grid.asc"));
        }
    }

    /**
     * Tests the metadata of the {@link "grid.asc"} file.
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
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-9500.0 20500.0”\n" +
            "│   ├───OffsetVectors\n" +
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
        /*
         * Forces a scan of pixel values and test again.
         */
        final DimensionAccessor helper = new DimensionAccessor(metadata);
        assertFalse("Pixels scan should not be needed.", helper.isScanSuggested(reader, 0));
        // Scan anyway, even if the above returned 'false'.
        metadata.setReadOnly(false);
        helper.scanValidSampleValue(reader, 0);
        assertFalse("Pixels scan should not be needed.", helper.isScanSuggested(reader, 0));
        assertMultilinesEquals(decodeQuotes(
            GEOTK_FORMAT_NAME + '\n' +
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-9500.0 20500.0”\n" +
            "│   ├───OffsetVectors\n" +
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
            "            ├───fillSampleValues=“-9999.0”\n" +
            "            └───validSampleValues=“[-1.893 … 31.139999]”\n"), metadata.toString());
    }

    /**
     * Do not run this test for {@code AsciiGridReaderTest}. This is because the reader
     * implementation tries to read numbers as integers (which is the wanted behavior),
     * which result in an {@link IIOException} caused by a {@link NumberFormatException}:
     * "Can not parse -1.123".
     */
    @Test
    @Override
    public void testByteType() throws IOException {
        if (getClass() != AsciiGridReaderTest.class) {
            super.testByteType();
        }
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

    /**
     * Compares the content of the given raster with the value read from the given file.
     * Any mismatch found is printed to the standard output stream.
     *
     * @param  input            The file to read.
     * @param  width            The image width.
     * @param  raster           The raster from which to compare the values.
     * @param  region           The region of the source file to compare.
     * @param  xSubsampling     Subsampling along the <var>x</var> axis (1 if none).
     * @param  ySubsampling     Subsampling along the <var>y</var> axis (1 if none).
     * @param  headerLineCount  The number of header lines to skip.
     * @throws IOException If an error occurred while reading the file.
     */
    public static void verify(final File input, final int width, final Raster raster, final Rectangle region,
            final int xSubsampling, final int ySubsampling, final int headerLineCount) throws IOException
    {
        assertEquals(0, raster.getMinX());
        assertEquals(0, raster.getMinY());
        assertEquals((region.width  + xSubsampling-1) / xSubsampling, raster.getWidth());
        assertEquals((region.height + ySubsampling-1) / ySubsampling, raster.getHeight());
        String regionString = region.toString();
        regionString = regionString.substring(regionString.indexOf('['));
        final PrintStream out = System.out;
        out.println("Comparing the values in region " + regionString);

        int x=0, y=0;
        final long timestamp = System.currentTimeMillis();
        final BufferedReader reader = new BufferedReader(new FileReader(input));
        try {
            for (int i=1; i<=headerLineCount; i++) {
                out.println("Header " + i + ": " + reader.readLine());
            }
            final StringBuilder buffer = new StringBuilder(20);
            int c, errorCount=0;
            while ((c = reader.read()) >= 0) {
                if (c > ' ') {
                    buffer.append((char) c);
                } else if (buffer.length() != 0) {
                    final float value  = Float.parseFloat(buffer.toString());
                    if (region.contains(x, y)) {
                        int tx = x - region.x;
                        int ty = y - region.y;
                        if ((tx % xSubsampling) == 0 && (ty % ySubsampling) == 0) {
                            tx /= xSubsampling;
                            ty /= ySubsampling;
                            final float stored = raster.getSampleFloat(tx, ty, 0);
                            // Test only 'stored' for NaN because the values that we read from the
                            // file may be pad values like -9999, and we don't handle them in this
                            // simple test method.
                            if (value != stored && !Float.isNaN(stored)) {
                                out.println("Expected " + value + " but found " + stored +
                                        " at coordinate (" + x + ',' + y + ") in the file," +
                                        " which is (" + tx + ',' + ty + " in the raster.");
                                if (++errorCount >= 100) {
                                    out.println("Too many errors.");
                                    break;
                                }
                            }
                        }
                    }
                    if (++x == width) {
                        x = 0;
                        y++;
                    }
                    buffer.setLength(0);
                }
            }
        } finally {
            reader.close();
        }
        out.println("Coordinate of the next pixel to read, if it existed: (" + x + ',' + y + ')');
        out.println("Ellapsed time: " + (System.currentTimeMillis() - timestamp) / 1000f + " seconds.");
    }
}
