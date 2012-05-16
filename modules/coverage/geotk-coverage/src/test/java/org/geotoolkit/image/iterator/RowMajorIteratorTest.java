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
public class RowMajorIteratorTest extends IteratorTest {

    protected int dataType = DataBuffer.TYPE_INT;
    protected int[] tabRef, tabTest;

    public RowMajorIteratorTest() {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void differentMinRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectContainsRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerLeftRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerRightRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperLeftRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperRightRasterReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rasterContainsRectReadTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateRectRasterTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateMoveToRasterTest() {
        //no test about raster for this iterator
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
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
     * Test if iterator transverse expected value from moveTo(x, y) method.
     */
    @Test
    public void moveToTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 15;
        final int mY = 26;
        final int indexCut = (mY*width + mX) * numBand;
        final int lenght = width*height*numBand - indexCut;
        pixIterator.moveTo(mX, mY);

        tabTest = new int[lenght];
        int[] tabTemp = new int[lenght];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, lenght);
        tabRef = tabTemp;
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value from moveTo(x, y) method.
     */
    @Test
    public void moveToTest2() {
        minx = -17;
        miny = 12;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 57;
        final int mY = 26;
        final int indexCut = ((mY-miny)*width + (mX-minx)) * numBand;
        final int lenght = width*height*numBand - indexCut;
        pixIterator.moveTo(mX, mY);

        tabTest = new int[lenght];
        int[] tabTemp = new int[lenght];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, lenght);
        tabRef = tabTemp;
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
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


    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createRowMajorIterator(renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createRowMajorIterator(renderedImage, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(Raster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(Raster raster, Rectangle subArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabTestValue(int index, double value) {
        tabTest[index] = (int) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected boolean compareTab() {
        return compareTab(tabRef, tabTest);
    }
}
