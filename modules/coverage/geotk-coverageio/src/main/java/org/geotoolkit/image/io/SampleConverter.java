/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.awt.image.Raster;
import net.jcip.annotations.Immutable;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.util.converter.Classes;


/**
 * Converts samples from the values stored in the image file to the values stored in the
 * {@linkplain Raster raster}. Some typical conversions are:
 * <p>
 * <ul>
 *   <li>Replace "<cite>nodata</cite>" values (typically a fixed value like 9999 or
 *       {@value java.lang.Short#MAX_VALUE}) by {@link Float#NaN NaN} if the target type is
 *       {@code float} or {@code double}, or 0 if the target type is an integer.</li>
 *   <li>Replace <em>signed</em> integers by <em>unsigned</em> integers, by applying
 *       an offset to the values.</li>
 * </ul>
 * <p>
 * Note that pad values are replaced by 0 in the integer case, not by an arbitrary number,
 * because 0 is the result of {@code (int) NaN} cast. While not mandatory, this property
 * make some mathematics faster during conversions between <cite>geophysics</cite> and
 * <cite>display</cite> views in the coverage module.
 * <p>
 * There is no scaling because this class is not for <cite>samples to geophysics values</cite>
 * conversions (except the replacement of pad values by {@link Double#NaN NaN}). This class is
 * about the minimal changes needed in order to comply to the constraints of a target
 * {@linkplain java.awt.image.ColorModel color model}, for example in order to workaround
 * the fact that {@link java.awt.image.IndexColorModel} does not accept negative numbers.
 * <p>
 * {@code SampleConverter}s are typically created and used by the {@link SpatialImageReader}
 * class and subclasses. They are created (directly or indirectly) by the
 * {@link SpatialImageReader#getImageType getImageType} method and used by the
 * {@link SpatialImageReader#read read} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see SampleConversionType
 *
 * @since 2.4
 * @module
 */
@Immutable
public abstract class SampleConverter {
    /**
     * A sample converter that do not performs any conversion.
     */
    public static final SampleConverter IDENTITY = new Identity();

    /**
     * Constructs a sample converter.
     */
    protected SampleConverter() {
    }

    /**
     * Creates a sample converter that replaces a single pad value by {@link Double#NaN NaN}
     * for floating point numbers, or {@code 0} for integers.
     *
     * @param  padValue The pad values to replace by {@link Double#NaN NaN} or {@code 0}.
     * @return The sample converter.
     */
    public static SampleConverter createPadValueMask(final double padValue) {
        return Double.isNaN(padValue) ? IDENTITY : new PadValueMask(padValue);
    }

    /**
     * Creates a sample converter that replaces an arbitrary amount of pad values by
     * {@link Double#NaN NaN} for floating point numbers, or {@code 0} for integers.
     *
     * @param  padValues The pad values to replace by {@link Double#NaN NaN} or {@code 0},
     *         or {@code null} if none.
     * @return The sample converter.
     */
    public static SampleConverter createPadValuesMask(final double[] padValues) {
        if (padValues != null) {
            switch (padValues.length) {
                default: return new PadValuesMask(padValues);
                case 1:  return createPadValueMask(padValues[0]);
                case 0:  break;
            }
        }
        return IDENTITY;
    }

    /**
     * Creates a sample converter that replaces a pad value by {@link Double#NaN NaN} or
     * {@code 0}, and applies an offset on all other values. This is typically used in
     * order to shift a range of arbitrary (including negative) integer values to a range
     * of strictly positive values. The later is more manageable by
     * {@linkplain java.awt.image.IndexColorModel index color model}.
     *
     * @param  offset An offset to add to the values to be read, before to store them in the raster.
     *         This is used primarily for transforming <em>signed</em> short into <em>unsigned</em>
     *         short.
     * @param  padValue The pad value to replace. This the value before the offset is applied.
     * @return The sample converter.
     */
    public static SampleConverter createOffset(final double offset, final double padValue) {
        return (offset == 0) ? createPadValueMask(padValue) : new Offset(offset, padValue);
    }

    /**
     * Creates a sample converter that replaces an arbitrary amount of pad values by
     * {@link Double#NaN NaN} or {@code 0}, and applies an offset on all other values.
     *
     * @param  offset An offset to add to the values to be read, before to store them in the raster.
     * @param  padValues The pad values to replace. They the values before the offset is applied.
     * @return The sample converter.
     */
    public static SampleConverter createOffset(final double offset, final double[] padValues) {
        if (offset == 0) {
            return createPadValuesMask(padValues);
        }
        if (padValues != null) {
            switch (padValues.length) {
                default: return new MaskAndOffset(offset, padValues);
                case 1:  return createOffset(offset, padValues[0]);
                case 0:  break;
            }
        }
        return createOffset(offset, Double.NaN);
    }

    /**
     * Converts a double-precision value before to store it in the raster.
     * Subclasses should override this method if some fixed values need to
     * be converted into {@link Double#NaN} value.
     *
     * @param  value The value read from the image file.
     * @return The value to store in the {@linkplain Raster raster}.
     */
    public abstract double convert(double value);

    /**
     * Converts a float-precision value before to store it in the raster.
     * Subclasses should override this method if some fixed values need to
     * be converted into {@link Float#NaN} value.
     *
     * @param value The value read from the image file.
     * @return The value to store in the {@linkplain Raster raster}.
     */
    public abstract float convert(float value);

    /**
     * Converts a float-precision value before to store it in the raster.
     *
     * @param  value The value read from the image file.
     * @return The value to store in the {@linkplain Raster raster}.
     */
    public abstract int convert(int value);

