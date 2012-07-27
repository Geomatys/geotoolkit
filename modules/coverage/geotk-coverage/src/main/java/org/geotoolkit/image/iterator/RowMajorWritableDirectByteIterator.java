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
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;

/**
 * An Iterator for traversing anyone rendered Image with Byte type data.
 * <p>
 * Iteration transverse each pixel from rendered image source line per line.
 * <p>
 *
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; next X tile position in rendered image tiles array
 * --&gt; current tiles y coordinates --&gt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a write or read each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Furthermore iterator is only appropriate to iterate on Byte data type.
 *
 * Code example :
 * {@code
 *                  final RowMajorWritableDirectByteIterator dWRII = new RowMajorWritableDirectByteIterator(renderedImage);
 *                  while (dWRII.next()) {
 *                      dWRII.setSample(int value);
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
class RowMajorWritableDirectByteIterator extends RowMajorDirectByteIterator{

    /**
     * Current last x tile position in rendered image tile array.
     */
    private int prectX;

    /**
     * Current last y tile position in rendered image tile array.
     */
    private int prectY;

    /**
     * Current writable raster data table.
     */
    private byte[] currentWritableDataArray;

    /**
     * Rendered image which is followed by iterator and wherein caller write.
     */
    private WritableRenderedImage writableRenderedImage;

    /**
     * Create an appropriate iterator to read and write in a rendered image sub area, row by row.
     *
     * @param renderedImage rendered image which is followed by read-only iterator.
     * @param writableRI    writable rendered image which is followed by this write-only iterator and wherein value is writing.
     * @param subArea       Rectangle which represent rendered image sub area, wherein move read and write iterator.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage dimensions are not conform.
     * @throws IllegalArgumentException if renderedImage and writable renderedImage tiles configurations are not conform.
     */
    RowMajorWritableDirectByteIterator(final RenderedImage renderedImage, final WritableRenderedImage writableRI, final Rectangle subArea) {
        super(renderedImage, subArea);
        checkRenderedImage(renderedImage, writableRI);
        this.writableRenderedImage = writableRI;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() {
        currentWritableDataArray = null;
        super.rewind();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        currentWritableDataArray[dataCursor] = (byte) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        currentWritableDataArray[dataCursor] = (byte) value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        currentWritableDataArray[dataCursor] = (byte) value;
    }

    /**
     * Update current writable raster from tiles array coordinates.
     */
    @Override
    protected void updateCurrentRaster(int tileX, int tileY) {
        super.updateCurrentRaster(tileX, tileY);
        if (currentWritableDataArray != null) writableRenderedImage.releaseWritableTile(prectX, prectY);
        currentWritableDataArray = ((DataBufferByte) writableRenderedImage.getWritableTile(tileX, tileY).getDataBuffer()).getData();
        this.prectX = tileX;
        this.prectY = tileY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() {
         writableRenderedImage.releaseWritableTile(prectX, prectY);
    }
}
