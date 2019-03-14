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

import org.apache.sis.image.PixelIterator;
import org.apache.sis.util.ArgumentChecks;

/**
 * Interpolation which interpolate in X direction and Y direction independently.
 *
 * @author Rémi Maréchal (Geomatys).
 */
abstract class SeparableInterpolation extends Interpolation {

    /** 2D Array storing row data of each band when an interpolation per pixel is performed. */
    private final double[] rows;

    /** 2D Array storing row interpolated data for each band when an interpolation per pixel is performed. */
    private final double[] cols;


    /**
     * Build a bi-dimensional interpolation.
     *
     * @param pixelIterator iterator which travel source image samples.
     * @param windowSide length of samples in X and Y direction needed from interpolation type.
     * @param rbc enum which define interpolation comportement at the source image border.
     * @param fillValue define destination sample value in case where interpolation is out of source image boundary.
     */
    public SeparableInterpolation(PixelIterator pixelIterator, int windowSide, ResampleBorderComportement rbc, double[] fillValue) {
        super(pixelIterator, windowSide, rbc, fillValue);
        rows = new double[windowSide];
        cols = new double[windowSide];
    }

    /**
     * Returns interpolate value from x, y pixel coordinates and band index.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @param b band index.
     * @return interpolate value from x, y pixel coordinates and band index.
     */
    @Override
    public double interpolate(double x, double y, int b) {
        ArgumentChecks.ensureBetween("band index", 0, getNumBands(), b);
        setInterpolateMin(x, y);

        for (int dy = 0; dy < windowSide; dy++) {
            fillRow(dy, b);
            cols[dy] = interpolate1D(minX, x, rows);
        }
        return interpolate1D(minY, y, cols);
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
//        if (!checkInterpolate(x, y)) return fillValue;
        setInterpolateMin(x, y);

        for (int b = 0; b < numBands; b++) {
            for (int dy = 0; dy < windowSide; dy++) {
                fillRow(dy, b);
                cols[dy] = interpolate1D(minX, x, rows);
            }
            result[b] = interpolate1D(minY, y, cols);
        }
        return result;
    }

    private void fillRow(int y, int band) {
        for(int x=0; x<windowSide;x++) {
            pixelIterator.moveTo(minX + x, minY + y);
            rows[x] = pixelIterator.getSampleDouble(band);
        }
    }

    /**
     * Compute interpolation value define by interpolation type implementation.
     *
     * @param t0 f(t0) = f[0].Current position from first pixel interpolation.
     * @param t position of interpolation.
     * @param f pixel values from t = {0 ... n}.
     * @return interpolation value.
     */
    protected abstract double interpolate1D(double t0, double t, double...f);
}
