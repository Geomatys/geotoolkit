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
import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test RasterBasedIterator.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class RasterBasedIteratorTest {

    private int numBand;
    private int width;
    private int height;
    private int dataType = DataBuffer.TYPE_INT;
    private int minX;
    private int minY;

    WritableRaster raster;
    PixelIterator sampleIterator;

    public RasterBasedIteratorTest() {

    }

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind fonction.
     */
    @Test
    public void differentMinTest() {
        numBand = 3;
        width = 10;
        height = 10;
        minX = 0;
        minY = 0;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
        sampleIterator = new RasterBasedIterator(raster);
        final int[] tabA = getIterate();

        minX = 3;
        minY = 5;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
        sampleIterator = new RasterBasedIterator(raster);
        final int[] tabB = getIterate();
        assertTrue(compareTab(tabA, tabB));

        minX = -3;
        minY = 5;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
        sampleIterator = new RasterBasedIterator(raster);
        final int[] tabC = getIterate();
        assertTrue(compareTab(tabB, tabC));

        minX = 3;
        minY = -5;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
        sampleIterator = new RasterBasedIterator(raster);
        final int[] tabD = getIterate();
        assertTrue(compareTab(tabC, tabD));

        minX = -3;
        minY = -5;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
        sampleIterator = new RasterBasedIterator(raster);
        final int[] tabE = getIterate();
        assertTrue(compareTab(tabD, tabE));

        sampleIterator.rewind();
        final int[] tabF = getIterate();
        assertTrue(compareTab(tabE, tabF));
    }

    /**
     * Affect appropriate value for rectangle test.
     */
    private void setRasterRectTest() {
        numBand = 3;
        width = 20;
        height = 10;
        minX = 5;
        minY = 7;
        raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        fillRaster();
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(4, 6, 5, 4);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[36];
        for (int y = 7; y<10; y++) {
            for (int x = 5; x<9; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[36];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(16, 6, 10, 6);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[135];
        for (int y = 7; y<12; y++) {
            for (int x = 16; x<25; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[135];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(14, 10, 15, 9);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[264];
        for (int y = 10; y<17; y++) {
            for (int x = 14; x<25; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[264];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(2, 12, 10, 6);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[126];
        for (int y = 12; y<17; y++) {
            for (int x = 5; x<12; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[126];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within raster's area.
     */
    @Test
    public void rasterContainsRectTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(10, 9, 11, 6);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[198];
        for (int y = 9; y<15; y++) {
            for (int x = 10; x<21; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[198];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all raster's area.
     */
    @Test
    public void rectContainsRasterTest() {
        setRasterRectTest();
        final Rectangle2D rect = new Rectangle2D.Double(2, 3, 25, 17);
        sampleIterator = new RasterBasedIterator(raster, rect);
        int comp = 0;
        final int[] tabA = new int[600];
        for (int y = 7; y<17; y++) {
            for (int x = 5; x<25; x++) {
                for (int b = 0; b < numBand; b++) {
                    tabA[comp] = b + 3*((x-5) + 20*(y-7));
                    comp++;
                }
            }
        }
        final int[] tabB = new int[600];
        int comp2 = 0;
        while (sampleIterator.hasNext()) tabB[comp2++] = sampleIterator.nextSample();
        assertTrue(compareTab(tabA, tabB));
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectTest() {
        final Rectangle2D rect = new Rectangle2D.Double(7, 20, 25, 17);
        boolean testTry = false;
        try{
            sampleIterator = new RasterBasedIterator(raster, rect);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }


    /**
     * Fill raster with apppropriate test value.
     */
    private void fillRaster() {
        int comp = 0;
        for (int y = 0; y<height; y++) {
            for (int x = 0; x<width; x++) {
                for (int b = 0; b<numBand; b++) {
                    raster.setSample(minX + x, minY + y, b, comp);
                    comp++;
                }
            }
        }
    }

    /**
     * @return int table which represent all values obtained by raster iterate.
     */
    private int[] getIterate() {
        int sIcomp = 0;
        int[] tab = new int[numBand * width * height];
        for (int y = minY; y<minY+height; y++) {
            for (int x = minX; x<minX+width; x++) {
                for (int n = 0; n<numBand; n++) {
                    assertTrue(sampleIterator.nextX() == x);
                    assertTrue(sampleIterator.nextY() == y);
                    assertTrue(sampleIterator.hasNext());
                    tab[sIcomp] = sampleIterator.nextSample();
                    sIcomp++;
                }
            }
        }
        assertFalse(sampleIterator.hasNext());
        return tab;
    }

    /**
     * Compare 2 int table.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    private boolean compareTab(int[] tabA, int[] tabB) {
        int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }

}
