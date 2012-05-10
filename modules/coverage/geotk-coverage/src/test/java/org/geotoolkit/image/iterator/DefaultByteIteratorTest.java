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
import java.awt.image.*;
import javax.media.jai.TiledImage;
import org.geotoolkit.test.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test DefaultByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultByteIteratorTest {

    WritableRaster rasterTest;
    byte[] tabRef, tabTest;
    int minx, miny, width, height, numBand, tilesWidth, tilesHeight;
    PixelIterator pixIterator;
    TiledImage renderedImage;

    public DefaultByteIteratorTest() {

    }

    //////////////////Raster tests///////////////////
    /**
     * Create and fill an appropriate Raster for tests.
     */
    private void setRasterTest(int minx, int miny, int width, int height, int numband, Rectangle subArea) {
        int comp = 0;
        rasterTest = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, width, height, numband, new Point(minx, miny));
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                for (int b = 0; b<numband; b++) {
                    rasterTest.setSample(x, y, b, comp-128);
                }
                comp++;
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
        tabRef  = new byte[length];
        tabTest = new byte[length];
        comp = 0;
        for (int y = my; y<my + h; y++) {
            for (int x = mx; x<mx + w; x++) {
                for (int b = 0; b<numband; b++) {
                    tabRef[comp++] = (byte) ((x-minx) + (y-miny) * width - 128);
                }
            }
        }
    }

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void differentMinRasterReadTest() {
        width = 16;
        height = 16;
        minx = 0;
        miny = 0;
        numBand = 3;
        tabTest = new byte[width*height*numBand];
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = 3;
        minx = 5;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = -3;
        miny = 5;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = 3;
        miny = -5;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = -3;
        miny = -5;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }


    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftRasterReadTest() {
        final Rectangle subArea = new Rectangle(4, 6, 5, 4);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightRasterReadTest() {
        final Rectangle subArea = new Rectangle(16, 6, 10, 6);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightRasterReadTest() {
        final Rectangle subArea = new Rectangle(14, 20, 15, 9);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftRasterReadTest() {
        final Rectangle subArea = new Rectangle(2, 12, 10, 6);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within raster area.
     */
    @Test
    public void rasterContainsRectReadTest() {
        final Rectangle subArea = new Rectangle(10, 9, 8, 6);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));

        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all raster area.
     */
    @Test
    public void rectContainsRasterReadTest() {
        final Rectangle subArea = new Rectangle(2, 3, 25, 17);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        setPixelIterator(rasterTest, subArea);
        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRasterTest() {
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        final int mX = 17;
        final int mY = 15;
        pixIterator.moveTo(mX, mY);
        final int indexCut = ((mY-miny)*width + mX - minx)*numBand;
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
    public void unappropriateMoveToRasterTest() {
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
        boolean testTry = false;
        try{
            pixIterator.moveTo(2, 3);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectRasterReadTest() {
        final Rectangle subArea = new Rectangle(-17, -20, 5, 15);
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, subArea);
        boolean testTry = false;
        try{
            setPixelIterator(rasterTest, subArea);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }

    //////////////////////imageRenderer test/////////////////////////

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

        tabRef  = new byte[tabLenght];
        tabTest = new byte[tabLenght];
        comp = 0;
        for (int tileY = tileMinY; tileY<tileMaxY; tileY++) {
            rastminY = tileY * tilesHeight;
            rastmaxY = rastminY + tilesHeight;
            for (int tileX = tileMinX; tileX<tileMaxX; tileX++) {
                //tile by tile
                rastminX = tileX * tilesWidth;
                rastmaxX = rastminX + tilesWidth;
                if (areaIterate == null) {
                    depX = rastminX;
                    depY = rastminY;
                    endX = rastmaxX;
                    endY = rastmaxY;
                } else {
                    depX = Math.max(rastminX, minIX);
                    depY = Math.max(rastminY, minIY);
                    endX = Math.min(rastmaxX, maxIX);
                    endY = Math.min(rastmaxY, maxIY);
                }

                for (int y = depY; y<endY; y++) {
                    for (int x = depX; x<endX; x++) {
                        for (int b = 0; b<numBand; b++) {
                            tabRef[comp++] =  (byte) ((x-depX) + (y-depY) * tilesWidth - 128);
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
    public void transversingAllReadTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        setPixelIterator(renderedImage);

        int comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
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
        while (pixIterator.next()) tabTest[comp++] = (byte) pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));

        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            tabTest[comp++] = (byte) pixIterator.getSample();
        }
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
     * Area is defined on upper right raster corner.
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
     * Area is defined on lower right raster corner.
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
     * Area is defined on lower left raster corner.
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
        boolean testTry = false;
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        try{
            setPixelIterator(renderedImage, rect);
        }catch(IllegalArgumentException e){
            testTry = true;
        }
        assertTrue(testTry);
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
        final int tileBulk = tilesHeight*tilesWidth*numBand;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 17;
        final int mY = 15;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY);
        final int indexCut = ity*10*tileBulk+itx*tileBulk+((mY-ity*tilesHeight)*tilesWidth + (mX-itx*tilesWidth))*numBand;
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
        boolean testTry = false;
        try{
            pixIterator.moveTo(102, 53);
        }catch(IllegalArgumentException e){
            testTry = true;
        }
        assertTrue(testTry);
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
    protected void setPixelIterator(Raster raster) {
        pixIterator = new DefaultByteIterator(raster);
    }

    /**
     * {@inheritDoc }.
     */
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = new DefaultByteIterator(renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    protected void setPixelIterator(final Raster raster, final Rectangle subArea) {
        pixIterator = new DefaultByteIterator(raster, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = new DefaultByteIterator(renderedImage, subArea);
    }

}
