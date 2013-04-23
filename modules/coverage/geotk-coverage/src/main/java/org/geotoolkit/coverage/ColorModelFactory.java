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
package org.geotoolkit.coverage;

import java.awt.Color;
import java.util.Map;
import java.util.Arrays;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.DataBuffer;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.internal.image.ScaledColorSpace;
import org.geotoolkit.util.collection.WeakValueHashMap;


/**
 * A factory for {@link ColorModel} objects built from a list of {@link Category} objects.
 * This factory provides only one public static method: {@link #getColorModel}.  Instances
 * of {@link ColorModel} are shared among all callers in the running virtual machine.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.1
 * @module
 */
final class ColorModelFactory extends Static {
    /**
     * A pool of color models previously created by {@link #getColorModel}.
     * <p>
     * <b>Note:</b> we use {@linkplain java.lang.ref.WeakReference weak references} instead of
     * {@linkplain java.lang.ref.SoftReference soft references} because the intend is not to
     * cache the values. The intend is to share existing instances in order to reduce memory
     * usage. Rational:
     * <p>
     * <ul>
     *   <li>{@link ColorModel} may consume a lot of memory. A 16 bits indexed color model
     *       can consume up to 256 kb. We don't want to retain such large objects longer
     *       than necessary. We want to share existing instances without preventing the
     *       garbage collector to collect them.</li>
     *   <li>{@link #getColorModel()} is reasonably fast if invoked only occasionally, so it
     *       is not worth consuming 256 kb for saving the few milliseconds requiring for
     *       building a new color model. Client code should retains their own reference to a
     *       {@link ColorModel} if they plan to reuse it often in a short period of time.</li>
     * </ul>
     */
    private static final Map<ColorModelFactory,ColorModel> colors =
            new WeakValueHashMap<ColorModelFactory,ColorModel>();

    /**
     * The list of categories for the construction of a single instance of a {@link ColorModel}.
     */
    private final Category[] categories;

    /**
     * The visible band (usually 0) used for the construction
     * of a single instance of a {@link ColorModel}.
     */
    private final int visibleBand;

    /**
     * The number of bands (usually 1) used for the construction
     * of a single instance of a {@link ColorModel}.
     */
    private final int numBands;

    /**
     * The color model type. One of {@link DataBuffer#TYPE_BYTE}, {@link DataBuffer#TYPE_USHORT},
     * {@link DataBuffer#TYPE_FLOAT} or {@link DataBuffer#TYPE_DOUBLE}.
     *
     * @todo The user may want to set explicitly the number of bits each pixel occupied.
     *       We need to think about an API to allows that.
     */
    private final int type;

    /**
     * Constructs a new {@code ColorModelFactory}. This object will actually be used
     * as a key in a {@link Map}, so this is not really a {@code ColorModelFactory}
     * but a kind of "{@code ColorModelKey}" instead. However, since this constructor
     * is private, user doesn't need to know that.
     */
    private ColorModelFactory(final Category[] categories, final int type,
                              final int visibleBand, final int numBands)
    {
        this.categories  = categories;
        this.visibleBand = visibleBand;
        this.numBands    = numBands;
        this.type        = type;
        if (visibleBand < 0 || visibleBand >= numBands) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_BAND_NUMBER_1, visibleBand));
        }
    }

    /**
     * Returns a color model for a category set. This method builds up the color model
     * from each category's colors (as returned by {@link Category#getColors}).
     *
     * @param  categories The set of categories.
     * @param  type The color model type. One of {@link DataBuffer#TYPE_BYTE},
     *         {@link DataBuffer#TYPE_USHORT}, {@link DataBuffer#TYPE_FLOAT} or
     *         {@link DataBuffer#TYPE_DOUBLE}.
     * @param  visibleBand The band to be made visible (usually 0). All other bands, if any
     *         will be ignored.
     * @param  numBands The number of bands for the color model (usually 1). The returned color
     *         model will renderer only the {@code visibleBand} and ignore the others, but
     *         the existence of all {@code numBands} will be at least tolerated. Supplemental
     *         bands, even invisible, are useful for processing with Java Advanced Imaging.
     * @return The requested color model, suitable for {@link java.awt.image.RenderedImage}
     *         objects with values in the <code>{@linkplain CategoryList#getRange}</code> range.
     */
    public static ColorModel getColorModel(final Category[] categories, final int type,
                                           final int visibleBand, final int numBands)
    {
        synchronized (colors) {
            ColorModelFactory key = new ColorModelFactory(categories, type, visibleBand, numBands);
            ColorModel model = colors.get(key);
            if (model == null) {
                model = key.getColorModel();
                colors.put(key, model);
            }
            return model;
        }
    }

    /**
     * Constructs the color model.
     */
    private ColorModel getColorModel() {
        double minimum = 0;
        double maximum = 1;
        final int categoryCount = categories.length;
        if (categoryCount != 0) {
            minimum = categories[0].minimum;
            for (int i=categoryCount; --i >= 0;) {
                final double value = categories[i].maximum;
                if (!Double.isNaN(value)) {
                    maximum = value;
                    break;
                }
            }
        }
        /*
         * If the requested type is any type not supported by IndexColorModel,
         * fallback on a generic (but very slow!) color model.
         */
        if (type != DataBuffer.TYPE_BYTE && type != DataBuffer.TYPE_USHORT) {
            final int  transparency = Transparency.OPAQUE;
            final ColorSpace colors = new ScaledColorSpace(numBands, visibleBand, minimum, maximum);
            return new ComponentColorModel(colors, false, false, transparency, type);
        }
        /*
         * If there is no category, constructs a gray scale palette.
         */
        if (numBands == 1 && categoryCount == 0) {
            final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            final int[] nBits = {
                DataBuffer.getDataTypeSize(type)
            };
            return new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, type);
        }
        /*
         * Computes the number of entries required for the color palette.
         * We take the upper range value of the last non-NaN category.
         */
        final int mapSize = ((int) Math.round(maximum)) + 1;
        final int[]  ARGB = new int[mapSize];
        /*
         * Interpolates the colors in the color palette. Colors that do not fall
         * in the range of a category will be set to a transparent color. The first
         */
        int transparent = -1;
        for (int i=0; i<categoryCount; i++) {
            final Category category = categories[i];
            final Color[] colors = category.getColors();
            if (transparent < 0 && isTransparent(colors)) {
                transparent = (int) category.minimum;
                if (transparent != category.minimum) {
                    // Search an other value if we dont have an integer.
                    transparent = -1;
                }
            }
            ColorUtilities.expand(colors, ARGB,
                                  (int) Math.round(category.minimum),
                                  (int) Math.round(category.maximum) + 1);
        }
        return ColorUtilities.getIndexColorModel(ARGB, numBands, visibleBand, transparent);
    }

    /**
     * Returns {@code true} if the given array of colors contains only transparent colors.
     */
    private static boolean isTransparent(final Color[] colors) {
        for (final Color color : colors) {
            if (color.getAlpha() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a hash code.
     */
    @Override
    public int hashCode() {
        final int categoryCount = categories.length;
        int code = 962745549 + (numBands*31 + visibleBand)*31 + categoryCount;
        for (int i=0; i<categoryCount; i++) {
            code += categories[i].hashCode();
            // Better be independent of categories order.
        }
        return code;
    }

    /**
     * Checks this object with an other one for equality.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ColorModelFactory) {
            final ColorModelFactory that = (ColorModelFactory) other;
            return this.numBands    == that.numBands    &&
                   this.visibleBand == that.visibleBand &&
                   Arrays.equals(this.categories, that.categories);
        }
        return false;
    }
}
