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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.opengis.coverage.grid.SequenceType;

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
 * @author Alexis Manin        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
abstract class DefaultDirectIterator extends PixelIterator {

    /**
     * Current X coordinate of the pixel iterator (in image grid)
     */
    protected int currentX;

    /**
     * Minimum coordinate allowed for iteration in current raster.
     */
    protected int minX, minY;

    /**
     * Current raster width.
     */
    protected final int rasterWidth;

    /**
     * Step value to move on the next line.
     * @see DefaultDirectIterator#next()
     */
    private int cursorStep;

    /**
     * First iteration position.
     * @see DefaultDirectIterator#rewind()
     */
    private int baseCursor;

    /**
     * Cursor position in current raster {@link java.awt.image.DataBuffer}.
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
     * Number of steps to move to get to next sample.
     */
    protected int pixelStride;

    /**
     * Current raster lower corner X coordinate;
     */
    protected int crMinX;

    /**
     * Current raster lower corner Y coordinate;
     */
    protected int crMinY;

    /**
     * An array specifying increment value to get to the next sample.
     */
    protected int[] bandSteps;

    protected int[] bandOffsets;

    /**
     * Create raster iterator to follow from minX, minY raster and rectangle intersection coordinate.
     *
     * @param raster will be followed by this iterator.
     * @param subArea {@code Rectangle} which define read iterator area.
     * @throws IllegalArgumentException if subArea don't intersect raster boundary.
     */
    protected DefaultDirectIterator(final Raster raster, final Rectangle subArea) {
        super(raster, subArea);

        final SampleModel sampleM = raster.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            ComponentSampleModel sModel = (ComponentSampleModel) sampleM;
            pixelStride = sModel.getPixelStride();
            scanLineStride = sModel.getScanlineStride();
            bandOffsets = sModel.getBandOffsets();
            bandSteps = getBandSteps(bandOffsets, pixelStride);
        } else {
            throw new IllegalArgumentException("DefaultDirectIterator constructor : sample model not conform");
        }

        rasterWidth = raster.getWidth();
        crMinX = raster.getMinX();
        crMinY = raster.getMinY();

        /* Initialize iteration attributes to be at a virtual position before the first pixel. Doing so, the first call
         * to next() will give us first sample of the first pixel.
         */
        maxBanks  = (areaIterateMaxX - crMinX) * pixelStride + (areaIterateMaxY - crMinY - 1) * scanLineStride;
        cursorStep = scanLineStride - ((areaIterateMaxX - areaIterateMinX) * pixelStride) + bandSteps[0];
        // Virtual position : last sample of px[-1]
        dataCursor = baseCursor = (areaIterateMinX - crMinX - 1) * pixelStride + (areaIterateMinY - crMinY) * scanLineStride + bandOffsets[rasterNumBand - 1];

