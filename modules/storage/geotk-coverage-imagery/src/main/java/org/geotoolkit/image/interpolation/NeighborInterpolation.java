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
    final int maxxId;
    final int maxyId;
    /**
     * Create a NeighBor Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public NeighborInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator, 0);
        Rectangle rect = pixelIterator.getBoundary(false);
        maxxId = rect.x + rect.width  - 1;
        maxyId = rect.y + rect.height - 1;
    }

    /**
     * Return nearest pixel value.
     *
     * @param x coordinate cursor position.
     * @param y coordinate cursor position.
     * @return nearest pixel value.
     */
    @Override
    public double interpolate(double x, double y, int bands) {
        checkInterpolate(x, y);
        if (x < 0) x = Math.round(x - 0.5);
        if (y < 0) y = Math.round(y - 0.5);
        
        pixelIterator.moveTo((int) Math.min(maxxId, x), (int) Math.min(maxyId, y), bands);
        return pixelIterator.getSampleDouble();
    }
}
