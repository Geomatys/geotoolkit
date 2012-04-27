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

import java.awt.Rectangle;
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RowMajorRenderedImageIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorRenderedImageIteratorTest extends RasterBasedIteratorTest {
    TiledImage renderedImage;

    public RowMajorRenderedImageIteratorTest() {
    }

    private void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        int comp = 0;
        for(int y = miny, ly = miny+height; y<ly; y++){
            for(int x = minx, lx = minx + width; x<lx; x++){
                for(int b = 0; b<numBand; b++){
                    renderedImage.setSample(x, y, b, comp++);
                }
            }
        }

        int cULX, cULY, cBRX, cBRY, minIX = 0, minIY = 0, maxIX = 0, maxIY = 0;
        int tileMinX, tileMinY, tileMaxX, tileMaxY;
        int rastminY, rastminX, rastmaxY, rastmaxX, depX, depY, endX, endY, tabLenght;

        if (areaIterate != null) {
            cULX = areaIterate.x;
            cULY = areaIterate.y;
            cBRX = cULX + areaIterate.width;
            cBRY = cULY + areaIterate.height;
            minIX = Math.max(cULX, minx);
            minIY = Math.max(cULY, miny);
            maxIX = Math.min(cBRX, minx + width);
            maxIY = Math.min(cBRY, miny + height);
            tabLenght = Math.abs((maxIX-minIX)*(maxIY-minIY)) *numBand;
            tileMinX = (minIX - minx) / tilesWidth;
            tileMinY = (minIY - miny) / tilesHeight;
            tileMaxX = (maxIX - minx) / tilesWidth;
            tileMaxY = (maxIY - miny) / tilesHeight;
        } else {
            tileMinX = tileMinY = 0;
            tileMaxX = width/tilesWidth;
            tileMaxY = height/tilesHeight;
            tabLenght = width*height*numBand;
        }

        tabRef  = new int[tabLenght];
        tabTest = new int[tabLenght];

        comp = 0;
        for (int tileY = tileMinY; tileY<tileMaxY; tileY++) {
            rastminY = tileY * tilesHeight;
            rastmaxY = rastminY + tilesHeight;
            if (areaIterate == null) {
                depY = rastminY;
                endY = rastmaxY;
            } else {
                depY = Math.max(rastminY, minIY);
                endY = Math.min(rastmaxY, maxIY);
            }

            for (int y = depY; y<endY; y++) {
                for (int tileX = tileMinX; tileX<tileMaxX; tileX++) {
                    //tile by tile
                    rastminX = tileX * tilesWidth;
                    rastmaxX = rastminX + tilesWidth;
                    if (areaIterate == null) {
                        depX = rastminX;
                        endX = rastmaxX;
                    } else {
                        depX = Math.max(rastminX, minIX);
                        endX = Math.min(rastmaxX, maxIX);
                    }

                    for (int x = depX; x<endX; x++) {
                        for (int b = 0; b<numBand; b++) {
                            tabRef[comp++] =  b + numBand * ((x-depX) + tilesWidth*tileX + width * ((y-depY) + tilesHeight*tileY));
                        }
                    }
                }
            }
        }
    }

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void transversingAllTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        int tilesWidth = 10;
        int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        setPixelIterator(renderedImage);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        setPixelIterator(renderedImage);

        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));

        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftTest() {
        final Rectangle rect = new Rectangle(-10, -20, 40, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightTest() {
        final Rectangle rect = new Rectangle(80, -20, 30, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightTest() {
        final Rectangle rect = new Rectangle(80, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftTest() {
        final Rectangle rect = new Rectangle(-20, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within raster area.
     */
    @Test
    public void imageContainsRectTest() {
        final Rectangle rect = new Rectangle(20, 10, 70, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all raster area.
     */
    @Test
    public void rectContainsImageTest() {
        final Rectangle rect = new Rectangle(-10, -10, 150, 80);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectTest() {
        final Rectangle rect = new Rectangle(-100, -50, 5, 17);
        boolean testTry = false;
        try{
            setPixelIterator(renderedImage, rect);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }

    @Override
    protected void setPixelIterator(Raster raster) {
        super.pixIterator = new RowMajorRenderedImageIterator(raster);
    }

    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        super.pixIterator = new RowMajorRenderedImageIterator(renderedImage);
    }

    @Override
    protected void setPixelIterator(Raster raster, Rectangle subArea) {
        super.pixIterator = new RowMajorRenderedImageIterator(raster, subArea);
    }

    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        super.pixIterator = new RowMajorRenderedImageIterator(renderedImage, subArea);
    }

}
