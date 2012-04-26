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
import javax.media.jai.TiledImage;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test DefaultRenderedImageIterator class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class DefaultRenderedImageIteratorTest {

    TiledImage renderedImage;
    int[] tabRef, tabTest;

    public DefaultRenderedImageIteratorTest() {

    }

    private void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand) {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        int comp = 0;
        tabRef = new int[width*height*numBand];

        for(int y = miny, ly = miny+height; y<ly; y++){
            for(int x = minx, lx = minx + width; x<lx; x++){
                for(int b = 0; b<numBand; b++){
                    renderedImage.setSample(x, y, b, comp);
                    comp++;
                }
            }
        }

        comp = 0;
        for(int tileY = 0; tileY<height/tilesHeight; tileY++){
            for(int tileX = 0; tileX<width/tilesWidth; tileX++){
                //tile by tile
                for(int y = 0; y<tilesHeight; y++){
                    for(int x = 0; x<tilesWidth; x++){
                        for(int b = 0; b<numBand; b++){
                            tabRef[comp] =  b + numBand * (x + tilesWidth*tileX + width * (y + tilesHeight*tileY));
                            comp++;
                        }
                    }
                }

            }
        }
    }

    private void setRenderedImgTest(int minx, int miny, int width, int height, int tilesWidth, int tilesHeight, int numBand, Rectangle areaIterate) {
        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_INT, tilesWidth, tilesHeight, numBand);
        renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);

        int comp = 0;
        for(int y = miny, ly = miny+height; y<ly; y++){
            for(int x = minx, lx = minx + width; x<lx; x++){
                for(int b = 0; b<numBand; b++){
                    renderedImage.setSample(x, y, b, comp);
                    comp++;
                }
            }
        }

        int cULX, cULY, cBRX, cBRY, minIX = 0, minIY = 0, maxIX = 0, maxIY = 0;
        int tileMinX, tileMinY, tileMaxX, tileMaxY;
        int rastminY, rastminX, rastmaxY, rastmaxX, depX, depY, endX, endY, tabLenght;

        if (areaIterate != null) {
            cULX = areaIterate.x;
            cULY = areaIterate.y;
            cBRX = cULX + areaIterate.width;
            cBRY = cULY + areaIterate.height;
            minIX = Math.max(cULX, minx);
            minIY = Math.max(cULY, miny);
            maxIX = Math.min(cBRX, minx + width);
            maxIY = Math.min(cBRY, miny + height);
            tabLenght = Math.abs((maxIX-minIX)*(maxIY-minIY)) *numBand;
            tileMinX = (minIX - minx) / tilesWidth;
            tileMinY = (minIY - miny) / tilesHeight;
            tileMaxX = (maxIX - minx) / tilesWidth;
            tileMaxY = (maxIY - miny) / tilesHeight;
        } else {
            tileMinX = tileMinY = 0;
            tileMaxX = width/tilesWidth;
            tileMaxY = height/tilesHeight;
            tabLenght = width*height*numBand;
        }

        tabRef  = new int[tabLenght];
        tabTest = new int[tabLenght];

        comp = 0;
        for (int tileY = tileMinY; tileY<tileMaxY; tileY++) {
            rastminY = tileY * tilesHeight;
            rastmaxY = rastminY + tilesHeight;
            for (int tileX = tileMinX; tileX<tileMaxX; tileX++) {
                //tile by tile
                rastminX = tileX * tilesWidth;
                rastmaxX = rastminX + tilesWidth;
                if (areaIterate == null) {
                    depX = rastminX;
                    depY = rastminY;
                    endX = rastmaxX;
                    endY = rastmaxY;
                } else {
                    depX = Math.max(rastminX, minIX);
                    depY = Math.max(rastminY, minIY);
                    endX = Math.min(rastmaxX, maxIX);
                    endY = Math.min(rastmaxY, maxIY);
                }

                for (int y = depY; y<endY; y++) {
                    for (int x = depX; x<endX; x++) {
                        for (int b = 0; b<numBand; b++) {
//                            System.out.println(comp+" : "+(b + numBand * ((x-depX) + tilesWidth*tileX + width * ((y-depY) + tilesHeight*tileY))));
                            tabRef[comp] =  b + numBand * ((x-depX) + tilesWidth*tileX + width * ((y-depY) + tilesHeight*tileY));
                            comp++;
                        }
                    }
                }

            }
        }
    }

    /**
     * Test if iterator transverse all raster positions with different minX and maxY coordinates.
     * Also test rewind function.
     */
    @Test
    public void transversingAllTest() {
        int minx = 0;
        int miny = 0;
        int width = 100;
        int height = 50;
        int tilesWidth = 10;
        int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        minx = 1;
        miny = -50;
        width = 100;
        height = 50;
        tilesWidth = 10;
        tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, null);
        dfrii = new DefaultRenderedImageIterator(renderedImage);

        comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

        dfrii.rewind();
        comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));

    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper left raster corner.
     */
    @Test
    public void rectUpperLeftTest() {
        final Rectangle rect = new Rectangle(-10, -20, 40, 30);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on upper right raster corner.
     */
    @Test
    public void rectUpperRightTest() {
        final Rectangle rect = new Rectangle(80, -20, 30, 50);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower right raster corner.
     */
    @Test
    public void rectLowerRightTest() {
        final Rectangle rect = new Rectangle(80, 30, 50, 50);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is defined on lower left raster corner.
     */
    @Test
    public void rectLowerLeftTest() {
        final Rectangle rect = new Rectangle(-20, 30, 50, 50);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area is within raster area.
     */
    @Test
    public void rasterContainsRectTest() {
        final Rectangle rect = new Rectangle(20, 10, 70, 30);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test if iterator transverse expected value in define area.
     * Area contains all raster area.
     */
    @Test
    public void rectContainsRasterTest() {
        final Rectangle rect = new Rectangle(-10, -10, 150, 80);
        final int minx = 0;
        final int miny = 0;
        final int width = 100;
        final int height = 50;
        final int tilesWidth = 10;
        final int tilesHeight = 5;
        setRenderedImgTest(minx, miny, width, height, tilesWidth, tilesHeight, 3, rect);
        final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);

        int comp = 0;
        while (dfrii.next()) {
            tabTest[comp] = dfrii.getSample();
            comp++;
        }
        assertTrue(compareTab(tabTest, tabRef));
    }

    /**
     * Test catching exception with rectangle which don't intersect raster area.
     */
    @Test
    public void unappropriateRectTest() {
        final Rectangle rect = new Rectangle(-100, -50, 5, 17);
        boolean testTry = false;
        try{
            final DefaultRenderedImageIterator dfrii = new DefaultRenderedImageIterator(renderedImage, rect);
        }catch(Exception e){
            testTry = true;
        }
        assertTrue(testTry);
    }

    /**
     * Compare 2 integer table.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    private boolean compareTab(int[] tabA, int[] tabB) {
        final int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }

}
