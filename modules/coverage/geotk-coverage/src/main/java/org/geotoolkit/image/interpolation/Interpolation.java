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
 * Define standard interpolation.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class Interpolation {

    /**
     * Current {@code PixelIterator} which is interpolate.
     */
    protected final PixelIterator pixelIterator;

    /**
     * Number of bands within object that iterate.
     */
    protected final int numBands;

    /**
     * Boundary from object that iterate.
     */
    protected final Rectangle boundary;

    /**
     * Build an Interpolate object.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public Interpolation(PixelIterator pixelIterator) {
        this.pixelIterator = pixelIterator;
        this.numBands = pixelIterator.getNumBands();
        this.boundary = pixelIterator.getBoundary();
    }

    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    public abstract double[] interpolate(double x, double y);

    /**
     * Verify coordinates are within iterate area boundary.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @throws IllegalArgumentException if pixel coordinates are out of iterate area boundary.
     */
    protected void checkInterpolate(double x, double y) {
        final int mx = boundary.x;
        final int my = boundary.y;
        if (x < mx || x > mx + boundary.width || y < my || y > my + boundary.height)
            throw new IllegalArgumentException("coordinates out of iterate area boundary : "+boundary);
    }
}
