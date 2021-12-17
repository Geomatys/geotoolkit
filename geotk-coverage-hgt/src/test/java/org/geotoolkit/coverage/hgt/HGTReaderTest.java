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

package org.geotoolkit.coverage.hgt;

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.test.DependsOnMethod;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

/**
 * Unit tests for HGT reader. We'll make an simple image which each pixel value is its position in a 1D buffer.
 *
 * Ex : if we are on pixel (x, y), its value is : y * image.width() + x;
 *
 * @author Alexis Manin (Geomatys)
 */
public class HGTReaderTest extends org.geotoolkit.test.TestBase {

    private static Path TEMP_DIR;
    private static Path TEMP_IMG;

    private static final int IMG_WIDTH = 11;
    private static final Rectangle SOURCE_REGION = new Rectangle(5, 3, 6, 4);
    private HGTStore store;

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

    @Before
    public void setUp() throws Exception {
        store = new HGTStore(new StorageConnector(TEMP_IMG));
    }

    /**
     * Test the capacity of the reader to decode full HGT image in full resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @Test
    public void readFullyTest() throws DataStoreException {
        final RenderedImage read = store.read(null).render(null);

        final PixelIterator pxIt = PixelIterator.create(read);
        int expected = 0;
        while (pxIt.next()) {
            assertEquals("A pixel value is invalid !", expected++, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode a rectangle of source image, at full resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readRegion() throws DataStoreException {

        final MathTransform gridToCrsCorner = new AffineTransform2D(1d/IMG_WIDTH, 0, 0, -1d/IMG_WIDTH, 0, 0+1);
        final GridGeometry readParam = new GridGeometry(new GridExtent(null, new long[]{SOURCE_REGION.x,SOURCE_REGION.y}, new long[]{SOURCE_REGION.x + SOURCE_REGION.width, SOURCE_REGION.y + SOURCE_REGION.height}, false), PixelInCell.CELL_CORNER, gridToCrsCorner, CommonCRS.defaultGeographic());

        final RenderedImage read = store.read(readParam).render(null);
        assertEquals("Read image width is invalid !", SOURCE_REGION.width, read.getWidth());
        assertEquals("Read image height is invalid !", SOURCE_REGION.height, read.getHeight());
        final PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);

        int expected = SOURCE_REGION.y * IMG_WIDTH;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                expected += SOURCE_REGION.x;
                previousY = pxIt.getPosition().y;
            }
            assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected++, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @DependsOnMethod("readFullyTest")
    @Test
    public void readSubsampled() throws DataStoreException {

        // Subsampling without offset
        final int xSubsampling = 5;
        final int ySubsampling = 3;
        final MathTransform gridToCrsCorner = new AffineTransform2D(1d/IMG_WIDTH, 0, 0, -1d/IMG_WIDTH, 0, 0+1);
        GridGeometry readParam = new GridGeometry(new GridExtent(IMG_WIDTH, IMG_WIDTH), PixelInCell.CELL_CORNER, gridToCrsCorner, CommonCRS.defaultGeographic())
                .derive().subgrid((GridExtent) null, xSubsampling, ySubsampling).build();
        RenderedImage read = store.read(readParam).render(null);
        assertEquals("Read image width is invalid !", 3, read.getWidth());
        assertEquals("Read image height is invalid !", 4, read.getHeight());

        PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        int expected = -1;
        // When we change line, we must add an offset to expected value.
        int previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getPosition().y;
                expected = (previousY * ySubsampling) * IMG_WIDTH;
            } else {
                expected += xSubsampling;
            }
            assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected, pxIt.getSample(0));
        }

        // Subsampling with an offset
        final int xOffset = 2;
        final int yOffset = 1;
        readParam = new GridGeometry(new GridExtent(null, new long[]{xOffset, yOffset}, new long[]{IMG_WIDTH,IMG_WIDTH}, false), PixelInCell.CELL_CORNER, gridToCrsCorner, CommonCRS.defaultGeographic())
                .derive().subgrid((GridExtent) null, xSubsampling, ySubsampling).build();
        read = store.read(readParam).render(null);
        assertEquals("Read image width is invalid !", 2, read.getWidth());
        assertEquals("Read image height is invalid !", 4, read.getHeight());
        pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        expected = -1;
        previousY = -1;
        while (pxIt.next()) {
            if (previousY < pxIt.getPosition().y) {
                // We reset expected value to the one we should get at the beginning of current line.
                previousY = pxIt.getPosition().y;
                expected = (yOffset + (previousY * ySubsampling)) * IMG_WIDTH + xOffset;
            } else {
                expected += xSubsampling;
            }
            assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expected, pxIt.getSample(0));
        }
    }

    /**
     * Test the capacity of the reader to decode entire source image, at a degraded resolution.
     * @throws DataStoreException If temporary image file has been corrupted.
     */
    @DependsOnMethod({"readRegion", "readSubsampled"})
    @Test
    public void readSubSampledRegion() throws DataStoreException {

        // Subsampling with an offset
        final int xSubsampling = 2;
        final int ySubsampling = 2;
        final int xOffset = 1;
        final int yOffset = 1;
        final MathTransform gridToCrsCorner = new AffineTransform2D(1d/IMG_WIDTH, 0, 0, -1d/IMG_WIDTH, 0, 0+1);
        GridGeometry readParam = new GridGeometry(new GridExtent(null, new long[]{SOURCE_REGION.x + xOffset,SOURCE_REGION.y + yOffset}, new long[]{SOURCE_REGION.x + SOURCE_REGION.width, SOURCE_REGION.y + SOURCE_REGION.height}, false), PixelInCell.CELL_CORNER, gridToCrsCorner, CommonCRS.defaultGeographic())
                .derive().subgrid((GridExtent) null, xSubsampling, ySubsampling).build();

        final RenderedImage read = store.read(readParam).render(null);
        assertEquals("Read image width is invalid !", 3, read.getWidth());
        assertEquals("Read image height is invalid !", 2, read.getHeight());

        final int firstPxValue = (SOURCE_REGION.y + yOffset) * IMG_WIDTH + SOURCE_REGION.x + xOffset;
        final int[] expectedValues = new int[]{
                firstPxValue,
                firstPxValue + xSubsampling,
                firstPxValue + xSubsampling*2,
                firstPxValue + (ySubsampling * IMG_WIDTH),
                firstPxValue + (ySubsampling * IMG_WIDTH) + xSubsampling,
                firstPxValue + (ySubsampling * IMG_WIDTH) + xSubsampling*2

        };

        final PixelIterator pxIt = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(read);
        int expectedIndex = 0;
        while (pxIt.next()) {
            assertEquals("Pixel value at ("+pxIt.getPosition().x+", "+pxIt.getPosition().y+") is invalid !", expectedValues[expectedIndex++], pxIt.getSample(0));
        }

    }

