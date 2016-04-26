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
package org.geotoolkit.image.interpolation;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Interpolation which interpolate in X direction and Y direction independently.
 *
 * @author Rémi Maréchal (Geomatys).
 */
abstract class SeparableInterpolation extends Interpolation {

    /** 2D Array storing row data of each band when an interpolation per pixel is performed. */
    private final double[] rows;
    
    /** 2D Array storing row interpolated data for each band when an interpolation per pixel is performed. */
    private final double[] cols;
    
    /**
     * {@link Rectangle} which represente pixel area needed to compute interpolation from projected source coordinate.
     */
    protected final Rectangle interpolArea;
    
    /**
     * Array which contain all samples values needed to compute interpolation.
     * Moreover its length equals band number 
     */
    protected final Object[] buffer;
    
    /**
     * {@link DataBuffer} type of internal datas.
     */
    private final int sourceDataType;
    
    /**
     * Build a bi-dimensional interpolation. 
     * 
     * @param pixelIterator iterator which travel source image samples.
     * @param windowSide length of samples in X and Y direction needed from interpolation type.
     * @param rbc enum which define interpolation comportement at the source image border.
     * @param fillValue define destination sample value in case where interpolation is out of source image boundary.
     */
    public SeparableInterpolation(PixelIterator pixelIterator, int windowSide, ResampleBorderComportement rbc, double[] fillValue) {
        super(pixelIterator, windowSide, rbc, fillValue);
        rows = new double[windowSide];
        cols = new double[windowSide];
        interpolArea = new Rectangle(windowSide, windowSide);
        sourceDataType = pixelIterator.getSourceDatatype();
        switch (sourceDataType) {
            case DataBuffer.TYPE_BYTE   : {
                buffer = new byte[numBands][windowSide * windowSide];
                break;
            }
            case DataBuffer.TYPE_SHORT  : 
            case DataBuffer.TYPE_USHORT : {
                buffer = new short[numBands][windowSide * windowSide];
                break;
            }
            case DataBuffer.TYPE_INT    : {
                buffer = new int[numBands][windowSide * windowSide];
                break;
            }
            case DataBuffer.TYPE_FLOAT  : {
                buffer = new float[numBands][windowSide * windowSide];
                break;
            }
            case DataBuffer.TYPE_DOUBLE : {
                buffer = new double[numBands][windowSide * windowSide];
                break;
            }
            default : throw new IllegalArgumentException("Unknow datatype");
        }
    }
    
    /**
     * Returns interpolate value from x, y pixel coordinates and band index.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param b band index.
     * @return interpolate value from x, y pixel coordinates and band index.  
     */
    @Override
    public double interpolate(double x, double y, int b) {
        ArgumentChecks.ensureBetween("band index", 0, getNumBands(), b);
//        if (!checkInterpolate(x, y)) return fillValue[b];
        setInterpolateMin(x, y);
        
        interpolArea.setLocation(minX, minY);
        pixelIterator.getArea(interpolArea, buffer[b], b);
        
        int bufferID = 0;
        for (int dy = 0; dy < windowSide; dy++) {
            WriteInInterpolArray(buffer[b], bufferID, rows, 0, windowSide);
            bufferID += windowSide;
            cols[dy] = interpolate1D(minX, x, rows);
        }
        return interpolate1D(minY, y, cols);
    }

    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    @Override
    public double[] interpolate(double x, double y) {
//        if (!checkInterpolate(x, y)) return fillValue;
        setInterpolateMin(x, y);
        
        interpolArea.setLocation(minX, minY);
        pixelIterator.getArea(interpolArea, buffer);
        int bufferID;
        for (int b = 0; b < numBands; b++) {
            bufferID = 0;
            for (int dy = 0; dy < windowSide; dy++) {
                WriteInInterpolArray(buffer[b], bufferID, rows, 0, windowSide);
                bufferID += windowSide;
                cols[dy] = interpolate1D(minX, x, rows);
            }
            result[b] = interpolate1D(minY, y, cols);
        }
        return result;
    }
    
    /**
     * Fill double destination array from unknow type source array.
     * 
     * @param src source array.
     * @param srcPos first copied source array element.
     * @param dest destination double array which will be filled.
     * @param destPos first copied destination element.
     * @param length length of the copy.
     */
    private void WriteInInterpolArray (final Object src, int srcPos, final double[] dest, int destPos, final int length) {
         int l = -1;
        switch (sourceDataType) {
            case DataBuffer.TYPE_BYTE   : {
                byte[] array = (byte[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++] & 0xFF;
                }
                break;
            }
            case DataBuffer.TYPE_SHORT  : {
                short[] array = (short[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++];
                }
                break;
            }
            case DataBuffer.TYPE_USHORT : {
                short[] array = (short[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++] & 0xFFFF;
                }
                break;
            }
            case DataBuffer.TYPE_INT    : {
                int[] array = (int[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++];
                }
                break;
            }
            case DataBuffer.TYPE_FLOAT  : {
                float[] array = (float[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++];
                }
                break;
            }
            case DataBuffer.TYPE_DOUBLE : {
                double[] array = (double[])src;
                while (++l < length) {
                    dest[destPos++] = array[srcPos++];
                }
                break;
            }
            default : throw new IllegalArgumentException("Unknow datatype");
        }
    }
    
    /**
     * Compute interpolation value define by interpolation type implementation.
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param t position of interpolation.
     * @param f pixel values from t = {0 ... n}.
     * @return interpolation value.
     */
    protected abstract double interpolate1D(double t0, double t, double...f);
}
