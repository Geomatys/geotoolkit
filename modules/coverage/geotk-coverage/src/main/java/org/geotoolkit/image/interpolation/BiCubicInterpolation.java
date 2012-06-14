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
 * Define Bicubic Interpolation.
 *
 * Bicubic interpolation is computed from 16 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BiCubicInterpolation extends Interpolation {

    /**
     * Create an Bicubic Interpolator.
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller"+boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller"+boundary.height);
    }

    /**
     * Compute bicubic interpolation
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return pixel interpolated values for each bands.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int minx = (int) x;
        int miny = (int) y;
        if (x<0) minx--;
        if (y<0) miny--;
        minx--;miny--;
        int debX = Math.max(minx, boundary.x);
        int debY = Math.max(miny, boundary.y);
        int compteur = 0;
        int bands;
        double[] data = new double[16*numBands];
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

        final double[] tabInter = new double[16];
        final double[] result = new double[numBands];
        final double[] tabInteRow = new double[4];
        final double[] tabInteCol = new double[4];

        //build pixel band per band
        for (int n = 0; n < numBands; n++) {
            //16 value for each interpolation
            for (int i = 0; i<16; i++) {
                tabInter[i] = data[n + i * numBands];//get 16 values for each bands
            }
            for (int idRow = 0; idRow<4; idRow++) {
                for (int idC = 0; idC<4;idC++) {
                    tabInteRow[idC] = tabInter[4*idRow + idC];//ya surement moy de le faire sans tabinter a voir apres test
                }
                tabInteCol[idRow] = cubicInterpol(tabInteRow[0], tabInteRow[1], tabInteRow[2], tabInteRow[3], (x - debX));
            }
            result[n] = cubicInterpol(tabInteCol[0], tabInteCol[1], tabInteCol[2], tabInteCol[3], (y - debY));
        }
        return result;
    }

    /**
     * Cubic interpolation from 4 values.<br/>
     * With always 0 &lt= t&lt= 4 <br/>
     * <p>For example : cubic interpolation between 4 pixels.<br/>
     *
     *
     * &nbsp;&nbsp;&nbsp;x =&nbsp;&nbsp; 0 &nbsp;1 &nbsp;2 &nbsp;3<br/>
     * f(x) = |f0|f1|f2|f3|<br/>
     * In this example t = x;<br/><br/>
     *
     * Another example :<br/>
     * &nbsp;&nbsp;&nbsp;x =&nbsp; -5 -4 -3 -2<br/>
     * f(x) = |f0|f1|f2|f3|<br/>
     * In this example parameter t must be equals t = x - (-5);</p>
     *
     * @param f0 pixel value from t = 0 pixel coordinate.
     * @param f1 pixel value from t = 1 pixel coordinate.
     * @param f2 pixel value from t = 2 pixel coordinate.
     * @param f3 pixel value from t = 3 pixel coordinate.
     * @param t instant of interpolation. It's currently x value sub, min x value.
     * @return cubic interpolation at t position.
     */
    public double cubicInterpol(double f0, double f1, double f2, double f3, double t) {
        final double a1 =  f3/3 - 3*f2/2 + 3*f1   - 11*f0/6;
        final double a2 = -f3/2 + 2*f2   - 5*f1/2 + f0;
        final double a3 =  f3/6 - f2/2   + f1/2   - f0/6;
        return f0 + t*a1 + t*t*a2 + t*t*t*a3;
    }
}
