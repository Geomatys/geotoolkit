/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.image.PlanarImage;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferedImagesTest {

    @Test
    public void testPointStream() {

        final Stream<Point> stream = BufferedImages.pointStream(new Rectangle(10, 20, 3, 4));

        Iterator<Point> ite = stream.sequential().iterator();
        Assert.assertEquals(new Point(10, 20), ite.next());
        Assert.assertEquals(new Point(11, 20), ite.next());
        Assert.assertEquals(new Point(12, 20), ite.next());
        Assert.assertEquals(new Point(10, 21), ite.next());
        Assert.assertEquals(new Point(11, 21), ite.next());
        Assert.assertEquals(new Point(12, 21), ite.next());
        Assert.assertEquals(new Point(10, 22), ite.next());
        Assert.assertEquals(new Point(11, 22), ite.next());
        Assert.assertEquals(new Point(12, 22), ite.next());
        Assert.assertEquals(new Point(10, 23), ite.next());
        Assert.assertEquals(new Point(11, 23), ite.next());
        Assert.assertEquals(new Point(12, 23), ite.next());
        Assert.assertFalse(ite.hasNext());

    }

    @Test
    public void testTileStream() {

        { // simple case
            final RenderedImage image = new MockImage(0, 0, 20, 30,0, 0, 10, 10);

            final List<Rectangle> rectangles = BufferedImages.tileStream(image, 0, 0, 0, 0).sequential().collect(Collectors.toList());
            Assert.assertEquals(6, rectangles.size());

            Assert.assertEquals(new Rectangle( 0,  0, 10, 10), rectangles.get(0));
            Assert.assertEquals(new Rectangle(10,  0, 10, 10), rectangles.get(1));
            Assert.assertEquals(new Rectangle( 0, 10, 10, 10), rectangles.get(2));
            Assert.assertEquals(new Rectangle(10, 10, 10, 10), rectangles.get(3));
            Assert.assertEquals(new Rectangle( 0, 20, 10, 10), rectangles.get(4));
            Assert.assertEquals(new Rectangle(10, 20, 10, 10), rectangles.get(5));
        }

        { // simple case + margin
            final RenderedImage image = new MockImage(0, 0, 20, 30,0, 0, 10, 10);

            final List<Rectangle> rectangles = BufferedImages.tileStream(image, 1, 2, 3, 4).sequential().collect(Collectors.toList());
            Assert.assertEquals(6, rectangles.size());

            Assert.assertEquals(new Rectangle( 0,  0, 10+2, 10+1), rectangles.get(0));
            Assert.assertEquals(new Rectangle( 10-4,  0, 10+4, 10+1), rectangles.get(1));
            Assert.assertEquals(new Rectangle( 0,  10-3, 10+2, 10+1+3), rectangles.get(2));
            Assert.assertEquals(new Rectangle( 10-4,  10-3, 10+4, 10+1+3), rectangles.get(3));
            Assert.assertEquals(new Rectangle( 0, 20-3, 10+2, 10+3), rectangles.get(4));
            Assert.assertEquals(new Rectangle( 10-4, 20-3, 10+4, 10+3), rectangles.get(5));
        }

        { // complex image tile system
            final RenderedImage image = new MockImage(40, -50, 20, 30,5, 6, 10, 10);

            final List<Rectangle> rectangles = BufferedImages.tileStream(image, 0, 0, 0, 0).sequential().collect(Collectors.toList());
            Assert.assertEquals(6, rectangles.size());

            Assert.assertEquals(new Rectangle(40, -50, 10, 10), rectangles.get(0));
            Assert.assertEquals(new Rectangle(50, -50, 10, 10), rectangles.get(1));
            Assert.assertEquals(new Rectangle(40, -40, 10, 10), rectangles.get(2));
            Assert.assertEquals(new Rectangle(50, -40, 10, 10), rectangles.get(3));
            Assert.assertEquals(new Rectangle(40, -30, 10, 10), rectangles.get(4));
            Assert.assertEquals(new Rectangle(50, -30, 10, 10), rectangles.get(5));
        }

        { // complex image tile system + margin
            final RenderedImage image = new MockImage(40, -50, 20, 30,5, 6, 10, 10);

            final List<Rectangle> rectangles = BufferedImages.tileStream(image, 1, 2, 3, 4).sequential().collect(Collectors.toList());
            Assert.assertEquals(6, rectangles.size());

            Assert.assertEquals(new Rectangle(40, -50, 10+2, 10+1), rectangles.get(0));
            Assert.assertEquals(new Rectangle(50-4, -50, 10+4, 10+1), rectangles.get(1));
            Assert.assertEquals(new Rectangle(40, -40-3, 10+2, 10+1+3), rectangles.get(2));
            Assert.assertEquals(new Rectangle(50-4, -40-3, 10+4, 10+1+3), rectangles.get(3));
            Assert.assertEquals(new Rectangle(40, -30-3, 10+2, 10+3), rectangles.get(4));
            Assert.assertEquals(new Rectangle(50-4, -30-3, 10+4, 10+3), rectangles.get(5));
        }

    }

    private static final class MockImage extends PlanarImage {

        private final int width;
        private final int height;
        private final int tileWidth;
        private final int tileHeight;
        private final int minX;
        private final int minY;
        private final int minTileX;
        private final int minTileY;

        private MockImage(int minX, int minY, int width, int height, int minTileX, int minTileY, int tileWidth, int tileHeight) {
            this.minX = minX;
            this.minY = minY;
            this.width = width;
            this.height = height;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.minTileX = minTileX;
            this.minTileY = minTileY;
        }

        @Override
        public ColorModel getColorModel() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public SampleModel getSampleModel() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getMinX() {
            return minX;
        }

        @Override
        public int getMinY() {
            return minY;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public int getTileWidth() {
            return tileWidth;
        }

        @Override
        public int getTileHeight() {
            return tileHeight;
        }

        @Override
        public int getMinTileX() {
            return minTileX;
        }

        @Override
        public int getMinTileY() {
            return minTileY;
        }

        @Override
        public Raster getTile(int tileX, int tileY) {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
