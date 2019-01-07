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

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;

import org.opengis.util.InternationalString;
import org.opengis.coverage.SampleDimensionType;
import static org.opengis.coverage.SampleDimensionType.*;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.iso.AbstractInternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.geotoolkit.lang.Static;
import org.geotoolkit.math.NumberSet;


/**
 * Utility methods for choosing a {@linkplain SampleModel sample model} or a
 * {@linkplain ColorModel color model} on the basis of a range of values.
 * This class provides also some methods for mapping {@link SampleDimensionType}
 * to {@link DataBuffer} types.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.10
 *
 * @since 2.1
 * @module
 */
public final class TypeMap extends Static {
    /**
     * The mapping of {@link SampleDimensionType} to {@link DataBuffer} types.
     * Must be sorted in increasing number of bits.
     */
    private static final TypeMap[] MAP = new TypeMap[SampleDimensionType.values().length];
    static {
        final Map<Number,Number> pool = new HashMap<>(32);
        final Float  M1 = -Float .MAX_VALUE;
        final Float  P1 =  Float .MAX_VALUE;
        final Double M2 = -Double.MAX_VALUE;
        final Double P2 =  Double.MAX_VALUE;
        // The constructor will register automatically those objects in the above array.
        new TypeMap( UNSIGNED_1BIT,  DataBuffer.TYPE_BYTE,   (byte) 1, false, false,        pool);
        new TypeMap( UNSIGNED_2BITS, DataBuffer.TYPE_BYTE,   (byte) 2, false, false,        pool);
        new TypeMap( UNSIGNED_4BITS, DataBuffer.TYPE_BYTE,   (byte) 4, false, false,        pool);
        new TypeMap( UNSIGNED_8BITS, DataBuffer.TYPE_BYTE,   (byte) 8, false, false,        pool);
        new TypeMap(   SIGNED_8BITS, DataBuffer.TYPE_BYTE,   (byte) 8, true,  false,        pool);
        new TypeMap(UNSIGNED_16BITS, DataBuffer.TYPE_USHORT, (byte)16, false, false,        pool);
        new TypeMap(  SIGNED_16BITS, DataBuffer.TYPE_SHORT,  (byte)16, true,  false,        pool);
        new TypeMap(UNSIGNED_32BITS, DataBuffer.TYPE_INT,    (byte)32, false, false,        pool);
        new TypeMap(  SIGNED_32BITS, DataBuffer.TYPE_INT,    (byte)32, true,  false,        pool);
        new TypeMap(    REAL_32BITS, DataBuffer.TYPE_FLOAT,  (byte)32, true,  true, M1, P1, pool);
        new TypeMap(    REAL_64BITS, DataBuffer.TYPE_DOUBLE, (byte)64, true,  true, M2, P2, pool);
    };

    /**
     * One of {@link SampleDimensionType} code list.
     */
    private final SampleDimensionType code;

    /**
     * The {@link DataBuffer} type. Must be one of the following constants:
     * {@link DataBuffer#TYPE_BYTE},  {@link DataBuffer#TYPE_USHORT},
     * {@link DataBuffer#TYPE_SHORT}, {@link DataBuffer#TYPE_INT},
     * {@link DataBuffer#TYPE_FLOAT}, {@link DataBuffer#TYPE_DOUBLE}.
     */
    private final int type;

    /**
     * The size in bits. The value range from 1 to 64. This is different than
     * {@link DataBuffer#getDataTypeSize}, which have values ranging from 8 to 64.
     */
    private final byte size;

    /**
     * {@code true} for signed sample type.
     */
    private final boolean signed;

    /**
     * {@code true} for floating-point data type.
     */
    private final boolean real;

    /**
     * The full range of sample values.
     */
    private final NumberRange<?> range;

    /**
     * The range of positive sample values (excluding 0). This range is non-null only for unsigned
     * type. A range excluding 0 is sometime useful when the 0 value is reserved for a "no data"
     * category.
     */
    private final NumberRange<?> positiveRange;

