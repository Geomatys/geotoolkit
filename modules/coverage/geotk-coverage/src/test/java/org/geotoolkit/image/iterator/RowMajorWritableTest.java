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
import java.awt.image.WritableRenderedImage;

/**
 * Implement only row major writable tests.
 *
 * @author Rémi Marechal (Geomatys).
 */
public abstract class RowMajorWritableTest extends WritableIteratorTest {

    /**
     * {@inheritDoc }.
     */
    @Override
    public void differentMinRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectContainsRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerLeftRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectLowerRightRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperLeftRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rectUpperRightRasterReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rasterContainsRectReadTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateRectRasterTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void unappropriateMoveToRasterTest() {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setRasterTest(int minx, int miny, int width, int height, int numBand, Rectangle subArea) {
        //no test about raster for this iterator.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(Raster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
     @Override
    protected void setPixelIterator(Raster raster, Rectangle subArea) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

     /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage) {
        pixIterator = PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void setPixelIterator(RenderedImage renderedImage, Rectangle subArea) {
        pixIterator = PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, (WritableRenderedImage)renderedImage, subArea);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected PixelIterator getWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRenderedImage) {
        return PixelIteratorFactory.createRowMajorWriteableIterator(renderedImage, writableRenderedImage);
    }
}
