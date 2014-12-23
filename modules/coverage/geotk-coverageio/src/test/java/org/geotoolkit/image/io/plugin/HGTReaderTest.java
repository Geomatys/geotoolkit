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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Unit tests for HGT reader. We'll make an simple image which each pixel value is its position in a 1D buffer.
 *
 * Ex : if we are on pixel (x, y), its value is : y * image.width() + x;
 *
 * @author Alexis Manin (Geomatys)
 */
public class HGTReaderTest {

    private static Path TEMP_DIR;
    private static Path TEMP_IMG;

    private static final int IMG_WIDTH = 11;
    private static Rectangle SOURCE_REGION = new Rectangle(5, 3, 6, 4);

    private static final String[] TEST_DEP = new String[]{"readSubsampled", "readRegion"};

    @BeforeClass
    public static void init() throws IOException {
        TEMP_DIR = Files.createTempDirectory("hgtTests");

        final int size = IMG_WIDTH * IMG_WIDTH;
        final ByteBuffer buffer = ByteBuffer.allocate(size*(Short.SIZE/Byte.SIZE));
        for (int i = 0 ; i < size; i++) {
            buffer.putShort((short)i);
        }

        TEMP_IMG = Files.createFile(TEMP_DIR.resolve("N00E000.hgt"));
        Files.write(TEMP_IMG, buffer.array());
    }

    @AfterClass
    public static void destroy() throws IOException {
        Files.walkFileTree(TEMP_DIR, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    /**
     * Test the capacity of the reader to decode full HGT image in full resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @Test
    public void readFullyTest() throws IOException {

        final HGTReader reader = new HGTReader(new HGTReader.Spi());
        reader.setInput(TEMP_IMG);
        final BufferedImage read = reader.read(0);

        final PixelIterator pxIt = PixelIteratorFactory.createDefaultIterator(read);
        int expected = 0;
        while (pxIt.next()) {
            Assert.assertEquals("A pixel value is invalid !", expected++, pxIt.getSample());
        }
    }

    /**
     * Test the capacity of the reader to decode a rectangle of source image, at full resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readRegion() throws IOException {
        final HGTReader reader = new HGTReader(new HGTReader.Spi());
        final SpatialImageReadParam readParam = reader.getDefaultReadParam();
        readParam.setSourceRegion(SOURCE_REGION);

        reader.setInput(TEMP_IMG);
        final BufferedImage read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", SOURCE_REGION.width, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", SOURCE_REGION.height, read.getHeight());
        final PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);

        int expected = SOURCE_REGION.y * IMG_WIDTH;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                expected += SOURCE_REGION.x;
                previousY = pxIt.getY();
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected++, pxIt.getSample());
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readSubsampled() throws IOException {
        final HGTReader reader = new HGTReader(new HGTReader.Spi());
        final SpatialImageReadParam readParam = reader.getDefaultReadParam();
        reader.setInput(TEMP_IMG);

        // Subsampling without offset
        final int xSubsampling = 5;
        final int ySubsampling = 3;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
        BufferedImage read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", 3, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", 4, read.getHeight());

        PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        int expected = -1;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getY();
                expected = (previousY * ySubsampling) * IMG_WIDTH;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected, pxIt.getSample());
        }

        // Subsampling with an offset
        final int xOffset = 2;
        final int yOffset = 1;
        readParam.setSourceSubsampling(xSubsampling, ySubsampling, xOffset, yOffset);
        read = reader.read(0, readParam);
        Assert.assertEquals("Read image width is invalid !", 2, read.getWidth());
        Assert.assertEquals("Read image height is invalid !", 4, read.getHeight());
        pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        expected = -1;
        previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getY()) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getY();
                expected = (yOffset + (previousY * ySubsampling)) * IMG_WIDTH + xOffset;
            } else {
                expected += xSubsampling;
            }
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expected, pxIt.getSample());
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws java.io.IOException If temporary image file has been corrupted.
     */
    @DependsOnMethod({"readRegion", "readSubsampled"})
    @Test
    public void readSubSampledRegion() throws IOException {
        final HGTReader reader = new HGTReader(new HGTReader.Spi());
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

        final int firstPxValue = (SOURCE_REGION.y + yOffset) * IMG_WIDTH + SOURCE_REGION.x + xOffset;
        final int[] expectedValues = new int[]{
                firstPxValue,
                firstPxValue + xSubsampling,
                firstPxValue + xSubsampling*2,
                firstPxValue + (ySubsampling * IMG_WIDTH),
                firstPxValue + (ySubsampling * IMG_WIDTH) + xSubsampling,
                firstPxValue + (ySubsampling * IMG_WIDTH) + xSubsampling*2

        };

        final PixelIterator pxIt = PixelIteratorFactory.createRowMajorIterator(read);
        int expectedIndex = 0;
        while (pxIt.next()) {
            Assert.assertEquals("Pixel value at ("+pxIt.getX()+", "+pxIt.getY()+") is invalid !", expectedValues[expectedIndex++], pxIt.getSample());
        }

    }
}
