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
import java.awt.image.Raster;
import org.geotoolkit.util.ArgumentChecks;

/**
 * An Iterator for traversing anyone raster from anyone type.
 * <p>
 * Iteration begin to follow raster band, next, raster x coordinates
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * raster band --&lt; raster x coordinates --&lt; raster y coordinates.
 *
 * Moreover iterator traversing a read-only raster in top-to-bottom, left-to-right order.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class RasterBasedIterator extends PixelIterator {
    /**
     * Raster which is followed by Iterator.
     */
    private final Raster raster;

    /**
     * Number of raster band .
     */
    private final int numBand;

    /**
     * The X coordinate of the upper-left pixel of this Raster.
     */
    private final int minX;

    /**
     * The Y coordinate of the upper-left pixel of this Raster.
     */
    private final int minY;

    /**
     * The X coordinate of the bottom-right pixel of this Raster.
     */
    private final int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of this Raster.
     */
    private final int maxY;

    /**
     * Current X pixel coordinate in raster.
     */
    private int x;

    /**
     * Current Y pixel coordinate in raster.
     */
    private int y;

    /**
     * Current band position in raster.
     */
    private int band;

    /**
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    public RasterBasedIterator(final Raster raster) {
        super();
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.raster   = raster;
        this.numBand  = raster.getNumBands();
        this.minX     = raster.getMinX();
        x = minX;
        this.minY     = raster.getMinY();
        y = minY;
        this.maxY     = minY + raster.getHeight();
        this.maxX     = minX + raster.getWidth();
    }

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle2D intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     */
    public RasterBasedIterator(final Raster raster, final Rectangle subArea) {
        super();
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.raster     = raster;
        final int minx  = raster.getMinX();
        final int miny  = raster.getMinY();
        final int maxx  = minx + raster.getWidth();
        final int maxy  = miny + raster.getHeight();
        final int sminx = subArea.x;
        final int sminy = subArea.y;
        this.numBand    = raster.getNumBands();
        this.minX       =  Math.max(sminx, minx);
        this.minY       =  Math.max(sminy, miny);
        this.maxX       =  Math.min(sminx + subArea.width, maxx);
        this.maxY       =  Math.min(sminy + subArea.height, maxy);
        if(minX > maxX || minY > maxY)
        throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and raster"+raster+subArea);
        x  = this.minX;
        y  = this.minY;
    }

    /**
     * To follow iterator like this pattern :
     * band position --&lt; X position --&lt; Y position.
     *
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++x == maxX) {
                x = minX;
                return (++y != maxY);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getSample() {
        return raster.getSample(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public float getSampleFloat() {
        return raster.getSampleFloat(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSampleDouble() {
        return raster.getSampleDouble(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        band = 0; x = minX; y = minY;
    }
}
