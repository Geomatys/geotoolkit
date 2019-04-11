/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.awt.Color;
import java.util.function.DoubleToIntFunction;
import javax.measure.Unit;
import org.apache.sis.coverage.Category;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.Numbers;
import org.geotoolkit.math.XMath;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.referencing.operation.MathTransform1D;


/**
 * An Apache SIS category with the addition of colors.
 *
 * @deprecated Used only for transition from Geotk to Apache SIS.
 *             Will be removed in a future Geotk version.
 */
@Deprecated
public final class ColoredCategory extends Category {
    private static final NumberRange<Byte> BYTE_0;
    static {
        final Byte index = 0;
        BYTE_0 = NumberRange.create(index, true, index, true);
    }

    private static final NumberRange<Byte> BYTE_1;
    static {
        final Byte index = 1;
        BYTE_1 = NumberRange.create(index, true, index, true);
    }

    public static final ColoredCategory NODATA = new ColoredCategory(
            Vocabulary.formatInternational(Vocabulary.Keys.Nodata), new Color(0,0,0,0), 0);

    public static final ColoredCategory FALSE = new ColoredCategory(
            Vocabulary.formatInternational(Vocabulary.Keys.False), Color.BLACK, false);

    public static final ColoredCategory TRUE = new ColoredCategory(
            Vocabulary.formatInternational(Vocabulary.Keys.True), Color.WHITE, true);

    private static final Color[] CYCLE = {
        Color.BLUE,    Color.RED,   Color.ORANGE, Color.YELLOW,     Color.PINK,
        Color.MAGENTA, Color.GREEN, Color.CYAN,   Color.LIGHT_GRAY, Color.GRAY
    };

    private static final int[] DEFAULT = {0xFF000000, 0xFFFFFFFF};

    private final int[] ARGB;

    /**
     * Copy constructor with addition of colors.
     */
    public ColoredCategory(Category copy, Color... colors) {
        super(copy);
        ARGB = toARGB(colors);
    }

    /**
     * Constructs a qualitative or quantitative category.
     *
     * @param  name     the category name (mandatory).
     * @param  samples  the minimum and maximum sample values (mandatory).
     * @param  toUnits  the conversion from sample values to real values,
     *                  or {@code null} for constructing a qualitative category.
     * @param  units    the units of measurement, or {@code null} if not applicable.
     *                  This is the target units after conversion by {@code toUnits}.
     * @param  toNaN    mapping from sample values to ordinal values to be supplied to {@link MathFunctions#toNanFloat(int)}.
     *                  That mapping is used only if {@code toUnits} is {@code null}. That mapping is responsible to ensure that
     *                  there is no ordinal value collision between different categories in the same {@link SampleDimension}.
     *                  The input is a real number in the {@code samples} range and the output shall be a unique value between
     *                  {@value MathFunctions#MIN_NAN_ORDINAL} and {@value MathFunctions#MAX_NAN_ORDINAL} inclusive.
     */
    public ColoredCategory(final CharSequence name, final Color[] colors, final NumberRange<?> samples, final MathTransform1D toUnits, final Unit<?> units,
             final DoubleToIntFunction toNaN)
    {
        super(name, samples, toUnits, units, toNaN);
        ARGB = toARGB(colors);
    }

    public ColoredCategory(CharSequence name, Color color, boolean sample) {
        this(name, new int[] {color != null ? color.getRGB() : sample ? 0xFFFFFFFF : 0xFF000000},
                sample ? BYTE_0 : BYTE_1, (MathTransform1D) MathTransforms.identity(1));
    }

    public ColoredCategory(CharSequence name, Color color, int sample) {
        this(name, toARGB(color, sample), Integer.valueOf(sample));
    }

    public ColoredCategory(CharSequence name, Color color, double sample) {
        this(name, toARGB(color, (int) Math.round(sample)), Double.valueOf(sample));
    }

    private ColoredCategory(CharSequence name, int[] ARGB, Number sample) {
        this(name, ARGB, new NumberRange(sample.getClass(), sample, true, sample, true), null);
    }

    public ColoredCategory(CharSequence name, Color color, NumberRange<?> sampleValueRange) {
        this(name, toARGB(color, sampleValueRange), sampleValueRange, (MathTransform1D) null);
    }

    public ColoredCategory(CharSequence name, Color[] colors, int lower, int upper, double scale, double offset) {
        this(name, colors, NumberRange.create(lower, true, upper, false), scale, offset);
    }

    public ColoredCategory(CharSequence name, Color[] colors, NumberRange<?> sampleValueRange, double scale, double offset) {
        this(name, colors, sampleValueRange, createLinearTransform(scale, offset));
    }

    public ColoredCategory(CharSequence name, Color[] colors, NumberRange<?> sampleValueRange, NumberRange<?> geophysicsValueRange) {
        this(name, colors, sampleValueRange, createLinearTransform(sampleValueRange, geophysicsValueRange));
    }

