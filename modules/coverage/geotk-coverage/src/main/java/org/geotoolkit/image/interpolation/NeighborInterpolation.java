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
 * Define Interpolation from neighbor.
 *
 * Neighbor interpolation round coordinates values at nearest integer value
 * and return nearest pixel value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class NeighborInterpolation extends Interpolation {

    /**
     * Create a NeighBor Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public NeighborInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
    }

    /**
     * Return nearest pixel value.
     *
     * @param x coordinate cursor position.
     * @param y coordinate cursor position.
     * @return nearest pixel value.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        pixelIterator.moveTo((int) Math.round(x), (int) Math.round(y));
        final double[] result = new double[numBands];
        int bands = 0;
        while (bands++ != numBands) {
            pixelIterator.next();
            result[bands - 1] = pixelIterator.getSampleDouble();
        }
        return result;
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
                minMax[6*band]         = value;
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
                    minMax[6*band]         = value;
                    minMax[6*band + 1] = pixelIterator.getX();
                    minMax[6*band + 2] = pixelIterator.getX();
                }
                if (value > minMax[6*band + 3]) {
                    //max value, x, y coordinates
                    minMax[6*band + 3]     = value;
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
                minMax[6*band]         = value;
                minMax[6*band + 1] = pixelIterator.getX();
                minMax[6*band + 2] = pixelIterator.getX();
                //max value, x, y coordinates
                minMax[6*band + 3]     = value;
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
                            minMax[6*band]         = value;
                            minMax[6*band + 1] = pixelIterator.getX();
                            minMax[6*band + 2] = pixelIterator.getX();
                        }
                        if (value > minMax[6*band + 3]) {
                            //max value, x, y coordinates
                            minMax[6*band + 3]     = value;
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
