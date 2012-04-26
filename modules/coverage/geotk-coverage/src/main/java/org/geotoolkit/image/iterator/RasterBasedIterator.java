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
     * Current raster which is followed by Iterator.
     */
    protected Raster currentRaster;

    /**
     * Number of raster band .
     */
    protected int numBand;

    /**
     * The X coordinate of the upper-left pixel of this current raster.
     */
    protected int minX;

    /**
     * The Y coordinate of the upper-left pixel of this current raster.
     */
    protected int minY;

    /**
     * The X coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxY;

    /**
     * Current X pixel coordinate in this current raster.
     */
    protected int x;

    /**
     * Current Y pixel coordinate in this current raster.
     */
    protected int y;

    /**
     * Current band position in this current raster.
     */
    protected int band;

    /**
     * Default constructor to daughter classes.
     */
    protected RasterBasedIterator(){
        super();
    }

    /**
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    public RasterBasedIterator(final Raster raster) {
        super();
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.currentRaster = raster;
        this.numBand  = raster.getNumBands();
        this.minX = raster.getMinX();
        this.minY = raster.getMinY();
        this.x    = minX;
        this.y    = minY;
        this.maxY = minY + raster.getHeight();
        this.maxX = minX + raster.getWidth();
        this.band = -1;
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
        this.currentRaster     = raster;
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
        this.band = -1;
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
        return currentRaster.getSample(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public float getSampleFloat() {
        return currentRaster.getSampleFloat(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSampleDouble() {
        return currentRaster.getSampleDouble(x, y, band);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        band = -1; x = minX; y = minY;
    }
}
