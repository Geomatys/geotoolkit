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
import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import javax.media.jai.TiledImage;

/**
 *
 * @author rmarech
 */
public class App {

    public static void main( String[] args ) {

        int numBand = 3;
        int width = 10;
        int height = 10;
        int dataType = DataBuffer.TYPE_INT;
        int minX = 0;
        int minY = 0;
        Rectangle subArea = new Rectangle(8, 4, 5, 2);
        WritableRaster raster = Raster.createBandedRaster(dataType, width, height, numBand, new Point(minX, minY));
        int comp = 0;
        for (int y = 0; y<height; y++) {
            for (int x = 0; x<width; x++) {
                for (int b = 0; b<numBand; b++) {
                    raster.setSample(minX+x, minY+y, b, comp);
                    comp++;
                }
            }
        }

        final BandedSampleModel sampleM = new BandedSampleModel(DataBuffer.TYPE_BYTE, /*tilesWidth*/5, /*tilesHeight*/5, 3);
        TiledImage renderedImage = new TiledImage(0, 0, 10, 10, 0, 0, sampleM, null);

        PixelIterator pixIt = PixelIteratorFactory.createRowMajorIterator(renderedImage);

        int val = 0;

        for(int y = 0; y<10; y++){
            for(int x = 0; x<10; x++){
                for(int b = 0; b <3;b++){
                    renderedImage.setSample(x, y, b, val);
                }
                val++;
            }
        }

        while (pixIt.next()) {
            System.out.println(pixIt.getSample());
        }

//        final PixelIterator sI = new DefaultRasterIntIterator(raster, subArea);
//        int w = Math.min(subArea.x+subArea.width,  minX+width)  - Math.max(subArea.x, minX);
//        int h = Math.min(subArea.y+subArea.height, minY+height) - Math.max(subArea.y, minY);
//        int sIcomp = 0;
//        int[] tab = new int[numBand * w * h];
//        while (sI.next()) {
//            if(sIcomp ==11){
//                System.out.println("");
//            }
//            tab[sIcomp++] = sI.getSample();
//
//        }
//        System.out.println("size = "+tab.length);
//        System.out.println(Arrays.toString(tab));

    }
}
