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
public class DefaultWritableIteratorTest extends DefaultIteratorTest {



    public DefaultWritableIteratorTest() {

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
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    private void fillGoodTabRef(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        int depy = Math.max(miny, areaIterate.y);
        int depx = Math.max(minx, areaIterate.x);
        int endy = Math.min(miny+height, areaIterate.y+areaIterate.height);
        int endx = Math.min(minx+width, areaIterate.x+areaIterate.width);
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
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
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
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
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
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
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
        while (pixIterator.next()) tabTest[comp++] = pixIterator.getSample();
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
     * Test catching exception if rendered images haven't got same criterion.
     */
    @Test
    public void unappropriateRenderedImageTest(){
        final BandedSampleModel sampleMR = new BandedSampleModel(DataBuffer.TYPE_INT, 100, 50, 3);
        final RenderedImage rendReadImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMR, null);

        BandedSampleModel sampleMW = new BandedSampleModel(DataBuffer.TYPE_INT, 100, 50, 3);
        WritableRenderedImage rendWriteImage = new TiledImage(0, 0, 100, 500, 15, 25, sampleMW, null);
        boolean test = false;
        try {
            final DefaultWritableIterator iter = new DefaultWritableIterator(rendReadImage, rendWriteImage);
        } catch(Exception e) {
            test = true;
        }
        assertTrue(test);

        test = false;
        sampleMW = new BandedSampleModel(DataBuffer.TYPE_INT, 10, 5, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            final DefaultWritableIterator iter = new DefaultWritableIterator(rendReadImage, rendWriteImage);
        } catch(Exception e) {
            test = true;
        }
        assertTrue(test);
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
}
