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
package org.geotoolkit.image.interpolation;

import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define BiCubic Interpolation.
 *
 * BiCubic interpolation is computed from 16 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BiCubicInterpolation extends Interpolation {

    /**
     * Table to keep all 16 pixels values used to interpolate.
     */
    private final double[] data;

    /**
     * Table used to compute interpolation from rows values.
     */
    private final double[] tabInteRow;

    /**
     * Table used to interpolate values from rows interpolation result.
     */
    private final double[] tabInteCol;

    /**
     * Create an BiCubic Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller"+boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller"+boundary.height);
        data       = new double[16*numBands];
        tabInteRow = new double[4];
        tabInteCol = new double[4];
    }

    /**
     * Compute biCubic interpolation.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return pixel interpolated values for each bands.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] deb = getInterpolateMin(x, y, 4, 4);
        int debX = deb[0];
        int debY = deb[1];
        int compteur = 0;
        int bands;
        final double[] result = new double[numBands];
        for (int idY = debY; idY < debY + 4; idY++) {
            for (int idX = debX; idX < debX + 4; idX++) {
                pixelIterator.moveTo(idX, idY);
                bands = 0;
                while (bands++ != numBands) {
                    pixelIterator.next();
                    data[compteur++] = pixelIterator.getSampleDouble();
                }
            }
        }
        //build pixels interpolation band per band
        for (int n = 0; n < numBands; n++) {
            //16 values for each interpolation per band
            for (int idRow = 0; idRow<4; idRow++) {
                for (int idC = 0; idC<4;idC++) {
                    tabInteRow[idC] = data[n + (4*idRow + idC) * numBands];
                }
                tabInteCol[idRow] = getCubicValue(debX, x, tabInteRow);
            }
            result[n] = getCubicValue(debY, y, tabInteCol);
        }
        return result;
    }

    /**
     * <p>Find polynomials roots from BiCubic interpolation.<br/><br/>
     *
     * note : return null if : - delta (discriminant)&lt;0<br/>
     *                         - roots found are out of definition domain.</p>
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param minDf minimum of definition domain.
     * @param maxDf maximum of definition domain.
     * @param f pixel values from t = {0, 1, 2, 3}.
     * @return polynomial root(s).
     */
    double[] getCubicRoots(double t0, double minDf, double maxDf, double...f) {
        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
        assert (minDf < maxDf) : "definition domain invalid";
        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;
        final double delta = 4 * (a2*a2 - 3*a3*a1);
        double x, x2;
        if (delta > 0) {
            x  = -(2*a2 + Math.sqrt(delta)) / a3 / 6;
            x2 = (-2*a2 + Math.sqrt(delta)) / a3 / 6;
            x  += t0;
            x2 += t0;
            if (x >= minDf && x <= maxDf) {
                if (x2 >= minDf && x2 <= maxDf) return new double[]{x, x2};
                return new double[]{x};
            } else {
                if (x2 >= minDf && x2 <= maxDf) return new double[]{x2};
                return null;
            }
        } else if (delta == 0) {
            x = -a2/a3/3 + t0;
            if (x >= minDf && x <= maxDf) return new double[]{x};
            return null;
        } else {
            return null;
        }
    }

    /**
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
    double getCubicValue(double t0, double t, double[]f) {
        assert (f.length == 4) : "impossible to interpolate with less or more than 4 values";
        final double a1 =  f[3]/3 - 3*f[2]/2 + 3*f[1]   - 11*f[0]/6;
        final double a2 = -f[3]/2 + 2*f[2]   - 5*f[1]/2 + f[0];
        final double a3 =  f[3]/6 - f[2]/2   + f[1]/2   - f[0]/6;
        final double x  = t-t0;
        return f[0] + a1*x + a2*x*x + a3*x*x*x;
    }
}
