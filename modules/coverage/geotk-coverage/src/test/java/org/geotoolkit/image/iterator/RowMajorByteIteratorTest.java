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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.media.jai.TiledImage;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RowMajorByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorByteIteratorTest {

    WritableRaster rasterTest;
    byte[] tabRef, tabTest;
    int minx, miny, width, height, numBand, tilesWidth, tilesHeight;
    PixelIterator pixIterator;
    TiledImage renderedImage;

    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);//on decalle l'index des tiles de 1

        int comp = 0;
        int nbrTX = width/tilesWidth;
        int nbrTY = height/tilesHeight;
        int val;
        for(int j = 0;j<nbrTY;j++){
            for(int i = 0; i<nbrTX;i++){
                val = -128;
                for (int y = miny+j*tilesHeight, ly = y+tilesHeight; y<ly; y++) {
                    for (int x = minx+i*tilesWidth, lx = x + tilesWidth; x<lx; x++) {
                        for (int b = 0; b<numBand; b++) {
                            renderedImage.setSample(x, y, b, val);
                            comp++;
                        }
                        val++;
                    }
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
            tabLenght = Math.abs((maxIX-minIX)*(maxIY-minIY)) * numBand;
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

//        tabRef  = new byte[tabLenght];
        tabRef = new byte[tabLenght];
        tabTest = new byte[tabLenght];
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
                        final byte value = (byte)(x-depX + (y-depY) * tilesWidth - 128);
                        for (int b = 0; b<numBand; b++) {
                            tabRef[comp++] =  value;
                        }
                    }
                }
            }
        }
    }

    /**
     * Test if iterator transverse all image renderer tiles (raster) positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void transversingAllReadTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        setPixelIterator(renderedImage);
//
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
//
        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        setPixelIterator(renderedImage);

        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte) pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
//
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if getX() getY() iterator methods are conform from rendered image.
     */
    @Test
    public void getXYImageTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                pixIterator.next();
                assertTrue(pixIterator.getX() == x);
                assertTrue(pixIterator.getY() == y);
                for (int b = 0; b<numBand-1; b++) {
                    pixIterator.next();
                }
            }
        }
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left rendered image corner.
     */
    @Test
    public void rectUpperLeftTest() {
        final Rectangle rect = new Rectangle(-10, -20, 40, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right rendered image corner.
     */
    @Test
    public void rectUpperRightTest() {
        final Rectangle rect = new Rectangle(80, -20, 30, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right rendered image corner.
     */
    @Test
    public void rectLowerRightTest() {
        final Rectangle rect = new Rectangle(80, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left rendered image corner.
     */
    @Test
    public void rectLowerLeftTest() {
        final Rectangle rect = new Rectangle(-20, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within image area.
     */
    @Test
    public void imageContainsRectTest() {
        final Rectangle rect = new Rectangle(20, 10, 70, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all image area.
     */
    @Test
    public void rectContainsImageTest() {
        final Rectangle rect = new Rectangle(-10, -10, 150, 80);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        setPixelIterator(renderedImage, rect);

        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectTest() {
        final Rectangle rect = new Rectangle(-100, -50, 5, 17);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        try{
            setPixelIterator(renderedImage, rect);
            Assert.fail("test should had failed");
        }catch(IllegalArgumentException e){
            //ok
        }
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRITest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 17;
        final int mY = 15;
        pixIterator.moveTo(mX, mY);
        final int indexCut = (mX - minx + (mY-miny) *width) * numBand;
        final int lenght = tabRef.length-indexCut;
        tabTest = new byte[lenght];
        byte[] tabTemp = new byte[lenght];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, lenght);
        tabRef = tabTemp.clone();
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte) pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception with x, y moveTo method coordinates out of raster boundary.
     */
    @Test
    public void unappropriateMoveToRITest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        try{
            pixIterator.moveTo(102, 53);
            Assert.fail("test should had failed");
        }catch(IllegalArgumentException e){
            //ok
        }
    }



    /**
     * Compare 2 integer table.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    protected boolean compareTab(byte[] tabA, byte[] tabB) {
        int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createRowMajorIterator(renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createRowMajorIterator(renderedImage, subArea);
    }

}
