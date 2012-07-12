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

import java.awt.Rectangle;
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
     * Parameter use in "cubic convolution" computing.
     * a equal -0.5 for classic biCubic interpolation.
     * a equal -1.0 for more amplitude between interpolation results.
     * @see #getConvolutionValue(double) .
     */
    private final double a;

    /**
     * <p>Create an BiCubic Interpolator.<br/>
     * This definition is also sometimes known as "cubic convolution".<br/><br/>
     *
     * if keys parameter is true<br/>
     * interpolator may produce somewhat sharper results than classic
     * InterpolationBicubic, but that result is image dependent.<br/>
     * It's a consequence from increase of biCubic curve amplitude.<br/>
     * else if keys parameter is false<br/>
     * define a standard biCubic interpolation.<br/>
     * (Reference: Digital Image Warping, George Wolberg, 1990,
     * pp 129-131, IEEE Computer Society Press, ISBN 0-8186-8944-7)</p>
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator, boolean keys) {
        super(pixelIterator);
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller" + boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller" + boundary.height);
        data       = new double[16*numBands];
        tabInteRow = new double[4];
        tabInteCol = new double[4];
        this.a = (keys) ? -1.0 : -0.5;
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
    double getConvolutionValue(double t) {
        final double tAbs = Math.abs(t);
        if (tAbs<=1) {
            return (a+2)*tAbs*tAbs*tAbs - (a+3)*tAbs*tAbs + 1;//(a + 2)|x|^3 - (a + 3)|x|^2 +  1
        } else if(tAbs >1 && tAbs < 2) {
            return a*tAbs*tAbs*tAbs - 5*a*tAbs*tAbs + 8*a*tAbs - 4*a;// a|x|^3 - 5a|x|^2 + 8a|x| - 4a
        } else {
            return 0;
        }
    }

    /**
     * Return appropriate interpolation minX and minY coordinates from x, y interpolate coordinates.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param width interpolate area width.
     * @param height interpolate area height.
     * @throws IllegalArgumentException if there are necessary pixels out of boundary.
     * @return appropriate interpolation minX and minY coordinates.
     */
    private int[] getInterpolateMin(double x, double y, int width, int height) {
        assert (width <= boundary.width && height <= boundary.height) : "area dimensions are out of boundary";
        int minx = (int) x;
        int miny = (int) y;
        if (x<minx) minx--;
        if (y<miny) miny--;

        //ajust area interpolation on x, y center.
        for (int i = 0; i<width/2-1;i++) {
            minx--;
        }
        for (int i = 0; i<height/2-1;i++) {
            miny--;
        }

        /*
         * Test if interpolate area is within iterate object boundary
         */
        if (!boundary.contains(minx, miny) || !boundary.contains(minx + width-1, miny + height-1))
            throw new IllegalArgumentException("interpolate definition domain out of boundary");
        return new int[]{minx, miny};
    }

    /**
     * {@inheritDoc }.
     * <p>If Rectangle area parameter is {@code null} method will search minimum
     * and maximum on all iterate object.</p>
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
