/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image;

import java.util.Arrays;
import java.util.Objects;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.util.collection.WeakHashSet;


/**
 * Shared instances of {@link ColorModel}s. Maintaining shared instance is not that much
 * interesting for most kind of color models, except {@link IndexColorModel} which can
 * potentially be quite big. This class works for all color models because they were no
 * technical reasons to restrict, but the real interest is to share index color models.
 *
 * @param <T> For internal use only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class ColorModels<T extends ColorModel> extends Static {
    /**
     * Pool of shared color models.
     */
    @SuppressWarnings("rawtypes")
    private static final WeakHashSet<ColorModels> POOL = WeakHashSet.newInstance(ColorModels.class);

    /**
     * The color model to share.
     */
    private final T cm;

    /**
     * For internal use only.
     */
    private ColorModels(final T cm) {
        this.cm = cm;
    }

    /**
     * Returns a unique instance of the given color model.
     *
     * @param  <T> The type of the color model to canonicalize.
     * @param  cm The color model for which to get a unique instance.
     * @return A unique (shared) instance of the given color model.
     */
    public static <T extends ColorModel> T unique(T cm) {
        ColorModels<T> c = new ColorModels<>(cm);
        c = POOL.unique(c);
        return c.cm;
    }

    /**
     * Returns {@code true} if the given color models are equal. The {@link ColorModel} class
     * defines an {@code equals} method, but as of Java 6 that method doesn't compare every
     * attributes. For example it doesn't compare the color space and the transfer type, so
     * we have to compare them here.
     *
     * @param cm1 The first color model.
     * @param cm2 The second color model.
     * @return {@code true} if the two color models are equal.
     */
    @Workaround(library="JDK", version="6")
    public static boolean equals(final ColorModel cm1, final ColorModel cm2) {
        if (cm1 == cm2) {
            return true;
        }
        if (cm1 != null && cm1.equals(cm2) &&
            cm1.getClass().equals(cm2.getClass()) &&
            cm1.getTransferType() == cm2.getTransferType() &&
            Objects.equals(cm1.getColorSpace(), cm2.getColorSpace()))
        {
            if (cm1 instanceof IndexColorModel) {
                final IndexColorModel icm1 = (IndexColorModel) cm1;
                final IndexColorModel icm2 = (IndexColorModel) cm2;
                final int size = icm1.getMapSize();
                if (icm2.getMapSize() == size &&
                    icm1.getTransparentPixel() == icm2.getTransparentPixel() &&
                    Objects.equals(icm1.getValidPixels(), icm2.getValidPixels()))
                {
                    for (int i=0; i<size; i++) {
                        if (icm1.getRGB(i) != icm2.getRGB(i)) {
                            return false;
                        }
                    }
                }
                if (cm1 instanceof MultiBandsIndexColorModel) {
                    final MultiBandsIndexColorModel micm1 = (MultiBandsIndexColorModel) cm1;
                    final MultiBandsIndexColorModel micm2 = (MultiBandsIndexColorModel) cm2;
                    if (micm1.numBands != micm2.numBands || micm1.visibleBand != micm2.visibleBand) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * For internal use only.
     *
     * @param object Object The object to compare to.
     * @return {@code true} if both object are equal.
     */
    @Override
    public boolean equals(final Object object) {
        return (object instanceof ColorModels<?>) && equals(cm, ((ColorModels<?>) object).cm);
    }

    /**
     * For internal use only.
     */
    @Override
    public int hashCode() {
        int code = cm.hashCode() ^ cm.getClass().hashCode();
        if (cm instanceof IndexColorModel) {
            final IndexColorModel icm = (IndexColorModel) cm;
            final int[] ARGB = new int[icm.getMapSize()];
            icm.getRGBs(ARGB);
            code ^= Arrays.hashCode(ARGB);
        }
        return code;
    }
}
