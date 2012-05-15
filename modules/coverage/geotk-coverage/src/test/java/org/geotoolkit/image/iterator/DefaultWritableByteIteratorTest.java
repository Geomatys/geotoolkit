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
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test DefaultByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultWritableByteIteratorTest extends DefaultByteIteratorTest{

    public DefaultWritableByteIteratorTest() {
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

        BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        setPixelIterator(renderedImage);
        final int length = width*height*numBand;
        tabRef  = new byte[length];
        tabTest = new byte[length];
        int comp = -128;
        int tabPos = 0;
        for (int j = 0; j<height/tilesHeight; j++) {
            for (int i = 0; i<width/tilesWidth; i++) {
                for (int y = 0; y<tilesHeight; y++) {
                    for (int x = 0; x<tilesWidth; x++) {
                        for (int b = 0; b<numBand; b++) {
                            tabRef[tabPos++] = (byte)comp++;
                        }
                    }
                }
                comp=-128;
            }
        }
        comp = -128;
        while (pixIterator.next()) {
            pixIterator.setSample(comp++);
            if (comp == 22) comp = -128;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        sampleM = new BandedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
        setPixelIterator(renderedImage);

        comp = -128;
        while (pixIterator.next()) {
            pixIterator.setSample(comp++);
            if (comp == 22) comp = -128;
        }

        comp = 0;
        pixIterator.rewind();
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    private void fillGoodTabRef(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
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
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftWriteTest() {
        final Rectangle rect = new Rectangle(-10, -20, 40, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightWriteTest() {
        final Rectangle rect = new Rectangle(80, -20, 30, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightWriteTest() {
        final Rectangle rect = new Rectangle(80, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftWriteTest() {
        final Rectangle rect = new Rectangle(-20, 30, 50, 50);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within image area.
     */
    @Test
    public void imageContainsRectWriteTest() {
        final Rectangle rect = new Rectangle(20, 10, 70, 30);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all image area.
     */
    @Test
    public void rectContainsImageWriteTest() {
        final Rectangle rect = new Rectangle(-10, -10, 150, 80);
        minx = 0;
        miny = 0;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        numBand = 3;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, numBand, null);
        setPixelIterator(renderedImage, rect);
        while (pixIterator.next()) pixIterator.setSample(-1);
        fillGoodTabRef(minx, miny, width, height, tilesWidth, tilesHeight, numBand, rect);
        setPixelIterator(renderedImage);
        int comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception if rasters haven't got same criterion.
     */
    @Test
    public void unappropriateRasterTest() {
        Raster rasterRead = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 20, 10, 3, new Point(0,0));
        WritableRaster rasterWrite = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, 200, 100, 30, new Point(3,1));
        //test : different raster dimension.
        try {
            final DefaultWritableByteIterator iter = new DefaultWritableByteIterator(rasterRead, rasterWrite);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
        //test : different datas type.
        rasterWrite = Raster.createBandedRaster(DataBuffer.TYPE_INT, 20, 10, 3, new Point(0,0));
        try {
            final DefaultWritableByteIterator iter = new DefaultWritableByteIterator(rasterRead, rasterWrite);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
    }

    /**
     * Test catching exception if rendered images haven't got same criterion.
     */
    @Test
    public void unappropriateRenderedImageTest(){
        final BandedSampleModel sampleMR = new BandedSampleModel(DataBuffer.TYPE_BYTE, 100, 50, 3);
        final RenderedImage rendReadImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMR, null);

        BandedSampleModel sampleMW = new BandedSampleModel(DataBuffer.TYPE_BYTE, 100, 50, 3);
        WritableRenderedImage rendWriteImage = new TiledImage(0, 0, 100, 500, 15, 25, sampleMW, null);
        //test : different image dimension.
        try {
            final DefaultWritableByteIterator iter = new DefaultWritableByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
        //test : different tiles dimension.
        sampleMW = new BandedSampleModel(DataBuffer.TYPE_BYTE, 10, 5, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            final DefaultWritableByteIterator iter = new DefaultWritableByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
        //test : different datas type.
        sampleMW = new BandedSampleModel(DataBuffer.TYPE_INT, 100, 50, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            final DefaultWritableByteIterator iter = new DefaultWritableByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final Raster raster) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final Raster raster, final Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(raster, (WritableRaster)raster, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage, subArea);
    }
}
