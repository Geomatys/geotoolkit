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
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.TiledImage;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Writing-only tests from any PixelIterator.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class WritableIteratorTest extends IteratorTest{

    /**
     * Fill expect iteration table relative to {@link #renderedImage} attributes.
     *
     * @param minx        {@link #renderedImage} X coordinate from upper left corner.
     * @param miny        {@link #renderedImage} Y coordinate from upper left corner.
     * @param width       {@link #renderedImage} width.
     * @param height      {@link #renderedImage} height.
     * @param tilesWidth  {@link #renderedImage} tiles width.
     * @param tilesHeight {@link #renderedImage} tiles height.
     * @param numBand     {@link #renderedImage} band number.
     * @param subArea     {@code Rectangle} which represent {@link #renderedImage} sub area iteration.
     */
    protected abstract void fillGoodTabRef(int minx, int miny, int width, int height,
                    int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate);



    /**
     * Return "writable" {@code PixelIterator} adapted for {@code RenderedImage}.
     *
     * @param renderedImage {@code RenderedImage} which is followed by read only iterator.
     * @param writableRenderedImage {@code RenderedImage} which is followed by write only iterator.
     * @see #unappropriateRenderedImageTest().
     * @return "writable" {@code PixelIterator}.
     */
    protected abstract PixelIterator getWritableRIIterator(RenderedImage renderedImage,
                                    WritableRenderedImage writableRenderedImage);

    public WritableIteratorTest() {
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftWriteTest() {
        final Rectangle rect = new Rectangle(-10, -20, 40, 30);
        minx = -2;
        miny = 4;
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightWriteTest() {
        final Rectangle rect = new Rectangle(80, -20, 30, 50);
        minx = -7;
        miny = 2;
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightWriteTest() {
        final Rectangle rect = new Rectangle(80, 30, 50, 50);
        minx = 3;
        miny = -7;
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within image area.
     */
    @Test
    public void imageContainsRectWriteTest() {
        final Rectangle rect = new Rectangle(20, 10, 70, 30);
        minx = -5;
        miny = 7;
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
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
        while (pixIterator.next()) {
            setTabTestValue(comp++, pixIterator.getSampleDouble());
        }
        assertTrue(compareTab());
    }

    /**
     * Test catching exception if rendered images haven't got same criterion.
     */
    @Test
    public void unappropriateRenderedImageTest() {
        final int dataType = getDataBufferType();
        final BandedSampleModel sampleMR = new BandedSampleModel(dataType, 100, 50, 3);
        final RenderedImage rendReadImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMR, null);

        BandedSampleModel sampleMW = new BandedSampleModel(dataType, 100, 50, 3);
        WritableRenderedImage rendWriteImage = new TiledImage(0, 0, 100, 500, 15, 25, sampleMW, null);

        //test : different image dimension.
        try {
            getWritableRIIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }

        //test : different tiles dimension.
        sampleMW = new BandedSampleModel(dataType, 10, 5, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            getWritableRIIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }

        //test : different datas type.
        final int dataTypeTest = (dataType == DataBuffer.TYPE_INT) ? DataBuffer.TYPE_BYTE : DataBuffer.TYPE_INT;
        sampleMW = new BandedSampleModel(dataTypeTest, 100, 50, 3);
        rendWriteImage = new TiledImage(0, 0, 1000, 500, 0, 0, sampleMW, null);
        try {
            getWritableRIIterator(rendReadImage, rendWriteImage);
            Assert.fail("test should had failed");
        } catch(IllegalArgumentException e) {
            //ok
        }
    }
}
