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
abstract class BiCubicInterpolation extends Interpolation {

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
     * <p>Create an BiCubic Interpolator.<br/>
     * This definition is also sometimes known as "cubic convolution".<br/><br/>
     *
     * @param pixelIterator Iterator used to interpolation.
     */
    public BiCubicInterpolation(PixelIterator pixelIterator) {
        super(pixelIterator);
        if (boundary.width < 4)
            throw new IllegalArgumentException("iterate object width too smaller" + boundary.width);
        if (boundary.height < 4)
            throw new IllegalArgumentException("iterate object height too smaller" + boundary.height);
        data       = new double[16*numBands];
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
        minx = Math.max(minx, boundary.x);
        miny = Math.max(miny, boundary.y);
        while(minx+width > boundary.x+boundary.width) {
            minx--;
        }
        while(miny+height > boundary.y+boundary.height) {
            miny--;
        }
//        double diffx = Math.abs(x-minx);
//        double diffy = Math.abs(y-miny);
        //diff € [1; 2]
//        if(diffx<width/2-1 ||diffy<width/2-1 || diffx>width/2 || diffy>height/2)//diff>window/2
//            throw new IllegalArgumentException("interpolate definition domain out of boundary");

//        /*
//         * Test if interpolate area is within iterate object boundary
//         */
//        if (!boundary.contains(minx, miny) || !boundary.contains(minx + width-1, miny + height-1))
//            throw new IllegalArgumentException("interpolate definition domain out of boundary");
        return new int[]{minx, miny};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    int getWindowSide() {
        return 4;
    }
}