    public ColoredCategory(CharSequence name, Color[] colors, NumberRange<?> sampleValueRange, MathTransform1D sampleToGeophysics) {
        this(name, (colors == null && sampleToGeophysics == null) ? NODATA.ARGB : toARGB(colors), sampleValueRange, sampleToGeophysics);
    }

    private ColoredCategory(CharSequence name, int[] ARGB, NumberRange<?> range, MathTransform1D sampleToGeophysics) {
        super(name, range, sampleToGeophysics, null, (v) -> (int) v);
        this.ARGB = ARGB;
    }

    /**
     * Returns the set of colors for this category. Change to the returned array will not affect this category.
     *
     * @param  category  the category for which to get the colors, or {@code null}.
     * @return the colors palette for this category.
     */
    public static Color[] getColors(final Category category) {
        if (category instanceof ColoredCategory) {
            final int[] ARGB = ((ColoredCategory) category).ARGB;
            final Color[] colors = new Color[ARGB.length];
            for (int i=0; i < colors.length; i++) {
                colors[i] = new Color(ARGB[i], true);
            }
            return colors;
        }
        return new Color[0];
    }

    private static MathTransform1D createLinearTransform(final double scale, final double offset) {
        if (scale == 0 || Double.isNaN(scale) || Double.isInfinite(scale)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "scale", scale));
        }
        if (Double.isNaN(offset) || Double.isInfinite(offset)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "offset", offset));
        }
        return (MathTransform1D) MathTransforms.linear(scale, offset);
    }

    private static MathTransform1D createLinearTransform(final NumberRange<?> sampleValueRange, final NumberRange<?> geophysicsValueRange) {
        final Class<? extends Number> sType =     sampleValueRange.getElementType();
        final Class<? extends Number> gType = geophysicsValueRange.getElementType();
        int sMinInc =     sampleValueRange.isMinIncluded() ? 0 : +1;
        int sMaxInc =     sampleValueRange.isMaxIncluded() ? 0 : -1;
        int gMinInc = geophysicsValueRange.isMinIncluded() ? 0 : +1;
        int gMaxInc = geophysicsValueRange.isMaxIncluded() ? 0 : -1;
        if (sMinInc == gMinInc) sMinInc = gMinInc = 0;
        if (sMaxInc == gMaxInc) sMaxInc = gMaxInc = 0;
        final boolean adjustSamples = (Numbers.isInteger(sType) && !Numbers.isInteger(gType));
        if ((adjustSamples ? gMinInc : sMinInc) != 0) {
            int swap = sMinInc;
            sMinInc = -gMinInc;
            gMinInc = -swap;
        }
        if ((adjustSamples ? gMaxInc : sMaxInc) != 0) {
            int swap = sMaxInc;
            sMaxInc = -gMaxInc;
            gMaxInc = -swap;
        }
        final double minSample = doubleValue(sType,     sampleValueRange.getMinValue(), sMinInc);
        final double maxSample = doubleValue(sType,     sampleValueRange.getMaxValue(), sMaxInc);
        final double minValue  = doubleValue(gType, geophysicsValueRange.getMinValue(), gMinInc);
        final double maxValue  = doubleValue(gType, geophysicsValueRange.getMaxValue(), gMaxInc);
        final double dValue    = maxValue  - minValue;
        final double dSample   = maxSample - minSample;
        double scale = dValue / dSample;
        if (Double.isNaN(scale) && !Double.isNaN(dValue) && !Double.isNaN(dSample)) {
            scale = 1.0;
        }
        final double offset = minValue - scale*minSample;
        return createLinearTransform(scale, offset);
    }

    private static double doubleValue(final Class<? extends Number> type, final Number value, final int direction) {
        assert (direction >= -1) && (direction <= +1) : direction;
        return XMath.adjacentForType(type, value.doubleValue(), direction);
    }

    private static int[] toARGB(final Color[] colors) {
        final int[] ARGB;
        if (colors != null && colors.length != 0) {
            ARGB = new int[colors.length];
            for (int i=0; i<ARGB.length; i++) {
                final Color color = colors[i];
                if (color != null) {
                    ARGB[i] = color.getRGB();
                } else {
                    // Left ARGB[i] to its default value (0), which is the transparent color.
                }
            }
        } else {
            ARGB = DEFAULT;
        }
        return ARGB;
    }

    private static int[] toARGB(Color color, final int sample) {
        if (color == null) {
            color = CYCLE[Math.abs(sample) % CYCLE.length];
        }
        return new int[] {
            color.getRGB()
        };
    }

    private static int[] toARGB(final Color color, final NumberRange<?> sampleValueRange) {
        int sample = 0;
        if (color == null && sampleValueRange != null) {
            sample = (int) Math.round(sampleValueRange.getMinDouble(true));
            if (sample != Math.round(sampleValueRange.getMaxDouble(true))) {
                return DEFAULT;
            }
        }
        return toARGB(color, sample);
    }
}
