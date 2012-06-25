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
package org.geotoolkit.util.converter;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.Collections;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.reflect.Array;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.XCollections;


/**
 * Miscellaneous static methods working on {@link Number} objects, and a few primitive types
 * by extension.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 2.5)
 * @module
 */
public final class Numbers extends Static {
    /**
     * Constants to be used in {@code switch} statements.
     */
    public static final byte
            DOUBLE=8, FLOAT=7, LONG=6, INTEGER=5, SHORT=4, BYTE=3, CHARACTER=2, BOOLEAN=1, OTHER=0;
    // Note: This class assumes that DOUBLE is the greatest public constant.

    /**
     * Mapping between a primitive type and its wrapper, if any.
     */
    private static final Map<Class<?>,Numbers> MAPPING = new HashMap<Class<?>,Numbers>(16);
    static {
        new Numbers(BigDecimal.class, true, false, (byte) (DOUBLE+2)); // Undocumented enum.
        new Numbers(BigInteger.class, false, true, (byte) (DOUBLE+1)); // Undocumented enum.
        new Numbers(Double   .TYPE, Double   .class, true,  false, (byte) Double   .SIZE, DOUBLE,    'D', Double   .valueOf(Double.NaN));
        new Numbers(Float    .TYPE, Float    .class, true,  false, (byte) Float    .SIZE, FLOAT,     'F', Float    .valueOf(Float .NaN));
        new Numbers(Long     .TYPE, Long     .class, false, true,  (byte) Long     .SIZE, LONG,      'J', Long     .valueOf(        0L));
        new Numbers(Integer  .TYPE, Integer  .class, false, true,  (byte) Integer  .SIZE, INTEGER,   'I', Integer  .valueOf(        0));
        new Numbers(Short    .TYPE, Short    .class, false, true,  (byte) Short    .SIZE, SHORT,     'S', Short    .valueOf((short) 0));
        new Numbers(Byte     .TYPE, Byte     .class, false, true,  (byte) Byte     .SIZE, BYTE,      'B', Byte     .valueOf((byte)  0));
        new Numbers(Character.TYPE, Character.class, false, false, (byte) Character.SIZE, CHARACTER, 'C', Character.valueOf((char)  0));
        new Numbers(Boolean  .TYPE, Boolean  .class, false, false, (byte) 1,              BOOLEAN,   'Z', Boolean.FALSE);
        new Numbers(Void     .TYPE, Void     .class, false, false, (byte) 0,              OTHER,     'V', null);
    }

    /** The primitive type.                     */ private final Class<?> primitive;
    /** The wrapper for the primitive type.     */ private final Class<?> wrapper;
    /** {@code true} for floating point number. */ private final boolean  isFloat;
    /** {@code true} for integer number.        */ private final boolean  isInteger;
    /** The size in bytes.                      */ private final byte     size;
    /** Constant to be used in switch statement.*/ private final byte     ordinal;
    /** The internal form of the primitive name.*/ private final char     internal;
    /** The null, NaN, 0 or false value.        */ private final Object   nullValue;