    /**
     * The name as an international string.
     */
    private final InternationalString name = new AbstractInternationalString() {
        @Override public String toString(final Locale locale) {
            return Vocabulary.getResources(locale).getString(Vocabulary.Keys.DataType_2,
                    Integer.valueOf(real ? 2 : signed ? 1 : 0), size);
        }
    };

    /**
     * Constructs a new mapping with the specified value.
     */
    private TypeMap(final SampleDimensionType code,
                    final int     type,   final byte    size,
                    final boolean signed, final boolean real,
                    final Map<Number,Number> pool)
    {
        this(code, type, size, signed, real, null, null, pool);
    }

    /**
     * Constructs a new mapping with the specified value.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private TypeMap(final SampleDimensionType code,
                    final int     type,   final byte    size,
                    final boolean signed, final boolean real,
                    Number lower, Number upper, final Map<Number,Number> pool)
    {
        Number one = null;
        if (lower == null) {
            final long max = (1L << (signed ? size-1 : size)) - 1;
            final long min = signed ? ~max : 0; // Tild (~), not minus sign (-).
            if (max <= Byte.MAX_VALUE) {
                lower = Byte.valueOf((byte) min);
                upper = Byte.valueOf((byte) max);
                one   = Byte.valueOf((byte) 1);
            } else if (max <= Short.MAX_VALUE) {
                lower = Short.valueOf((short) min);
                upper = Short.valueOf((short) max);
                one   = Short.valueOf((short) 1);
            } else if (max <= Integer.MAX_VALUE) {
                lower = Integer.valueOf((int) min);
                upper = Integer.valueOf((int) max);
                one   = Integer.valueOf(1);
            } else {
                lower = Long.valueOf(min);
                upper = Long.valueOf(max);
                one   = Long.valueOf(1L);
            }
            lower = unique(pool, lower);
            upper = unique(pool, upper);
            one   = unique(pool, one);
            assert lower.longValue() == min;
            assert upper.longValue() == max;
        }
        assert ((Comparable) lower).compareTo(upper) < 0 : upper;
        final Class<? extends Number> c = upper.getClass();
        this.code          = code;
        this.type          = type;
        this.size          = size;
        this.signed        = signed;
        this.real          = real;
        this.range         = new NumberRange(c, lower, true, upper, true);
        this.positiveRange = signed ? null : new NumberRange(c, one, true, upper, true);
        final int ordinal  = code.ordinal();
        assert MAP[ordinal] == null : code;
        MAP[ordinal] = this;
        assert code.equals(getSampleDimensionType(range)) : code;
    }

    /**
     * Returns a single instance of the specified number.
     */
    private static Number unique(final Map<Number,Number> pool, final Number n) {
        final Number candidate = pool.put(n, n);
        if (candidate == null) {
            return n;
        }
        pool.put(candidate, candidate);
        return candidate;
    }

    /**
     * Returns the smallest sample dimension type capable to hold the specified range of values.
     *
     * @param  range The range of values.
     * @return The smallest sample dimension type for the specified range.
     */
    public static SampleDimensionType getSampleDimensionType(final Range<? extends Number> range) {
        final Class<? extends Number> type = range.getElementType();
        if (Double.class.isAssignableFrom(type)) {
            return REAL_64BITS;
        }
        if (Float.class.isAssignableFrom(type)) {
            return REAL_32BITS;
        }
        long min = range.getMinValue().longValue();
        long max = range.getMaxValue().longValue();
        if (!range.isMinIncluded()) min++;
        if (!range.isMaxIncluded()) max--;
        return getSampleDimensionType(min, max);
    }

