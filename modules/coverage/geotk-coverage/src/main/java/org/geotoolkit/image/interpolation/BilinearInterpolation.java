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
     * Return appropriate interpolation minX and minY coordinates from x, y interpolate coordinates.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param width interpolate area width.
     * @param height interpolate area height.
     * @throws IllegalArgumentException if there are necessary pixels out of boundary.
     * @return appropriate interpolation minX and minY coordinates.
     */
    private int[] getInterpolateMin(double x, double y, int width, int height) {
        assert (width <= boundary.width && height <= boundary.height) : "area dimensions are out of boundary";
        int minx = (int) x;
        int miny = (int) y;
        if (x<minx) minx--;
        if (y<miny) miny--;

        //ajust area interpolation on x, y center.
        for (int i = 0; i<width/2-1;i++) {
            minx--;
        }
        for (int i = 0; i<height/2-1;i++) {
            miny--;
        }
        while(minx+width >= boundary.x+boundary.width) {
            minx--;
        }
        while(miny+height >= boundary.y+boundary.height) {
            miny--;
        }
        /*
         * Test if interpolate area is within iterate object boundary
         */
        if (!boundary.contains(minx, miny))
            throw new IllegalArgumentException("interpolate definition domain out of boundary");
        return new int[]{minx, miny};
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

    /**
     * {@inheritDoc }.
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        if (minMax != null) {
            if (area == null && precMinMax == null) return minMax;
            if (area.equals(precMinMax))            return minMax;
        }
        //compute minMax values
        minMax = new double[6*numBands];
        int band = 0;
        double value;
        if (area == null) {//iterate on all image
            pixelIterator.rewind();
            //first iteration
            for (;band<numBands; band++) {
                pixelIterator.next();
                value = pixelIterator.getSampleDouble();
                //min value, x, y coordinates
                minMax[6*band]     = value;
                minMax[6*band + 1] = pixelIterator.getX();
                minMax[6*band + 2] = pixelIterator.getX();
                //max value, x, y coordinates
                minMax[6*band + 3]     = value;
                minMax[6*band + 4] = pixelIterator.getX();
                minMax[6*band + 5] = pixelIterator.getX();
            }
            band = 0;
            while (pixelIterator.next()) {
                value = pixelIterator.getSampleDouble();
                if (value < minMax[6*band]) {
                    //min value, x, y coordinates
                    minMax[6*band]     = value;
                    minMax[6*band + 1] = pixelIterator.getX();
                    minMax[6*band + 2] = pixelIterator.getX();
                }
                if (value > minMax[6*band + 3]) {
                    //max value, x, y coordinates
                    minMax[6*band + 3] = value;
                    minMax[6*band + 4] = pixelIterator.getX();
                    minMax[6*band + 5] = pixelIterator.getX();
                }
                if (++band == numBands) band = 0;
            }
        } else {//iterate within rectangle.
            if (!getBoundary().contains(area))
                throw new IllegalArgumentException("impossible to define min and max in area out of Iterate object boundary");
            pixelIterator.moveTo(area.x, area.y);
            for (;band<numBands; band++) {
                pixelIterator.next();
                value = pixelIterator.getSampleDouble();
                //min value, x, y coordinates
                minMax[6*band]     = value;
                minMax[6*band + 1] = pixelIterator.getX();
                minMax[6*band + 2] = pixelIterator.getX();
                //max value, x, y coordinates
                minMax[6*band + 3] = value;
                minMax[6*band + 4] = pixelIterator.getX();
                minMax[6*band + 5] = pixelIterator.getX();
            }
            band = 0;
            for (int y = area.y; y<area.y + area.height; y++) {
                for (int x = area.x; x<area.x + area.width; x++) {
                    pixelIterator.moveTo(x, y);
                    for (;band<numBands; band++) {
                        pixelIterator.next();
                        value = pixelIterator.getSampleDouble();
                        if (value < minMax[6*band]) {
                            //min value, x, y coordinates
                            minMax[6*band]     = value;
                            minMax[6*band + 1] = pixelIterator.getX();
                            minMax[6*band + 2] = pixelIterator.getX();
                        }
                        if (value > minMax[6*band + 3]) {
                            //max value, x, y coordinates
                            minMax[6*band + 3] = value;
                            minMax[6*band + 4] = pixelIterator.getX();
                            minMax[6*band + 5] = pixelIterator.getX();
                        }
                    }
                }
            }
        }
        precMinMax = area;
        return minMax;
    }
}
