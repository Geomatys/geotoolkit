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
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Implement only Default writable tests.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class DefaultWritableTest extends WritableIteratorTest {

    /**
     * {@inheritDoc }
     */
    @Override
    protected PixelIterator getWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRenderedImage) {
        return PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void setPixelIterator(Raster raster) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void setPixelIterator(Raster raster, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster, subArea);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        DefaultReadTest.setRasterTest(this, minx, miny, width, height, numBand, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        DefaultReadTest.setRenderedImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
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
                    setTabRefValue(pos, -1);
                }
            }
        }
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
            case DataBuffer.TYPE_BYTE  : valueRef = -128;break;
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

        //SampleModel sampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
        SampleModel sampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, new int[]{0, 1, 2});
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        setPixelIterator(renderedImage);
        final int length = width*height*numBand;
        final int rasterBulk = tilesWidth * tilesHeight * numBand;
        createTable(length);
        double comp = valueRef;
        int tabPos = 0;
        for (int j = 0; j<height/tilesHeight; j++) {
            for (int i = 0; i<width/tilesWidth; i++) {
                for (int y = 0; y<tilesHeight; y++) {
                    for (int x = 0; x<tilesWidth; x++) {
                        for (int b = 0; b<numBand; b++) {
                            setTabRefValue(tabPos++, comp++);
                        }
                    }
                }
                comp = valueRef;
            }
        }
        comp = valueRef;
        while (pixIterator.next()) {
            pixIterator.setSampleDouble(comp++);
            if (comp == valueRef+rasterBulk) comp = valueRef;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) {
            setTabTestValue((int)comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
//        sampleM = new BandedSampleModel(dataType, tilesWidth, tilesHeight, numBand);
        sampleM = new PixelInterleavedSampleModel(dataType, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, new int[]{0, 1, 2});
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        setPixelIterator(renderedImage);

        comp = valueRef;
        while (pixIterator.next()) {
            pixIterator.setSampleDouble(comp++);
            if (comp == valueRef+rasterBulk) comp = valueRef;
        }

        comp = 0;
        pixIterator.rewind();
        while (pixIterator.next()) {
            setTabTestValue((int)comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }

    /**
     * Test catching exception if rasters haven't got same criterion.
     */
    @Test
    public void unappropriateRasterTest() {
        final int databuffer = getDataBufferType();
        //Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, numBand, new Point(minx, miny));
        //Raster.createInterleavedRaster(databuffer, 20, 1, numBand, new Point(minx, miny));
        final Raster rasterRead = RasterFactory.createInterleavedRaster(databuffer, 20, 10, 3, new Point(0,0));
        WritableRaster rasterWrite = RasterFactory.createInterleavedRaster(databuffer, 200, 100, 30, new Point(3,1));

        //test : different raster dimension.
        try {
            PixelIteratorFactory.createDefaultWriteableIterator(rasterRead, rasterWrite);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }

        //test : different datas type.
        final int dataBufferTest = (databuffer == DataBuffer.TYPE_INT) ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT;
        rasterWrite = Raster.createBandedRaster(dataBufferTest, 20, 10, 3, new Point(0,0));
        try {
            PixelIteratorFactory.createDefaultWriteableIterator(rasterRead, rasterWrite);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
    }
}
