/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import java.util.Arrays;
import javax.media.jai.RasterFactory;
import javax.media.jai.TiledImage;
import org.geotoolkit.image.interpolation.BiCubicInterpolation;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author rmarech
 */
public class App {

    public static void main( String[] args ) {


//        int minx = -5;
//        int miny = 5;
//        int width = 100;//maxx = 95
//        int height = 50;//maxy=55
//        int tilesWidth = 10;
//        int tilesHeight = 5;
//        int numBand = 3;
//        int dataType = DataBuffer.TYPE_INT;
//
//        final BandedSampleModel sampleM = new BandedSampleModel(dataType, tilesWidth, tilesHeight, numBand);
//        TiledImage renderedImage = new TiledImage(minx, miny, width, height, minx+tilesWidth, miny+tilesHeight, sampleM, null);
//
//        int comp;
//        int nbrTX = width/tilesWidth;
//        int nbrTY = height/tilesHeight;
//        double valueRef = (dataType == DataBuffer.TYPE_FLOAT) ? -200.5 : 0;
//        for(int j = 0;j<nbrTY;j++){
//            for(int i = 0; i<nbrTX;i++){
//                for (int y = miny+j*tilesHeight, ly = y+tilesHeight; y<ly; y++) {
//                    for (int x = minx+i*tilesWidth, lx = x + tilesWidth; x<lx; x++) {
//                        for (int b = 0; b<numBand; b++) {
//                            renderedImage.setSample(x, y, b, valueRef++);
//                        }
//                    }
//                }
//            }
//        }
//
//
//        ////////////test upper left/////////////////
//
//        Rectangle rect = new Rectangle(-10, -20, 10, 30);
//
//        PixelIterator pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        int areaMinX = Math.max(minx, rect.x);
//        int areaMinY = Math.max(miny, rect.y);
//        int areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        int areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        int widthArea = areaMaxX - areaMinX;
//        int heightArea = areaMaxY - areaMinY;
//
//        double[] tabRef  = new double[widthArea*heightArea*numBand];
//        double[] tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        int tX = (areaMinX-minx)/tilesWidth;
//        int tY = (areaMinY-miny)/tilesHeight;
//        int tMaxX = (areaMaxX-minx)/tilesWidth;
//        int tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        int rasterMinX, rasterMinY, rasterMaxX, rasterMaxY, depX, depY, endX, endY;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (;tX<=tMaxX; tX++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tX*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tX*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            System.out.println(comp);
//            if(comp == 74){
//                System.out.println("");
//            }
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        String test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test upper left = "+test);
//
//
//        ////////////test upper right/////////////////
//
//        rect = new Rectangle(90, -20, 10, 30);
//
//        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        areaMinX = Math.max(minx, rect.x);
//        areaMinY = Math.max(miny, rect.y);
//        areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        widthArea = areaMaxX - areaMinX;
//        heightArea = areaMaxY - areaMinY;
//
//        tabRef  = new double[widthArea*heightArea*numBand];
//        tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        tX = (areaMinX-minx)/tilesWidth;
//        tY = (areaMinY-miny)/tilesHeight;
//        tMaxX = (areaMaxX-minx)/tilesWidth;
//        tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (;tX<=tMaxX; tX++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tX*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tX*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test upper right = "+test);
//
//        ////////////test lower right/////////////////
//
//        rect = new Rectangle(90, 52, 10, 30);
//
//        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        areaMinX = Math.max(minx, rect.x);
//        areaMinY = Math.max(miny, rect.y);
//        areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        widthArea = areaMaxX - areaMinX;
//        heightArea = areaMaxY - areaMinY;
//
//        tabRef  = new double[widthArea*heightArea*numBand];
//        tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        tX = (areaMinX-minx)/tilesWidth;
//        tY = (areaMinY-miny)/tilesHeight;
//        tMaxX = (areaMaxX-minx)/tilesWidth;
//        tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (;tX<=tMaxX; tX++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tX*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tX*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test lower right = "+test);
//
//        ////////////test lower left/////////////////
//
//        rect = new Rectangle(-10, 52, 10, 30);
//
//        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        areaMinX = Math.max(minx, rect.x);
//        areaMinY = Math.max(miny, rect.y);
//        areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        widthArea = areaMaxX - areaMinX;
//        heightArea = areaMaxY - areaMinY;
//
//        tabRef  = new double[widthArea*heightArea*numBand];
//        tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        tX = (areaMinX-minx)/tilesWidth;
//        tY = (areaMinY-miny)/tilesHeight;
//        tMaxX = (areaMaxX-minx)/tilesWidth;
//        tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (;tX<=tMaxX; tX++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tX*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tX*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test lower left = "+test);
//
//
//        ////////////test within raster/////////////////
//
//        rect = new Rectangle(-4, 53, 9, 3);
//
//        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        areaMinX = Math.max(minx, rect.x);
//        areaMinY = Math.max(miny, rect.y);
//        areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        widthArea = areaMaxX - areaMinX;
//        heightArea = areaMaxY - areaMinY;
//
//        tabRef  = new double[widthArea*heightArea*numBand];
//        tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        tX = (areaMinX-minx)/tilesWidth;
//        tY = (areaMinY-miny)/tilesHeight;
//        tMaxX = (areaMaxX-minx)/tilesWidth;
//        tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (;tX<=tMaxX; tX++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tX*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tX*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test within raster = "+test);
//
//        ////////////test within /////////////////
//
//        rect = new Rectangle(2, 42, 10, 9);
//
//        pixIterator = PixelIteratorFactory.createDefaultIterator(renderedImage, rect);
//
//        areaMinX = Math.max(minx, rect.x);
//        areaMinY = Math.max(miny, rect.y);
//        areaMaxX = Math.min(minx+width, rect.x+rect.width);
//        areaMaxY = Math.min(miny+height, rect.y+rect.height);
//
//        widthArea = areaMaxX - areaMinX;
//        heightArea = areaMaxY - areaMinY;
//
//        tabRef  = new double[widthArea*heightArea*numBand];
//        tabTest = new double[widthArea*heightArea*numBand];
//
//
//        ////////////////////remplir tabRef/////////////////
//
//        tX = (areaMinX-minx)/tilesWidth;
//        tY = (areaMinY-miny)/tilesHeight;
//        tMaxX = (areaMaxX-minx)/tilesWidth;
//        tMaxY = (areaMaxY-miny)/tilesHeight;
//
//        comp = 0;
//        for (;tY <= tMaxY; tY++) {
//            for (int tx = tX;tx<=tMaxX; tx++) {
//
//                ///pour chaque raster on determine la zone d'iteration
//                rasterMinX = minx+tx*tilesWidth;
//                rasterMinY = miny+tY*tilesHeight;
//                rasterMaxX = rasterMinX + tilesWidth;
//                rasterMaxY = rasterMinY + tilesHeight;
//
//                ///zone d'iteration
//                depX = Math.max(rasterMinX, areaMinX);
//                depY = Math.max(rasterMinY, areaMinY);
//                endX = Math.min(rasterMaxX, areaMaxX);
//                endY = Math.min(rasterMaxY, areaMaxY);
//
//                for (;depY < endY; depY++) {
//                    for (int x = depX; x < endX; x++) {
//                        for (int b = 0;b<numBand;b++) {
//                            tabRef[comp++] = b + (x-rasterMinX)*numBand + (depY-rasterMinY)*tilesWidth*numBand + tx*tilesHeight*tilesWidth*numBand + tY*(width/tilesWidth)*tilesHeight*tilesWidth*numBand;///
//
//                        }
//                    }
//                }
//            }
//        }
//
//        comp = 0;
//        while (pixIterator.next()) {
//            tabTest[comp++] = pixIterator.getSampleDouble();
//        }
//
//        test = (compareTab(tabRef, tabTest)) ? "TRUE ;-)" : "FALSE ;-(";
//        System.out.println("test within multiraster = "+test);

        int width = 4;
        int heigth = 1;
        int minx = 0;
        int miny = 0;
        int val = 216;
        int comptab = 0;
        double[] tabTest = new double[4];
        WritableRaster rastertest = RasterFactory.createBandedRaster(DataBuffer.TYPE_DOUBLE, width, heigth, 1, new Point(0, 0));
//        for (int y = miny; y < miny + heigth; y++) {
//            for (int x = minx; x < minx + width; x++) {
//                rastertest.setSample(x, y, 0, val);
//                tabTest[comptab++] = val++;
//            }
//        }

        rastertest.setSample(0, 0, 0, 105);
        rastertest.setSample(1, 0, 0, -10);
        rastertest.setSample(2, 0, 0, 36);
        rastertest.setSample(3, 0, 0, 514);

        tabTest[0] = 105;
        tabTest[1] = -10;
        tabTest[2] = 36;
        tabTest[3] = 514;

        PixelIterator pixIterator = PixelIteratorFactory.createDefaultIterator(rastertest);

        BiCubicInterpolation bicub = new BiCubicInterpolation(pixIterator, false);


//        double t0 = bicub.cubicInterpol(tabTest[0], tabTest[1], tabTest[2], tabTest[3], 0);
//        double t1 = bicub.cubicInterpol(tabTest[0], tabTest[1], tabTest[2], tabTest[3], 1);
//        double t2 = bicub.cubicInterpol(tabTest[0], tabTest[1], tabTest[2], tabTest[3], 2);
//        double t3 = bicub.cubicInterpol(tabTest[0], tabTest[1], tabTest[2], tabTest[3], 3);

//        for (double t = 0; t<=3.0;t+=0.1) {
//            System.out.println("interpol t"+t+" = "+bicub.cubicInterpol(tabTest[0], tabTest[1], tabTest[2], tabTest[3], t));
//        }

//        System.out.println("interpol to = "+t0);
//        System.out.println("interpol t1 = "+t1);
//        System.out.println("interpol t2 = "+t2);
//        System.out.println("interpol t3 = "+t3);

    }


    /**
     * Compare 2 double table.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    public static boolean compareTab(double[] tabA, double[] tabB) {
        int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }
}
