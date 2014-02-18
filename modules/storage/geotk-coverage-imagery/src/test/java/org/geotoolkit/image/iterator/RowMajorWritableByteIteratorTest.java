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
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RowMajorWritableByteIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RowMajorWritableByteIteratorTest extends RowMajorWritableTest {

    /**
     * byte type table wherein is put iterator result.
     */
    private byte[] tabTest;

    /**
     * byte type table wherein expect result is putting.
     */
    private byte[] tabRef;

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        RowMajorByteIteratorTest.setRenderedImgTest(this, minx, miny, width, height, tilesWidth, tilesHeight, numBand, areaIterate);
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
    protected int getDataBufferType() {
        return DataBuffer.TYPE_BYTE;
    }

    /**
     * Test if iterator transverse all rasters positions from image with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    @Override
    public void transversingAllWriteTest() {
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
        final SampleModel sampleM = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, tilesWidth, tilesHeight, numBand, tilesWidth*numBand, bandOffset);
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

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
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

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setTabRefValue(int index, double value) {
        tabRef[index] = (byte) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void createTable(int length) {
        tabRef = new byte[length];
        tabTest = new byte[length];
    }
}
