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

import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define Bilinear Interpolation.
 *
 * Bilinear interpolation is computed from 4 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BilinearInterpolation extends SeparableInterpolation {

    /**
     * Create a Bilinear Interpolator.
     * 
     * @param pixelIterator Iterator used to interpolation.
     * @param borderChoice define comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     */
    public BilinearInterpolation(PixelIterator pixelIterator, ResampleBorderComportement rbc, double[] fillValue) {
        super(pixelIterator, 2, rbc, fillValue);
    }
    
    /**
     * Create a Bilinear Interpolator.<br/><br/>
     * 
     * Define border comportement at {@link ResampleBorderComportement#FILL_VALUE} 
     * and fillValue is an arrays of the same length than band number from source image and filled by {@link Double#NaN} value.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BilinearInterpolation(PixelIterator pixelIterator) {
        this(pixelIterator, ResampleBorderComportement.FILL_VALUE, null);
    }

    /**
     * Compute linear interpolation between 2 values.
     * {@inheritDoc }
     */
    @Override
    protected double interpolate1D(double t0, double t, double... f) {
        assert (f.length == 2) : " bilinear interpolation table not conform";
        return (t-t0)*(f[1]-f[0]) + f[0];
    }
}
