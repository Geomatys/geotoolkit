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
import java.awt.image.*;

/**
 *
 * @author rmarech
 */
public class DefaultByteIterator extends PixelIterator{

    /**
     * Current raster which is followed by Iterator.
     */
    protected Raster currentRaster;

    /**
     * RenderedImage which is followed by Iterator.
     */
    private RenderedImage renderedImage;

    /**
     * Number of raster band .
     */
    protected int numBand;

    /**
     * The X coordinate of the upper-left pixel of this current raster.
     */
    protected int minX;

    /**
     * The Y coordinate of the upper-left pixel of this current raster.
     */
    protected int minY;

    /**
     * The X coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxX;

    /**
     * The Y coordinate of the bottom-right pixel of this current raster.
     */
    protected int maxY;

    /**
     * Current X pixel coordinate in this current raster.
     */
    protected int x;

    /**
     * Current Y pixel coordinate in this current raster.
     */
    protected int y;

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

    private byte[][] currentDataArray;

    int dataCursor;
    int numBanks;
    int rasterWidth;
    int cursorStep;
    int baseCursor;



    public DefaultByteIterator(final Raster raster) {
        final DataBuffer databuf = raster.getDataBuffer();
        assert (databuf.getDataType() == DataBuffer.TYPE_BYTE) : "raster datas or not Byte type"+databuf;
        this.currentRaster = raster;
        this.currentDataArray = ((DataBufferByte)databuf).getBankData();
        this.numBanks = ((DataBufferByte)databuf).getSize();
        this.rasterWidth = raster.getWidth();
        this.numBand = raster.getNumBands();

        //init
        this.minX = 0;
        this.minY = 0;
        this.maxX = rasterWidth;
        this.maxY = raster.getHeight();

        this.band = -1;
        this.dataCursor = 0;
        this.tY = this.tX = 0;
        this.tMaxX = this.tMaxY = 1;
        this.cursorStep = 0;
    }

    public DefaultByteIterator(final Raster raster, final Rectangle subArea) {
        //data attributs
        final DataBuffer databuf = raster.getDataBuffer();
        assert (databuf.getDataType() == DataBuffer.TYPE_BYTE) : "raster data or not Byte type"+databuf;
        this.currentDataArray = ((DataBufferByte)databuf).getBankData();
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
        this.maxX = Math.min(subAreaMaxX, rasterMinX + raster.getWidth()) - rasterMinX;
        this.maxY = Math.min(subAreaMaxY, rasterMinY + raster.getHeight()) - rasterMinY;
        if(minX > maxX || minY > maxY)
            throw new IllegalArgumentException("invalid subArea coordinate no intersection between it and raster"+raster+subArea);
        this.numBanks = (maxY-1)*rasterWidth+maxX;

        //step
        cursorStep = rasterWidth - (maxX-minX);
        dataCursor = baseCursor = minX + minY * rasterWidth;
        this.band = -1;
    }

    public DefaultByteIterator(final RenderedImage renderedImage) {
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

    public DefaultByteIterator(final RenderedImage renderedImage, final Rectangle subArea) {
        this.currentRaster = currentRaster;
        this.renderedImage = renderedImage;
        this.numBand = numBand;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.x = x;
        this.y = y;
        this.band = band;
        this.tMinX = tMinX;
        this.tMinY = tMinY;
        this.tMaxX = tMaxX;
        this.tMaxY = tMaxY;
        this.subAreaMinX = subAreaMinX;
        this.subAreaMinY = subAreaMinY;
        this.subAreaMaxX = subAreaMaxX;
        this.subAreaMaxY = subAreaMaxY;
        this.tX = tX;
        this.tY = tY;
    }

    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++dataCursor == numBanks) {
                if (++tX == tMaxX) {
                    tX = tMinX;
                    if(++tY == tMaxY) return false;
                }
            } else if (dataCursor % rasterWidth % maxX == 0) {
                dataCursor += cursorStep;
            }
        }
        return true;
    }

    /**
     * Update current data array from tiles array coordinates.
     *
     * @param tileX current X coordinate from rendered image tiles array.
     * @param tileY current Y coordinate from rendered image tiles array.
     */
    protected void updateCurrentRaster(int tileX, int tileY){
        final Raster raster = renderedImage.getTile(tileX, tileY);
        final int cRMinX   = raster.getMinX();
        final int cRMinY   = raster.getMinY();
        this.minX    = Math.max(subAreaMinX, cRMinX);
        this.minY    = Math.max(subAreaMinY, cRMinY);
        this.maxX    = Math.min(subAreaMaxX, cRMinX + raster.getWidth());
        this.maxY    = Math.min(subAreaMaxY, cRMinY + raster.getHeight());
        this.numBand = raster.getNumBands();
        this.currentDataArray = ((DataBufferByte)raster.getDataBuffer()).getBankData();
    }

    @Override
    public int getX() {
        return dataCursor%rasterWidth;
    }

    @Override
    public int getY() {
        return dataCursor/rasterWidth;
    }

    @Override
    public int getSample() {
        return currentDataArray[band][dataCursor];
    }

    @Override
    public float getSampleFloat() {
        return currentDataArray[band][dataCursor];
    }

    @Override
    public double getSampleDouble() {
        return currentDataArray[band][dataCursor];
    }

    @Override
    public void rewind() {
        band = -1;
        dataCursor = 0;
    }

    @Override
    public void setSample(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSampleFloat(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSampleDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveTo(int x, int y) {
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
        final int minx = currentRaster.getMinX();
        final int miny = currentRaster.getMinY();
        if (x < minx || x >= minx + currentRaster.getWidth() || y < miny || y >= miny + currentRaster.getHeight())
            throw new IllegalArgumentException("coordinate out of raster boundary"+currentRaster+x+y);
        this.band = -1;
        this.dataCursor = (x-minx) + (y-miny)*rasterWidth;
    }

}
