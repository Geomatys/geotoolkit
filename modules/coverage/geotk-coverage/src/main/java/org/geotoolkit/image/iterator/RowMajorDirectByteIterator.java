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
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;

/**
 * An Iterator for traversing anyone rendered Image with Byte type data.
 * <p>
 * Iteration transverse each pixel from rendered image or raster source line per line.
 * <p>
 * Iteration follow this scheme :
 * tiles band --&gt; tiles x coordinates --&gt; next X tile position in rendered image tiles array
 * --&gt; current tiles y coordinates --&gt; next Y tile position in rendered image tiles array.
 *
 * Moreover iterator traversing a read-only each rendered image tiles(raster) in top-to-bottom, left-to-right order.
 *
 * Furthermore iterator is only appropriate to iterate on Byte data type.
 *
 * Code example :
 * {@code
 *                  final RowMajorDirectByteIterator dRII = new RowMajorDirectByteIterator(renderedImage);
 *                  while (dRII.next()) {
 *                      dRii.getSample();
 *                  }
 * }
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
class RowMajorDirectByteIterator extends RowMajorDirectIterator {

    /**
     * Current raster data table.
     */
    private byte[] currentDataArray;

    /**
     * Create Byte type row-major rendered image iterator.
     *
     * @param renderedImage image which will be follow by iterator.
     * @param subArea {@code Rectangle} which represent image sub area iteration.
     * @throws IllegalArgumentException if subArea don't intersect image boundary.
     */
    public RowMajorDirectByteIterator(RenderedImage renderedImage, Rectangle subArea) {
        super(renderedImage, subArea);
        assert (renderedImage.getTile(tMinX, tMinY).getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE)
               : "renderedImage datas or not Byte type";
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void updateCurrentRaster(int tileX, int tileY) {
        super.updateCurrentRaster(tileX, tileY);
        this.currentDataArray = ((DataBufferByte)currentRaster.getDataBuffer()).getData();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getSample() {
        return currentDataArray[dataCursor] & 0xff;
    }

    @Override
    public float getSampleFloat() {
        return currentDataArray[dataCursor] & 0xff;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double getSampleDouble() {
        return currentDataArray[dataCursor] & 0xff;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSample(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleFloat(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSampleDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
