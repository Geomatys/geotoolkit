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

/**
 * An Iterator for traversing anyone rendered Image.
 * <p>
 * Iteration transverse each tiles(raster) from rendered image source one by one in order.
 * Iteration to follow tiles(raster) begin by raster bands, next, raster x coordinates,
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * raster band --&lt; raster x coordinates --&lt; raster y coordinates --&lt; next rendered image raster.
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
public class DefaultRenderedImageIterator extends PixelIterator {

    /**
     * Current raster which is followed by Iterator.
     */
    private Raster currentRaster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    private final RenderedImage renderedImage;

    /**
     * Number of raster bands .
     */
    private int numBand;

    /**
     * The X coordinate of the upper-left pixel of current Raster.
     */
    private int minX;

    /**
     * The X coordinate of the bottom-right pixel of current Raster.
     */
    private int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of current Raster.
     */
    private int maxY;

    /**
     * The X index coordinate of the upper-left tile of this rendered image.
     */
    private final int tMinX;

    /**
     * The Y index coordinate of the upper-left tile of this rendered image.
     */
    private final int tMinY;

    /**
     * The X index coordinate of the bottom-right tile of this rendered image.
     */
    private final int tMaxX;

    /**
     * The Y index coordinate of the bottom-right tile of this rendered image.
     */
    private final int tMaxY;

    /**
     * The X coordinate of the sub-Area upper-left corner.
     */
    private final int subAreaMinX;

    /**
     * The Y coordinate of the sub-Area upper-left corner.
     */
    private final int subAreaMinY;

    /**
     * The X index coordinate of the sub-Area bottom-right corner.
     */
    private final int subAreaMaxX;

    /**
     * The Y index coordinate of the sub-Area bottom-right corner.
     */
    private final int subAreaMaxY;

    /**
     * Current X pixel coordinate in current rendered image raster.
     */
    private int x;

    /**
     * Current Y pixel coordinate in current rendered image raster.
     */
    private int y;

    /**
     * Current band position in current rendered image raster.
     */
    private int band;

    /**
     * Current x tile position in rendered image tile array.
     */
    private int tX;
    /**
     * Current y tile position in rendered image tile array.
     */
    private int tY;

    public DefaultRenderedImageIterator(final RenderedImage renderedImage) {
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

    public DefaultRenderedImageIterator(final RenderedImage renderedImage, final Rectangle subArea) {
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
                    currentRaster = renderedImage.getTile(tX, tY);
                    final int cRMinX = currentRaster.getMinX();
                    final int cRMinY = currentRaster.getMinY();
                    this.minX = this.x = Math.max(subAreaMinX, cRMinX);
                    this.y = Math.max(subAreaMinY, cRMinY);
                    this.maxX = Math.min(subAreaMaxX, cRMinX + currentRaster.getWidth());
                    this.maxY = Math.min(subAreaMaxY, cRMinY + currentRaster.getHeight());
                    this.numBand = currentRaster.getNumBands();
                }
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
        this.x    = this.y    = this.band    = 0;
        this.maxX = this.maxY = this.numBand = 1;
        this.tX = tMinX - 1;
        this.tY = tMinY;
    }
}
