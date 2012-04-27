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
 * An Iterator for traversing anyone rendered Image.
 * <p>
 * Iteration transverse each tiles(raster) from rendered image or raster source one by one in order.
 * Iteration to follow tiles(raster) begin by raster bands, next, raster x coordinates,
 * and to finish raster y coordinates.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&lt; tiles x coordinates --&lt; tiles y coordinates --&lt; next rendered image tiles.
 *
 * Moreover iterator traversing a write or read each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Code example :
 * {@code
 *                  final DefaultWritableRIIterator dWRII = new DefaultWritableRIIterator(renderedImage);
 *                  while (dWRII.next()) {
 *                      dWRII.setSample(int value);
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class DefaultWritableRIIterator extends DefaultRenderedImageIterator {

    /**
     * Raster which is followed by iterator and wherein caller write.
     */
    private WritableRaster currentWritableRaster;

    /**
     * Rendered image which is followed by iterator and wherein caller write.
     */
    private WritableRenderedImage writableRenderedImage;

    /**
     * Create an appropriate iterator to read and write in a raster.
     *
     * @param raster         : raster which is followed by read-only iterator.
     * @param writableRaster : raster which is followed by this write-only iterator and wherein value is writing.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     */
    public DefaultWritableRIIterator(Raster raster, WritableRaster writableRaster) {
        super(raster);
        if (raster.getMinX()     != writableRaster.getMinX()
         || raster.getMinY()     != writableRaster.getMinY()
         || raster.getWidth()    != writableRaster.getWidth()
         || raster.getHeight()   != writableRaster.getHeight()
         || raster.getNumBands() != writableRaster.getNumBands())
         throw new IllegalArgumentException("raster and writable raster are not in same dimension"+raster+writableRaster);
        this.currentWritableRaster = writableRaster;
    }

    /**
     * Create an appropriate iterator to read and write in a raster sub area.
     *
     * @param raster         : raster which is followed by read-only iterator.
     * @param writableRaster : raster which is followed by this write-only iterator and wherein value is writing.
     * @param subArea        : Rectangle which represent raster sub area, wherein move read and write iterator.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     */
    public DefaultWritableRIIterator(Raster raster, WritableRaster writableRaster, Rectangle subArea) {
        super(raster, subArea);
        if (raster.getMinX()     != writableRaster.getMinX()
         || raster.getMinY()     != writableRaster.getMinY()
         || raster.getWidth()    != writableRaster.getWidth()
         || raster.getHeight()   != writableRaster.getHeight()
         || raster.getNumBands() != writableRaster.getNumBands())
         throw new IllegalArgumentException("raster and writable raster dimensions are not conform "+raster+writableRaster);
        this.currentWritableRaster = writableRaster;
    }

    /**
     * Create an appropriate iterator to read and write in a rendered image.
     *
     * @param renderedImage : rendered image which is followed by read-only iterator.
     * @param writableRI    : writable rendered image which is followed by this write-only iterator and wherein value is writing.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     * @throws IllegalArgumentException if raster and writable raster tiles configurations are not conform.
     */
    public DefaultWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRI) {
        super(renderedImage);
        if (renderedImage.getMinX()     != writableRI.getMinX()
         || renderedImage.getMinY()     != writableRI.getMinY()
         || renderedImage.getWidth()    != writableRI.getWidth()
         || renderedImage.getHeight()   != writableRI.getHeight()
         || renderedImage.getSampleModel().getNumBands() != writableRI.getSampleModel().getNumBands())
         throw new IllegalArgumentException("rendered image and writable rendered image dimensions are not conform"+renderedImage+writableRI);

        if (renderedImage.getMinTileX() != writableRI.getMinTileX()
         || renderedImage.getMinTileY() != writableRI.getMinTileY()
         || renderedImage.getNumXTiles()!= writableRI.getNumXTiles()
         || renderedImage.getNumYTiles()!= writableRI.getNumYTiles()
         || renderedImage.getTileGridXOffset() != writableRI.getTileGridXOffset()
         || renderedImage.getTileGridYOffset() != writableRI.getTileGridYOffset()
         || renderedImage.getTileHeight() != writableRI.getTileHeight()
         || renderedImage.getTileWidth()  != writableRI.getTileWidth())
         throw new IllegalArgumentException("rendered image and writable rendered image tiles configuration are not conform"+renderedImage+writableRI);
        this.writableRenderedImage = writableRI;
    }

    /**
     * Create an appropriate iterator to read and write in a rendered image sub area.
     *
     * @param renderedImage : rendered image which is followed by read-only iterator.
     * @param writableRI    : writable rendered image which is followed by this write-only iterator and wherein value is writing.
     * @param subArea       : Rectangle which represent rendered image sub area, wherein move read and write iterator.
     * @throws IllegalArgumentException if raster and writable raster dimensions are not conform.
     * @throws IllegalArgumentException if raster and writable raster tiles configurations are not conform.
     */
    public DefaultWritableRIIterator(RenderedImage renderedImage, WritableRenderedImage writableRI, Rectangle subArea) {
        super(renderedImage, subArea);
        if (renderedImage.getMinX()     != writableRI.getMinX()
         || renderedImage.getMinY()     != writableRI.getMinY()
         || renderedImage.getWidth()    != writableRI.getWidth()
         || renderedImage.getHeight()   != writableRI.getHeight()
         || renderedImage.getSampleModel().getNumBands() != writableRI.getSampleModel().getNumBands())
         throw new IllegalArgumentException("rendered image and writable rendered image dimensions are not conform"+renderedImage+writableRI);

        if (renderedImage.getMinTileX() != writableRI.getMinTileX()
         || renderedImage.getMinTileY() != writableRI.getMinTileY()
         || renderedImage.getNumXTiles()!= writableRI.getNumXTiles()
         || renderedImage.getNumYTiles()!= writableRI.getNumYTiles()
         || renderedImage.getTileGridXOffset() != writableRI.getTileGridXOffset()
         || renderedImage.getTileGridYOffset() != writableRI.getTileGridYOffset()
         || renderedImage.getTileHeight() != writableRI.getTileHeight()
         || renderedImage.getTileWidth()  != writableRI.getTileWidth())
         throw new IllegalArgumentException("rendered image and writable rendered image tiles configuration are not conform"+renderedImage+writableRI);
        this.writableRenderedImage = writableRI;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        currentWritableRaster.setSample(x, y, band, value);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        currentWritableRaster.setSample(x, y, band, value);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        currentWritableRaster.setSample(x, y, band, value);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void updateCurrentRaster(int tileX, int tileY) {
        super.updateCurrentRaster(tileX, tileY);
        this.currentWritableRaster = writableRenderedImage.getWritableTile(tileX, tileY);
    }
}
