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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test DefaultWritableRIIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultWritableIteratorTest extends WritableIteratorTest {

    protected int dataType = DataBuffer.TYPE_INT;
    protected int[] tabRef, tabTest;

    public DefaultWritableIteratorTest() {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void fillGoodTabRef(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        int depy = Math.max(miny, areaIterate.y);
        int depx = Math.max(minx, areaIterate.x);
        int endy = Math.min(miny + height, areaIterate.y + areaIterate.height);
        int endx = Math.min(minx + width, areaIterate.x + areaIterate.width);
        int mody, modx, x2, y2, pos;
        for(int y = depy; y<endy; y++){
            for(int x = depx; x<endx; x++){
                for(int b = 0; b<numBand; b++){
                    mody = (y-miny) / tilesHeight;
                    modx = (x-minx) / tilesWidth;//division entière voulue
                    x2 = (x-minx)-modx*tilesWidth;
                    y2 = (y-miny)-mody*tilesHeight;
                    pos = b + numBand*(x2 + tilesWidth*(y2 + modx*tilesHeight) + mody*width*tilesHeight);
                    tabRef[pos] = -1;
                }
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(Raster raster) {
        super.pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        super.pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final Raster raster, final Rectangle subArea) {
        super.pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        super.pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage, subArea);
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

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        int comp = 0;
        for (int y = miny, ly = miny+height; y<ly; y++) {
            for (int x = minx, lx = minx + width; x<lx; x++) {
                for (int b = 0; b<numBand; b++) {
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

        tabRef  = new int[tabLenght];
        tabTest = new int[tabLenght];

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
                            tabRef[comp++] =  b + numBand * ((x-depX) + tilesWidth*tileX + width * ((y-depY) + tilesHeight*tileY));
                        }
                    }
                }

            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        int comp = 0;
        rasterTest = Raster.createBandedRaster(DataBuffer.TYPE_INT, width, height, numBand, new Point(minx, miny));
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                for (int b = 0; b<numBand; b++) {
                    rasterTest.setSample(x, y, b, comp++);
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
        final int length = w * h * numBand;
        tabRef  = new int[length];
        tabTest = new int[length];
        comp = 0;
        for (int y = my; y<my + h; y++) {
            for (int x = mx; x<mx + w; x++) {
                for (int b = 0; b<numBand; b++) {
                    tabRef[comp++] = b + numBand * ((x-minx) + (y-miny) * width);
                }
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected int getDataBufferType() {
        return DataBuffer.TYPE_INT;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected PixelIterator getWritableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void transversingAllWriteTest() {
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;

        BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        setPixelIterator(renderedImage);
        final int length = width*height*numBand;
        tabRef  = new int[length];
        tabTest = new int[length];
        int comp = 0;
        while (pixIterator.next()) {
            pixIterator.setSample(comp);
            tabRef[comp] = comp++;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        setPixelIterator(renderedImage);

        comp = 0;
        while (pixIterator.next()) {
            pixIterator.setSample(comp);
            tabRef[comp] = comp++;
        }

        comp = 0;
        pixIterator.rewind();
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
        ////////////Test rewind//////////////
        pixIterator.rewind();
        while (pixIterator.next()) {
            pixIterator.setSample(comp);
            tabRef[length-comp] = comp--;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception if rasters haven't got same criterion.
     */
    @Test
    public void unappropriateRasterTest() {
        final Raster rasterRead = Raster.createBandedRaster(dataType, 20, 10, 3, new Point(0,0));
        final WritableRaster rasterWrite = Raster.createBandedRaster(dataType, 200, 100, 30, new Point(3,1));
        boolean test = false;
        try {
            final DefaultWritableIterator iter = new DefaultWritableIterator(rasterRead, rasterWrite);
        } catch(Exception e) {
            test = true;
        }
        assertTrue(test);
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
