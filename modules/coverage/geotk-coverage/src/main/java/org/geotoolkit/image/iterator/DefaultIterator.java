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
import java.awt.image.RenderedImage;
import org.geotoolkit.util.ArgumentChecks;

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
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    DefaultIterator(final Raster raster) {
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.currentRaster = raster;
        this.numBand       = raster.getNumBands();
        this.minX  = raster.getMinX();
        this.minY  = raster.getMinY();
        this.x     = minX;
        this.y     = minY;
        this.maxY  = minY + raster.getHeight();
        this.maxX  = minX + raster.getWidth();
        this.band  = -1;
        this.tX    = this.tY    = 0;
        this.tMaxX = this.tMaxY = 1;
    }

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    DefaultIterator(final Raster raster, final Rectangle subArea) {
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.currentRaster = raster;
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
        tX  = tY  = 0;
        tMaxX = tMaxY = 1;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     */
    DefaultIterator(final RenderedImage renderedImage) {
        super();
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        this.renderedImage = renderedImage;
        //rect attributs
        this.subAreaMinX = renderedImage.getMinX();
        this.subAreaMinY = renderedImage.getMinY();
        this.subAreaMaxX = this.subAreaMinX + renderedImage.getWidth();
        this.subAreaMaxY = this.subAreaMinY + renderedImage.getHeight();
        //tiles attributs
        this.tMinX = renderedImage.getMinTileX();
        this.tMinY = renderedImage.getMinTileY();
        this.tMaxX = tMinX + renderedImage.getNumXTiles();
        this.tMaxY = tMinY + renderedImage.getNumYTiles();
        //initialize attributs to first iteration
        this.numBand = this.maxX = this.maxY = 1;
        this.tY = tMinY;
        this.tX = tMinX - 1;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    DefaultIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super();
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.renderedImage = renderedImage;
        //rect attributs
        this.subAreaMinX = subArea.x;
        this.subAreaMinY = subArea.y;
        this.subAreaMaxX = this.subAreaMinX + subArea.width;
        this.subAreaMaxY = this.subAreaMinY + subArea.height;
        //define min max intervals
        final int minIAX = Math.max(renderedImage.getMinX(), subAreaMinX);
        final int minIAY = Math.max(renderedImage.getMinY(), subAreaMinY);
        final int maxIAX = Math.min(renderedImage.getMinX() + renderedImage.getWidth(), subAreaMaxX);
        final int maxIAY = Math.min(renderedImage.getMinY() + renderedImage.getHeight(), subAreaMaxY);
        //intersection test
        if (minIAX > maxIAX || minIAY > maxIAY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and RenderedImage"+renderedImage+subArea);
        //tiles attributs
        final int rITWidth   = renderedImage.getTileWidth();
        final int rITHeight  = renderedImage.getTileHeight();
        final int rIMinTileX = renderedImage.getMinTileX();
        final int rIMinTileY = renderedImage.getMinTileY();
        this.tMinX = minIAX / rITWidth  + rIMinTileX;
        this.tMinY = minIAY / rITHeight + rIMinTileY;
        this.tMaxX = maxIAX / rITWidth  + rIMinTileX;
        this.tMaxY = maxIAY / rITHeight + rIMinTileY;
        this.tY = tMinY;
        //initialize attributs to first iteration
        this.numBand = this.maxX = this.maxY = 1;
        this.tX = tMinX - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++x == maxX) {
                x = minX;
                if (++y == maxY) {
                    if (++tX == tMaxX) {
                        tX = tMinX;
                        if(++tY == tMaxY) return false;
                    }
                    //initialize from new tile(raster).
                    updateCurrentRaster(tX, tY);
                }
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
        currentRaster      = renderedImage.getTile(tileX, tileY);
        final int cRMinX   = currentRaster.getMinX();
        final int cRMinY   = currentRaster.getMinY();
        this.minX = this.x = Math.max(subAreaMinX, cRMinX);
        this.minY = this.y = Math.max(subAreaMinY, cRMinY);
        this.maxX          = Math.min(subAreaMaxX, cRMinX + currentRaster.getWidth());
        this.maxY          = Math.min(subAreaMaxY, cRMinY + currentRaster.getHeight());
        this.numBand       = currentRaster.getNumBands();
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
            band = -1; x = minX; y = minY;
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
        if (renderedImage != null) {
            final int riMinX = renderedImage.getMinX();
            final int riMinY = renderedImage.getMinY();
            if (x < riMinX || x >= riMinX+renderedImage.getWidth()
            ||  y < riMinY || x >= riMinY+renderedImage.getHeight())
                throw new IllegalArgumentException("coordinate out of rendered image boundary"+renderedImage+x+y);
            boolean update = false;
            if (x < minX || x >= maxX) {
                tX = (x - riMinX)/renderedImage.getTileWidth() + renderedImage.getMinTileX();
                update = true;
            }
            if (y < riMinY || y >= maxY) {
                tY = (y - riMinY)/renderedImage.getTileHeight() + renderedImage.getMinTileY();
                update = true;
            }
            if (update) updateCurrentRaster(tX, tY);
        }
        if (x < minX || x >= maxX || y < minY || y >= maxY)
            throw new IllegalArgumentException("coordinate out of raster boundary"+currentRaster+x+y);
        this.x = x;
        this.y = y;
        this.band = -1;
    }
}
