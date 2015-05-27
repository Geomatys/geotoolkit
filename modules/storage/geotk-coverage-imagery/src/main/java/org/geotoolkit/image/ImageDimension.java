/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.awt.Dimension;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.apache.sis.util.Classes;


/**
 * An image dimension, including the number of bands.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public class ImageDimension extends Dimension {
    /**
     * For compatibility between different version of this class.
     */
    private static final long serialVersionUID = -4349573462196081362L;

    /**
     * The number of bands in the image or raster.
     */
    public int numBands;

    /**
     * The image data type.
     */
    private final int dataType;

    /**
     * Creates a new dimension initialized to the dimension of the given image.
     *
     * @param image The image from which to fetch the dimensions.
     */
    public ImageDimension(final RenderedImage image) {
        super(image.getWidth(), image.getHeight());
        final SampleModel model = image.getSampleModel();
        numBands = model.getNumBands();
        dataType = model.getDataType();
    }

    /**
     * Creates a new dimension initialized to the dimension of the given raster.
     *
     * @param raster The raster from which to fetch the dimensions.
     */
    public ImageDimension(final Raster raster) {
        super(raster.getWidth(), raster.getHeight());
        numBands = raster.getNumBands();
        dataType = raster.getDataBuffer().getDataType();
    }

    /**
     * Returns the number of sample values. This is the product of
     * {@link #width width}, {@link #height height} and {@link #numBands}.
     *
     * @return The number of sample values.
     */
    public long getNumSampleValues() {
        return (long) width * (long) height * (long) numBands;
    }

    /**
     * Returns the number of bytes required in order to memorize {@linkplain #getNumSampleValues
     * all sample values}. The sample values size is determined by the image or raster given at
     * construction time.
     *
     * @return The number bytes requires for storing the image.
     */
    public long getMemoryUsage() {
        return getNumSampleValues() * (DataBuffer.getDataTypeSize(dataType) / Byte.SIZE);
    }

    /**
     * Checks whether two dimension objects have equal values.
     *
     * @param object The object to compare with this dimension for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (object instanceof ImageDimension) {
            final ImageDimension that = (ImageDimension) object;
            return this.numBands == that.numBands && this.dataType == that.dataType;
        }
        return true; // For preserving reflexivity.
    }

    /**
     * Returns the hash code for this dimension.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + 31*numBands;
    }

    /**
     * Returns a string representation of this dimension.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) +
                "[width=" + width + ", height=" + height + ", numBands=" + numBands + ']';
    }
}
