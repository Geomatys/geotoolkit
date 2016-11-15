/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package org.geotoolkit.image.io.plugin;

import org.apache.sis.test.DependsOnMethod;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 * Unit tests for VI3G reader. We'll make an simple image which each pixel value is its position in a 1D buffer.
 *
 * Ex : if we are on pixel (x, y), its value is : y * image.width() + x;
 *
 * @author Alexis Manin (Geomatys)
 */
public class VI3GReaderTest extends org.geotoolkit.test.TestBase {

    private static final short MAX_VALUE = 10000;
    private static final int SIZE = VI3GReader.WIDTH * VI3GReader.HEIGHT;

    private static Path TEMP_IMG;

    private static final Rectangle SOURCE_REGION = new Rectangle(5, 3, 6, 4);

    @BeforeClass
    public static void init() throws IOException {
        TEMP_IMG = Files.createTempFile("test", ".vi3g");

        final ByteBuffer bb = ByteBuffer.allocate(8192);
        final ShortBuffer sb = bb.asShortBuffer();
        int idx = 0;
        try (final OutputStream stream = Files.newOutputStream(TEMP_IMG);
                final DataOutputStream writer = new DataOutputStream(stream)) {
            while (idx < SIZE) {
                sb.rewind();
                final int limit = Math.min(sb.limit(), SIZE - idx);
                while (limit > sb.position()) {
                    sb.put((short) (idx++ % 10000));
                }

                writer.write(bb.array(), 0, limit * 2);
            }
        }
    }

    @AfterClass
    public static void destroy() throws IOException {
        Files.delete(TEMP_IMG);
    }

    @Test
    public void serviceLoadingTest() {
        ImageIO.scanForPlugins();
        final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("vi3g");
        while (readers.hasNext()) {
            if (readers.next() instanceof VI3GReader)
                return;
        }

        Assert.fail("VI3G reader cannot be found by ImageIO !");
    }

    /**
     * Test the capacity of the reader to decode all VI3G image in full resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @Test
    public void readFullyTest() throws IOException {

        final VI3GReader reader = new VI3GReader(new VI3GReader.Spi());
        reader.setInput(TEMP_IMG);
        final BufferedImage read = reader.read(0);

        final PixelIterator pxIt = PixelIteratorFactory.createDefaultIterator(read);
        int expected = 0;
        while (pxIt.next()) {
            Assert.assertEquals("A pixel value is invalid !", expected++ % 10000, pxIt.getSample());
        }

        Assert.assertEquals("Image has not been fully read !", SIZE, expected);
    }

    /**
     * Test the capacity of the reader to decode a rectangle of source image, at full resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readRegion() throws IOException {
        final VI3GReader reader = new VI3GReader(new VI3GReader.Spi());
        final SpatialImageReadParam readParam = reader.getDefaultReadParam();
        readParam.setSourceRegion(SOURCE_REGION);

        reader.setInput(TEMP_IMG);
        final BufferedImage read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", SOURCE_REGION.width, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", SOURCE_REGION.height, read.getHeight());
        final PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);

        final int widthPad = VI3GReader.WIDTH - SOURCE_REGION.width - SOURCE_REGION.x;
        int expected = SOURCE_REGION.y * VI3GReader.WIDTH - widthPad;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                expected += SOURCE_REGION.x + widthPad;
                previousY = pxIt.getY();
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected++ % MAX_VALUE, pxIt.getSample());
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readSubsampled() throws IOException {
        final VI3GReader reader = new VI3GReader(new VI3GReader.Spi());
        final SpatialImageReadParam readParam = reader.getDefaultReadParam();
        reader.setInput(TEMP_IMG);

        // Subsampling without offset
        final int xSubsampling = 5;
        final int ySubsampling = 3;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
        BufferedImage read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", VI3GReader.WIDTH / 5, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", VI3GReader.HEIGHT / 3, read.getHeight());

        PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        int expected = -1;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getY();
                expected = (previousY * ySubsampling) * VI3GReader.WIDTH;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected % MAX_VALUE, pxIt.getSample());
        }

        // Subsampling with an offset
        final int xOffset = 2;
        final int yOffset = 1;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);
        read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", Math.round((VI3GReader.WIDTH - xOffset) / 5.0), read.getWidth());
        Assert.assertEquals("Read image height is invalid !", Math.round((VI3GReader.HEIGHT - yOffset) / 3.0), read.getHeight());
        pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        expected = -1;
        previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getY();
                expected = (yOffset + (previousY * ySubsampling)) * VI3GReader.WIDTH + xOffset;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected % MAX_VALUE, pxIt.getSample());
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod({"readRegion", "readSubsampled"})
    @Test
    public void readSubSampledRegion() throws IOException {
        final VI3GReader reader = new VI3GReader(new VI3GReader.Spi());
        final SpatialImageReadParam readParam = reader.getDefaultReadParam();
        reader.setInput(TEMP_IMG);

        readParam.setSourceRegion(SOURCE_REGION);
        // Subsampling with an offset
        final int xSubsampling = 2;
        final int ySubsampling = 2;
        final int xOffset = 1;
        final int yOffset = 1;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);

        final BufferedImage read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", 3, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", 2, read.getHeight());

        final int firstPxValue = (SOURCE_REGION.y + yOffset) * VI3GReader.WIDTH + SOURCE_REGION.x + xOffset;
        final int[] expectedValues = new int[]{
                firstPxValue,
                firstPxValue + xSubsampling,
                firstPxValue + xSubsampling*2,
                firstPxValue + (ySubsampling * VI3GReader.WIDTH),
                firstPxValue + (ySubsampling * VI3GReader.WIDTH) + xSubsampling,
                firstPxValue + (ySubsampling * VI3GReader.WIDTH) + xSubsampling*2
        };

        final PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        int expectedIndex = 0;
        while (pxIt.next()) {
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expectedValues[expectedIndex++] % 10000, pxIt.getSample());
        }

    }
}