    /**
     * Converts in-place an array of double-precision values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convert(final double[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = convert(values[offset]);
            offset++;
        }
    }

    /**
     * Converts in-place an array of single-precision values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convert(final float[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = convert(values[offset]);
            offset++;
        }
    }

    /**
     * Converts in-place an array of integer values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convert(final int[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = convert(values[offset]);
            offset++;
        }
    }

    /**
     * Converts in-place an array of short values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convert(final short[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = (short) convert(values[offset]);
            offset++;
        }
    }

    /**
     * Converts in-place an array of unsigned short values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convertUnsigned(final short[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = (short) convert(values[offset] & 0xFFFF);
            offset++;
        }
    }

    /**
     * Converts in-place an array of unsigned byte values.
     *
     * @param values The values to convert.
     * @param offset Index of the first sample to convert.
     * @param length Number of samples to convert.
     *
     * @since 3.07
     */
    public void convertUnsigned(final byte[] values, int offset, int length) {
        length += offset;
        while (offset < length) {
            values[offset] = (byte) convert(values[offset] & 0xFF);
            offset++;
        }
    }

    /**
     * If this converter applies an offset, returns the offset. Otherwise returns 0.
     *
     * @return The offset applied when converting sample values.
     */
    public double getOffset() {
        return 0;
    }

    /**
     * Returns a string representation of this sample converter.
     * This is mostly for debugging purpose and may change in any future version.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "[offset=" + getOffset() + ']';
    }

    /**
     * A sample converter that does not perform any conversion.
     */
    @Immutable
    private static final class Identity extends SampleConverter {
        @Override public double convert(double   value)  {return value;}
        @Override public float  convert(float    value)  {return value;}
        @Override public int    convert(int      value)  {return value;}
        @Override public void   convert(double[] values, int offset, int length) {}
        @Override public void   convert(float[]  values, int offset, int length) {}
        @Override public void   convert(int[]    values, int offset, int length) {}
        @Override public void   convert(short[]  values, int offset, int length) {}
        @Override public void   convertUnsigned(short[] values, int offset, int length) {}
        @Override public void   convertUnsigned(byte[]  values, int offset, int length) {}
    }

    /**
     * A sample converter that replaces a single pad value by {@link Double#NaN NaN}
     * for floating point numbers, or {@code 0} for integers.
     */
    @Immutable
    private static class PadValueMask extends SampleConverter {
        final double doubleValue;
        final float  floatValue;
        final int    integerValue;

        PadValueMask(final double padValue) {
            doubleValue  =         padValue;
            floatValue   = (float) padValue;
            final int p  = (int)   padValue;
            integerValue = (p == padValue) ? p : 0;
        }

        @Override public double convert(final double value) {
            return (value == doubleValue) ? Double.NaN : value;
        }

        @Override public float convert(final float value) {
            return (value == floatValue) ? Float.NaN : value;
        }

        @Override public int convert(final int value) {
            return (value == integerValue) ? 0 : value;
        }
    }

    /**
     * A sample converter that replaces a single pad value by 0,
     * and applies an offset on all other values.
     */
    @Immutable
    private static final class Offset extends PadValueMask {
        private final double doubleOffset;
        private final float  floatOffset;
        private final int    integerOffset;

        Offset(final double offset, final double padValue) {
            super(padValue);
            doubleOffset  = offset;
            floatOffset   = (float) offset;
            integerOffset = (int) Math.round(offset);
        }

        @Override public double convert(final double value) {
            return (value == doubleValue) ? Double.NaN : value + doubleOffset;
        }

        @Override public float convert(final float value) {
            return (value == floatValue) ? Float.NaN : value + floatOffset;
        }

        @Override public int convert(final int value) {
            return (value == integerValue) ? 0 : value + integerOffset;
        }

        @Override public double getOffset() {
            return doubleOffset;
        }
    }

    /**
     * A sample converter that replaces an arbitrary amount of pad values by
     * {@link Double#NaN NaN} for floating point numbers, or {@code 0} for integers.
     */
    @Immutable
    private static class PadValuesMask extends SampleConverter {
        private final double[] doubleValues;
        private final float [] floatValues;
        private final float [] NaNs;

        PadValuesMask(final double[] padValues) {
            doubleValues  = new double[padValues.length];
            floatValues   = new float [padValues.length];
            NaNs          = new float [padValues.length];
            for (int i=0; i<padValues.length; i++) {
                floatValues[i] = (float) (doubleValues[i] = padValues[i]);
                NaNs[i] = MathFunctions.toNanFloat(i);
            }
        }

        @Override public double convert(final double value) {
            for (int i=0; i<doubleValues.length; i++) {
                if (value == doubleValues[i]) {
                    return NaNs[i];
                }
            }
            return value;
        }

        @Override public float convert(final float value) {
            for (int i=0; i<floatValues.length; i++) {
                if (value == floatValues[i]) {
                    return NaNs[i];
                }
            }
            return value;
        }

        // Do not override: we really need the arithmetic on NaN values.
        @Override public final int convert(final int value) {
            return (int) convert((double) value);
        }
    }

    /**
     * A sample converter that replaces many pad values by 0,
     * and applies an offset on all other values.
     */
    @Immutable
    private static final class MaskAndOffset extends PadValuesMask {
        private final double doubleOffset;
        private final float  floatOffset;

        MaskAndOffset(final double offset, final double[] padValues) {
            super(padValues);
            doubleOffset =         offset;
            floatOffset  = (float) offset;
        }

        @Override public double convert(final double value) {
            return super.convert(value) + doubleOffset;
        }

        @Override public float convert(final float value) {
            return super.convert(value) + floatOffset;
        }

        @Override public double getOffset() {
            return doubleOffset;
        }
    }
}