    /**
     * Returns the smallest sample dimension type capable to hold the specified range of values.
     * An heuristic approach is used for non-integer values.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, <strong>inclusive</strong> as well.
     * @return The smallest sample dimension type for the specified range.
     */
    public static SampleDimensionType getSampleDimensionType(double min, double max) {
        final long lgMin = (long) min;
        if (lgMin == min) {
            final long lgMax = (long) max;
            if (lgMax == max) {
                return getSampleDimensionType(lgMin, lgMax);
            }
        }
        min = Math.abs(min);
        max = Math.abs(max);
        if (Math.min(min,max) >= -Float.MAX_VALUE  &&  Math.max(min,max) <= Float.MAX_VALUE) {
            return REAL_32BITS;
        }
        return REAL_64BITS;
    }

    /**
     * Returns the smallest sample dimension type capable to hold the specified range of values.
     *
     * @param  min The lower value, inclusive.
     * @param  max The upper value, <strong>inclusive</strong> as well.
     * @return The smallest sample dimension type for the specified range.
     */
    public static SampleDimensionType getSampleDimensionType(final long min, final long max) {
        if (min >= 0) {
            if (max < (1L <<  1)) return UNSIGNED_1BIT;
            if (max < (1L <<  2)) return UNSIGNED_2BITS;
            if (max < (1L <<  4)) return UNSIGNED_4BITS;
            if (max < (1L <<  8)) return UNSIGNED_8BITS;
            if (max < (1L << 16)) return UNSIGNED_16BITS;
            if (max < (1L << 32)) return UNSIGNED_32BITS;
        } else {
            if (min >= Byte   .MIN_VALUE && max <= Byte   .MAX_VALUE) return SIGNED_8BITS;
            if (min >= Short  .MIN_VALUE && max <= Short  .MAX_VALUE) return SIGNED_16BITS;
            if (min >= Integer.MIN_VALUE && max <= Integer.MAX_VALUE) return SIGNED_32BITS;
        }
        return REAL_32BITS;
    }

    /**
     * Returns the sample dimension for the given number of bits. This method may return a
     * type having more bits than the requested one. It may also return a signed type even
     * if a unsigned type was requested, provided that the returned type is wide enough for
     * holding all unsigned values expressed using the given number of bits.
     *
     * @param type The number type as one of {@link NumberSet#NATURAL NATURAL} for unsigned
     *        integers, {@link NumberSet#INTEGER INTEGER} for signed integers, or
     *        {@link NumberSet#REAL READ} for floating point values.
     * @param numBits The number of bits.
     * @return The sample dimension type for the given criterion, or {@code null} if none match.
     *
     * @since 3.10
     */
    public static SampleDimensionType getSampleDimensionType(final NumberSet type, final int numBits) {
        final boolean real = type.ordinal() > NumberSet.INTEGER.ordinal();
        final boolean signed = (type != NumberSet.NATURAL);
        for (final TypeMap candidate : MAP) {
            if (candidate.size >= numBits && candidate.real == real) {
                // If 'signed' doesn't match the requested one,
                // accept anyway if we have at least one more bit.
                if (candidate.signed == signed || (!signed && candidate.size != numBits)) {
                    return candidate.code;
                }
            }
        }
        return null;
    }

