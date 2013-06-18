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
package org.geotoolkit.image.interpolation;

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
        super(pixelIterator, 0);
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
        pixelIterator.moveTo((int) (x + Math.copySign(0.5, x)), (int) (y + Math.copySign(0.5, y)), 0);
        int bands = 0;
        while (bands++ != numBands) {
            result[bands - 1] = pixelIterator.getSampleDouble();
            pixelIterator.next();
        }
        return result;
    }
}
