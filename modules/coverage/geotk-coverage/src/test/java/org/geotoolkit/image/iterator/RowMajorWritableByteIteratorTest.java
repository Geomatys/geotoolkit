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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.TiledImage;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RowMajorWritableByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorWritableByteIteratorTest extends RowMajorByteIteratorTest{


    /**
     * Test if iterator transverse all rasters positions from image with different minX and maxY coordinates.
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

        for (int y = miny; y<miny+height; y++) {
            for (int x = minx; x<minx+width; x++) {
                for (int b = 0; b<numBand; b++) {
                    tabRef[tabPos++] = (byte) comp;
                }
                if (++comp == 122) comp = -128;
            }
        }
        comp = -128;
        int compteur = 0;
        while (pixIterator.next()) {
            pixIterator.setSample(comp);
            if (++compteur == numBand) {
                comp++;
                compteur = 0;
            }
            if (comp == 122) comp = -128;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
//
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
        compteur = 0;
        while (pixIterator.next()) {
            pixIterator.setSample(comp);
            if (++compteur == numBand) {
                comp++;
                compteur = 0;
            }
            if (comp == 122) comp = -128;
        }
        pixIterator.rewind();
        comp = 0;
        while (pixIterator.next()) tabTest[comp++] = (byte)pixIterator.getSample();
        assertTrue(compareTab(tabTest, tabRef));
    }

    private void fillGoodTabRef(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        int depy = Math.max(miny, areaIterate.y);
        int depx = Math.max(minx, areaIterate.x);
        int endy = Math.min(miny + height, areaIterate.y + areaIterate.height);
        int endx = Math.min(minx + width, areaIterate.x + areaIterate.width);
        int pos;
        for(int y = depy; y<endy; y++){
            for(int x = depx; x<endx; x++){
                for(int b = 0; b<numBand; b++){
                    pos = (x-minx + (width * (y-miny))) * numBand+b;
                    tabRef[pos] = -1;
                }
            }
        }
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left image corner.
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
     * Area is defined on upper right image corner.
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
     * Area is defined on lower right image corner.
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
     * Area is defined on lower left image corner.
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
            final RowMajorWritableDirectByteIterator iter = new RowMajorWritableDirectByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
        //test : different tiles dimension.
        sampleMW = new BandedSampleModel(DataBuffer.TYPE_BYTE, 10, 5, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            final RowMajorWritableDirectByteIterator iter = new RowMajorWritableDirectByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
        //test : different datas type.
        sampleMW = new BandedSampleModel(DataBuffer.TYPE_INT, 100, 50, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            final RowMajorWritableDirectByteIterator iter = new RowMajorWritableDirectByteIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
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
}
