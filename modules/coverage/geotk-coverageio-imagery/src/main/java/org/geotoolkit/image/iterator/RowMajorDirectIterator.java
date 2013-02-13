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
import java.awt.image.ComponentSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.grid.SequenceType;

/**
 * An Iterator for traversing anyone rendered Image with Byte type data.
 * <p>
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; next X tile position in rendered image tiles array
 * --&gt; current tiles y coordinates --&gt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Furthermore iterator directly read in data table within raster {@code DataBuffer}.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
abstract class RowMajorDirectIterator extends PixelIterator {

    /**
     * Cursor position of current raster data.
     */
    protected int dataCursor;

    /**
     * Current raster width.
     */
    protected int rasterWidth;

    /**
     * Abstract row index;
     */
    private int row;

    /**
     * Current raster lower X value.
     */
    private int cRMinX;

    /**
     * Current raster lower Y value.
     */
    private int cRMinY;

    /**
     * Tile scanLineStride from Raster or RenderedImage Sample model.
     */
    protected int scanLineStride;

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    RowMajorDirectIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        super(renderedImage, subArea);
        ArgumentChecks.ensureNonNull("RenderedImage : ", renderedImage);
        this.renderedImage = renderedImage;
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            this.scanLineStride = ((ComponentSampleModel)sampleM).getScanlineStride();
        } else {
            throw new IllegalArgumentException("RowMajorDirectIterator constructor : sample model not conform");
        }
        this.rasterWidth = renderedImage.getTileWidth();
        this.numBand = sampleM.getNumBands();
        //initialize attributs to first iteration
        this.row     = this.areaIterateMinY - 1;
        this.maxY    = this.row + 1;
        this.maxX = 1;
        this.tY      = tMinY - 1;
        this.tX      = tMaxX - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        if (++dataCursor == maxX) {
            if (++tX == tMaxX) {
                tX = tMinX;
                if (++row == maxY) {
                    if (++tY == tMaxY) return false;
                    updateCurrentRaster(tX, tY);
                    this.cRMinY = this.currentRaster.getMinY();
                    this.maxY   = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight());
                }
            }
            updateCurrentRaster(tX, tY);
            this.cRMinX    = currentRaster.getMinX();
            this.maxX      = (Math.min(areaIterateMaxX, cRMinX + rasterWidth) - cRMinX)*numBand;
            final int step = (row - cRMinY) * scanLineStride;
            this.maxX     +=  step;
            dataCursor     = (Math.max(areaIterateMinX, cRMinX) - cRMinX)*numBand + step;
        }
        return true;
    }

    /**
     * Update current data array and current raster from tiles array coordinates.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY) {
        this.currentRaster = renderedImage.getTile(tileX, tileY);
        this.scanLineStride = ((ComponentSampleModel)currentRaster.getSampleModel()).getScanlineStride();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getX() {
        return cRMinX + dataCursor % scanLineStride/numBand;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        return cRMinY + dataCursor / scanLineStride;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        this.maxY       = this.areaIterateMinY;
        this.maxX       = 1;
        this.dataCursor = this.band = 0;
        this.tY         = this.tMinY - 1;
        this.tX         = this.tMaxX - 1;
        this.row        = this.maxY - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void moveTo(int x, int y, int b) {
        super.moveTo(x, y, b);
        final int tTempX = (x - renderedImage.getMinX())/renderedImage.getTileWidth() + renderedImage.getMinTileX();
        final int tTempY = (y - renderedImage.getMinY())/renderedImage.getTileHeight() + renderedImage.getMinTileY();
        if (tTempX != tX || tTempY != tY) {
            tX = tTempX;
            tY = tTempY;
            updateCurrentRaster(tX, tY);
            this.cRMinX = currentRaster.getMinX();
            this.cRMinY = currentRaster.getMinY();
        }
//        updateCurrentRaster(tX, tY);
//        this.cRMinX = currentRaster.getMinX();
//        this.cRMinY = currentRaster.getMinY();
        this.row = y;
        final int step = (row - cRMinY) * scanLineStride;
        this.maxX = (Math.min(areaIterateMaxX, cRMinX + rasterWidth) - cRMinX) * numBand;
        this.maxX += step;

        //initialize row
        this.maxY = Math.min(areaIterateMaxY, cRMinY + currentRaster.getHeight());
        this.dataCursor = (x - cRMinX) * numBand + step + b;// - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SequenceType getIterationDirection() {
        return SequenceType.LINEAR;
    }
}
