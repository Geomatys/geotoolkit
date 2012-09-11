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
import java.awt.image.*;
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Implement only row major writable tests.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class RowMajorWritableTest extends WritableIteratorTest {

    /**
     * {@inheritDoc }.
     */
    @Override
    public void differentMinRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectContainsRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerLeftRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerRightRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperLeftRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperRightRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rasterContainsRectReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateRectRasterTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateMoveToRasterTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        //no test about raster for this iterator.
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
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, WritableRenderedImage writableRI, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, writableRI, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected PixelIterator getWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRenderedImage) {
        return PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected PixelIterator getWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRenderedImage, Rectangle subarea) {
        return PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * Fill reference test array with appropriate value from test.
     */
    @Override
    protected void fillGoodTabRef(int minx, int miny, int width, int height,
                    int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final int depy = Math.max(miny, areaIterate.y);
        final int depx = Math.max(minx, areaIterate.x);
        final int endy = Math.min(miny+height, areaIterate.y+areaIterate.height);
        final int endx = Math.min(minx+width, areaIterate.x+areaIterate.width);
        for(int y = depy; y<endy; y++){
            for(int x = depx; x<endx; x++){
                for(int b = 0; b<numBand; b++){
                    setTabRefValue(b + numBand*(x-minx+(y-miny)*width), -1);
                }
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        RowMajorReadTest.setRenderedImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

     /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void transversingAllWriteTest() {

        final int dataType = getDataBufferType();
        double valueRef;
        switch (dataType) {
            case DataBuffer.TYPE_FLOAT : valueRef = -2000.5;break;
            default : valueRef = 0;break;
        }

        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;

        final int[] bandOffset = new int[numBand];
        for (int i = 0;i<numBand; i++) {
            bandOffset[i] = i;
        }
        final SampleModel sampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        setPixelIterator(renderedImage);
        final int length = width*height*numBand;
        createTable(length);
        double comp = valueRef;
        int compteur = 0;
        while (pixIterator.next()) {
            pixIterator.setSampleDouble(comp);
            setTabRefValue(compteur++, comp++);
        }
        pixIterator.rewind();
        compteur = 0;
        while (pixIterator.next()) {
            setTabTestValue(compteur++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        setPixelIterator(renderedImage);

        compteur = 0;
        comp = valueRef;
        while (pixIterator.next()) {
            pixIterator.setSampleDouble(comp);
            setTabRefValue(compteur++, comp++);
        }

        comp = 0;
        compteur = 0;
        pixIterator.rewind();
        while (pixIterator.next()) {
            setTabTestValue(compteur++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
        ////////////Test rewind//////////////
        pixIterator.rewind();
        while (pixIterator.next()) {
            pixIterator.setSampleDouble(comp);
            setTabRefValue(length-compteur--, comp--);
        }
        pixIterator.rewind();
        compteur = 0;
        while (pixIterator.next()) {
            setTabTestValue(compteur++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }
}
