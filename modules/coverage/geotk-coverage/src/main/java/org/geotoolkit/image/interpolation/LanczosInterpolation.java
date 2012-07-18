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
 * Define Lanczos interpolation.
 *
 * @author rémi Marechal (Geomatys).
 */
public class LanczosInterpolation extends Interpolation {

    /**
     * Table to keep all (2*lanczosWindow)² pixels values used to interpolate.
     */
    private final double[] data;

    /**
     * Pixels number use to interpolate before and after pixel coordinate on one dimension.
     */
    private final int lanczosWindow;

    /**
     * 2*lanczosWindow.
     */
    private final int l2W;

    private static double PI = Math.PI;

    /**
     * Create a Lanczos interpolation.
     *
     * The Lanczos window is the central lobe of a horizontally-stretched sinc,<br/>
     * sinc(x/a) for |x| ≤ lanczos window.<br/>
     * The normalized sinc function is commonly defined by sinc(x) = sin(PIx)/(PIx).<br/>
     * Lanczos window define interpolate area boundary defined by square of side length 2*lanczos window.
     *
     * @param pixelIterator Iterator used to interpolation.
     * @param lanczosWindow define pixel number use to interpolate.
     */
    public LanczosInterpolation(PixelIterator pixelIterator, int lanczosWindow) {
        super(pixelIterator);
        if (lanczosWindow > boundary.width || lanczosWindow > boundary.height)
            throw new IllegalArgumentException("lanczosWindow more longer");
        this.lanczosWindow = lanczosWindow;
        this.l2W = 2 * lanczosWindow;
        data = new double[l2W * l2W * numBands];
    }


    /**
     * Return Lanczos kernel filter value.
     *
     * @param t0 interpolation position from first pixel use to interpolate.
     * @param t interpolation instant.
     * @return Lanczos kernel filter value.
     */
    private double getLCZt(double t0, double t) {
        double x = t-t0;
        if (x == 0) return 1;
        if (Math.abs(x) > lanczosWindow) return 0;
        return (lanczosWindow * Math.sin(PI*x) * Math.sin(PI*x/lanczosWindow))/(PI*PI*x*x);
    }

    /**
     * {@inheritDoc }.
     * <br/>
     * In other words, interpolate result is compute from bounding box centered<br/>
     * from pixel coordinates with side length equal to 2 * lanczos window.
     */
    @Override
    public double[] interpolate(double x, double y) {
        checkInterpolate(x, y);
        int[] deb = getInterpolateMin(x, y, l2W, l2W);
        int debX = deb[0];
        int debY = deb[1];
        int compteur = 0;
        int band;
        for (int dy = debY; dy < debY + l2W; dy++) {
            for (int dx = debX; dx < debX + l2W; dx++) {
                pixelIterator.moveTo(dx, dy);
                band = 0;
                while (band++ != numBands) {
                    pixelIterator.next();
                    data[compteur++] = pixelIterator.getSampleDouble();
                }
            }
        }
        double[] result = new double[numBands];
        for (int n = 0; n < numBands; n++) {
            double interpol = 0;
            for (int dy = debY; dy < debY + l2W; dy++) {
                for (int dx = debX; dx < debX + l2W; dx++) {
                    interpol += data[n + (l2W * (dy - debY) + (dx - debX)) * numBands] * getLCZt(dx, x) * getLCZt(dy, y);
                }
            }
            result[n] = interpol;
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
        final int boundW = boundary.width;
        final int boundH = boundary.height;
        final int bx = boundary.x;
        final int by = boundary.y;
        assert (width <= boundW && height <= boundH) : "area dimensions are out of boundary";
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

        int debX = Math.max(minx, bx);
        int debY = Math.max(miny, by);

        while (debX + width > bx + boundW) {
            debX--;
        }
        while (debY + height > by + boundH) {
            debY--;
        }
        return new int[]{debX, debY};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    int getWindowSide() {
        return l2W;
    }

    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
