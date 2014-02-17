/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import javax.media.jai.TiledImage;

/**
 * Test RowMajorByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorByteIteratorTest extends RowMajorReadTest{

    /**
     * byte type table wherein is put iterator result.
     */
    private byte[] tabTest;

    /**
     * byte type table wherein expect result is putting.
     */
    private byte[] tabRef;

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        setRenderedByteImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

    /**
     * {@inheritDoc }.
     */
    protected void setRenderedByteImgTest(IteratorTest test, int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {

        final int[] bandOffset = new int[numBand];
        for (int i = 0;i<numBand; i++) {
            bandOffset[i] = i;
        }
        final SampleModel sampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
        test.renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

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
                        }
                        val++;
                    }
                }
            }
        }

        ////////////////////remplir tabRef/////////////////

        int areaMinX, areaMinY, areaMaxX, areaMaxY;
        int tabLength;

        if (areaIterate != null) {
            //iteration area boundary
            areaMinX = Math.max(minx, areaIterate.x);
            areaMinY = Math.max(miny, areaIterate.y);
            areaMaxX = Math.min(minx+width, areaIterate.x+areaIterate.width);
            areaMaxY = Math.min(miny+height, areaIterate.y+areaIterate.height);
            tabLength = (areaMaxX - areaMinX) * (areaMaxY - areaMinY) * numBand;
        } else {
            areaMinX = minx;
            areaMinY = miny;
            areaMaxX = minx + width;
            areaMaxY = miny + height;
            tabLength = width * height * numBand;
        }

        test.createTable(tabLength);

        //tiles iteration area
        final int tminX = (areaMinX-minx)/tilesWidth;
        final int tminY = (areaMinY-miny)/tilesHeight;
        final int tmaxX = (areaMaxX - minx + tilesWidth  - 1) / tilesWidth;
        final int tmaxY = (areaMaxY - miny + tilesHeight - 1) / tilesHeight;

        int comp = 0;

        for (int tY = tminY; tY<tmaxY; tY++) {

            int rastMinY = miny + tY*tilesHeight;
            int rastMaxY = rastMinY + tilesHeight;
            int aIminY = Math.max(areaMinY, rastMinY);
            int aImaXY = Math.min(areaMaxY, rastMaxY);

            for (int y = aIminY; y<aImaXY; y++) {
                for (int tX = tminX; tX<tmaxX; tX++) {

                    int rastMinX = minx + tX*tilesWidth;
                    int rastMaxX = rastMinX + tilesWidth;
                    //Iteration area within current tile
                    int aIminX = Math.max(areaMinX, rastMinX);
                    int aImaxX = Math.min(areaMaxX, rastMaxX);
                    for (int x = aIminX; x<aImaxX; x++) {
                        double value = (x-rastMinX) + (y-rastMinY) * tilesWidth - 128;
                        for (int b = 0; b<numBand; b++) {
                            test.setTabRefValue(comp++, value);
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
        tabTest[index] = (byte)value;
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
        tabRef = tabTemp;
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
        tabRef = new byte[length];
        tabTest = new byte[length];
    }
}
