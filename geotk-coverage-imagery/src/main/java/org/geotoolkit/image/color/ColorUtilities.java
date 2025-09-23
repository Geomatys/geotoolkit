/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.color;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.IndexColorModel;
import java.awt.image.DataBuffer;
import org.apache.sis.image.internal.shared.ColorModelFactory;

import org.geotoolkit.lang.Static;


/**
 * A set of static methods for handling of colors informations. Some of those methods
 * are useful, but not really rigorous. This is why they do not appear in any "official"
 * package, but instead in this private one.
 *
 *                      <strong>Do not rely on this API!</strong>
 *
 * It may change in incompatible way in any future version.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 *
 * @since 1.2
 * @module
 */
public final class ColorUtilities extends Static {
    /**
     * An index color model having only two colors, black and white.
     */
    public static final IndexColorModel BINARY_COLOR_MODEL = new IndexColorModel(
            1, 2, new int[] {0, -1}, 0, false, -1, DataBuffer.TYPE_BYTE);

    /**
     * Do not allow creation of instances of this class.
     */
    private ColorUtilities() {
    }

    /**
     * Returns the color components in an array of {@code double} values.
     * This is mostly for usage with JAI operators which require sample values
     * as {@code double} no matter the actual image data type.
     *
     * @param  color    The color for which to extract the component values.
     * @param  numBands The length of the array to be returned.
     *         If greater than 4, then the extra values will be initialized to 0.
     * @return The color components in an array of the given length.
     */
    @SuppressWarnings("fallthrough")
    public static double[] toDoubleValues(final Color color, final int numBands) {
        final double[] values = new double[numBands];
        switch (numBands) {
            default: values[3] = color.getAlpha();
            case 3:  values[2] = color.getBlue();
            case 2:  values[1] = color.getGreen();
            case 1:  values[0] = color.getRed();
            case 0:  break;
        }
        return values;
    }

    /**
     * Copies {@code colors} into array {@code ARGB} from index {@code lower}
     * inclusive to index {@code upper} exclusive. If {@code upper-lower} is not
     * equal to the length of {@code colors} array, then colors will be interpolated.
     *
     * @param colors Colors to copy into the {@code ARGB} array.
     * @param ARGB   Array of integer to write ARGB values to.
     * @param lower  Index (inclusive) of the first element of {@code ARGB} to change.
     * @param upper  Index (exclusive) of the last  element of {@code ARGB} to change.
     */
    public static void expand(final Color[] colors, final int[] ARGB, final int lower, final int upper) {
        final int[] codes = new int[colors.length];
        for (int i=0; i<codes.length; i++) {
            codes[i] = colors[i].getRGB();      // Note: getRGB() is really getARGB().
        }
        ColorModelFactory.expand(codes, ARGB, lower, upper);
    }

    /**
     * Returns an index color model for specified ARGB codes.   If the specified
     * array has not transparent color (i.e. all alpha values are 255), then the
     * returned color model will be opaque. Otherwise, if the specified array has
     * one and only one color with alpha value of 0, the returned color model will
     * have only this transparent color. Otherwise, the returned color model will
     * be translucent.
     *
     * @param  ARGB An array of ARGB values.
     * @return An index color model for the specified array.
     */
    public static IndexColorModel getIndexColorModel(final int[] ARGB) {
        return ColorModelFactory.createIndexColorModel(null, 0, 1, 0, ARGB, true, -1);
    }

    /**
     * Transforms a color from XYZ color space to LAB. The color are transformed
     * in place. This method returns {@code color} for convenience.
     * <p>
     * Reference: http://www.brucelindbloom.com/index.html?ColorDifferenceCalc.html
     *
     * @param  color The XYZ color to convert.
     * @return The LAB color.
     */
    private static float[] XYZtoLAB(final float[] color) {
        color[0] /= 0.9642;   // Other refeference: 0.95047;
        color[1] /= 1.0000;   //                    1.00000;
        color[2] /= 0.8249;   //                    1.08883;
        for (int i=0; i<3; i++) {
            final float c = color[i];
            color[i] = (float)((c > 216/24389f) ? Math.pow(c, 1.0/3) : ((24389/27.0)*c + 16)/116);
        }
        final float L = 116 *  color[1] - 16;
        final float a = 500 * (color[0] - color[1]);
        final float b = 200 * (color[1] - color[2]);
        assert !Float.isNaN(L) && !Float.isNaN(a) && !Float.isNaN(b);
        color[0] = L;
        color[1] = a;
        color[2] = b;
        return color;
    }

