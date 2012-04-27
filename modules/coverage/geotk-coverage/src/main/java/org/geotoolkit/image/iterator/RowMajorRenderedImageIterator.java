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
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&lt; tiles x coordinates --&lt; next X tile position in rendered image tiles array
 * --&lt; current tiles y coordinates --&lt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Code example :
 * {@code
 *                  final RowMajorRenderedImageIterator dRII = new RowMajorRenderedImageIterator(renderedImage);
 *                  while (dRII.next()) {
 *                      dRii.getSample();
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class RowMajorRenderedImageIterator extends RasterBasedIterator {

    /**
     * true if raster constructor is used else false.
     */
    private final boolean raster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    private RenderedImage renderedImage;

    /**
     * The X index coordinate of the upper-left tile of this rendered image.
     */
    private int tMinX;

    /**
     * The Y index coordinate of the upper-left tile of this rendered image.
     */
    private int tMinY;

    /**
     * The X index coordinate of the bottom-right tile of this rendered image.
     */
    private int tMaxX;

    /**
     * The Y index coordinate of the bottom-right tile of this rendered image.
     */
    private int tMaxY;

    /**
     * The X coordinate of the sub-Area upper-left corner.
     */
    private int subAreaMinX;

    /**
     * The Y coordinate of the sub-Area upper-left corner.
     */
    private int subAreaMinY;

    /**
     * The X index coordinate of the sub-Area bottom-right corner.
     */
    private int subAreaMaxX;

    /**
     * The Y index coordinate of the sub-Area bottom-right corner.
     */
    private int subAreaMaxY;

    /**
     * Current x tile position in rendered image tile array.
     */
    private int tX;
    /**
     * Current y tile position in rendered image tile array.
     */
    private int tY;

    /**
     * Create row-major rendered image iterator.
     *
     * @param renderedImage : image which will be follow by iterator.
     */
    public RowMajorRenderedImageIterator(RenderedImage renderedImage) {
        this.renderedImage = renderedImage;
        this.raster = false;
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
        this.tY = tMinY - 1;
        this.tX = tMaxX - 1;
    }

    /**
     * Create row-major rendered image iterator.
     *
     * @param renderedImage : image which will be follow by iterator.
     * @param subArea : Rectangle which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image.
     */
    public RowMajorRenderedImageIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        this.renderedImage = renderedImage;
        this.raster = false;
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
        //initialize attributs to first iteration
        this.numBand = this.maxX = this.maxY = 1;
        this.tY = tMinY - 1;
        this.tX = tMaxX - 1;
    }

    /**
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    public RowMajorRenderedImageIterator(final Raster raster) {
        super(raster);
        this.raster = true;
    }

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle2D intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     */
    public RowMajorRenderedImageIterator(final Raster raster, final Rectangle subArea) {
        super(raster, subArea);
        this.raster = true;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (raster) return super.next();
        if (++band == numBand) {
            band = 0;
            if (++x == maxX) {
                x = minX;
                if (++tX == tMaxX) {
                    tX = tMinX;
                    if (++y == maxY) {
                        if(++tY == tMaxY) return false;
                        //initialize from new tile(raster) after tiles Y move.
                        currentRaster = renderedImage.getTile(tX, tY);
                        final int cRMinY = currentRaster.getMinY();
                        this.y = Math.max(subAreaMinY, cRMinY);
                        this.maxY = Math.min(subAreaMaxY, cRMinY + currentRaster.getHeight());
                    }
                }
                //initialize from new tile(raster) after tiles X move.
                currentRaster = renderedImage.getTile(tX, tY);
                final int cRMinX = currentRaster.getMinX();
                this.minX = this.x = Math.max(subAreaMinX, cRMinX);
                this.maxX = Math.min(subAreaMaxX, cRMinX + currentRaster.getWidth());
                this.numBand = currentRaster.getNumBands();
            }
        }
        return true;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        if(raster) {
            super.rewind();
        } else {
            this.x    = this.y    = this.band    = 0;
            this.maxX = this.maxY = this.numBand = 1;
            this.tX = tMaxX - 1;
            this.tY = tMinY - 1;
        }
    }
}
