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
 * Iteration transverse each tiles(raster) from rendered image or raster source one by one in order.
 * Iteration to follow tiles(raster) begin by raster bands, next, raster x coordinates,
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; tiles y coordinates --&gt; next rendered image tiles.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 * Furthermore iterator directly read in data table within raster {@code DataBuffer}.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
abstract class DefaultDirectIterator extends PixelIterator{

    /**
     * Current raster width.
     */
    private int rasterWidth;

    /**
     * Step value to move forward in iteration.
     * @see DefaultDirectIterator#next()
     */
    private int cursorStep;

    /**
     * First iteration position.
     * @see DefaultDirectIterator#rewind()
     */
    private int baseCursor;

    /**
     * Cursor position when {@code cursorStep} is add.
     */
    private int indexStep;

    /**
     * Cursor position of current raster data.
     */
    protected int dataCursor;

    /**
     * Maximum iteration value from current raster.
     */
    protected int maxBanks;

    /**
     * Create raster iterator to follow from its minX and minY coordinates.
     *
     * @param raster will be followed by this iterator.
     */
    protected DefaultDirectIterator(final Raster raster) {
        super(raster);
        this.rasterWidth = raster.getWidth();

        //init
        this.minX = 0;
        this.minY = 0;
        this.maxX = this.indexStep = rasterWidth;
        this.maxY = raster.getHeight();
        this.maxBanks   = rasterWidth*maxY;
        this.dataCursor = 0;
        this.cursorStep = 0;
    }

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    protected DefaultDirectIterator(final Raster raster, final Rectangle subArea) {
        super(raster, subArea);
        this.rasterWidth = raster.getWidth();

        final int minx = raster.getMinX();
        final int miny = raster.getMinY();
        this.maxBanks = (maxX-minx) + (maxY-miny-1) * rasterWidth;//index of last banks
        this.tMaxX = this.tMaxY = 1;

        //step
        cursorStep = rasterWidth - (maxX-minX);
        dataCursor = baseCursor = (areaIterateMinX-minx) + (areaIterateMinY-miny) * rasterWidth;
        this.indexStep = baseCursor + maxX-minX;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     */
    protected DefaultDirectIterator(final RenderedImage renderedImage) {
        super(renderedImage);
        this.rasterWidth = renderedImage.getTileWidth();
        this.maxBanks = 1;
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
    protected DefaultDirectIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super(renderedImage, subArea);
        this.rasterWidth = renderedImage.getTileWidth();

        //initialize attributs to first iteration
        this.maxBanks = 1;
        this.tY = tMinY;
        this.tX = tMinX - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++dataCursor == maxBanks) {
                if (++tX == tMaxX) {
                    tX = tMinX;
                    if(++tY == tMaxY) return false;
                }
                updateCurrentRaster(tX, tY);
            } else if (dataCursor == indexStep) {
                dataCursor += cursorStep;
                indexStep += rasterWidth;
            }
        }
        return true;
    }

    /**
     * Update current data array and current raster from tiles array coordinates.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY){
        //update raster
        this.currentRaster = renderedImage.getTile(tileX, tileY);
        final int cRMinX   = currentRaster.getMinX();
        final int cRMinY   = currentRaster.getMinY();
        this.rasterWidth = currentRaster.getWidth();

        //update min max from subArea and raster boundary
        this.minX    = Math.max(subAreaMinX, cRMinX) - cRMinX;
        this.minY    = Math.max(subAreaMinY, cRMinY) - cRMinY;
        this.maxX    = Math.min(subAreaMaxX, cRMinX + rasterWidth) - cRMinX;
        this.maxY    = Math.min(subAreaMaxY, cRMinY + currentRaster.getHeight()) - cRMinY;
        this.numBand = currentRaster.getNumBands();
        this.maxBanks = maxX + (maxY-1) * rasterWidth;//index of last banks

        //step
        cursorStep = rasterWidth - (maxX-minX);
        dataCursor = baseCursor = minX + minY * rasterWidth;
        this.indexStep = baseCursor + maxX-minX;
        this.band = 0;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getX() {
        return (renderedImage == null)
                ? currentRaster.getMinX() + dataCursor%rasterWidth
                : renderedImage.getMinX() + (tX - renderedImage.getMinTileX())*rasterWidth + dataCursor%rasterWidth;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        return (renderedImage == null)
                ? currentRaster.getMinY() + dataCursor/rasterWidth
                : renderedImage.getMinY() + (tY - renderedImage.getMinTileY())*renderedImage.getTileHeight() + dataCursor/rasterWidth ;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        //initialize attributs like first iteration
        if (renderedImage == null) {
            band = -1;
            tX = tY = 0;
            tMaxX = tMaxY = 1;
            this.dataCursor = baseCursor;
            this.indexStep = baseCursor + maxX-minX;
        } else {
            this.numBand = this.maxX = this.maxY = this.maxBanks = 1;
            this.dataCursor = this.band = 0;
            this.tY = tMinY;
            this.tX = tMinX - 1;
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void moveTo(int x, int y) {
        super.moveTo(x, y);
        if (renderedImage != null) {
            final int riMinX = renderedImage.getMinX();
            final int riMinY = renderedImage.getMinY();
            tX = (x - riMinX)/renderedImage.getTileWidth() + renderedImage.getMinTileX();
            tY = (y - riMinY)/renderedImage.getTileHeight() + renderedImage.getMinTileY();
            updateCurrentRaster(tX, tY);
        }
        this.band = -1;
        this.dataCursor = x - currentRaster.getMinX() + (y - currentRaster.getMinY()) * rasterWidth;
    }
}
