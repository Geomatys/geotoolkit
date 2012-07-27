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
package org.geotoolkit.image.iterator;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

/**
 * An Iterator for traversing anyone rendered Image.
 * <p>
 * Iteration transverse each tiles(raster) from rendered image or raster source one by one in order.
 * Iteration to follow tiles(raster) begin by raster bands, next, raster x coordinates,
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; tiles y coordinates --&gt; next rendered image tiles.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Code example :
 * {@code
 *                  final DefaultRenderedImageIterator dRII = new DefaultRenderedImageIterator(renderedImage);
 *                  while (dRII.next()) {
 *                      dRii.getSample();
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
class DefaultIterator extends PixelIterator {

    /**
     * Current X pixel coordinate in this current raster.
     */
    protected int x;

    /**
     * Current Y pixel coordinate in this current raster.
     */
    protected int y;

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    DefaultIterator(final Raster raster, final Rectangle subArea) {
        super(raster, subArea);
        this.minX = areaIterateMinX;
        this.maxX = areaIterateMaxX;
        this.maxY = areaIterateMaxY;
        x  = this.minX;
        y  = areaIterateMinY;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    DefaultIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super(renderedImage, subArea);
        tX = tMinX-1;
        tY = tMinY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++x == maxX) {
                if (++y == maxY) {
                    if (++tX == tMaxX) {
                        tX = tMinX;
                        if (++tY == tMaxY) return false;
                    }
                    //initialize from new tile(raster).
                    updateCurrentRaster(tX, tY);
                }
                x = minX;
            }
        }
        return true;
    }

    /**
     * Update current raster from tiles array coordinates.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY){
        this.currentRaster = this.renderedImage.getTile(tileX, tileY);
        final int cRMinX   = this.currentRaster.getMinX();
        final int cRMinY   = this.currentRaster.getMinY();
        this.minX = this.x = Math.max(areaIterateMinX, cRMinX);
        this.y    = Math.max(areaIterateMinY, cRMinY);
        this.maxX          = Math.min(areaIterateMaxX, cRMinX + currentRaster.getWidth());
        this.maxY          = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight());
        this.numBand       = this.currentRaster.getNumBands();
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
        if (renderedImage == null) {
            band = -1; x = minX; y = areaIterateMinY;
            tX = tY = 0;
            tMaxX = tMaxY = 1;
        } else {
            this.x    = this.y    = this.band    = 0;
            this.maxX = this.maxY = this.numBand = 1;
            this.tX   = tMinX - 1;
            this.tY   = tMinY;
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() {
    }

    @Override
    public void moveTo(final int x, final int y) {
        super.moveTo(x, y);
        if (renderedImage != null) {
            final int riMinX = renderedImage.getMinX();
            final int riMinY = renderedImage.getMinY();
            tX = (x - riMinX)/renderedImage.getTileWidth() + renderedImage.getMinTileX();
            tY = (y - riMinY)/renderedImage.getTileHeight() + renderedImage.getMinTileY();
            updateCurrentRaster(tX, tY);
        }
        this.x = x;
        this.y = y;
        this.band = -1;
    }
}
