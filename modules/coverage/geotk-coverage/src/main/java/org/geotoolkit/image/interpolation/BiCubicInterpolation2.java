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
 * <p>This interpolator may produce somewhat sharper results than classic
 * classic BiCubic Interpolation, but that result is image dependent.<br/>
 * It's a consequence from increase of biCubic curve amplitude.<br/>
 * It use the parameter 'a' recommended by Keys.<br/>
 * (Reference: Digital Image Warping, George Wolberg, 1990, pp 129-131, IEEE Computer Society Press, ISBN 0-8186-8944-7)</p>
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BiCubicInterpolation2 extends BiCubicInterpolation {

    /**
     * <p>Parameter use in "cubic convolution" computing.<br/>
     * a equal -0.5 for classic biCubic interpolation.<br/>
     * a equal -1.0 for more amplitude between interpolation results.</p>
     *
     * @see #getConvolutionValue(double) .
     */
    private final double a = -1.0;

    /**
     * {@inheritDoc }.
     */
    public BiCubicInterpolation2(PixelIterator pixelIterator) {
        super(pixelIterator);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    double getCubicValue(double t0, double t, double ...f) {
        double result = 0;
        int compteur = 0;
        for (double ft : f) {
            result += getConvolutionValue(t-t0 - compteur++)*ft;
        }
        return result;
    }

    /**
     * Compute coefficient apply on current pixel value.
     * Compute value of Kernel filter.
     *
     * @param t difference between interpolation position and pixel position.
     * t &gt;= 0 && t&lt;2.
     * @return Kernel filter value.
     */
    private double getConvolutionValue(double t) {
        final double tAbs = Math.abs(t);
        if (tAbs <= 1) {
            return ((a+2)*tAbs - (a+3))*tAbs*tAbs + 1;//(a + 2)|x|^3 - (a + 3)|x|^2 +  1
        } else if(tAbs >1 && tAbs < 2) {
            return (((tAbs - 5)*tAbs + 8)*tAbs - 4)*a;// a|x|^3 - 5a|x|^2 + 8a|x| - 4a
        } else {
            return 0;
        }
    }
}
