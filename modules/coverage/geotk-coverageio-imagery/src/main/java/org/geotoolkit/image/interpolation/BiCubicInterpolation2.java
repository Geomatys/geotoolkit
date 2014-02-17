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
        double res = 0;
        int compteur = 0;
        for (double ft : f) {
            res += getConvolutionValue(t-t0 - compteur++)*ft;
        }
        return checkValue(res);
    }
}
