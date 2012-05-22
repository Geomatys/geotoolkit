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
import javax.media.jai.TiledImage;

/**
 * Test RowMajorRenderedImageIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorIteratorTest extends RowMajorReadTest {

    protected int[] tabRef, tabTest;

    public RowMajorIteratorTest() {
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
    protected void setMoveToRITabs(int indexCut, int length) {
        tabTest = new int[length];
        int[] tabTemp = new int[length];
        System.arraycopy(tabRef.clone(), indexCut, tabTemp, 0, length);
        tabRef = tabTemp;
    }
}
