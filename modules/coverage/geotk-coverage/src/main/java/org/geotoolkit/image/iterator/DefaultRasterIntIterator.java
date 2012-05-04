/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.iterator;

import java.awt.Rectangle;
import java.awt.image.*;

/**
 *
 * @author rmarech
 */
public class DefaultRasterIntIterator extends PixelIterator{

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

    int[][] dataArray;
    int dataCursor;
    int numBanks;
    int rasterWidth;
    int cursorStep;
    int baseCursor;

    public DefaultRasterIntIterator(final Raster raster) {
        final DataBuffer databuf = raster.getDataBuffer();
        assert (databuf.getDataType() == DataBuffer.TYPE_INT) : "raster data or not Byte type"+databuf;
        this.dataArray = ((DataBufferInt)databuf).getBankData();
        this.numBanks = ((DataBufferInt)databuf).getSize();
        this.currentRaster = raster;
        this.numBand = raster.getNumBands();
        this.minX = raster.getMinX();
        this.minY = raster.getMinY();
        this.rasterWidth = raster.getWidth();
        this.maxX = minX + rasterWidth;
        this.maxY = minY + raster.getHeight();
        this.x = minX;
        this.y = minY;
        this.baseCursor = 0;
        this.band = -1;
    }

    public DefaultRasterIntIterator(final Raster raster, Rectangle subArea) {
        //data attributs
        final DataBuffer databuf = raster.getDataBuffer();
        assert (databuf.getDataType() == DataBuffer.TYPE_INT) : "raster data or not Byte type"+databuf;
        this.dataArray = ((DataBufferInt)databuf).getBankData();
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
        this.numBanks = (maxY-1)*rasterWidth+maxX;

        //jump
        cursorStep = rasterWidth - (maxX-minX);
        dataCursor = baseCursor = minX + minY * rasterWidth;
        this.band = -1;
    }

    @Override
    public boolean next() {
        if (++band == numBand) {
            band = 0;
            if (++dataCursor == numBanks) {
                return false;
            }
            if (dataCursor % rasterWidth % maxX == 0) {
                dataCursor += cursorStep;
            }
        }
        return true;
    }

    @Override
    public int getX() {
        return dataCursor%rasterWidth;
    }

    @Override
    public int getY() {
        return dataCursor/currentRaster.getWidth();
    }

    @Override
    public int getSample() {
        return dataArray[band][dataCursor];
    }

    @Override
    public float getSampleFloat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSampleDouble() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rewind() {
        dataCursor = 0;
        band = -1;
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
        band = -1;
        dataCursor = y*currentRaster.getWidth()+x;
    }
}