    /**
     * Returns the sample dimension type for the specified sample model and band number. If
     * the sample model use an undefined data type, then this method returns {@code null}.
     *
     * @param  model The sample model.
     * @param  band  The band to query.
     * @return The sample dimension type for the specified sample model and band number.
     * @throws IllegalArgumentException if the band number is not in the valid range.
     */
    @SuppressWarnings("fallthrough")
    public static SampleDimensionType getSampleDimensionType(final SampleModel model, final int band)
            throws IllegalArgumentException
    {
        if (band < 0 || band >= model.getNumBands()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalBandNumber_1, band));
        }
        /*
         * TODO: we have a bug with some types like BufferedImage.TYPE_RGB: the model use DataBuffer.TYPE_INT
         *       but the sample size are 8 bits. We are probably mixing two info that should not be mixed.
         */
        boolean signed = true;
        switch (model.getDataType()) {
            case DataBuffer.TYPE_DOUBLE: return REAL_64BITS;
            case DataBuffer.TYPE_FLOAT:  return REAL_32BITS;
            case DataBuffer.TYPE_USHORT: // Fall through
            case DataBuffer.TYPE_BYTE:   signed = false; // Fall through
            case DataBuffer.TYPE_INT:
            case DataBuffer.TYPE_SHORT: {
                switch (model.getSampleSize(band)) {
                    case  1: return UNSIGNED_1BIT;
                    case  2: return UNSIGNED_2BITS;
                    case  4: return UNSIGNED_4BITS;
                    case  8: return signed ? SIGNED_8BITS  : UNSIGNED_8BITS;
                    case 16: return signed ? SIGNED_16BITS : UNSIGNED_16BITS;
                    case 32: return signed ? SIGNED_32BITS : UNSIGNED_32BITS;
                }
            }
        }
        return null;
    }

    /**
     * Returns the sample dimension type name as an international string. For example, the localized
     * name for {@link SampleDimensionType#UNSIGNED_16BITS} is "<cite>16 bits unsigned integer</cite>"
     * in English and "<cite>Entier non-signé sur 16 bits</cite>" in French.
     *
     * @param type The sample dimension type for which to get a localized description.
     * @return The localized description of the given type.
     */
    public static InternationalString getName(final SampleDimensionType type) {
        final int ordinal = type.ordinal();
        if (ordinal >= 0 && ordinal < MAP.length) {
            return MAP[ordinal].name;
        }
        return new SimpleInternationalString(type.name());
    }

    /**
     * Returns the {@link DataBuffer} type. This is one of the following constants:
     * {@link DataBuffer#TYPE_BYTE   TYPE_BYTE},
     * {@link DataBuffer#TYPE_USHORT TYPE_USHORT},
     * {@link DataBuffer#TYPE_SHORT  TYPE_SHORT},
     * {@link DataBuffer#TYPE_INT    TYPE_INT},
     * {@link DataBuffer#TYPE_FLOAT  TYPE_FLOAT},
     * {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE} or
     * {@link DataBuffer#TYPE_UNDEFINED} if the type is unrecognized.
     *
     * @param type The sample dimension type for which to get the data buffer type.
     * @return The data buffer type for the given sample dimension type.
     */
    public static int getDataBufferType(final SampleDimensionType type) {
        if (type != null) {
            final int ordinal = type.ordinal();
            if (ordinal >= 0 && ordinal < MAP.length) {
                return MAP[ordinal].type;
            }
        }
        return DataBuffer.TYPE_UNDEFINED;
    }

    /**
     * Returns the size in bits. The value range from 1 to 64. This is similar, but
     * different than {@link DataBuffer#getDataTypeSize}, which have values ranging
     * from 8 to 64.
     *
     * @param type The sample dimension type for which to get the data size.
     * @return The data size in bits for the given sample dimension type.
     */
    public static int getSize(final SampleDimensionType type) {
        return map(type).size;
    }

    /**
     * Returns {@code true} for signed sample type.
     *
     * @param type The sample dimension type for which to get information about its data.
     * @return {@code true} if the data of the given sample dimension type are signed.
     */
    public static boolean isSigned(final SampleDimensionType type) {
        return map(type).signed;
    }

    /**
     * Returns {@code true} for floating-point data type.
     *
     * @param type The sample dimension type for which to get information about its data.
     * @return {@code true} if the data of the given sample dimension type are floating points.
     */
    public static boolean isFloatingPoint(final SampleDimensionType type) {
        return map(type).real;
    }

    /**
     * Returns the full range of sample values for the specified dimension type.
     *
     * @param type The sample dimension type for which to get its data range.
     * @return The range of values for the given sample dimension type.
     */
    public static NumberRange<?> getRange(final SampleDimensionType type) {
        if (type != null) {
            final int ordinal = type.ordinal();
            if (ordinal >= 0 && ordinal < MAP.length) {
                return MAP[ordinal].range;
            }
        }
        return null;
    }

    /**
     * Returns the range of positive sample values (excluding 0). This range is non-null only for
     * unsigned type. A range excluding 0 is sometime useful when the 0 value is reserved for a
     * "no data" category.
     *
     * @param type The sample dimension type for which to get its data range.
     * @return The range of values for the given sample dimension type.
     */
    public static NumberRange<?> getPositiveRange(final SampleDimensionType type) {
        if (type != null) {
            final int ordinal = type.ordinal();
            if (ordinal >= 0 && ordinal < MAP.length) {
                return MAP[ordinal].positiveRange;
            }
        }
        return null;
    }

    /**
     * Returns the mapper for the specified sample dimension type. If no map is found for the
     * specified sample dimension type, then an exception is thrown.
     */
    private static TypeMap map(final SampleDimensionType type) throws IllegalArgumentException {
        if (type != null) {
            final int ordinal = type.ordinal();
            if (ordinal >= 0 && ordinal < MAP.length) {
                final TypeMap map = MAP[ordinal];
                if (map != null) {
                    return map;
                }
            }
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "type", type));
    }

    /**
     * Wraps the specified value into a number of the specified data type. If the
     * value can't fit in the specified type, then a wider type is chosen unless
     * {@code allowWidening} is {@code false}.
     *
     * @param  value The value to wrap in a {@link Number} object.
     * @param  type A constant from the {@link SampleDimensionType} code list.
     * @param  allowWidening {@code true} if this method is allowed to returns
     *         a wider type than the usual one for the specified {@code type}.
     * @return The value as a {@link Number}.
     * @throws IllegalArgumentException if {@code type} is not a recognized constant, or
     *         if {@code allowWidening} is {@code false} and the specified {@code value}
     *         can't fit in the specified sample type.
     */
    @SuppressWarnings("fallthrough")
    public static Number wrapSample(final double value, final SampleDimensionType type,
            final boolean allowWidening) throws IllegalArgumentException
    {
        /*
         * Note about 'ordinal' computation: We would like to switch on SampleDimensionType
         * ordinal values. But the compiler requires constant values, and doesn't recognize
         * SampleDimensionType ordinal as such. As a workaround, we use the sample size (in
         * bits) with the following convention: negative value if signed, and offset by 16
         * bits if floating point numbers.
         */
        final TypeMap map = map(type);
        int ordinal = map.size;
        if (map.real) {
            ordinal <<= 16;
        } else if (map.signed) {
            ordinal = -ordinal;
        }
        switch (ordinal) {
            case  1: // Fall through
            case  2: // Fall through
            case  4: // Fall through
            case -8: {
                final byte candidate = (byte) value;
                if (candidate == value) {
                    return Byte.valueOf(candidate);
                }
                if (!allowWidening) break;
                // Fall through
            }
            case   8: // Fall through
            case -16: {
                final short candidate = (short) value;
                if (candidate == value) {
                    return Short.valueOf(candidate);
                }
                if (!allowWidening) break;
                // Fall through
            }
            case  16: // Fall through
            case -32: {
                final int candidate = (int) value;
                if (candidate == value) {
                    return Integer.valueOf(candidate);
                }
                if (!allowWidening) break;
                // Fall through
            }
            case 32: {
                final long candidate = (long) value;
                if (candidate == value) {
                    return Long.valueOf(candidate);
                }
                if (!allowWidening) break;
                // Fall through
            }
            case (32 << 16): {
                if (!allowWidening || Math.abs(value) <= Float.MAX_VALUE) {
                    return Float.valueOf((float) value);
                }
                // Fall through
            }
            case (64 << 16): {
                return Double.valueOf(value);
            }
            default: {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "type", type));
            }
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "value", value));
    }
}
