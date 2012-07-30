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
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;

/**
 * Test DefaultWritableByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultWritableByteIteratorTest extends DefaultWritableTest {

    /**
     * byte type table wherein is put iterator result.
     */
    private byte[] tabTest;

    /**
     * byte type table wherein expect result is putting.
     */
    private byte[] tabRef;

    public DefaultWritableByteIteratorTest() {
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabTestValue(int index, double value) {
        tabTest[index] = (byte) value;
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
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        DefaultByteIteratorTest.setRenderedImgByteTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected int getDataBufferType() {
        return DataBuffer.TYPE_BYTE;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        DefaultByteIteratorTest.setRasterByteTest(this, minx, miny, width, height, numBand, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected PixelIterator getWritableRIIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return PixelIteratorFactory.createDefaultWriteableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void createTable(final int length){
        tabRef = new byte[length];
        tabTest = new byte[length];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabRefValue(int index, double value) {
        tabRef[index] = (byte) value;
    }
}
