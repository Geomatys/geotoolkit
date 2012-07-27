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
 * Interpolation which interpolate in X direction and Y direction independently.
 *
 * @author Rémi Maréchal (Geomatys).
 */
abstract class SeparableInterpolation extends Interpolation{

    /**
     * Table used to compute interpolation from rows values.
     */
    private final double[] tabInteRow;

    /**
     * Table used to interpolate values from rows interpolation result.
     */
    private final double[] tabInteCol;

    /**
     * Interpolation results table.
     */
    private final double[] result;

    public SeparableInterpolation(PixelIterator pixelIterator, int windowSide) {
        super(pixelIterator, windowSide);
        tabInteRow = new double[windowSide];
        tabInteCol = new double[windowSide];
        result = new double[numBands];
    }


        /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] mins = getInterpolateMin(x, y, windowSide, windowSide);
        minX = mins[0];
        minY = mins[1];

        for (int b = 0; b<numBands; b++) {
            for (int dy = minY; dy < minY + windowSide; dy++) {
                for (int dx = minX; dx < minX + windowSide; dx++) {
                    pixelIterator.moveTo(dx, dy, b);
                    pixelIterator.next();
                    tabInteRow[dx - minX] = pixelIterator.getSampleDouble();
                }
                tabInteCol[dy-minY] = getInterpolValue(minX, x, tabInteRow);
            }
            result[b] = getInterpolValue(minY, y, tabInteCol);
        }
        return result;
    }

    /**
     * Compute interpolation value define by interpolation type implementation.
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param t position of interpolation.
     * @param f pixel values from t = {0 ... n}.
     * @return interpolation value.
     */
    protected abstract double getInterpolValue(double t0, double t, double...f);
}
