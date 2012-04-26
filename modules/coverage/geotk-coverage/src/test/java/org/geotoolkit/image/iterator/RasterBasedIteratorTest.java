/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.iterator;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RasterBasedIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class RasterBasedIteratorTest {

    protected int numBand;
    protected int width;
    protected int height;
    protected int dataType = DataBuffer.TYPE_INT;
    protected int minx;
    protected int miny;
    protected int[] tabRef, tabTest;

    protected WritableRaster rasterTest;
    protected PixelIterator pixIterator;

    public RasterBasedIteratorTest() {

    }

    protected abstract void setPixelIterator(final Raster raster, final Rectangle subArea);

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void differentMinRasterTest() {
        width = 10;
        height = 10;
        minx = 0;
        miny = 0;
        numBand = 3;
        tabTest = new int[width*height*numBand];
        setRasterTest(minx, miny, width, height, numBand, null);
//        DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest);
        setPixelIterator(rasterTest, null);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = 3;
        minx = 5;
        setRasterTest(minx, miny, width, height, numBand, null);
//        dfrii = new DefaultRenderedImageIterator(rasterTest);
        setPixelIterator(rasterTest, null);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = -3;
        miny = 5;
        setRasterTest(minx, miny, width, height, numBand, null);
//        dfrii = new DefaultRenderedImageIterator(rasterTest);
        setPixelIterator(rasterTest, null);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = 3;
        miny = -5;
        setRasterTest(minx, miny, width, height, numBand, null);
//        dfrii = new DefaultRenderedImageIterator(rasterTest);
        setPixelIterator(rasterTest, null);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = -3;
        miny = -5;
        setRasterTest(minx, miny, width, height, numBand, null);
//        dfrii = new DefaultRenderedImageIterator(rasterTest);
        setPixelIterator(rasterTest, null);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }


    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftRasterTest() {
        final Rectangle subArea = new Rectangle(4, 6, 5, 4);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightRasterTest() {
        final Rectangle subArea = new Rectangle(16, 6, 10, 6);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightRasterTest() {
        final Rectangle subArea = new Rectangle(14, 10, 15, 9);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftRasterTest() {
        final Rectangle subArea = new Rectangle(2, 12, 10, 6);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within raster area.
     */
    @Test
    public void rasterContainsRectTest() {
        final Rectangle subArea = new Rectangle(10, 9, 11, 6);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all raster area.
     */
    @Test
    public void rectContainsRasterTest() {
        final Rectangle subArea = new Rectangle(2, 3, 25, 17);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
//        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp] = pixIterator.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectRasterTest() {
        final Rectangle subArea = new Rectangle(-17, -20, 5, 15);
        numBand = 3;
        width = 20;
        height = 10;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        boolean testTry = false;
        try{
//            final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(rasterTest, subArea);
            setPixelIterator(rasterTest, subArea);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }


    /**
     * Create and fill an appropriate Raster for tests.
     */
    private void setRasterTest(int minx, int miny, int width, int height, int numband, Rectangle subArea) {
        int comp = 0;
        rasterTest = Raster.createBandedRaster(DataBuffer.TYPE_INT, width, height, numband, new Point(minx, miny));
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                for (int b = 0; b<numband; b++) {
                    rasterTest.setSample(x, y, b, comp);
                    comp++;
                }
            }
        }

        int mx, my, w,h;
        if (subArea == null) {
            mx = minx;
            my = miny;
            w = width;
            h = height;

        } else {
            mx = Math.max(minx, subArea.x);
            my = Math.max(miny, subArea.y);
            w  = Math.min(minx + width, subArea.x + subArea.width) - mx;
            h  = Math.min(miny + height, subArea.y + subArea.height) - my;
        }
        final int length = w * h * numband;
        tabRef = new int[length];
        tabTest = new int[length];
        comp = 0;
        for (int y = my; y<my + h; y++) {
            for (int x = mx; x<mx + w; x++) {
                for (int b = 0; b<numband; b++) {
                    tabRef[comp] = b + numband * ((x-minx) + (y-miny) * width);
                    comp++;
                }
            }
        }
    }

    /**
     * Compare 2 integer table.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    protected boolean compareTab(int[] tabA, int[] tabB) {
        int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }

}