        minX = areaIterateMinX;
        maxX = areaIterateMaxX;
        minY = areaIterateMinY;
        maxY = areaIterateMaxY;
        currentX = minX -1;
        band = -1;
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
            ComponentSampleModel sModel = (ComponentSampleModel) sampleM;
            pixelStride = sModel.getPixelStride();
            scanLineStride = sModel.getScanlineStride();
            bandSteps = getBandSteps(sModel.getBandOffsets(), pixelStride);
        } else {
            throw new IllegalArgumentException("DefaultDirectIterator constructor : sample model not conform");
        }

        rasterWidth = renderedImage.getTileWidth();
        rasterNumBand = sampleM.getNumBands();

        //initialize attributes to update current raster on first next() call.
        band = -1;
        tY = tMinY;
        tX = tMinX - 1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean next() {
        band = ++band % rasterNumBand;
        // Change pixel, end of a line
        if (band == 0 && ++currentX >= maxX) {
            // Check if we must change tile
            if ((dataCursor += cursorStep) >= maxBanks) {
                if (++tX >= tMaxX) {
                    tX = tMinX;
                    if (++tY >= tMaxY) {
                        //-- initialize attribut with expected values to throw exception if another next() is  called.
                        band = -1;
                        tX = tMaxX;
                        if (tY - 1 >= tMaxY)//-- at first out tY == tMaxY and with another next() tY = tMaxY + 1.
                            throw new IllegalStateException("Out of raster boundary. Illegal next call, you should rewind iterator first.");
                        return false;
                    }
                }
                updateCurrentRaster(tX, tY);
            } else {
                // Just change line
                currentX = minX;
            }
            // next sample
        } else {
            dataCursor += bandSteps[band];
        }

        return true;
    }

    /**
     * Update current data array and current raster from tiles array coordinates. Data cursor will be positioned at first
     * sample of the first pixel to browse.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY) {
        //update raster
        this.currentRaster = renderedImage.getTile(tileX, tileY);
        this.crMinX = currentRaster.getMinX();
        this.crMinY = currentRaster.getMinY();
        final ComponentSampleModel sModel = (ComponentSampleModel) currentRaster.getSampleModel();
        this.scanLineStride = sModel.getScanlineStride();
        this.pixelStride = sModel.getPixelStride();
        this.bandOffsets = sModel.getBandOffsets();
        this.bandSteps = getBandSteps(bandOffsets, pixelStride);
        //update min max from subArea and raster boundary
        this.minX = Math.max(areaIterateMinX, crMinX);
        this.maxX = Math.min(areaIterateMaxX, crMinX + rasterWidth);
        this.minY = Math.max(areaIterateMinY, crMinY);
        this.maxY = Math.min(areaIterateMaxY, crMinY + currentRaster.getHeight());
        final int minx  = this.minX - crMinX;
        final int miny  = this.minY - crMinY;
        final int maxx  = this.maxX - crMinX;
        final int maxy  = this.maxY - crMinY;
        this.maxBanks   = maxx * pixelStride + (maxy-1) * scanLineStride;

        this.cursorStep = scanLineStride - ((maxx - minx) * pixelStride) + bandSteps[0];
        this.dataCursor = minx * pixelStride + miny * scanLineStride + bandOffsets[0];
        currentX = minX;
        band = 0;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getX() {
        return currentX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getY() {
        return crMinY + (dataCursor - bandOffsets[band]) / scanLineStride;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        //initialize attributes like it was at built.
        if (renderedImage == null) {
            tX = tY = 0;
            tMaxX = tMaxY = 1;
            minX = areaIterateMinX;
            maxX = areaIterateMaxX;
            minY = areaIterateMinY;
            maxY = areaIterateMaxY;
            currentX = minX -1;
            dataCursor = baseCursor;
        } else {
            // Prepare Iterator to update its current raster on next call to next().
            currentX = dataCursor = maxX = maxBanks = 0;
            this.tY = tMinY;
            this.tX = tMinX - 1;
        }
        band = -1;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void moveTo(int x, int y, int b) {
        super.moveTo(x, y, b);
        if (renderedImage != null) {
            final int riMinX = renderedImage.getMinX();
            final int riMinY = renderedImage.getMinY();
//            tX = (x - riMinX) / rasterWidth                   + renderedImage.getMinTileX();
//            tY = (y - riMinY) / renderedImage.getTileHeight() + renderedImage.getMinTileY();
            final int tTempX = (x - riMinX) / rasterWidth                   + renderedImage.getMinTileX();
            final int tTempY = (y - riMinY) / renderedImage.getTileHeight() + renderedImage.getMinTileY();
            if (tTempX != tX || tTempY != tY) {
                tX = tTempX;
                tY = tTempY;
                updateCurrentRaster(tX, tY);
            }
        }

        // We are on the right tile, but not on the right pixel.
        this.dataCursor = (x - crMinX) * pixelStride + (y - crMinY) * scanLineStride + bandOffsets[0];
        currentX = x;
        // Do not perform first sample step, the move above already put cursor on it.
        band = 0;
        while (band < b) {
            dataCursor += bandSteps[++band];
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SequenceType getIterationDirection() {
        if (renderedImage == null) return SequenceType.LINEAR;//1 raster seul
        if (renderedImage.getNumXTiles() <=1 && renderedImage.getNumYTiles() <= 1)
            return SequenceType.LINEAR;
        return null;
    }
}