    /**
     * Computes the distance E (CIE 1994) between two colors in LAB color space.
     * <p>
     * Reference: http://www.brucelindbloom.com/index.html?ColorDifferenceCalc.html
     *
     * @param  lab1 The first LAB color.
     * @param  lab2 The second LAB color.
     * @return The CIE94 distance between the two supplied colors.
     */
    private static float colorDistance(final float[] lab1, final float[] lab2) {
        double sum;
        if (false) {
            // Computes distance using CIE94 formula.
            // NOTE: this formula sometime fails because of negative
            //       value in the first Math.sqrt(...) expression.
            final double dL = (double) lab1[0] - lab2[0];
            final double da = (double) lab1[1] - lab2[1];
            final double db = (double) lab1[2] - lab2[2];
            final double C1 = Math.hypot(lab1[1], lab1[2]);
            final double C2 = Math.hypot(lab2[1], lab2[2]);
            final double dC = C1 - C2;
            final double dH = Math.sqrt(da*da + db*db - dC*dC);
            final double sL = dL / 2;
            final double sC = dC / (1 + 0.048*C1);
            final double sH = dH / (1 + 0.014*C1);
            sum = sL*sL + sC*sC + sH*sH;
        } else {
            // Computes distance using delta E formula.
            sum = 0;
            for (int i=Math.min(lab1.length, lab2.length); --i>=0;) {
                final double delta = lab1[i] - lab2[i];
                sum += delta*delta;
            }
        }
        return (float) Math.sqrt(sum);
    }

    /**
     * Returns the most transparent pixel in the specified color model. If many colors has
     * the same alpha value, than the darkest one is returned. This method never returns
     * a negative value (0 is returned if the color model has no colors).
     *
     * @param  colors The color model in which to look for a transparent color.
     * @return The index of a transparent color, or 0.
     */
    public static int getTransparentPixel(final IndexColorModel colors) {
        int index = colors.getTransparentPixel();
        if (index < 0) {
            index = 0;
            int   alpha = Integer.MAX_VALUE;
            float delta = Float.POSITIVE_INFINITY;
            final ColorSpace space = colors.getColorSpace();
            final float[] RGB   = new float[3];
            final float[] BLACK = XYZtoLAB(space.toCIEXYZ(RGB)); // Black in Lab color space.
            assert BLACK != RGB;
            for (int i=colors.getMapSize(); --i>=0;) {
                final int a = colors.getAlpha(i);
                if (a <= alpha) {
                    RGB[0] = colors.getRed  (i)/255f;
                    RGB[1] = colors.getGreen(i)/255f;
                    RGB[2] = colors.getBlue (i)/255f;
                    final float d = colorDistance(XYZtoLAB(space.toCIEXYZ(RGB)), BLACK);
                    assert d >= 0 : i; // Check mostly for NaN value
                    if (a<alpha || d<delta) {
                        alpha = a;
                        delta = d;
                        index = i;
                    }
                }
            }
        }
        return index;
    }

    /**
     * Returns the index of the specified color, excluding the specified one. If the color
     * is not explicitly found, a close color is returned. This method never returns a negative
     * value (0 is returned if the color model has no colors).
     *
     * @param  colors The color model in which to look for a color index.
     * @param  color The color to search for.
     * @param  exclude An index to exclude from the search (usually the background or the
     *         {@linkplain #getTransparentPixel transparent} pixel), or -1 if none.
     * @return The index of the color, or 0.
     */
    public static int getColorIndex(final IndexColorModel colors,
                                    final Color color,
                                    final int exclude)
    {
        final ColorSpace space = colors.getColorSpace();
        final float[] RGB = {
            color.getRed()   / 255f,
            color.getGreen() / 255f,
            color.getBlue()  / 255f
        };
        final float[] REF = XYZtoLAB(space.toCIEXYZ(RGB));
        float delta = Float.POSITIVE_INFINITY;
        int index = 0;
        assert REF != RGB;
        for (int i=colors.getMapSize(); --i>=0;) {
            if (i != exclude) {
                RGB[0] = colors.getRed  (i) / 255f;
                RGB[1] = colors.getGreen(i) / 255f;
                RGB[2] = colors.getBlue (i) / 255f;
                final float d = colorDistance(XYZtoLAB(space.toCIEXYZ(RGB)), REF);
                assert d >= 0 : i; // Check mostly for NaN value
                if (d <= delta) {
                    delta = d;
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * Tells if a specific {@link IndexColorModel} contains only gray color,
     * ignoring alpha information.
     *
     * @param  model index color model to be inspected.
     * @param  ignoreTransparents {@code true} if the RGB values of fully transparent pixels
     *         (the ones with an {@linkplain IndexColorModel#getAlpha(int) alpha} value of 0)
     *         should not be taken in account during the check for gray color.
     * @return {@code true} if the palette is grayscale, {@code false} otherwise.
     */
    public static boolean isGrayPalette(final IndexColorModel model, boolean ignoreTransparents) {
        if (!model.hasAlpha()) {
            // We will not check transparent pixels if there is none in the color model.
            ignoreTransparents = false;
        }
        final int mapSize = model.getMapSize();
        for (int i=0; i<mapSize; i++) {
            if (ignoreTransparents) {
                // If this entry is transparent and we were asked
                // to ignore fully transparents pixels, let's leave.
                if (model.getAlpha(i) == 0) {
                    continue;
                }
            }
            // Get the color for this pixel only if it is requested.
            // If gray, all components are the same.
            final int green = model.getGreen(i);
            if (green != model.getRed(i) || green != model.getBlue(i)) {
                return false;
            }
        }
        return true;
    }
}
