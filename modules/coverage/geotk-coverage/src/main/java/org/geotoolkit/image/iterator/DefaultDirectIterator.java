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
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

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
    protected int rasterWidth;

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
     * Tile scanLineStride from Raster or RenderedImage Sample model.
     */
    protected int scanLineStride;

    /**
     * Band offset for all band.
     */
    protected int[] bandOffset;

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
        final SampleModel sampleM = raster.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            this.scanLineStride = ((ComponentSampleModel)sampleM).getScanlineStride();
            this.bandOffset = ((ComponentSampleModel)sampleM).getBandOffsets();
        } else {
            throw new IllegalArgumentException("DefaultDirectIterator constructor : sample model not conform");
        }

        if (subArea != null) {
            final int minx = raster.getMinX();
            final int miny = raster.getMinY();
            this.maxBanks = (areaIterateMaxX-minx) + (areaIterateMaxY-miny-1) * rasterWidth;
            this.tMaxX = this.tMaxY = 1;

            //step
            cursorStep = (rasterWidth - (areaIterateMaxX-areaIterateMinX));
            dataCursor = baseCursor = (areaIterateMinX-minx) + (areaIterateMinY-miny) * rasterWidth;
            this.indexStep = baseCursor + (areaIterateMaxX-areaIterateMinX);
        } else {
            this.maxBanks   = rasterWidth*raster.getHeight();
            this.dataCursor = baseCursor = 0;
            this.cursorStep = 0;
            this.band = -1;
        }
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
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            this.scanLineStride = ((ComponentSampleModel)sampleM).getScanlineStride();
            this.bandOffset = ((ComponentSampleModel)sampleM).getBandOffsets();
        } else {
            throw new IllegalArgumentException("DefaultDirectIterator constructor : sample model not conform");
        }
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
                indexStep  += rasterWidth;
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
        this.rasterWidth   = currentRaster.getWidth();

        //update min max from subArea and raster boundary
        final int minx    = Math.max(areaIterateMinX, cRMinX) - cRMinX;
        final int miny    = Math.max(areaIterateMinY, cRMinY) - cRMinY;
        final int maxx    = Math.min(areaIterateMaxX, cRMinX + rasterWidth) - cRMinX;
        final int maxy    = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight()) - cRMinY;
        this.numBand = currentRaster.getNumBands();
        this.maxBanks = maxx + (maxy-1) * rasterWidth;

        //step
        cursorStep = rasterWidth - (maxx-minx);
        dataCursor = baseCursor = minx + miny * rasterWidth;
        this.indexStep = baseCursor + (maxx-minx);
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
                : renderedImage.getMinY() + (tY - renderedImage.getMinTileY())*renderedImage.getTileHeight() + dataCursor/rasterWidth;
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
            this.indexStep  = baseCursor + (areaIterateMaxX-areaIterateMinX);
        } else {
            this.numBand = this.maxBanks = 1;
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
        this.dataCursor = (x - currentRaster.getMinX() + (y - currentRaster.getMinY()) * rasterWidth);
    }
}
