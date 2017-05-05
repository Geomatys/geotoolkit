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

import java.awt.color.ColorSpace;
import org.apache.sis.util.Classes;


/**
 * Color space for images storing pixels as real numbers. The color model can have an
 * arbitrary number of bands, but in current implementation only one band is used.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public final class ScaledColorSpace extends ColorSpace {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 438226855772441165L;

    /**
     * Minimal normalized RGB value.
     */
    private static final float MIN_VALUE = 0f;

    /**
     * Maximal normalized RGB value.
     */
    private static final float MAX_VALUE = 1f;

    /**
     * The scaling factor for pixel values.
     */
    private final float scale;

    /**
     * The offset to apply after the {@linkplain #scale} on pixel values.
     */
    private final float offset;

    /**
     * Index of the band to display.
     */
    private final int visibleBand;

    /**
     * Creates a color model.
     *
     * @param numComponents The number of components.
     * @param visibleBand The band to use for computing colors.
     * @param minimum The minimal sample value expected.
     * @param maximum The maximal sample value expected.
     */
    public ScaledColorSpace(final int numComponents, final int visibleBand,
                            final float minimum, final float maximum)
    {
        super(TYPE_GRAY, numComponents);
        this.visibleBand = visibleBand;
        scale  = (maximum - minimum) / (MAX_VALUE - MIN_VALUE);
        offset = minimum - MIN_VALUE*scale;
    }

    /**
     * Creates a color model.
     *
     * @param numComponents The number of components.
     * @param visibleBand The band to use for computing colors.
     * @param minimum The minimal sample value expected.
     * @param maximum The maximal sample value expected.
     */
    public ScaledColorSpace(final int numComponents, final int visibleBand,
                            final double minimum, final double maximum)
    {
        super(TYPE_GRAY, numComponents);
        this.visibleBand = visibleBand;
        final double scale  = (maximum - minimum) / (MAX_VALUE - MIN_VALUE);
        this.scale  = (float) scale;
        this.offset = (float) (minimum - MIN_VALUE*scale);
    }

    /**
     * Returns a RGB color for a gray scale value.
     *
     * @param values The gray scale values.
     */
    @Override
    public float[] toRGB(final float[] values) {
        float value = (values[visibleBand] - offset) / scale;
        //-- logic operation >, < ,= return false when at least one argument is Double.NAN
        //-- to avoid bad NAN value condition is coded as follow :
        if (!(value >= MIN_VALUE)) {
            value = MIN_VALUE;
        } else if (value > MAX_VALUE) {
            value = MAX_VALUE;
        }
        return new float[] {value, value, value};
    }

    /**
     * Returns a real value for the specified RGB color.
     * The RGB color is assumed to be a gray scale value.
     *
     * @param RGB The RGB values.
     */
    @Override
    public float[] fromRGB(final float[] RGB) {
        final float[] values = new float[getNumComponents()];
        values[visibleBand] = (RGB[0] + RGB[1] + RGB[2]) / 3 * scale + offset;
        return values;
    }

    /**
     * Converts a color to the CIEXYZ color space.
     *
     * @param values The color values.
     */
    @Override
    public float[] toCIEXYZ(final float[] values) {
        float value = (values[visibleBand] - offset) / scale;
        //-- logic operation >, < ,= return false when at least one argument is Double.NAN
        //-- to avoid bad NAN value condition is coded as follow :
        if (!(value >= MIN_VALUE)) {
            value = MIN_VALUE;
        } else if (value > MAX_VALUE) {
            value = MAX_VALUE;
        }
        return new float[] {
            value * 0.9642f,
            value * 1.0000f,
            value * 0.8249f
        };
    }

    /**
     * Converts a color from the CIEXYZ color space.
     *
     * @param RGB The RGB values.
     */
    @Override
    public float[] fromCIEXYZ(final float[] RGB) {
        final float[] values = new float[getNumComponents()];
        values[visibleBand] = (RGB[0] / 0.9642f + RGB[1] + RGB[2] / 0.8249f) / 3 * scale + offset;
        return values;
    }

    /**
     * Returns the minimum value for the specified RGB component.
     */
    @Override
    public float getMinValue(final int component) {
        return MIN_VALUE * scale + offset;
    }

    /**
     * Returns the maximum value for the specified RGB component.
     */
    @Override
    public float getMaxValue(final int component) {
        return MAX_VALUE * scale + offset;
    }

    /**
     * Returns a string representation of this color model.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) +
                '[' + getMinValue(visibleBand) + ", " + getMaxValue(visibleBand) + ']';
    }
}
