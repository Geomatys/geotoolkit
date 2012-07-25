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
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import javax.media.jai.TiledImage;

/**
 * Test DefaultByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultByteIteratorTest extends DefaultReadTest{

    /**
     * Table which contains expected tests results values.
     */
    private byte[] tabRef;

    /**
     * Table which contains tests results values.
     */
    private byte[] tabTest;

    public DefaultByteIteratorTest() {

    }

    ///////////////////////////////Raster tests/////////////////////////////////
    /**
     * {@inheritDoc}.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        setRasterByteTest(this, minx, miny, width, height, numBand, subArea);
    }

    static void setRasterByteTest(IteratorTest test, int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        int comp = 0;
        test.rasterTest = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, numBand, new Point(minx, miny));
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                for (int b = 0; b<numBand; b++) {
                    test.rasterTest.setSample(x, y, b, comp-128);
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
            w  = Math.min(minx + width,  subArea.x + subArea.width) - mx;
            h  = Math.min(miny + height, subArea.y + subArea.height) - my;
        }

        final int length = w * h * numBand;
        test.createTable(length);
        comp = 0;
        for (int y = my; y<my + h; y++) {
            for (int x = mx; x<mx + w; x++) {
                for (int b = 0; b<numBand; b++) {
                    test.setTabRefValue(comp++, (x-minx) + (y-miny) * width - 128);
                }
            }
        }
    }

    /**
     *{@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        setRenderedImgByteTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

    static void setRenderedImgByteTest(IteratorTest test, int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {

        final int[] bandOffset = new int[numBand];
        for (int i = 0;i<numBand; i++) {
            bandOffset[i] = i;
        }

        final SampleModel sampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
        test.renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        double comp;
        final int nbrTX = width/tilesWidth;
        final int nbrTY = height/tilesHeight;
        double valueRef = -128;
        for(int j = 0;j<nbrTY;j++){
            for(int i = 0; i<nbrTX;i++){
                comp = valueRef;
                for (int y = miny+j*tilesHeight, ly = y+tilesHeight; y<ly; y++) {
                    for (int x = minx+i*tilesWidth, lx = x + tilesWidth; x<lx; x++) {
                        for (int b = 0; b<numBand; b++) {
                            test.renderedImage.setSample(x, y, b, comp++);
                        }
                    }
                }
            }
        }

        ////////////////////remplir tabRef/////////////////

        int tX, tY, tMaxX, tMaxY;
        int areaMinX = 0, areaMinY = 0, areaMaxX = 0, areaMaxY = 0;
        int tabLength;

        if (areaIterate != null) {
            //iteration area boundary
            areaMinX = Math.max(minx, areaIterate.x);
            areaMinY = Math.max(miny, areaIterate.y);
            areaMaxX = Math.min(minx+width, areaIterate.x+areaIterate.width);
            areaMaxY = Math.min(miny+height, areaIterate.y+areaIterate.height);
            tabLength = (areaMaxX - areaMinX) * (areaMaxY - areaMinY) * numBand;

            //iteration tiles index
            tX = (areaMinX-minx)/tilesWidth;
            tY = (areaMinY-miny)/tilesHeight;
            tMaxX = (areaMaxX-minx)/tilesWidth;
            tMaxY = (areaMaxY-miny)/tilesHeight;
            if (tMaxX == width/tilesWidth) tMaxX--;
            if (tMaxY == height/tilesHeight) tMaxY--;
        } else {
            tX = tY = 0;
            tMaxX = width/tilesWidth - 1;
            tMaxY = height/tilesHeight - 1;
            tabLength = width * height * numBand;
        }

        //test table creation
        test.createTable(tabLength);

        int rasterMinX, rasterMinY, rasterMaxX, rasterMaxY, depX, depY, endX, endY;

        comp = 0;
        for (;tY <= tMaxY; tY++) {
            for (int tx = tX;tx<=tMaxX; tx++) {

                //find iteration area from each tiles
                rasterMinX = minx+tx*tilesWidth;
                rasterMinY = miny+tY*tilesHeight;
                rasterMaxX = rasterMinX + tilesWidth;
                rasterMaxY = rasterMinY + tilesHeight;

                //iteration area
                if (areaIterate != null) {
                    depX = Math.max(rasterMinX, areaMinX);
                    depY = Math.max(rasterMinY, areaMinY);
                    endX = Math.min(rasterMaxX, areaMaxX);
                    endY = Math.min(rasterMaxY, areaMaxY);
                } else {
                    depX = rasterMinX;
                    depY = rasterMinY;
                    endX = rasterMaxX;
                    endY = rasterMaxY;
                }

                for (;depY < endY; depY++) {
                    for (int x = depX; x < endX; x++) {
                        for (int b = 0;b<numBand;b++) {
                            test.setTabRefValue((int)comp++, valueRef + b + (x-rasterMinX+(depY-rasterMinY)*tilesWidth)*numBand);
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
    protected void setTabTestValue(int index, double value) {
        tabTest[index] = (byte) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabRefValue(int index, double value) {
        tabRef[index] = (byte) value;
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
    protected void setMoveToRITabs(int indexCut, int length) {
        tabTest = new byte[length];
        byte[] tabTemp = new byte[length];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, length);
        tabRef = tabTemp.clone();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected int getDataBufferType() {
        return DataBuffer.TYPE_BYTE;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void createTable(int length) {
        tabRef  = new byte[length];
        tabTest = new byte[length];
    }
}
