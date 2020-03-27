/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Hashtable;
import javax.media.jai.RasterFactory;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.coverage.j2d.ColorModelFactory;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferedImages extends Static {

    /**
     * Create a new image, trying to preserve raster, sample model and color model
     * when possible.
     *
     * @param reference not null
     * @param width if null reference image width is copied
     * @param height if null reference image height is copied
     * @param nbBand if null reference image number of bands is copied
     * @param dataType if null reference image data type is copied
     * @return
     * @throws IllegalArgumentException
     */
    public static BufferedImage createImage(RenderedImage reference, Integer width, Integer height, Integer nbBand, Integer dataType) throws IllegalArgumentException{
        if (width == null) width = reference.getWidth();
        if (height == null) height = reference.getHeight();
        if (nbBand == null) nbBand = reference.getSampleModel().getNumBands();
        if (dataType == null) dataType = reference.getSampleModel().getDataType();
        ArgumentChecks.ensureStrictlyPositive("width", width);
        ArgumentChecks.ensureStrictlyPositive("height", height);
        ArgumentChecks.ensureStrictlyPositive("nbBand", nbBand);

        if (nbBand == reference.getSampleModel().getNumBands() && dataType == reference.getSampleModel().getDataType()) {
            //we can preserver color model and raster configuration
            final Raster anyTile = reference.getTile(reference.getMinTileX(), reference.getMinTileY());
            final WritableRaster raster = anyTile.createCompatibleWritableRaster(width, height);
            final ColorModel cm = reference.getColorModel();
            final BufferedImage resultImage = new BufferedImage(cm,raster,cm.isAlphaPremultiplied(),new Hashtable<>());
            return resultImage;
        } else {
            //we need to create a new image
            return createImage(width, height, nbBand, dataType);
        }
    }

    /**
     * Create a new image of the same type with a different size.
     *
     * @param width
     * @param height
     * @param reference
     * @return
     * @throws IllegalArgumentException
     */
    public static BufferedImage createImage(final int width, final int height, RenderedImage reference) throws IllegalArgumentException {
        return createImage(reference, width, height, null, null);
    }

    public static BufferedImage createImage(final int width, final int height, final int nbBand, final int dataType) throws IllegalArgumentException{
        ArgumentChecks.ensureStrictlyPositive("width", width);
        ArgumentChecks.ensureStrictlyPositive("height", height);
        final Point upperLeft = new Point(0,0);
        final WritableRaster raster = createRaster(width, height, nbBand, dataType, upperLeft);

        //TODO try to reuse java colormodel if possible
        //create a temporary fallback colormodel which will always work
        //extract grayscale min/max from sample dimension
        final ColorModel graycm = ColorModelFactory.createGrayScale(dataType, nbBand, 0, 0, 1);
        final BufferedImage resultImage = new BufferedImage(graycm, raster, false, new Hashtable<>());
        return resultImage;
    }

    public static WritableRaster createRaster(int width, int height, int nbBand, int dataType, Point upperLeft) throws IllegalArgumentException{
        ArgumentChecks.ensureStrictlyPositive("width", width);
        ArgumentChecks.ensureStrictlyPositive("height", height);
        final WritableRaster raster;
        if(nbBand == 1){
            if(dataType == DataBuffer.TYPE_BYTE || dataType == DataBuffer.TYPE_USHORT || dataType == DataBuffer.TYPE_INT){
                raster = WritableRaster.createBandedRaster(dataType, width, height, nbBand, upperLeft);
            }else{
                //create it ourself
                final DataBuffer buffer;
                if(dataType == DataBuffer.TYPE_SHORT) buffer = new DataBufferShort(width*height);
                else if(dataType == DataBuffer.TYPE_FLOAT) buffer = new DataBufferFloat(width*height);
                else if(dataType == DataBuffer.TYPE_DOUBLE) buffer = new DataBufferDouble(width*height);
                else throw new IllegalArgumentException("Type not supported "+dataType);
                final int[] zero = new int[1];
                //TODO create our own raster factory to avoid JAI
                raster = RasterFactory.createBandedRaster(buffer, width, height, width, zero, zero, upperLeft);
            }

        }else{
            if(dataType == DataBuffer.TYPE_BYTE || dataType == DataBuffer.TYPE_USHORT){
                raster = WritableRaster.createInterleavedRaster(dataType, width, height, nbBand, upperLeft);
            }else{
                //create it ourself
                final long size = (long) width * height * nbBand;
                final int isize = Math.toIntExact(size);
                final DataBuffer buffer;
                switch (dataType) {
                    case DataBuffer.TYPE_SHORT: buffer = new DataBufferShort(isize); break;
                    case DataBuffer.TYPE_INT: buffer = new DataBufferInt(isize); break;
                    case DataBuffer.TYPE_FLOAT: buffer = new DataBufferFloat(isize); break;
                    case DataBuffer.TYPE_DOUBLE: buffer = new DataBufferDouble(isize); break;
                    default: throw new IllegalArgumentException("Type not supported "+dataType);
                }
                final int[] bankIndices = new int[nbBand];
                final int[] bandOffsets = new int[nbBand];
                for(int i=1;i<nbBand;i++){
                    bandOffsets[i] = bandOffsets[i-1] + width*height;
                }
                //TODO create our own raster factory to avoid JAI
                raster = RasterFactory.createBandedRaster(buffer, width, height, width, bankIndices, bandOffsets, upperLeft);
            }
        }
        return raster;
    }

    /**
     * Convert a primitive array to a DataBuffer.<br>
     * This DataBuffer can then be used to create a WritableRaster.<br>
     * The array is directly used by the buffer, they are not copied.
     *
     * @param data primitive array object with 1 or 2 dimensions.
     * @return DataBuffer never null
     * @throws IllegalArgumentException if the array type is not supported.
     */
    public static DataBuffer toDataBuffer(Object data) throws IllegalArgumentException{
        if(data instanceof byte[]){
            return new DataBufferByte((byte[])data,Array.getLength(data));
        }else if(data instanceof short[]){
            return new DataBufferShort((short[])data,Array.getLength(data));
        }else if(data instanceof int[]){
            return new DataBufferInt((int[])data,Array.getLength(data));
        }else if(data instanceof float[]){
            return new DataBufferFloat((float[])data,Array.getLength(data));
        }else if(data instanceof double[]){
            return new DataBufferDouble((double[])data,Array.getLength(data));
        }

        else if(data instanceof byte[][]){
            return new DataBufferByte((byte[][])data,Array.getLength(Array.get(data, 0)));
        }else if(data instanceof short[][]){
            return new DataBufferShort((short[][])data,Array.getLength(Array.get(data, 0)));
        }else if(data instanceof int[][]){
            return new DataBufferInt((int[][])data,Array.getLength(Array.get(data, 0)));
        }else if(data instanceof float[][]){
            return new DataBufferFloat((float[][])data,Array.getLength(Array.get(data, 0)));
        }else if(data instanceof double[][]){
            return new DataBufferDouble((double[][])data,Array.getLength(Array.get(data, 0)));
        }

        else{
            throw new IllegalArgumentException("Unexpected array type "+data.getClass());
        }
    }

    /**
     * Compare the pixles of given image to reference pixel and return true
     * if all pixels share those same samples.
     *
     * @param img image to test
     * @param pixel reference pixel to compare with
     * @return true if all pixels is image are equal to reference pixel.
     */
    public static boolean isAll(RenderedImage img, double[] pixel) {
        final PixelIterator ite = PixelIterator.create(img);
        final double[] buffer = new double[pixel.length];
        while (ite.next()) {
            ite.getPixel(buffer);
            if (!Arrays.equals(pixel, buffer)) {
                return false;
            }
        }
        return true;
    }

    public static void setAll(WritableRenderedImage img, double[] pixel) {
        final WritablePixelIterator ite = WritablePixelIterator.create(img);
        while (ite.next()) {
            ite.setPixel(pixel);
        }
    }
}