    /**
     * Creates an entry for a type which is not a primitive type.
     */
    private Numbers(final Class<?> type, final boolean isFloat, final boolean isInteger, final byte ordinal) {
        primitive = wrapper = type;
        this.isFloat   = isFloat;
        this.isInteger = isInteger;
        this.size      = -1;
        this.ordinal   = ordinal;
        this.internal  = 'L'; // Defined by Java, and tested elsewhere in this class.
        this.nullValue = null;
        if (MAPPING.put(type, this) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Creates a mapping between a primitive type and its wrapper.
     */
    private Numbers(final Class<?> primitive, final Class<?> wrapper,
                    final boolean  isFloat,   final boolean  isInteger,
                    final byte     size,      final byte     ordinal,
                    final char     internal,  final Object   nullValue)
    {
        this.primitive = primitive;
        this.wrapper   = wrapper;
        this.isFloat   = isFloat;
        this.isInteger = isInteger;
        this.size      = size;
        this.ordinal   = ordinal;
        this.internal  = internal;
        this.nullValue = nullValue;
        if (MAPPING.put(primitive, this) != null || MAPPING.put(wrapper, this) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Returns the Java letter used for the internal representation of this given primitive type.
     */
    static char getInternal(final Class<?> type) {
        return MAPPING.get(type).internal;
    }

    /**
     * Returns {@code true} if the given {@code type} is a floating point type.
     *
     * @param  type The type to test (may be {@code null}).
     * @return {@code true} if {@code type} is the primitive or wrapper class of
     *         {@link Float} or {@link Double}.
     *
     * @see #isInteger(Class)
     */
    public static boolean isFloat(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isFloat;
    }

    /**
     * Returns {@code true} if the given {@code type} is an integer type. The integer types are
     * {@link Long}, {@code long}, {@link Integer}, {@code int}, {@link Short}, {@code short},
     * {@link Byte}, {@code byte} and {@link BigInteger}.
     *
     * @param  type The type to test (may be {@code null}).
     * @return {@code true} if {@code type} is an integer type.
     *
     * @see #isFloat(Class)
     */
    public static boolean isInteger(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isInteger;
    }

    /**
     * Returns {@code true} if the given {@code type} is an integer type. This method performs
     * the same test than {@link #isPrimitiveInteger}, excluding {@link BigInteger}.
     *
     * @param  type The type to test (may be {@code null}).
     * @return {@code true} if {@code type} is the primitive of wrapper class of
     *         {@link Long}, {@link Integer}, {@link Short} or {@link Byte}.
     *
     * @see #isInteger(Class)
     */
    private static boolean isPrimitiveInteger(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isInteger && (mapping.internal != 'L');
    }

    /**
     * Returns the number of bits used by primitive of the specified type.
     * The given type must be a primitive type or its wrapper class.
     *
     * @param  type The primitive type (may be {@code null}).
     * @return The number of bits, or 0 if {@code type} is null.
     * @throws IllegalArgumentException if the given type is unknown.
     */
    public static int primitiveBitCount(final Class<?> type) throws IllegalArgumentException {
        final Numbers mapping = MAPPING.get(type);
        if (mapping != null) {
            final int size = mapping.size;
            if (size >= 0) {
                return size;
            }
        }
        if (type == null) {
            return 0;
        }
        throw unknownType(type);
    }

    /**
     * Changes a primitive class to its wrapper (for example {@code int} to {@link Integer}).
     * If the specified class is not a primitive type, then it is returned unchanged.
     *
     * @param  type The primitive type (may be {@code null}).
     * @return The type as a wrapper.
     *
     * @see #wrapperToPrimitive(Class)
     */
    public static Class<?> primitiveToWrapper(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        return (mapping != null) ? mapping.wrapper : type;
    }

    /**
     * Changes a wrapper class to its primitive (for example {@link Integer} to {@code int}).
     * If the specified class is not a wrapper type, then it is returned unchanged.
     *
     * @param  type The wrapper type (may be {@code null}).
     * @return The type as a primitive.
     *
     * @see #primitiveToWrapper(Class)
     */
    public static Class<?> wrapperToPrimitive(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        return (mapping != null) ? mapping.primitive : type;
    }

    /**
     * Returns the widest type of two numbers. Numbers {@code n1} and {@code n2} can be instance of
     * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double},
     * {@link BigInteger} or {@link BigDecimal} types.
     * <p>
     * If one of the given argument is null, then this method returns the class of the
     * non-null argument. If both arguments are null, then this method returns {@code null}.
     *
     * @param  n1 The first number, or {@code null}.
     * @param  n2 The second number, or {@code null}.
     * @return The widest type of the given numbers, or {@code null} if not {@code n1} and {@code n2} are null.
     * @throws IllegalArgumentException If a number is not of a known type.
     *
     * @see #widestClass(Number, Number)
     * @see #finestClass(Number, Number)
     */
    public static Class<? extends Number> widestClass(final Number n1, final Number n2)
            throws IllegalArgumentException
    {
        return widestClass((n1 != null) ? n1.getClass() : null,
                           (n2 != null) ? n2.getClass() : null);
    }

    /**
     * Returns the widest of the given types. Classes {@code c1} and {@code c2} can be
     * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}, {@link BigInteger} or {@link BigDecimal} types.
     * <p>
     * If one of the given argument is null, then this method returns the non-null argument.
     * If both arguments are null, then this method returns {@code null}.
     * <p>
     * Example:
     *
     * {@preformat java
     *     widestClass(Short.class, Long.class);
     * }
     *
     * returns {@code Long.class}.
     *
     * @param  c1 The first number type, or {@code null}.
     * @param  c2 The second number type, or {@code null}.
     * @return The widest of the given types, or {@code null} if both {@code c1} and {@code c2} are null.
     * @throws IllegalArgumentException If one of the given types is unknown.
     *
     * @see #widestClass(Class, Class)
     * @see #finestClass(Number, Number)
     */
    public static Class<? extends Number> widestClass(final Class<? extends Number> c1,
                                                      final Class<? extends Number> c2)
            throws IllegalArgumentException
    {
        final Numbers m1 = MAPPING.get(c1);
        if (m1 == null && c1 != null) {
            throw unknownType(c1);
        }
        final Numbers m2 = MAPPING.get(c2);
        if (m2 == null && c2 != null) {
            throw unknownType(c2);
        }
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return (m1.ordinal >= m2.ordinal) ? c1 : c2;
    }

    /**
     * Returns the finest type of two numbers. Numbers {@code n1} and {@code n2} must be instance
     * of any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}
     * {@link Double}, {@link BigInteger} or {@link BigDecimal} types.
     *
     * @param  n1 The first number.
     * @param  n2 The second number.
     * @return The finest type of the given numbers.
     * @throws IllegalArgumentException If a number is not of a known type.
     *
     * @see #finestClass(Class, Class)
     * @see #widestClass(Class, Class)
     */
    public static Class<? extends Number> finestClass(final Number n1, final Number n2)
            throws IllegalArgumentException
    {
        return finestClass((n1 != null) ? n1.getClass() : null,
                           (n2 != null) ? n2.getClass() : null);
    }

    /**
     * Returns the finest of the given types. Classes {@code c1} and {@code c2} can be
     * {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float},
     * {@link Double}, {@link BigInteger} or {@link BigDecimal} types.
     * <p>
     * If one of the given argument is null, then this method returns the non-null argument.
     * If both arguments are null, then this method returns {@code null}.
     * <p>
     * Example:
     *
     * {@preformat java
     *     finestClass(Short.class, Long.class);
     * }
     *
     * returns {@code Short.class}.
     *
     * @param  c1 The first number type, or {@code null}.
     * @param  c2 The second number type, or {@code null}.
     * @return The finest of the given types, or {@code null} if both {@code c1} and {@code c2} are null.
     * @throws IllegalArgumentException If one of the given types is unknown.
     *
     * @see #finestClass(Number, Number)
     * @see #widestClass(Class, Class)
     */
    public static Class<? extends Number> finestClass(final Class<? extends Number> c1,
                                                      final Class<? extends Number> c2)
            throws IllegalArgumentException
    {
        final Numbers m1 = MAPPING.get(c1);
        if (m1 == null && c1 != null) {
            throw unknownType(c1);
        }
        final Numbers m2 = MAPPING.get(c2);
        if (m2 == null && c2 != null) {
            throw unknownType(c2);
        }
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return (m1.ordinal < m2.ordinal) ? c1 : c2;
    }

    /**
     * Returns the smallest class capable to hold the specified value. If the given value is
     * {@code null}, then this method returns {@code null}. Otherwise this method delegates
     * to {@link #finestClass(double)} or {@link #finestClass(long)} depending on the value type.
     *
     * @param  value The value to be wrapped in a finer (if possible) {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestNumber(Number)
     *
     * @since 3.06
     */
    public static Class<? extends Number> finestClass(final Number value) {
        if (value == null) {
            return null;
        }
        if (isPrimitiveInteger(value.getClass())) {
            return finestClass(value.longValue());
        } else {
            return finestClass(value.doubleValue());
        }
    }

    /**
     * Returns the smallest class capable to hold the specified value.
     * This is similar to {@link #finestClass(long)}, but extended to floating point values.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestNumber(double)
     */
    public static Class<? extends Number> finestClass(final double value) {
        final long lg = (long) value;
        if (value == lg) {
            return finestClass(lg);
        }
        final float fv = (float) value;
        if (Double.doubleToRawLongBits(value) == Double.doubleToRawLongBits(fv)) {
            return Float.class;
        }
        return Double.class;
    }

    /**
     * Returns the smallest class capable to hold the specified value.
     * This method makes the following choice:
     * <p>
     * <ul>
     *   <li>If the given value is between {@value java.lang.Byte#MIN_VALUE} and
     *       {@value java.lang.Byte#MAX_VALUE}, then this method returns {@code Byte.class};</li>
     *   <li>If the given value is between {@value java.lang.Short#MIN_VALUE} and
     *       {@value java.lang.Short#MAX_VALUE}, then this method returns {@code Short.class};</li>
     *   <li>If the given value is between {@value java.lang.Integer#MIN_VALUE} and
     *       {@value java.lang.Integer#MAX_VALUE}, then this method returns {@code Integer.class};</li>
     *   <li>Otherwise this method returns {@code Long.class};</li>
     * </ul>
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestNumber(long)
     *
     * @since 3.00
     */
    public static Class<? extends Number> finestClass(final long value) {
        // Tests MAX_VALUE before MIN_VALUE because it is more likely to fail.
        if (value <= Byte   .MAX_VALUE  &&  value >= Byte   .MIN_VALUE) return Byte.class;
        if (value <= Short  .MAX_VALUE  &&  value >= Short  .MIN_VALUE) return Short.class;
        if (value <= Integer.MAX_VALUE  &&  value >= Integer.MIN_VALUE) return Integer.class;
        return Long.class;
    }

    /**
     * Returns the number of the smallest class capable to hold the specified value. If the
     * given value is {@code null}, then this method returns {@code null}. Otherwise this
     * method delegates to {@link #finestNumber(double)} or {@link #finestNumber(long)}
     * depending on the value type.
     *
     * @param  value The value to be wrapped in a finer (if possible) {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestClass(Number)
     *
     * @since 3.06
     */
    public static Number finestNumber(final Number value) {
        if (value == null) {
            return null;
        }
        final Number candidate;
        if (isPrimitiveInteger(value.getClass())) {
            candidate = finestNumber(value.longValue());
        } else {
            candidate = finestNumber(value.doubleValue());
        }
        // Keep the existing instance if possible.
        return value.equals(candidate) ? value : candidate;
    }

    /**
     * Returns the number of the smallest class capable to hold the specified value.
     * This is similar to {@link #finestNumber(long)}, but extended to floating point values.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestClass(double)
     */
    public static Number finestNumber(final double value) {
        final long lg = (long) value;
        if (value == lg) {
            return finestNumber(lg);
        }
        final float fv = (float) value;
        if (Double.doubleToRawLongBits(value) == Double.doubleToRawLongBits(fv)) {
            return Float.valueOf(fv);
        }
        return Double.valueOf(value);
    }

    /**
     * Returns the number of the smallest type capable to hold the specified value.
     * This method makes the following choice:
     * <p>
     * <ul>
     *   <li>If the given value is between {@value java.lang.Byte#MIN_VALUE} and
     *       {@value java.lang.Byte#MAX_VALUE}, then it is wrapped in a {@link Byte} object.</li>
     *   <li>If the given value is between {@value java.lang.Short#MIN_VALUE} and
     *       {@value java.lang.Short#MAX_VALUE}, then it is wrapped in a {@link Short} object.</li>
     *   <li>If the given value is between {@value java.lang.Integer#MIN_VALUE} and
     *       {@value java.lang.Integer#MAX_VALUE}, then it is wrapped in an {@link Integer} object.</li>
     *   <li>Otherwise the value is wrapped in a {@link Long} object.</li>
     * </ul>
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The given value as a number of the finest type capable to hold it.
     *
     * @see #finestClass(long)
     *
     * @since 3.00
     */
    public static Number finestNumber(final long value) {
        // Tests MAX_VALUE before MIN_VALUE because it is more likely to fail.
        if (value <= Byte   .MAX_VALUE  &&  value >= Byte   .MIN_VALUE) return Byte   .valueOf((byte)  value);
        if (value <= Short  .MAX_VALUE  &&  value >= Short  .MIN_VALUE) return Short  .valueOf((short) value);
        if (value <= Integer.MAX_VALUE  &&  value >= Integer.MIN_VALUE) return Integer.valueOf((int)   value);
        return Long.valueOf(value);
    }

    /**
     * Returns the smallest number capable to hold the specified value.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
     * @throws NumberFormatException if the given value can not be parsed as a number.
     *
     * @see #finestNumber(Number)
     * @see #finestNumber(double)
     * @see #finestNumber(long)
     *
     * @since 3.00
     */
    public static Number finestNumber(String value) throws NumberFormatException {
        value = value.trim();
        final int length = value.length();
        for (int i=0; i<length; i++) {
            final char c = value.charAt(i);
            if (c == '.' || c == 'e' || c == 'E') {
                return finestNumber(Double.parseDouble(value));
            }
        }
        return finestNumber(Long.parseLong(value));
    }

    /**
     * Casts a number to the specified class. The class must by one of {@link Byte},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * This method makes the following choice:
     * <p>
     * <ul>
     *   <li>If the given type is {@code Double.class}, then this method returns
     *       <code>{@linkplain Double#valueOf(double) Double.valueOf}(n.doubleValue())</code>;</li>
     *   <li>If the given type is {@code Float.class}, then this method returns
     *       <code>{@linkplain Float#valueOf(float) Float.valueOf}(n.floatValue())</code>;</li>
     *   <li>And likewise for all remaining known types.</li>
     * </ul>
     *
     * {@note This method is intentionally restricted to primitive types. Other types
     *        like <code>BigDecimal</code> are not the purpose of this method. See the
     *        <code>ConverterRegistry</code> class for a more generic method.}
     *
     * @param <N> The class to cast to.
     * @param n The number to cast.
     * @param c The destination type.
     * @return The number casted to the given type.
     * @throws IllegalArgumentException If the given type is unknown.
     */
    @SuppressWarnings("unchecked")
    public static <N extends Number> N cast(final Number n, final Class<N> c)
            throws IllegalArgumentException
    {
        if (n == null || n.getClass() == c) {
            return (N) n;
        }
        if (c == Byte   .class) return (N) Byte   .valueOf(n.  byteValue());
        if (c == Short  .class) return (N) Short  .valueOf(n. shortValue());
        if (c == Integer.class) return (N) Integer.valueOf(n.   intValue());
        if (c == Long   .class) return (N) Long   .valueOf(n.  longValue());
        if (c == Float  .class) return (N) Float  .valueOf(n. floatValue());
        if (c == Double .class) return (N) Double .valueOf(n.doubleValue());
        throw unknownType(c);
    }

    /**
     * Converts the specified string into a value object. The value object can be an instance of
     * {@link Double}, {@link Float}, {@link Long}, {@link Integer}, {@link Short}, {@link Byte},
     * {@link Boolean}, {@link Character} or {@link String} according the specified type. This
     * method makes the following choice:
     * <p>
     * <ul>
     *   <li>If the given type is {@code Double.class}, then this method returns
     *       <code>{@linkplain Double#valueOf(String) Double.valueOf}(value)</code>;</li>
     *   <li>If the given type is {@code Float.class}, then this method returns
     *       <code>{@linkplain Float#valueOf(String) Float.valueOf}(value)</code>;</li>
     *   <li>And likewise for all remaining known types.</li>
     * </ul>
     *
     * {@note This method is intentionally restricted to primitive types, with the addition of
     *        <code>String</code> which can be though as an identity operation. Other types
     *        like <code>BigDecimal</code> are not the purpose of this method. See the
     *        <code>ConverterRegistry</code> class for a more generic method.}
     *
     * @param  <T> The requested type.
     * @param  type The requested type.
     * @param  value the value to parse.
     * @return The value object, or {@code null} if {@code value} was null.
     * @throws IllegalArgumentException if {@code type} is not a recognized type.
     * @throws NumberFormatException if {@code type} is a subclass of {@link Number} and the
     *         string value is not parseable as a number of the specified type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T valueOf(final Class<T> type, final String value)
            throws IllegalArgumentException, NumberFormatException
    {
        if (value == null) {
            return null;
        }
        if (type == Double .class) return (T) Double .valueOf(value);
        if (type == Float  .class) return (T) Float  .valueOf(value);
        if (type == Long   .class) return (T) Long   .valueOf(value);
        if (type == Integer.class) return (T) Integer.valueOf(value);
        if (type == Short  .class) return (T) Short  .valueOf(value);
        if (type == Byte   .class) return (T) Byte   .valueOf(value);
        if (type == Boolean.class) return (T) Boolean.valueOf(value);
        if (type == Character.class) {
            /*
             * If the string is empty, returns 0 which means "end of string" in C/C++
             * and NULL in Unicode standard. If non-empty, take only the first char.
             * This is somewhat consistent with Boolean.valueOf(...) which is quite
             * lenient about the parsing as well, and throwing a NumberFormatException
             * for those would not be appropriate.
             */
            return (T) Character.valueOf(value.isEmpty() ? 0 : value.charAt(0));
        }
        if (type == String.class) {
            return (T) value;
        }
        throw unknownType(type);
    }

    /**
     * Returns a {@code NaN}, zero, empty or {@code null} value of the given type. This method
     * tries to return the closest value that can be interpreted as "<cite>none</cite>", which
     * is usually not the same than "<cite>zero</cite>". More specifically:
     * <p>
     * <ul>
     *   <li>If the given type is a floating point <strong>primitive</strong> type ({@code float}
     *       or {@code double}), then this method returns {@link Float#NaN} or {@link Double#NaN}
     *       depending on the given type.</li>
     *
     *   <li>If the given type is an integer <strong>primitive</strong> type or the character type
     *       ({@code long}, {@code int}, {@code short}, {@code byte} or {@code char}), then this
     *       method returns the zero value of the given type.</li>
     *
     *   <li>If the given type is the {@code boolean} <strong>primitive</strong> type, then this
     *       method returns {@link Boolean#FALSE}.</li>
     *
     *   <li>If the given type is an array or a collection, then this method returns an empty
     *       array or collection. The given type is honored on a <cite>best effort</cite> basis.</li>
     *
     *   <li>For all other cases, including the wrapper classes of primitive types, this method
     *       returns {@code null}.</li>
     * </ul>
     * <p>
     * Despite being defined in the {@code Numbers} class, the scope of this method has been
     * extended to array and collection types because those types can also be seen as mathematical
     * objects.
     *
     * @param  <T> The compile-time type of the requested object.
     * @param  type The type of the object for which to get a nil value.
     * @return An object of the given type which represents a nil value, or {@code null}.
     *
     * @since 3.18
     */
    @SuppressWarnings("unchecked")
    public static <T> T valueOfNil(final Class<T> type) {
        if (type.isPrimitive()) {
            return (T) MAPPING.get(type).nullValue;
        }
        if (type == Map      .class) return (T)  Collections.EMPTY_MAP;
        if (type == List     .class) return (T)  Collections.EMPTY_LIST;
        if (type == Queue    .class) return (T) XCollections.emptyQueue();
        if (type == SortedSet.class) return (T) XCollections.emptySortedSet();
        if (type != null && type != Object.class) {
            if (type.isAssignableFrom(Set.class)) {
                return (T) Collections.EMPTY_SET;
            }
            final Class<?> element = type.getComponentType();
            if (element != null) {
                return (T) Array.newInstance(element, 0);
            }
        }
        return null;
    }

    /**
     * Returns one of {@link #DOUBLE}, {@link #FLOAT}, {@link #LONG}, {@link #INTEGER},
     * {@link #SHORT}, {@link #BYTE}, {@link #CHARACTER}, {@link #BOOLEAN} or {@link #OTHER}
     * constants for the given type. This is a commodity for usage in {@code switch} statements.
     *
     * @param type A type (usually either a primitive type or its wrapper).
     * @return The constant for the given type, or {@link #OTHER} if unknown.
     */
    public static byte getEnumConstant(final Class<?> type) {
        final Numbers mapping = MAPPING.get(type);
        if (mapping != null) {
            // Filter out the non-public enum for BigDecimal and BigInteger.
            if (mapping.size >= 0) {
                return mapping.ordinal;
            }
        }
        return OTHER;
    }

    /**
     * Returns an exception for an unknown type.
     */
    private static IllegalArgumentException unknownType(final Class<?> type) {
        return new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, type));
    }
}
