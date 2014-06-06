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
import org.geotoolkit.image.iterator.RowMajorDirectIterator;
import org.geotoolkit.image.iterator.RowMajorIterator;

/**
 * Interpolation which interpolate in X direction and Y direction independently.
 *
 * @author Rémi Maréchal (Geomatys).
 */
abstract class SeparableInterpolation extends Interpolation {

    /**
     * Table used to compute interpolation from rows values.
     */
    private final double[] tabInteRow;

    /**
     * Table used to interpolate values from rows interpolation result.
     */
    private final double[] tabInteCol;

    /** 2D Array storing row data of each band when an interpolation per pixel is performed. */
    private final double[][] rows;
    
    /** 2D Array storing row interpolated data for each band when an interpolation per pixel is performed. */
    private final double[][] cols;
    
    protected boolean rowMajorBrowsing;
    
    /**
     * @param pixelIterator
     * @param windowSide 
     */
    public SeparableInterpolation(PixelIterator pixelIterator, int windowSide) {
        super(pixelIterator, windowSide);
        tabInteRow = new double[windowSide];
        tabInteCol = new double[windowSide];
        rows = new double[numBands][windowSide];
        cols = new double[numBands][windowSide];
        rowMajorBrowsing = (pixelIterator instanceof RowMajorIterator || pixelIterator instanceof RowMajorDirectIterator);
    }


    /**
     * Return interpolate value from x, y pixel coordinate.
     *
     * @param x pixel x coordinate.
     * @param y pixel y coordinate.
     * @return interpolate value from x, y pixel coordinate.
     */
    @Override
    public double interpolate(double x, double y, int b) {
        checkInterpolate(x, y);
        setInterpolateMin(x, y);
        final int wX = minX + windowSide;
        final int hY = minY + windowSide;
        for (int dy = minY; dy < hY; dy++) {
            for (int dx = minX; dx < wX; dx++) {
                pixelIterator.moveTo(dx, dy, b);
                tabInteRow[dx - minX] = pixelIterator.getSampleDouble();
            }
            tabInteCol[dy-minY] = interpolate1D(minX + 0.5, x, tabInteRow);
        }
        return interpolate1D(minY + 0.5, y, tabInteCol);
    }

    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        setInterpolateMin(x, y);
        final int wX = minX + windowSide;
        final int hY = minY + windowSide;

        for (int dy = minY; dy < hY; dy++) {
            // First element treatment to avoid un-necessary checks for row major browsing.
            pixelIterator.moveTo(minX, dy, 0);
            rows[0][0] = pixelIterator.getSampleDouble();
            for (int band = 1; band < numBands; band++) {
                pixelIterator.next();
                rows[band][0] = pixelIterator.getSampleDouble();
            }
            
            for (int dx = minX + 1; dx < wX; dx++) {
                if (rowMajorBrowsing) {
                    pixelIterator.next();
                } else {
                    pixelIterator.moveTo(dx, dy, 0);
                }
                rows[0][dx - minX] = pixelIterator.getSampleDouble();
                for (int band = 1; band < numBands; band++) {
                    pixelIterator.next();
                    rows[band][dx - minX] = pixelIterator.getSampleDouble();
                }
            }

            for (int band = 0; band < numBands; band++) {
                cols[band][dy - minY] = interpolate1D(minX + 0.5, x, rows[band]);
            }
        }

        for (int band = 0; band < numBands; band++) {
            result[band] = interpolate1D(minY + 0.5, y, cols[band]);
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
    protected abstract double interpolate1D(double t0, double t, double...f);
}
