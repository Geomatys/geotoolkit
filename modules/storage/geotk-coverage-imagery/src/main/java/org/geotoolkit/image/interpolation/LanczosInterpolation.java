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
import static java.lang.Math.sin;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.measure.NumberRange;

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
        NumberRange<?> range = pixelIterator.getSampleRanges()[0];
        minValue = range.getMinDouble();
        maxValue = range.getMaxDouble();
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

        //unroll lanczos window sizes
        //loops are very expensive
        switch (windowSide) {
            case 1 : {
                dx = minX;
                dy = minY;
                pixelIterator.moveTo(dx, dy);
                interpol += pixelIterator.getSampleDouble(b) * getLCZt(dx, x) * getLCZt(dy, y);
                } break;
            case 2 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy1;

                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy1;
                } break;
            case 3 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczx2 = getLCZt(minX+2, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);
                double lczy2 = getLCZt(minY+2, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy2;
                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy2;
                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy2;
                } break;
            case 4 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczx2 = getLCZt(minX+2, x);
                double lczx3 = getLCZt(minX+3, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);
                double lczy2 = getLCZt(minY+2, y);
                double lczy3 = getLCZt(minY+3, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy2;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx0 * lczy3;
                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy2;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx1 * lczy3;
                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy2;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx2 * lczy3;
                dx++;
                pixelIterator.moveTo(dx, dy=minY); interpol += pixelIterator.getSampleDouble(b) * lczx3 * lczy0;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx3 * lczy1;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx3 * lczy2;
                pixelIterator.moveTo(dx, ++dy   ); interpol += pixelIterator.getSampleDouble(b) * lczx3 * lczy3;
                } break;
            default :
                for (dy = minY; dy < hY; dy++) {
                    for (dx = minX; dx < wX; dx++) {
                        pixelIterator.moveTo(dx, dy);
                        interpol += pixelIterator.getSampleDouble(b) * getLCZt(dx, x) * getLCZt(dy, y);
                    }
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
//        if (!checkInterpolate(x, y)) return fillValue[b];
        setInterpolateMin(x, y);
        final int hY = minY + windowSide;
        final int wX = minX + windowSide;
        int dy, dx;
        final int nbBand = pixelIterator.getNumBands();
        final double[] interpol = new double[nbBand];
        final double[] pixel = new double[nbBand];

        //unroll lanczos window sizes
        //loops are very expensive
        switch (windowSide) {
            case 1 : {
                dx = minX;
                dy = minY;
                pixelIterator.moveTo(dx, dy);
                pixelIterator.getPixel(pixel);
                appendPixel(interpol, pixel, getLCZt(dx, x) * getLCZt(dy, y));
                } break;
            case 2 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy1);

                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy1);
                } break;
            case 3 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczx2 = getLCZt(minX+2, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);
                double lczy2 = getLCZt(minY+2, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy2);
                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy2);
                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy2);
                } break;
            case 4 : {
                double lczx0 = getLCZt(minX,   x);
                double lczx1 = getLCZt(minX+1, x);
                double lczx2 = getLCZt(minX+2, x);
                double lczx3 = getLCZt(minX+3, x);
                double lczy0 = getLCZt(minY,   y);
                double lczy1 = getLCZt(minY+1, y);
                double lczy2 = getLCZt(minY+2, y);
                double lczy3 = getLCZt(minY+3, y);

                dx = minX;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy2);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx0 * lczy3);
                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy2);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx1 * lczy3);
                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy2);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx2 * lczy3);
                dx++;
                pixelIterator.moveTo(dx, dy=minY); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx3 * lczy0);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx3 * lczy1);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx3 * lczy2);
                pixelIterator.moveTo(dx, ++dy   ); pixelIterator.getPixel(pixel); appendPixel(interpol, pixel, lczx3 * lczy3);
                } break;
            default :
                for (dy = minY; dy < hY; dy++) {
                    for (dx = minX; dx < wX; dx++) {
                        pixelIterator.moveTo(dx, dy);
                        pixelIterator.getPixel(pixel);
                        appendPixel(interpol, pixel, getLCZt(dx, x) * getLCZt(dy, y));
                    }
                }
        }

        for (int i=0;i<nbBand;i++) {
            if (interpol[i] < minValue) {
                interpol[i] = minValue;
            } else if (interpol[i] > maxValue) {
                interpol[i] = maxValue;
            }
        }

        return interpol;
    }

    private static void appendPixel(double[] interpol, double[] pixel, double lcz) {
        final int nbBand = pixel.length;
        switch (nbBand) {
            case 4 :
                interpol[3] += pixel[3] * lcz;
            case 3 :
                interpol[2] += pixel[2] * lcz;
            case 2 :
                interpol[1] += pixel[1] * lcz;
            case 1 :
                interpol[0] += pixel[0] * lcz;
                break;
            default :
                for (int i=0;i<nbBand;i++) {
                    interpol[i] += pixel[i] * lcz;
                }
        }

    }
}
