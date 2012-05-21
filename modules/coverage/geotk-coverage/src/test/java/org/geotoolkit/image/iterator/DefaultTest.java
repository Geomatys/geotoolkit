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

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Some tests only for Default type iterator.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class DefaultTest extends IteratorTest{

    protected DefaultTest() {
    }

    /**
     * Affect expected values in reference table implementation.
     *
     * @param indexCut starting position in {@code tabRef} array
     * @param length new {@code tabRef} length.
     */
    protected abstract void setMoveToRITabs(final int indexCut, final int length);


    ///////////////////////////////Raster Tests/////////////////////////////////
    /**
     * Test if getX() getY() iterator methods are conform from raster.
     */
    @Test
    public void getXYRasterTest() {
        numBand = 3;
        width = 16;
        height = 16;
        minx = 5;
        miny = 7;
        setRasterTest(minx, miny, width, height, numBand, null);
        setPixelIterator(rasterTest);
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
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSample());
        }
        assertTrue(compareTab());
    }


    ///////////////////////////Rendered Image Tests//////////////////////////////
    /**
     * Test if getX() getY() iterator methods are conform from rendered image.
     */
    @Test
    public void getXYImageTest() {
        minx = 56;
        miny = 1;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        for (int ty = 0; ty<height/tilesHeight; ty++) {
            for (int tx = 0; tx<width/tilesWidth; tx++) {
                for (int y = 0; y<tilesHeight; y++) {
                    for (int x = 0; x<tilesWidth; x++) {
                        pixIterator.next();
                        assertTrue(pixIterator.getX() == tx*tilesWidth+x+minx);
                        assertTrue(pixIterator.getY() == ty*tilesHeight+y+miny);
                        for (int b = 0; b<numBand-1; b++) {
                            pixIterator.next();
                        }
                    }
                }
            }
        }
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRITest() {
        minx = -1;
        miny = 3;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 17;
        final int mY = 15;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSample());
        }
        assertTrue(compareTab());
    }
}