    @Test
    public void verify_grid_geometry() {
        final GridGeometry geometry = store.getGridGeometry();
        assertTrue("Geometry should be complete", geometry.isDefined(GridGeometry.CRS | GridGeometry.GRID_TO_CRS | GridGeometry.EXTENT));
        final GeneralEnvelope expectedEnv = new GeneralEnvelope(CommonCRS.defaultGeographic());
        expectedEnv.setEnvelope(0, 0, 1, 1);
        final Envelope geomEnv = geometry.getEnvelope();
        assertTrue(
                String.format(
                        "Envelope is not well-defined.%nExpected: %s%nBut was: %s%n",
                        expectedEnv, geomEnv
                ),
                expectedEnv.equals(geomEnv, 1e-6, false)
        );

        final GridExtent extent = geometry.getExtent();
        assertArrayEquals(new long[]{0, 0}, extent.getLow().getCoordinateValues());
        assertArrayEquals(new long[]{IMG_WIDTH-1, IMG_WIDTH-1}, extent.getHigh().getCoordinateValues());
        final MathTransform tr = geometry.getGridToCRS(PixelInCell.CELL_CORNER);
        assertTrue(tr instanceof AffineTransform2D);
        final AffineTransform2D tr2d = (AffineTransform2D) tr;
        assertArrayEquals(
                new double[]{0.090909, 0, 0, -0.090909, 0, 1},
                new double[]{tr2d.getScaleX(), tr2d.getShearX(), tr2d.getShearY(), tr2d.getScaleY(), tr2d.getTranslateX(), tr2d.getTranslateY()},
                1e-5
        );
    }
}
