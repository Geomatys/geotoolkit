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
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;

/**
 * Define BiCubic Interpolation.
 *
 * BiCubic interpolation is computed from 16 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
abstract class BiCubicInterpolation extends SeparableInterpolation {

    /**
     * Minimum value authorized from type of data from source interpolation.
     */
    private final double minValue;

    /**
     * maximum value authorized from type of data from source interpolation.
     */
    private final double maxValue;

    /**
     * <p>Create an BiCubic Interpolator.<br/>
     * This definition is also sometimes known as "cubic convolution".<br/><br/>
     *
     * @param pixelIterator Iterator used to interpolation.
     * @param borderChoice define comportement of the destination image border.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator, ResampleBorderComportement borderChoice, double[] fillValue) {
        super(pixelIterator, 4, borderChoice, fillValue);
        NumberRange<?> range = pixelIterator.getSampleRanges()[0];
        minValue = range.getMinDouble();
        maxValue = range.getMaxDouble();
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller" + boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller" + boundary.height);
    }

    /**
     * Create a bicubic interpolation.<br/><br/>
     *
     * Define border comportement at {@link ResampleBorderComportement#FILL_VALUE}
     * and fillValue is an arrays of the same length than band number from source image and filled by {@link Double#NaN} value.
     *
     * @param pixelIterator Iterator to iterate on source image.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        this(pixelIterator, ResampleBorderComportement.FILL_VALUE, null);
    }

    /**
     * <p>Verify value is in [{@link #minValue}; {@link #maxValue}] interval.<br/><br/>
     *
     * If value &lt; {@link #minValue} value = {@link #minValue}.
     * If value &gt; {@link #maxValue} value = {@link #maxValue}.</p>
     *
     * @param value double which will be verify.
     * @return
     */
    protected double checkValue(double value) {
        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }
        return value;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
