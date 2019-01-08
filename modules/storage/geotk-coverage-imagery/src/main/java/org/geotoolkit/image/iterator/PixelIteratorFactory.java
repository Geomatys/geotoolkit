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
import java.awt.image.*;
import java.util.Arrays;

/**
 * Create an appropriate iterator.
 *
 * @author Rémi Marechal (Geomatys).
 */
public final class PixelIteratorFactory {

    private PixelIteratorFactory() {
    }

    /**
     * Create and return an adapted default raster iterator.
     *
     * @param raster   {@link Raster} will be traveled by iterator.
     * @return adapted {@link PixelIterator}.
     */
    public static PixelIterator createDefaultIterator(final Raster raster) {
        return createDefaultIterator(raster, null);
    }

    /**
     * Create and return an adapted default read-only raster iterator to read on raster sub-area.
     *
     * @param raster      {@link Raster} will be traveled by iterator from it's sub-area.
     * @param subReadArea {@link Rectangle} which define raster read area.
     * @return adapted    {@link PixelIterator}.
     */
    public static PixelIterator createDefaultIterator(final Raster raster, final Rectangle subReadArea) {
        final SampleModel sampleM = raster.getSampleModel();

        if (sampleM instanceof ComponentSampleModel) {
            if (checkBankIndices(((ComponentSampleModel)sampleM).getBankIndices())) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultDirectByteIterator(raster, subReadArea);
                    case DataBuffer.TYPE_FLOAT : return new DefaultDirectFloatIterator(raster, subReadArea);
                    default : return new DefaultIterator(raster, subReadArea);
                }
            }
        }
        return new DefaultIterator(raster, subReadArea);
    }

    /**
     * Create and return an adapted default read-only rendered image iterator.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator.
     * @return adapted      {@link PixelIterator}.
     */
    public static PixelIterator createDefaultIterator(final RenderedImage renderedImage) {
       return createDefaultIterator(renderedImage, null);
    }

    /**
     * Create and return an adapted default read-only raster iterator to read on raster sub-area.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param subReadArea   {@link Rectangle} which define rendered image read area.
     * @return adapted      {@link PixelIterator}.
     */
    public static PixelIterator createDefaultIterator(final RenderedImage renderedImage, final Rectangle subReadArea) {
        if(isSingleRaster(renderedImage)){
            return createDefaultIterator(renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY()), subReadArea);
        }

        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (checkBankIndices(((ComponentSampleModel)sampleM).getBankIndices())) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultDirectByteIterator(renderedImage, subReadArea);
                    case DataBuffer.TYPE_FLOAT : return new DefaultDirectFloatIterator(renderedImage, subReadArea);
                    default : return new DefaultIterator(renderedImage, subReadArea);
                }
            }
        }
        return new DefaultIterator(renderedImage, subReadArea);
    }

    /**
     * Create and return an adapted default read and write raster iterator.
     *
     * @param raster          {@link Raster} will be traveled by read-only iterator.
     * @param writeableRaster {@link WritableRaster} raster wherein value is set (write).
     * @return adapted        {@link PixelIterator} .
     */
    public static PixelIterator createDefaultWriteableIterator(final Raster raster, final WritableRaster writeableRaster) {
        return createDefaultWriteableIterator(raster, writeableRaster, null);
    }

    /**
     * Create and return an adapted default read and write raster iterator.
     * Iterator move in a raster sub-area.
     *
     * @param raster          {@link Raster} will be traveled by read-only iterator.
     * @param writeableRaster {@link WritableRaster} raster wherein value is set (write).
     * @param subArea     {@link Rectangle} which define raster read and write area.
     * @return adapted        {@link PixelIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final Raster raster, final WritableRaster writeableRaster, final Rectangle subArea) {
        final SampleModel srcSampleM = raster.getSampleModel();
        final SampleModel destSampleM = raster.getSampleModel();
        PixelIterator.checkRasters(raster, writeableRaster);

        if (srcSampleM instanceof ComponentSampleModel && destSampleM instanceof ComponentSampleModel) {
            ComponentSampleModel srcCSModel = (ComponentSampleModel) srcSampleM;
            ComponentSampleModel destCSModel = (ComponentSampleModel) destSampleM;

            // Source and destination image must have identical structure in order to allow a single iterator to move through them.
            if (checkBankIndices(srcCSModel.getBankIndices()) && checkBankIndices(destCSModel.getBankIndices())
             && Arrays.equals(srcCSModel.getBandOffsets(), destCSModel.getBandOffsets())
             && srcCSModel.getPixelStride() == destCSModel.getPixelStride()
             && srcCSModel.getScanlineStride() == destCSModel.getScanlineStride()) {

                switch (srcSampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultWritableDirectByteIterator(raster, writeableRaster, subArea);
                    case DataBuffer.TYPE_FLOAT : return new DefaultWritableDirectFloatIterator(raster, writeableRaster, subArea);
                    default : return new DefaultWritableIterator(raster, writeableRaster, subArea);
                }
            }
        }
        return new DefaultWritableIterator(raster, writeableRaster, subArea);
    }

    /**
     * Create and return an adapted default read and write rendered image iterator.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator.
     * @param writableRenderedImage {@link WritableRenderedImage} rendered image wherein value is set (write).
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return createDefaultWriteableIterator(renderedImage, writableRenderedImage, null);
    }

    /**
     * Create and return an adapted default read and write rendered image iterator from it's sub-area.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param writableRenderedImage {@link WritableRenderedImage} rendered image wherein value is set (write).
     * @param subArea               {@link Rectangle} which define rendered image read and write area.
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage, final Rectangle subArea) {
        final SampleModel srcSampleM = renderedImage.getSampleModel();
        final SampleModel destSampleM = renderedImage.getSampleModel();

        if (srcSampleM instanceof ComponentSampleModel && destSampleM instanceof ComponentSampleModel) {
            ComponentSampleModel srcCSModel = (ComponentSampleModel) srcSampleM;
            ComponentSampleModel destCSModel = (ComponentSampleModel) destSampleM;

            // Source and destination image must have identical structure in order to allow a single iterator to move through them.
            if (checkBankIndices(srcCSModel.getBankIndices()) && checkBankIndices(destCSModel.getBankIndices())
                    && Arrays.equals(srcCSModel.getBandOffsets(), destCSModel.getBandOffsets())
                    && srcCSModel.getPixelStride() == destCSModel.getPixelStride()
                    && srcCSModel.getScanlineStride() == destCSModel.getScanlineStride()) {

                switch (srcSampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultWritableDirectByteIterator(renderedImage, writableRenderedImage, subArea);
                    case DataBuffer.TYPE_FLOAT : return new DefaultWritableDirectFloatIterator(renderedImage, writableRenderedImage, subArea);
                    default : return new DefaultWritableIterator(renderedImage, writableRenderedImage, subArea);
                }
            }
        }
        return new DefaultWritableIterator(renderedImage, writableRenderedImage, subArea);
    }

    ////////////////////////////// Row Major Iterator ////////////////////////////

    /**
     * Create and return an adapted Row Major read-only rendered image iterator.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator.
     * @return adapted      {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorIterator(final RenderedImage renderedImage) {
        return createRowMajorIterator(renderedImage, null);
    }

    /**
     * Create and return an adapted Row Major read-only rendered image iterator from it's sub-area.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param subReadArea   {@link Rectangle} which define rendered image read-only area.
     * @return adapted      {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorIterator(final RenderedImage renderedImage, final Rectangle subReadArea) {
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            if (checkBankIndices(((ComponentSampleModel)sampleM).getBankIndices())) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new RowMajorDirectByteIterator(renderedImage, subReadArea);
                    case DataBuffer.TYPE_FLOAT : return new RowMajorDirectFloatIterator(renderedImage, subReadArea);
                    default : return new RowMajorIterator(renderedImage, subReadArea);
                }
            }
        }
        return new RowMajorIterator(renderedImage, subReadArea);
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * No build is allowed for single {@link java.awt.image.Raster} browsing, because {@link org.geotoolkit.image.iterator.DefaultDirectIterator}
     * will do the job as fast as it, and with the same behavior.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return createRowMajorWriteableIterator(renderedImage, writableRenderedImage, null);
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator from it's sub-area.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * No build is allowed for single {@link java.awt.image.Raster} browsing, because {@link org.geotoolkit.image.iterator.DefaultDirectIterator}
     * will do the job as fast as it, and with the same behavior.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @param subArea               {@link Rectangle} which define rendered image read and write area.
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage, final Rectangle subArea) {
        final SampleModel srcSampleM = renderedImage.getSampleModel();
        final SampleModel destSampleM = renderedImage.getSampleModel();

        if (srcSampleM instanceof ComponentSampleModel && destSampleM instanceof ComponentSampleModel) {
            ComponentSampleModel srcCSModel = (ComponentSampleModel) srcSampleM;
            ComponentSampleModel destCSModel = (ComponentSampleModel) destSampleM;

            // Source and destination image must have identical structure in order to allow a single iterator to move through them.
            if (checkBankIndices(srcCSModel.getBankIndices()) && checkBankIndices(destCSModel.getBankIndices())
                    && Arrays.equals(srcCSModel.getBandOffsets(), destCSModel.getBandOffsets())
                    && srcCSModel.getPixelStride() == destCSModel.getPixelStride()
                    && srcCSModel.getScanlineStride() == destCSModel.getScanlineStride()) {

                switch (srcSampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new RowMajorWritableDirectByteIterator(renderedImage, writableRenderedImage, subArea);
                    case DataBuffer.TYPE_FLOAT : return new RowMajorWritableDirectFloatIterator(renderedImage, writableRenderedImage, subArea);
                    default : return new RowMajorWritableIterator(renderedImage, writableRenderedImage, subArea);
                }
            }
        }
        return new RowMajorWritableIterator(renderedImage, writableRenderedImage, subArea);
    }

    /**
     * Check image samples are stored in a single bank. It's a needed condition for {@link org.geotoolkit.image.iterator.DefaultDirectIterator}
     *
     * @param bankIndices bank indice table retrieved from input image (see {@link java.awt.image.ComponentSampleModel#getBankIndices()}.
     * @return true if input image use a single bank. false otherwise.
     */
    private static boolean checkBankIndices(int[] bankIndices) {
        if (bankIndices.length == 1) return true;
        for (int i = 1, l = bankIndices.length; i<l; i++) if (bankIndices[i] != bankIndices[i-1]) return false;
        return true;
    }

    private static boolean isSingleRaster(final RenderedImage renderedImage){
        return renderedImage.getNumXTiles()==1 && renderedImage.getNumYTiles()==1;
    }
}
