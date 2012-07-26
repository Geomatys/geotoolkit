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
import java.awt.image.DataBuffer;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define BiCubic Interpolation.
 *
 * BiCubic interpolation is computed from 16 pixels at nearest integer value.
 *
 * @author Rémi Marechal (Geomatys).
 */
abstract class BiCubicInterpolation extends Interpolation {

    /**
     * Table used to compute interpolation from rows values.
     */
    private final double[] tabInteRow;

    /**
     * Table used to interpolate values from rows interpolation result.
     */
    private final double[] tabInteCol;

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
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator, 4);
        switch (pixelIterator.getSourceDatatype()) {
            case DataBuffer.TYPE_BYTE : {
                minValue = 0;
                maxValue = 255;
            }break;
            case DataBuffer.TYPE_SHORT : {
                minValue = -32768;
                maxValue = 32767;
            }break;
            case DataBuffer.TYPE_INT : {
                minValue = -2147483648;
                maxValue = 2147483647;
            }break;
            case DataBuffer.TYPE_FLOAT : {
                minValue = -3.40282347E38;
                maxValue = 3.40282347E38;
            }break;
            default : {//double border
                minValue = -1.79769313486231E308;
                maxValue = 1.79769313486231E308;
            }
        }
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller" + boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller" + boundary.height);
        tabInteRow = new double[4];
        tabInteCol = new double[4];
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
    abstract double getCubicValue(double t0, double t, double[]f);

    /**
     * Compute biCubic interpolation.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return pixel interpolated values for each bands.
     */
    @Override
    public double[] interpolate(double x, double y) {
        super.interpolate(x, y);
        double rn;
        final double[] result = new double[numBands];
        //build pixels interpolation band per band
        for (int n = 0; n < numBands; n++) {
            //16 values for each interpolation per band
            for (int idRow = 0; idRow<4; idRow++) {
                for (int idC = 0; idC<4;idC++) {
                    tabInteRow[idC] = data[n + (4*idRow + idC) * numBands];
                }
                tabInteCol[idRow] = getCubicValue(minX, x, tabInteRow);
            }
            rn = getCubicValue(minY, y, tabInteCol);
            if (rn < minValue) {
                rn = minValue;
            } else if (rn > maxValue) {
                rn = maxValue;
            }
            result[n] = rn;
        }
        return result;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
