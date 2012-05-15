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
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import org.geotoolkit.util.ArgumentChecks;

/**
 * An Iterator for traversing anyone rendered Image with Byte type data.
 * <p>
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&lt; tiles x coordinates --&lt; next X tile position in rendered image tiles array
 * --&lt; current tiles y coordinates --&lt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Furthermore iterator is only appropriate to iterate on Byte data type.
 *
 * Code example :
 * {@code
 *                  final RowMajorIterator dRII = new RowMajorIterator(renderedImage);
 *                  while (dRII.next()) {
 *                      dRii.getSample();
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
class RowMajorByteIterator extends PixelIterator{

    /**
     * Current raster which is followed by Iterator.
     */
    private Raster currentRaster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    private RenderedImage renderedImage;

    /**
     * Number of raster band .
     */
    private int numBand;

    /**
     * The X coordinate of the upper-left pixel of this current raster.
     */
    private int minX;

    /**
     * The Y coordinate of the upper-left pixel of this current raster.
     */
    private int minY;

    /**
     * The X coordinate of the bottom-right pixel of this current raster.
     */
    private int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of this current raster.
     */
    private int maxY;

    /**
     * Current band position in this current raster.
     */
    protected int band;

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
     * Current raster data table.
     */
    private byte[][] currentDataArray;

    /**
     * Cursor position of current raster data.
     */
    protected int dataCursor;

    /**
     * Current raster width.
     */
    private int rasterWidth;

    /**
     * Abstract row index;
     */
    private int row;

    /**
     * Create default rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     */
    RowMajorByteIterator(final RenderedImage renderedImage) {
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
        assert (renderedImage.getTile(tMinX, tMinY).getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE)
               : "renderedImage datas or not Byte type";
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
    RowMajorByteIterator(final RenderedImage renderedImage, final Rectangle subArea) {
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

        assert (renderedImage.getTile(tMinX, tMinY).getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE)
               : "renderedImage datas or not Byte type";

        //initialize attributs to first iteration
        this.numBand = this.maxX = this.maxY = 1;
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
            if (++dataCursor == maxX) {
                if (++tX == tMaxX) {
                    tX = tMinX;
                    if (++row == maxY) {
                        row = 0;
                        if (++tY == tMaxY) {
                            return false;
                        }
                    }
                }
                updateCurrentRaster(tX, tY);
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
    protected void updateCurrentRaster(int tileX, int tileY) {
        //update raster
        this.currentRaster = renderedImage.getTile(tileX, tileY);
        final int cRMinX   = currentRaster.getMinX();
        final int cRMinY   = currentRaster.getMinY();
        this.rasterWidth = currentRaster.getWidth();
        this.currentDataArray = ((DataBufferByte)currentRaster.getDataBuffer()).getBankData();

        //update min max from subArea and raster boundary
        this.minX    = Math.max(subAreaMinX, cRMinX) - cRMinX;
        this.minY    = Math.max(subAreaMinY, cRMinY) - cRMinY;
        this.maxX    = Math.min(subAreaMaxX, cRMinX + rasterWidth) - cRMinX;
        this.maxY    = Math.min(subAreaMaxY, cRMinY + currentRaster.getHeight()) - cRMinY;
        this.numBand = currentRaster.getNumBands();
        this.maxX += (minY + row ) * rasterWidth;
        dataCursor = minX + (minY + row) * rasterWidth;
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
    public int getSample() {
        return currentDataArray[band][dataCursor];
    }

    @Override
    public float getSampleFloat() {
        return currentDataArray[band][dataCursor];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSampleDouble() {
        return currentDataArray[band][dataCursor];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        this.numBand = this.maxX = this.maxY = 1;
        this.dataCursor = this.band = 0;
        this.tY = tMinY;
        this.tX = tMinX - 1;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void moveTo(int x, int y) {
        final int riMinX = renderedImage.getMinX();
        final int riMinY = renderedImage.getMinY();
        if (x < riMinX || x >= riMinX + renderedImage.getWidth()
        ||  y < riMinY || x >= riMinY + renderedImage.getHeight())
            throw new IllegalArgumentException("coordinate out of rendered image boundary"+renderedImage+x+y);
        tX = (x - riMinX)/renderedImage.getTileWidth() + renderedImage.getMinTileX();
        tY = (y - riMinY)/renderedImage.getTileHeight() + renderedImage.getMinTileY();
        updateCurrentRaster(tX, tY);
        this.band = -1;
        this.row = y;
        this.row -= currentRaster.getMinY();
        this.dataCursor = x;
        this.dataCursor -= currentRaster.getMinX();
        this.dataCursor += row * rasterWidth;
    }
}
