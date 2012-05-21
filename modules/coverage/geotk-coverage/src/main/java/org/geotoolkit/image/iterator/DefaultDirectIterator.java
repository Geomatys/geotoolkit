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
 * Furthermore iterator directly read in data table within raster {@code DataBuffer}.
 *
 * Code example :
 * {@code
 *                  final DefaultByteIterator dBI = new DefaultByteIterator(renderedImage);
 *                  while (dBI.next()) {
 *                      dBI.getSample();
 *                  }
 * }
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
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        this.currentRaster = raster;
        this.rasterWidth = raster.getWidth();
        this.numBand = raster.getNumBands();

        //init
        this.minX = 0;
        this.minY = 0;
        this.maxX = this.indexStep = rasterWidth;
        this.maxY = raster.getHeight();

        this.maxBanks =  rasterWidth*maxY;

        this.band = -1;
        this.dataCursor = 0;
        this.tY = this.tX = 0;
        this.tMaxX = this.tMaxY = 1;
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
        ArgumentChecks.ensureNonNull("Raster : ", raster);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        //data attributs
        this.currentRaster = raster;
        this.numBand = raster.getNumBands();
        this.rasterWidth = raster.getWidth();

        //subarea attributs
        this.subAreaMinX = subArea.x;
        this.subAreaMinY = subArea.y;
        this.subAreaMaxX = subAreaMinX + subArea.width;
        this.subAreaMaxY = subAreaMinY + subArea.height;

        //initialization
        final int rasterMinX = raster.getMinX();
        final int rasterMinY = raster.getMinY();
        this.minX = Math.max(subAreaMinX, rasterMinX) - rasterMinX;
        this.minY = Math.max(subAreaMinY, rasterMinY) - rasterMinY;
        this.maxX = Math.min(subAreaMaxX, rasterMinX + raster.getWidth())  - rasterMinX;
        this.maxY = Math.min(subAreaMaxY, rasterMinY + raster.getHeight()) - rasterMinY;
        if(minX > maxX || minY > maxY)
            throw new IllegalArgumentException("invalid subArea coordinates, no intersection between it and raster"+raster+subArea);
        this.maxBanks = maxX + (maxY-1) * rasterWidth;//index of last banks
        this.tY = this.tX = 0;
        this.tMaxX = this.tMaxY = 1;

        //step
        cursorStep = rasterWidth - (maxX-minX);
        dataCursor = baseCursor = minX + minY * rasterWidth;
        this.indexStep = baseCursor + maxX-minX;
        this.band = -1;
    }

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     */
    protected DefaultDirectIterator(final RenderedImage renderedImage) {
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
        this.numBand = this.maxX = this.maxY = this.maxBanks = 1;
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
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        ArgumentChecks.ensureNonNull("sub Area iteration : ", subArea);
        this.renderedImage = renderedImage;

        //rect attributs
        this.subAreaMinX = subArea.x;
        this.subAreaMinY = subArea.y;
        this.subAreaMaxX = this.subAreaMinX + subArea.width;
        this.subAreaMaxY = this.subAreaMinY + subArea.height;

        final int rimx = renderedImage.getMinX();
        final int rimy = renderedImage.getMinY();
        final int mtx = renderedImage.getMinTileX();
        final int mty = renderedImage.getMinTileY();

        final int mix = Math.max(subAreaMinX, rimx) - rimx;
        final int miy = Math.max(subAreaMinY, rimy) - rimy;
        final int max = Math.min(subAreaMaxX, rimx + renderedImage.getWidth()) - rimx;
        final int may = Math.min(subAreaMaxY, rimy + renderedImage.getHeight()) - rimy;
        if(mix > max || miy > may)
            throw new IllegalArgumentException("invalid subArea coordinates, no intersection between it and renderedImage"+renderedImage+subArea);

        final int tw = renderedImage.getTileWidth();
        final int th = renderedImage.getTileHeight();

        //tiles attributs
        this.tMinX = mix / tw + mtx;
        this.tMinY = miy / th + mty;
        this.tMaxX = max / tw + mtx;
        this.tMaxY = may / th + mty;

        //initialize attributs to first iteration
        this.numBand = this.maxX = this.maxY = this.maxBanks = 1;
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
        final int minx = (renderedImage == null) ? currentRaster.getMinX() : renderedImage.getMinX();
        return minx + (tX-tMinX)*rasterWidth + dataCursor%rasterWidth;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        final int miny = (renderedImage == null) ? currentRaster.getMinY() : renderedImage.getMinY();
        return miny + (tY-tMinY)*currentRaster.getHeight() + dataCursor/rasterWidth;
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
        if (renderedImage != null) {
            final int riMinX = renderedImage.getMinX();
            final int riMinY = renderedImage.getMinY();
            if (x < riMinX || x >= riMinX + renderedImage.getWidth()
            ||  y < riMinY || x >= riMinY + renderedImage.getHeight())
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
        int minx = x;
        int miny = y;
        minx -= currentRaster.getMinX();
        miny -= currentRaster.getMinY();
        if (minx < minX || minx >= maxX ||  miny < minY || miny >= maxY)
            throw new IllegalArgumentException("coordinate out of raster boundary"+currentRaster+x+y);
        this.band = -1;
        this.dataCursor = minx + miny * rasterWidth;
    }
}
