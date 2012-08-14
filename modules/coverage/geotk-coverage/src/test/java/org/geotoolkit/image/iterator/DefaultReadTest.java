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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Some tests only for Default type iterator.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class DefaultReadTest extends IteratorTest{

    protected DefaultReadTest() {
    }

    /**
     * Affect expected values in reference table implementation.
     *
     * @param indexCut starting position in {@code tabRef} array
     * @param length new {@code tabRef} length.
     */
    protected abstract void setMoveToRITabs(final int indexCut, final int length);

     /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(Raster raster) {
        pixIterator = PixelIteratorFactory.createDefaultIterator(raster);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final Raster raster, final Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultIterator(raster, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numband, Rectangle subArea) {
        setRasterTest(this, minx, miny, width, height, numband, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        setRenderedImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

    static void setRenderedImgTest(IteratorTest test, int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final int dataType = test.getDataBufferType();
        final SampleModel sampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight,numBand,tilesWidth*numBand, new int[]{0,1,2});
        test.renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        double comp;
        int nbrTX = width/tilesWidth;
        int nbrTY = height/tilesHeight;
        double valueRef = (dataType == DataBuffer.TYPE_FLOAT) ? -200.5 : 0;
        comp = valueRef;
        for(int j = 0;j<nbrTY;j++){
            for(int i = 0; i<nbrTX;i++){
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

                //iteration area from each tile
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
                            test.setTabRefValue((int)comp++, valueRef + b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tx*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand);
                        }
                    }
                }
            }
        }
    }

    static void setRasterTest(IteratorTest test, int minx, int miny, int width, int height, int numband, Rectangle subArea) {
        final int dataType = test.getDataBufferType();
        double valueRef;
        switch (dataType) {
            case DataBuffer.TYPE_FLOAT : valueRef = -2000.5;break;
            default : valueRef = 0;break;
        }
        SampleModel sampleM = new PixelInterleavedSampleModel(dataType, width, width, numband, width*numband, new int[]{0, 1, 2});
        test.rasterTest = Raster.createWritableRaster(sampleM, new Point(minx, miny));
        double comp = valueRef;
        for (int y = miny; y<miny + height; y++) {
            for (int x = minx; x<minx + width; x++) {
                for (int b = 0; b<numband; b++) {
                    test.rasterTest.setSample(x, y, b, comp++);
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
        test.createTable(length);
        comp = 0;
        for (int y = my; y<my + h; y++) {
            for (int x = mx; x<mx + w; x++) {
                for (int b = 0; b<numband; b++) {
                    test.setTabRefValue((int) comp++,  b + numband * ((x-minx) + (y-miny) * width) + valueRef);
                }
            }
        }
    }

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
        pixIterator.moveTo(mX, mY, 0);
        final int indexCut = ((mY-miny)*width + mX - minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
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
        pixIterator.moveTo(mX, mY,0);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRIUpperLeftTest() {
        minx = -1;
        miny = 3;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = -1;
        final int mY = 3;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY, 0);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRIUpperRightTest() {
        minx = -1;
        miny = 3;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 98;
        final int mY = 3;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY, 0);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRIlowerRightTest() {
        minx = -1;
        miny = 3;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = 98;
        final int mY = 52;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY, 0);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected values from x y coordinates define by moveTo method.
     */
    @Test
    public void moveToRIlowerLeftTest() {
        minx = -1;
        miny = 3;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage);
        final int mX = -1;
        final int mY = 52;
        final int ity = (mY-miny) / tilesHeight;
        final int itx = (mX-minx) / tilesWidth;
        pixIterator.moveTo(mX, mY, 0);
        final int indexCut = ((((ity*((width/tilesWidth)-1))+itx)*tilesHeight+mY-miny-itx)*tilesWidth + mX-minx)*numBand;
        final int lenght = width*height*numBand - indexCut;
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        do {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        } while (pixIterator.next());
        assertTrue(compareTab());
    }
}
