/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.awt.color.ColorSpace;
import org.geotoolkit.util.converter.Classes;


/**
 * Color space for raster backed by floating point numbers ranging between two arbitrary values.
 *
 * {@note Current implementation is a copy of <code>org.geotoolkit.image.io.ScaledColorSpace</code>.
 *        Future implementation will be differents (interpolate in a color table instead of
 *        computing grayscales).}
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 *
 * @todo Consider extending {@link javax.media.jai.ColorSpaceJAI}.
 */
final class ScaledColorSpace extends ColorSpace {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -9198924014622850866L;

    /**
     * Minimal normalized RGB value.
     */
    private static final float MIN_VALUE = 0f;

    /**
     * Maximal normalized RGB value.
     */
    private static final float MAX_VALUE = 1f;

    /**
     * The band to make visible (usually 0).
     */
    private final int band;

    /**
     * The scale factory by which to multiply pixel values.
     */
    private final float scale;

    /**
     * The offset to add to pixel values after the {@linkplain #scale}.
     */
    private final float offset;

    /**
     * Creates a new scaled color model.
     *
     * @param band La bande à rendre visible (habituellement 0).
     * @param numComponents Nombre de composante (seule la première sera prise en compte).
     * @param minimum La valeur géophysique minimale.
     * @param maximum La valeur géophysique maximale.
     */
    public ScaledColorSpace(final int band, final int numComponents,
                            final double minimum, final double maximum)
    {
        super(TYPE_GRAY, numComponents);
        this.band = band;
        final double scale  = (maximum - minimum) / (MAX_VALUE - MIN_VALUE);
        final double offset = minimum - MIN_VALUE*scale;
        this.scale  = (float)scale;
        this.offset = (float)offset;
    }

    /**
     * Converts the given value to RGB.
     */
    @Override
    public float[] toRGB(final float[] values) {
        float value = (values[band] - offset) / scale;
        if (Float.isNaN(value)) {
            value = MIN_VALUE;
        }
        return new float[] {value, value, value};
    }

    /**
     * Converts the given RGB to a value in this color model.
     */
    @Override
    public float[] fromRGB(final float[] RGB) {
        final float[] values = new float[getNumComponents()];
        values[band] = (RGB[0] + RGB[1] + RGB[2]) / 3f * scale + offset;
        return values;
    }

    /**
     * Converts the given values to CIEXYZ.
     */
    @Override
    public float[] toCIEXYZ(final float[] values) {
        float value = (values[band] - offset) / scale;
        if (Float.isNaN(value)) {
            value = MIN_VALUE;
        }
        return new float[] {
            value*0.9642f, value, value*0.8249f
        };
    }

    /**
     * Converts the given CIEXYZ values to a value in this color model.
     */
    @Override
    public float[] fromCIEXYZ(final float[] XYZ) {
        final float[] values = new float[getNumComponents()];
        values[band] = (XYZ[0]/0.9642f + XYZ[1] + XYZ[2]/0.8249f) / 3f * scale + offset;
        return values;
    }

    /**
     * Returns the minimal value allowed.
     */
    @Override
    public float getMinValue(final int component) {
        return MIN_VALUE * scale + offset;
    }

    /**
     * Returns the maximal value allowed.
     */
    @Override
    public float getMaxValue(final int component) {
        return MAX_VALUE*scale + offset;
    }

    /**
     * Returns a string representation of this color model.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + '[' + getMinValue(band) + ", " + getMaxValue(band) + ']';
    }
}
