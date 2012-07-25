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
import java.awt.image.*;
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Some tests only for RowMajor type iterator.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class RowMajorReadTest extends IteratorTest {

    protected RowMajorReadTest() {
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
    protected void setPixelIterator(Raster raster, Rectangle subArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Affect expected values in reference table implementation.
     *
     * @param indexCut starting position in {@code tabRef} array
     * @param length new {@code tabRef} length.
     */
    protected abstract void setMoveToRITabs(final int indexCut, final int length);

    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        setRenderedImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

    static void setRenderedImgTest(IteratorTest test, int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {

        final int dataType = test.getDataBufferType();
        final int[] bandOffset = new int[numBand];
        for (int i = 0;i<numBand; i++) {
            bandOffset[i] = i;
        }
        final SampleModel sampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
        test.renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        double comp;
        double valueRef = (dataType == DataBuffer.TYPE_FLOAT) ? -200.5 : 0;
        comp = valueRef;
        for (int y = miny, ly = miny + height; y<ly; y++) {
            for (int x = minx, lx = minx + width; x<lx; x++) {
                for (int b = 0; b<numBand; b++) {
                    test.renderedImage.setSample(x, y, b, comp++);
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

        //test table cretion
        test.createTable(tabLength);
        comp = 0;
        for (;areaMinY<areaMaxY;areaMinY++) {
            for (int x = areaMinX; x<areaMaxX; x++) {
                for (int b = 0; b<numBand; b++) {
                    test.setTabRefValue((int) comp++, b + (x-minx)*numBand + (areaMinY-miny) * width*numBand + valueRef);
                }
            }
        }

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
        final int indexCut = (mX - minx + (mY-miny) *width) * numBand;
        final int lenght = width*height*numBand - indexCut;
        pixIterator.moveTo(mX, mY);
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
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
        setMoveToRITabs(indexCut, lenght);
        int comp = 0;
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }
}
