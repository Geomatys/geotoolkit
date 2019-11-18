/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014-2019, Geomatys
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

package org.geotoolkit.coverage.vi3g;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.test.DependsOnMethod;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.coverage.grid.SequenceType;

/**
 * Unit tests for VI3G reader. We'll make an simple image which each pixel value is its position in a 1D buffer.
 *
 * Ex : if we are on pixel (x, y), its value is : y * image.width() + x;
 *
 * @author Alexis Manin (Geomatys)
 */
public class VI3GReaderTest extends org.geotoolkit.test.TestBase {

    private static final short MAX_VALUE = 10000;
    private static final int SIZE = VI3GStore.WIDTH * VI3GStore.HEIGHT;

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

    /**
     * Test the capacity of the reader to decode full VI3G image in full resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @Test
    public void readFullyTest() throws DataStoreException {

        final VI3GStore store = new VI3GStore(new StorageConnector(TEMP_IMG));
        final RenderedImage read = store.read(null).render(null);

        final PixelIterator pxIt = PixelIterator.create(read);
        int expected = 0;
        while (pxIt.next()) {
            Assert.assertEquals("A pixel value is invalid !", expected++ % 10000, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode a rectangle of source image, at full resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @Ignore
    @DependsOnMethod("readFullyTest")
    @Test
    public void readRegion() throws DataStoreException {
        final VI3GStore store = new VI3GStore(new StorageConnector(TEMP_IMG));
        final SpatialImageReadParam readParam = null; //TODO
        readParam.setSourceRegion(SOURCE_REGION);

        final RenderedImage read = store.read(null).render(null);
        Assert.assertEquals("Read image width is invalid !", SOURCE_REGION.width, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", SOURCE_REGION.height, read.getHeight());
        final PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);

        final int widthPad = VI3GStore.WIDTH - SOURCE_REGION.width - SOURCE_REGION.x;
        int expected = SOURCE_REGION.y * VI3GStore.WIDTH - widthPad;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                expected += SOURCE_REGION.x + widthPad;
                previousY = pxIt.getPosition().y;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected++ % MAX_VALUE, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @Ignore
    @DependsOnMethod("readFullyTest")
    @Test
    public void readSubsampled() throws DataStoreException {
        final VI3GStore store = new VI3GStore(new StorageConnector(TEMP_IMG));
        final SpatialImageReadParam readParam = null;

        // Subsampling without offset
        final int xSubsampling = 5;
        final int ySubsampling = 3;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
        RenderedImage read = store.read(null).render(null);
        Assert.assertEquals("Read image width is invalid !", VI3GStore.WIDTH / 5, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", VI3GStore.HEIGHT / 3, read.getHeight());

        PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        int expected = -1;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getPosition().y;
                expected = (previousY * ySubsampling) * VI3GStore.WIDTH;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected % MAX_VALUE, pxIt.getSample(0));
        }

        // Subsampling with an offset
        final int xOffset = 2;
        final int yOffset = 1;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);
        read = (BufferedImage) store.read(null).render(null);
        Assert.assertEquals("Read image width is invalid !", Math.round((VI3GStore.WIDTH - xOffset) / 5.0), read.getWidth());
        Assert.assertEquals("Read image height is invalid !", Math.round((VI3GStore.HEIGHT - yOffset) / 3.0), read.getHeight());
        pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        expected = -1;
        previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getPosition().y;
                expected = (yOffset + (previousY * ySubsampling)) * VI3GStore.WIDTH + xOffset;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected % MAX_VALUE, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @Ignore
    @DependsOnMethod({"readRegion", "readSubsampled"})
    @Test
    public void readSubSampledRegion() throws DataStoreException {
        final VI3GStore store = new VI3GStore(new StorageConnector(TEMP_IMG));
        final SpatialImageReadParam readParam = null;

        readParam.setSourceRegion(SOURCE_REGION);
        // Subsampling with an offset
        final int xSubsampling = 2;
        final int ySubsampling = 2;
        final int xOffset = 1;
        final int yOffset = 1;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);

        final RenderedImage read = store.read(null).render(null);
        Assert.assertEquals("Read image width is invalid !", 3, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", 2, read.getHeight());

        final int firstPxValue = (SOURCE_REGION.y + yOffset) * VI3GStore.WIDTH + SOURCE_REGION.x + xOffset;
        final int[] expectedValues = new int[]{
                firstPxValue,
                firstPxValue + xSubsampling,
                firstPxValue + xSubsampling*2,
                firstPxValue + (ySubsampling * VI3GStore.WIDTH),
                firstPxValue + (ySubsampling * VI3GStore.WIDTH) + xSubsampling,
                firstPxValue + (ySubsampling * VI3GStore.WIDTH) + xSubsampling*2
        };

        final PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        int expectedIndex = 0;
        while (pxIt.next()) {
            Assert.assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expectedValues[expectedIndex++] % 10000, pxIt.getSample(0));
        }

    }

}
