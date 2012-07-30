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
 * Define a standard biCubic interpolation.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BiCubicInterpolation1 extends BiCubicInterpolation {

    /**
     * {@inheritDoc }.
     */
    public BiCubicInterpolation1(PixelIterator pixelIterator) {
        super(pixelIterator);
    }

    /**
     * {@inheritDoc }
     *
     * Cubic interpolation from 4 values.<br/>
     * With always t0 &lt= t&lt= t0 + 3 <br/>
     * <p>For example : cubic interpolation between 4 pixels.<br/>
     *
     *
     * &nbsp;&nbsp;&nbsp;t =&nbsp;&nbsp; 0 &nbsp;1 &nbsp;2 &nbsp;3<br/>
     * f(t) = |f0|f1|f2|f3|<br/>
     * In this example t0 = 0.<br/><br/>
     *
     * Another example :<br/>
     * &nbsp;&nbsp;&nbsp;t =&nbsp; -5 -4 -3 -2<br/>
     * f(t) = |f0|f1|f2|f3|<br/>
     * In this example parameter t0 = -5.</p>
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param t position of interpolation.
     * @param f pixel values from t = {0, 1, 2, 3}.
     * @return cubic interpolation at t position.
     */
    @Override
    protected double interpolate1D(double t0, double t, double... f) {
        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;
        final double x  = t-t0;
        return checkValue(f[0] + (a1 + (a2 + a3*x)*x)*x);
    }
}
