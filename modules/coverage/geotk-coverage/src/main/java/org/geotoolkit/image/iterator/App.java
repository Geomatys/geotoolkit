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
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

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
        int minX = 2;
        int minY = 5;

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

        final PixelIterator sI = new RasterBasedIterator(raster);

        int sIcomp = 0;
        int[] tab = new int[numBand * width];
        while (sI.next()) {
            tab[sIcomp] = sI.getSample();
            sIcomp++;
            if (sIcomp == numBand * width) {
                System.out.println(Arrays.toString(tab));
                sIcomp = 0;
            }
        }

    }
}
