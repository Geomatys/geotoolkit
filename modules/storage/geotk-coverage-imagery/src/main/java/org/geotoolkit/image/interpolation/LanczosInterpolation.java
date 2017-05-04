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

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import static java.lang.Math.sin;
import org.geotoolkit.image.iterator.PixelIterator;

/**
 * Define Lanczos interpolation.
 *
 * @author rémi Marechal (Geomatys).
 */
public class LanczosInterpolation extends Interpolation {

    /**
     * Pixels number use to interpolate before and after pixel coordinate on one dimension.
     */
    private final int lanczosWindow;

    private static double PI = Math.PI;

    /**
     * Minimum value authorized from type of data from source interpolation.
     */
    private final double minValue;

    /**
     * maximum value authorized from type of data from source interpolation.
     */
    private final double maxValue;

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
     * @param rbc border comportement.
     * @param fillValue value(s) which will be filled when coordinate out of source boundary.
     */
    public LanczosInterpolation(PixelIterator pixelIterator, int lanczosWindow, ResampleBorderComportement rbc, double[] fillValue) {
        super(pixelIterator, lanczosWindow << 1, rbc, fillValue);
        if (lanczosWindow > boundary.width || lanczosWindow > boundary.height)
            throw new IllegalArgumentException("lanczosWindow more longer");
        this.lanczosWindow = lanczosWindow;
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
            default : {//double limits
                minValue = -1.79769313486231E308;
                maxValue = 1.79769313486231E308;
            }
        }
    }

    /**
     * Create a Lanczos interpolation.<br/><br/>
     *
     * Define border comportement at {@link ResampleBorderComportement#FILL_VALUE}
     * and fillValue is an arrays of the same length than band number from source image and filled by {@link Double#NaN} value.<br/><br/>
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
        this(pixelIterator, lanczosWindow, ResampleBorderComportement.FILL_VALUE, null);
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
        final double pix = PI*x;
        return (lanczosWindow * sin(pix) * sin(pix/lanczosWindow))/(pix*pix);
    }

    /**
     * {@inheritDoc }
     *
     * <p> In other words, interpolate result is compute from bounding box centered
     * from pixel coordinates with side length equal to 2 * lanczos window.</p>
     *
     */
    @Override
    public double interpolate(double x, double y, int b) {
//        if (!checkInterpolate(x, y)) return fillValue[b];
        setInterpolateMin(x, y);
        final int hY = minY + windowSide;
        final int wX = minX + windowSide;
        int dy, dx;
        double interpol = 0;
        for (dy = minY; dy < hY; dy++) {
            for (dx = minX; dx < wX; dx++) {
                pixelIterator.moveTo(dx, dy, b);
                interpol += pixelIterator.getSampleDouble() * getLCZt(dx, x) * getLCZt(dy, y);
            }
        }
        if (interpol < minValue) {
            interpol = minValue;
        } else if (interpol > maxValue) {
            interpol = maxValue;
        }
        return interpol;
    }


    /**
     * {@inheritDoc }.
     */
    @Override
    public double[] getMinMaxValue(Rectangle area) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double[] interpolate(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
