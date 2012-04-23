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

import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import org.geotoolkit.util.ArgumentChecks;

/**
 * An Iterator for traversing anyone raster from anyone type.
 * <p>
 * Iteration begin to follow raster's band, next, raster's x coordinates
 * and to finish raster's y coordinates.
 * <p>
 * Iteration follow this scheme :
 * raster's band --&lt; raster's x coordinates --&lt; raster's y coordinates.
 *
 * Moreother iterator traversing a read-only raster in top-to-bottom, left-to-right order.
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
     * Raster's band number.
     */
    private final int numBand;

    /**
     * The X coordinate of the upper-left pixel of this Raster's first iteration.
     */
    private final int minX;

    /**
     * The Y coordinate of the upper-left pixel of this Raster's first iteration.
     */
    private final int minY;

    /**
     * The X coordinate of the bottom-right pixel of this Raster's last iteration.
     */
    private final int maxX;

    /**
     * The X coordinate of the bottom-right pixel of this Raster's last iteration.
     */
    private final int maxY;

    /**
     * Current X pixel coordinate in raster.
     */
    private int currentlyXpos;

    /**
     * Current Y pixel coordinate in raster.
     */
    private int currentlyYpos;

    /**
     * Current band position in raster.
     */
    private int currentlyBandpos;

    /**
     * Create raster's iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    public RasterBasedIterator(final Raster raster) {
        super();
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.raster   = raster;
        this.numBand  = raster.getNumBands();
        this.minX     = raster.getMinX();
        currentlyXpos = minX;
        this.minY     = raster.getMinY();
        currentlyYpos = minY;
        this.maxY     = minY + raster.getHeight();
        this.maxX     = minX + raster.getWidth();
    }

    /**
     * Create raster's iterator to follow from minX, minY raster and rectangle2D intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     */
    public RasterBasedIterator(final Raster raster, final Rectangle2D subArea) {
        super();
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.raster    = raster;
        final int minx = raster.getMinX();
        final int miny = raster.getMinY();
        final int maxx = minx + raster.getWidth();
        final int maxy = miny + raster.getHeight();

        if(!subArea.intersects(new Rectangle2D.Double(minx, miny, maxx, maxy)))
        throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and raster"+raster+subArea);

        this.numBand   = raster.getNumBands();
        this.minX      = (int) Math.max(subArea.getMinX(), minx);
        this.minY      = (int) Math.max(subArea.getMinY(), miny);
        this.maxX      = (int) Math.min(subArea.getMaxX(), maxx);
        this.maxY      = (int) Math.min(subArea.getMaxY(), maxy);
        currentlyXpos  = this.minX;
        currentlyYpos  = this.minY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean hasNext() {
        return currentlyYpos != maxY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int nextX() {
        return currentlyXpos;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int nextY() {
        return currentlyYpos;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int nextSample() {
        final int val = raster.getSample(currentlyXpos, currentlyYpos, currentlyBandpos);
        increment();
        return val;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public float nextSampleFloat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double nextSampleDouble() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        currentlyBandpos = 0;
        currentlyXpos = minX;
        currentlyYpos = minY;
    }

    /**
     * To follow iterator like this pattern :
     * band position --&lt; X position --&lt; Y position.
     */
    private void increment() {
        if (++currentlyBandpos == numBand) {
            currentlyBandpos = 0;
            if (++currentlyXpos == maxX) {
                currentlyXpos = minX;
                currentlyYpos++;
            }
        }
    }
}
