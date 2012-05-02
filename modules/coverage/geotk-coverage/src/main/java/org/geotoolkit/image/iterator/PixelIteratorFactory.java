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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;

/**
 * Create some appropriate iterator.
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
     * @return adapted {@link DefaultRenderedImageIterator}.
     */
    public static PixelIterator createDefaultIterator(final Raster raster) {
        return new DefaultIterator(raster);
    }

    /**
     * Create and return an adapted default read-only raster iterator to read on raster sub-area.
     *
     * @param raster      {@link Raster} will be traveled by iterator from it's sub-area.
     * @param subReadArea {@link Rectangle} which define raster read area.
     * @return adapted    {@link DefaultRenderedImageIterator}.
     */
    public static PixelIterator createDefaultIterator(final Raster raster, final Rectangle subReadArea) {
        return new DefaultIterator(raster, subReadArea);
    }

    /**
     * Create and return an adapted default read-only rendered image iterator.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator.
     * @return adapted      {@link DefaultRenderedImageIterator}.
     */
    public static PixelIterator createDefaultIterator(final RenderedImage renderedImage) {
        return new DefaultIterator(renderedImage);
    }

    /**
     * Create and return an adapted default read-only raster iterator to read on raster sub-area.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param subReadArea   {@link Rectangle} which define rendered image read area.
     * @return adapted      {@link DefaultRenderedImageIterator}.
     */
    public static PixelIterator createDefaultIterator(final RenderedImage renderedImage, final Rectangle subReadArea) {
        return new DefaultIterator(renderedImage, subReadArea);
    }

    /**
     * Create and return an adapted default read and write raster iterator.
     *
     * @param raster          {@link Raster} will be traveled by read-only iterator.
     * @param writeableRaster {@link WritableRaster} raster wherein value is set (write).
     * @return adapted        {@link DefaultWritableRIIterator} .
     */
    public static PixelIterator createDefaultWriteableIterator(final Raster raster, final WritableRaster writeableRaster) {
        return new DefaultWritableIterator(raster, writeableRaster);
    }

    /**
     * Create and return an adapted default read and write raster iterator.
     * Iterator move in a raster sub-area.
     *
     * @param raster          {@link Raster} will be traveled by read-only iterator.
     * @param writeableRaster {@link WritableRaster} raster wherein value is set (write).
     * @param subReadArea     {@link Rectangle} which define raster read and write area.
     * @return adapted        {@link DefaultWritableRIIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final Raster raster, final WritableRaster writeableRaster, final Rectangle subReadArea) {
        return new DefaultWritableIterator(raster, writeableRaster, subReadArea);
    }

    /**
     * Create and return an adapted default read and write rendered image iterator.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator.
     * @param writableRenderedImage {@link WritableRenderedImage} rendered image wherein value is set (write).
     * @return adapted              {@link DefaultWritableRIIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return new DefaultWritableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * Create and return an adapted default read and write rendered image iterator from it's sub-area.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param writableRenderedImage {@link WritableRenderedImage} rendered image wherein value is set (write).
     * @param subReadArea           {@link Rectangle} which define rendered image read and write area.
     * @return adapted              {@link DefaultWritableRIIterator}.
     */
    public static PixelIterator createDefaultWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage, final Rectangle subReadArea) {
        return new DefaultWritableIterator(renderedImage, writableRenderedImage, subReadArea);
    }

    /**
     * Create and return an adapted Row Major read-only rendered image iterator.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator.
     * @return adapted      {@link RowMajorRenderedImageIterator}.
     */
    public static PixelIterator createRowMajorIterator(final RenderedImage renderedImage) {
        return new RowMajorIterator(renderedImage);
    }

    /**
     * Create and return an adapted Row Major read-only rendered image iterator from it's sub-area.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param subReadArea   {@link Rectangle} which define rendered image read-only area.
     * @return adapted      {@link RowMajorRenderedImageIterator}.
     */
    public static PixelIterator createRowMajorIterator(final RenderedImage renderedImage, final Rectangle subReadArea) {
        return new RowMajorIterator(renderedImage, subReadArea);
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @return adapted              {@link RowMajorWritableRIIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage) {
        return new RowMajorWritableIterator(renderedImage, writableRenderedImage);
    }

    /**
     * Create and return an adapted Row Major read and write rendered image iterator from it's sub-area.
     * RowMajor : iterator move forward line per line one by one in downward order.
     *
     * @param renderedImage         {@link RenderedImage} will be traveled by iterator from it's sub-area.
     * @param writableRenderedImage {@link WritableRenderedImage}  rendered image wherein value is set (write).
     * @param subReadArea           {@link Rectangle} which define rendered image read and write area.
     * @return adapted              {@link RowMajorWritableRIIterator}.
     */
    public static PixelIterator createRowMajorWriteableIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRenderedImage, final Rectangle subReadArea) {
        return new RowMajorWritableIterator(renderedImage, writableRenderedImage, subReadArea);
    }
}
