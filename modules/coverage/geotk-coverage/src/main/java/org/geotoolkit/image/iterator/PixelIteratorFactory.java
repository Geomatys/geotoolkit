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
        final SampleModel sampleM = raster.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultDirectByteIterator(raster, null);
                    case DataBuffer.TYPE_FLOAT : return new DefaultDirectFloatIterator(raster, null);
                    default : return new DefaultIterator(raster, null);
                }
            }
        }
        return new DefaultIterator(raster, null);
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
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
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
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultDirectByteIterator(renderedImage, null);
                    case DataBuffer.TYPE_FLOAT : return new DefaultDirectFloatIterator(renderedImage, null);
                    default : return new DefaultIterator(renderedImage, null);
                }
            }
        }
        return new DefaultIterator(renderedImage, null);
    }

    /**
     * Create and return an adapted default read-only raster iterator to read on raster sub-area.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param subReadArea   {@link Rectangle} which define rendered image read area.
     * @return adapted      {@link PixelIterator}.
     */
    public static PixelIterator createDefaultIterator(final RenderedImage renderedImage, final Rectangle subReadArea) {
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
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
        final SampleModel sampleM = raster.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultWritableDirectByteIterator(raster, writeableRaster, null);
                    case DataBuffer.TYPE_FLOAT : return new DefaultWritableDirectFloatIterator(raster, writeableRaster, null);
                    default : return new DefaultWritableIterator(raster, writeableRaster, null);
                }
            }
        }
        return new DefaultWritableIterator(raster, writeableRaster, null);
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
        final SampleModel sampleM = raster.getSampleModel();
        if (sampleM instanceof ComponentSampleModel) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
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
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
                    case DataBuffer.TYPE_BYTE  : return new DefaultWritableDirectByteIterator(renderedImage, writableRenderedImage, null);
                    case DataBuffer.TYPE_FLOAT : return new DefaultWritableDirectFloatIterator(renderedImage, writableRenderedImage, null);
                    default : return new DefaultWritableIterator(renderedImage, writableRenderedImage, null);
                }
            }
        }
        return new DefaultWritableIterator(renderedImage, writableRenderedImage, null);
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
        final SampleModel sampleM = renderedImage.getSampleModel();
        if (sampleM instanceof ComponentSampleModel ) {
            if (sampleM.getNumDataElements() == sampleM.getNumBands() && sampleM.createDataBuffer().getNumBanks() == 1) {
                switch (sampleM.getDataType()) {
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
        switch (renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY()).getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE  : return new RowMajorDirectByteIterator(renderedImage, null);
            case DataBuffer.TYPE_FLOAT : return new RowMajorDirectFloatIterator(renderedImage, null);
            default : return new RowMajorIterator(renderedImage, null);
        }
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
        switch (renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY()).getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE  : return new RowMajorDirectByteIterator(renderedImage, subReadArea);
            case DataBuffer.TYPE_FLOAT : return new RowMajorDirectFloatIterator(renderedImage, subReadArea);
            default : return new RowMajorIterator(renderedImage, subReadArea);
        }
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        switch (renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY()).getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE  : return new RowMajorWritableDirectByteIterator(renderedImage, writableRenderedImage, null);
            case DataBuffer.TYPE_FLOAT : return new RowMajorWritableDirectFloatIterator(renderedImage, writableRenderedImage, null);
            default : return new RowMajorWritableIterator(renderedImage, writableRenderedImage, null);
        }
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator from it's sub-area.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @param subArea               {@link Rectangle} which define rendered image read and write area.
     * @return adapted              {@link PixelIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage, final Rectangle subArea) {
        switch (renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY()).getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE  : return new RowMajorWritableDirectByteIterator(renderedImage, writableRenderedImage, subArea);
            case DataBuffer.TYPE_FLOAT : return new RowMajorWritableDirectFloatIterator(renderedImage, writableRenderedImage, subArea);
            default : return new RowMajorWritableIterator(renderedImage, writableRenderedImage, subArea);
        }
    }
}
