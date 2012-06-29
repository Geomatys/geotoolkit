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
package org.geotoolkit.image.interpolation;

import java.awt.Rectangle;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define Bilinear Interpolation.
 *
 * Bilinear interpolation is computed from 4 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BilinearInterpolation extends Interpolation {

    /**
     * Create a Bilinear Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BilinearInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
    }

    /**
     * Compute bilinear interpolation
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return pixel interpolated values for each bands.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] mins = getInterpolateMin(x, y, 2, 2);
        int minx = mins[0];
        int miny = mins[1];
        int bands;
        final double[] data = new double[4 * numBands];
        int compteur = 0;
        final double[] result = new double[numBands];

        //get appropriate datas
        for (int idy = miny; idy <= miny+1; idy++) {
            for(int idx = minx; idx <= minx+1; idx++) {
                bands = 0;
                pixelIterator.moveTo(idx, idy);
                while (bands++ != numBands) {
                    pixelIterator.next();
                    data[compteur++] = pixelIterator.getSampleDouble();
                }
            }
        }

        //interpolation
        for (int n=0; n<numBands; n++) {
            result[n] = bilinear(minx, minx + 1, miny, miny + 1, x, y, data[n], data[n + numBands], data[n + 2 * numBands], data[n + 3 * numBands]);
        }
        return result;
    }

    /**
     * Compute and return bilinear interpolation value.
     *
     * @param minx min x interpolation area value.
     * @param maxX max x interpolation area value.
     * @param miny min y interpolation area value.
     * @param maxY max y interpolation area value.
     * @param pixX pixel x coordinate.
     * @param pixY pixel y coordinate.
     * @param iVal different values use to interpolate.
     * @return bilinear interpolation value at pixX, pixY coordinate values.
     */
    private double bilinear(int minx, int maxX, int miny, int maxY, double pixX, double pixY, double...iVal) {
        if (minx == maxX || miny == maxY)
            throw new IllegalArgumentException("impossible to effectuate a bilinear interpolation area = "+new Rectangle(minx, miny, maxX-minx, maxY-miny));
        final double pixmix = pixX - minx;
        final double mixpix = maxX - pixX;
        final double wid = maxX-minx;
        final double r2 = (mixpix*iVal[2] + pixmix*iVal[3])/wid;
        final double r1 = (mixpix*iVal[0] + pixmix*iVal[1])/wid;
        return ((pixY-miny) * r2 + (maxY-pixY) * r1) / (maxY-miny);
    }
}
