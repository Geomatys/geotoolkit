/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.palette;

import java.io.IOException;
import java.awt.image.DataBuffer;
import java.awt.image.ColorModel;
import java.awt.color.ColorSpace;
import javax.imageio.ImageTypeSpecifier;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.image.color.ScaledColorSpace;


/**
 * A factory for building {@linkplain ColorModel color model} suitable for data type that
 * do not fit in an index color model.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.11
 *
 * @since 2.4
 * @module
 */
final class ContinuousPalette extends Palette {
    /**
     * The minimal value, inclusive.
     */
    protected final float minimum;

    /**
     * The maximal value, inclusive.
     */
    protected final float maximum;

    /**
     * The data type, as a {@link DataBuffer#TYPE_INT}, {@link DataBuffer#TYPE_FLOAT}
     * or {@link DataBuffer#TYPE_DOUBLE} constant.
     */
    private final int dataType;

    /**
     * Creates a palette with the specified name.
     *
     * @param factory     The originating factory.
     * @param name        The palette name.
     * @param minimum     The minimal sample value expected.
     * @param maximum     The maximal sample value expected.
     * @param dataType    The data type as a {@link DataBuffer#TYPE_INT}, {@link DataBuffer#TYPE_FLOAT}
     *                    or {@link DataBuffer#TYPE_DOUBLE} constant.
     * @param numBands    The number of bands (usually 1).
     * @param visibleBand The band to use for color computations (usually 0).
     */
    protected ContinuousPalette(final PaletteFactory factory, final String name, final float minimum,
            final float maximum, final int dataType, final int numBands, final int visibleBand)
    {
        super(factory, name, numBands, visibleBand);
        this.minimum  = minimum;
        this.maximum  = maximum;
        this.dataType = dataType;
    }

    /**
     * Returns the scale from <cite>normalized values</cite> (values in the range [0..1])
     * to values in the range of this palette.
     */
    @Override
    final double getScale() {
        return maximum - minimum;
    }

    /**
     * Returns the offset from <cite>normalized values</cite> (values in the range [0..1])
     * to values in the range of this palette.
     */
    @Override
    final double getOffset() {
        return minimum;
    }

    /**
     * Creates a grayscale image type for this palette.
     * The image type is suitable for floating point values.
     *
     * @return  A default color space scaled to fit data.
     * @throws  IOException If an I/O operation was needed and failed.
     */
    @Override
    protected ImageTypeSpecifier createImageTypeSpecifier() throws IOException {
        final ColorSpace colorSpace;
        if (minimum < maximum && !Float.isInfinite(minimum) && !Float.isInfinite(maximum)) {
            colorSpace = new ScaledColorSpace(numBands, visibleBand, minimum, maximum);
        } else {
            colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        }
        final int[] bankIndices = new int[numBands];
        final int[] bandOffsets = new int[numBands];
        for (int i=numBands; --i>=0;) {
            bankIndices[i] = i;
        }
        return ImageTypeSpecifier.createBanded(colorSpace,
                bankIndices, bandOffsets, dataType, false, false);
    }

    /**
     * Returns a hash value for this palette.
     */
    @Override
    public int hashCode() {
        return 31 * (31 * (31 * super.hashCode() + Float.floatToIntBits(minimum)) +
                Float.floatToIntBits(maximum)) + dataType;
    }

    /**
     * Compares this palette with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final ContinuousPalette that = (ContinuousPalette) object;
            return Utilities.equals(this.minimum, that.minimum) &&
                   Utilities.equals(this.maximum, that.maximum) &&
                   this.dataType == that.dataType;
        }
        return false;
    }

    /**
     * Returns a string representation of this palette. Used for debugging purpose only.
     */
    @Override
    public String toString() {
        return name + " [" + minimum + " \u2026 " + maximum + ']';
    }
}
