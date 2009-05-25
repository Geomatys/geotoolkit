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
package org.geotoolkit.util.converter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * A set of miscellaneous methods working on {@link Class} objects. Some methods are specialized
 * on primitive types, sometime more specifically on numeric types and their {@link Number}
 * wrappers.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
@Static
public final class Classes {
    /**
     * Constants to be used in {@code switch} statements.
     */
    public static final byte
            DOUBLE=8, FLOAT=7, LONG=6, INTEGER=5, SHORT=4, BYTE=3, CHARACTER=2, BOOLEAN=1, OTHER=0;

    /**
     * Mapping between a primitive type and its wrapper, if any.
     */
    private static final Map<Class<?>,Classes> MAPPING = new HashMap<Class<?>,Classes>(16);
    static {
        new Classes(Double   .TYPE, Double   .class, true,  false, (byte) Double   .SIZE, DOUBLE   );
        new Classes(Float    .TYPE, Float    .class, true,  false, (byte) Float    .SIZE, FLOAT    );
        new Classes(Long     .TYPE, Long     .class, false, true,  (byte) Long     .SIZE, LONG     );
        new Classes(Integer  .TYPE, Integer  .class, false, true,  (byte) Integer  .SIZE, INTEGER  );
        new Classes(Short    .TYPE, Short    .class, false, true,  (byte) Short    .SIZE, SHORT    );
        new Classes(Byte     .TYPE, Byte     .class, false, true,  (byte) Byte     .SIZE, BYTE     );
        new Classes(Character.TYPE, Character.class, false, false, (byte) Character.SIZE, CHARACTER);
        new Classes(Boolean  .TYPE, Boolean  .class, false, false, (byte) 1,              BOOLEAN  );
        new Classes(Void     .TYPE, Void     .class, false, false, (byte) 0,              OTHER    );
    }

    /** The primitive type.                     */ private final Class<?> primitive;
    /** The wrapper for the primitive type.     */ private final Class<?> wrapper;
    /** {@code true} for floating point number. */ private final boolean  isFloat;
    /** {@code true} for integer number.        */ private final boolean  isInteger;
    /** The size in bytes.                      */ private final byte     size;
    /** Constant to be used in switch statement.*/ private final byte     ordinal;

    /**
     * Creates a mapping between a primitive type and its wrapper.
     */
    private Classes(final Class<?> primitive, final Class<?> wrapper,
                    final boolean  isFloat,   final boolean  isInteger,
                    final byte     size,      final byte     ordinal)
    {
        this.primitive = primitive;
        this.wrapper   = wrapper;
        this.isFloat   = isFloat;
        this.isInteger = isInteger;
        this.size      = size;
        this.ordinal   = ordinal;
        if (MAPPING.put(primitive, this) != null || MAPPING.put(wrapper, this) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Returns the upper bounds of the parameterized type of the given attribute.
     * If the attribute does not have a parameterized type, returns {@code null}.
     * <p>
     * This method is typically used for fetching the type of elements in a collection.
     * We do not provide a method working from a {@link Class} instance because of the
     * way parameterized types are implemented in Java (by erasure).
     * <p>
     * <b>Examples:</b> When invoking this method for a field of the type below:
     * <ul>
     *   <li>{@code Set<Number>} returns {@code Number.class}.</li>
     *
     *   <li>{@code Set<? extends Number>} returns {@code Number.class} as well, since that
     *       collection can not (in theory) contain instances of super-classes; {@code Number}
     *       is the <cite>upper bound</cite>.</li>
     *
     *   <li>{@code Set<? super Number>} returns {@code Object.class}, because that collection
     *       is allowed to contain such elements.</li>
     *
     *   <li>{@code Set} returns {@code null} because that collection is un-parameterized.</li>
     * </ul>
     *
     * @param  field The field for which to obtain the parameterized type.
     * @return The upper bound of parameterized type, or {@code null} if the given field
     *         is not of a parameterized type.
     */
    public static Class<?> boundOfParameterizedAttribute(final Field field) {
        return getActualTypeArgument(field.getGenericType());
    }

    /**
     * If the given method is a getter or a setter for a parameterized attribute, returns the
     * upper bounds of the parameterized type. Otherwise returns {@code null}. This method
     * provides the same semantic than {@link #boundOfParameterizedAttribute(Field)}, but
     * works on a getter or setter method rather then the field. See the javadoc of above
     * method for more details.
     * <p>
     * This method is typically used for fetching the type of elements in a collection.
     * We do not provide a method working from a {@link Class} instance because of the
     * way parameterized types are implemented in Java (by erasure).
     *
     * @param  method The getter or setter method for which to obtain the parameterized type.
     * @return The upper bound of parameterized type, or {@code null} if the given method
     *         do not opperate on an object of a parameterized type.
     */
    public static Class<?> boundOfParameterizedAttribute(final Method method) {
        Class<?> c = getActualTypeArgument(method.getGenericReturnType());
        if (c == null) {
            final Type[] parameters = method.getGenericParameterTypes();
            if (parameters != null && parameters.length == 1) {
                c = getActualTypeArgument(parameters[0]);
            }
        }
        return c;
    }

    /**
     * Delegates to {@link ParameterizedType#getActualTypeArguments} and returns the result as a
     * {@link Class}, provided that every objects are of the expected classes and the result was
     * an array of length 1 (so there is no ambiguity). Otherwise returns {@code null}.
     */
    private static Class<?> getActualTypeArgument(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) type).getActualTypeArguments();
            while (p != null && p.length == 1) {
                type = p[0];
                if (type instanceof Class) {
                    return (Class<?>) type;
                } else if (type instanceof WildcardType) {
                    p = ((WildcardType) type).getUpperBounds();
                } else {
                    break; // Unknown type.
                }
            }
        }
        return null;
    }

    /**
     * Returns the class of the specified object, or {@code null} if {@code object} is null.
     * This method is also useful for fetching the class of an object known only by its bound
     * type. As of Java 6, the usual pattern:
     *
     * {@preformat java
     *     Number n = 0;
     *     Class<? extends Number> c = n.getClass();
     * }
     *
     * doesn't seem to work if {@link Number} is replaced by a parameterized type {@code T}.
     *
     * @param  <T> The type of the given object.
     * @param  object The object for which to get the class, or {@code null}.
     * @return The class of the given object, or {@code null} if the given object was null.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(final T object) {
        return (object != null) ? (Class<? extends T>) object.getClass() : null;
    }

    /**
     * Returns all classes implemented by the given collection of objects. If the given collection
     * contains some null elements, then the returned set will contains a null element as well.
     * The returned set is modifiable and can be freely updated by the caller.
     * <p>
     * Note that interfaces are not included in the returned set.
     *
     * @param  <T> The base type of elements in the given collection.
     * @param  objects The collection of objects.
     * @return The set of classes of all objects in the given collection.
     *
     * @since 3.00
     */
    public static <T> Set<Class<? extends T>> getClasses(final Collection<? extends T> objects) {
        final Set<Class<? extends T>> types = new LinkedHashSet<Class<? extends T>>();
        for (final T object : objects) {
            types.add(getClass(object));
        }
        return types;
    }

    /**
     * Returns the most specific class implemented by the objects in the given collection.
     * If there is more than one specialized class, returns their {@linkplain #commonClass
     * most specific common super class}.
     * <p>
     * This method searches for classes only, not interfaces.
     *
     * @param  <T> The base type of elements in the given collection.
     * @param  objects A collection of objects. May contains duplicated values and null values.
     * @return The most specialized class, or {@code null} if the given collection does not contain
     *         at least one non-null element.
     */
    public static <T> Class<? extends T> specializedClass(final Collection<? extends T> objects) {
        final Set<Class<? extends T>> types = getClasses(objects);
        types.remove(null);
        /*
         * Removes every classes in the types collection which are assignable from an other
         * class from the same collection. As a result, the collection should contains only
         * leaf classes.
         */
        for (final Iterator<Class<? extends T>> it=types.iterator(); it.hasNext();) {
            final Class<?> candidate = it.next();
            for (final Class<?> type : types) {
                if (candidate != type && candidate.isAssignableFrom(type)) {
                    it.remove();
                    break;
                }
            }
        }
        return common(types);
    }

    /**
     * Returns the most specific class which is a common parent of all the specified classes.
     * This method is not public in other to make sure that it contains only classes, not
     * interfaces, since our implementation is not designed for multi-inheritances.
     *
     * @param  <T> The base type of elements in the given collection.
     * @param  types The collection where to search for a common parent.
     * @return The common parent, or {@code null} if the given collection is empty.
     */
    private static <T> Class<? extends T> common(final Set<Class<? extends T>> types) {
        final Iterator<Class<? extends T>> it = types.iterator();
        if (!it.hasNext()) {
            return null;
        }
        Class<? extends T> type = it.next();
        while (it.hasNext()) {
            type = commonClass(type, it.next());
        }
        return type;
    }

    /**
     * Returns the most specific class which is a common parent of
     * all specified objects. If no element in the given collection is
     * {@linkplain Class#isAssignableFrom assignable from} all other elements, then
     * this method searches for a common {@linkplain Class#getSuperclass super class}.
     * <p>
     * This method searches for classes only, not interfaces.
     *
     * @param  <T> The base type of elements in the given collection.
     * @param  objects A collection of objects. May contains duplicated values and null values.
     * @return The most specific class common to all supplied objects, or {@code null} if the
     *         given collection does not contain at least one non-null element.
     */
    public static <T> Class<? extends T> commonClass(final Collection<? extends T> objects) {
        final Set<Class<? extends T>> types = getClasses(objects);
        types.remove(null);
        return common(types);
    }

    /**
     * Returns the most specific class which is a common parent of the given classes.
     * This method considers classes only, not the interfaces.
     *
     * @param  <T> The base type of both classes.
     * @param  c1 The first class, or {@code null}.
     * @param  c2 The second class, or {@code null}.
     * @return The most specific class common to the supplied classes, or {@code null}
     *         if both {@code c1} and {@code c2} are null.
     *
     * @since 3.00
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> commonClass(Class<? extends T> c1, Class<? extends T> c2) {
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        do {
            if (c1.isAssignableFrom(c2)) {
                return c1;
            }
            if (c2.isAssignableFrom(c1)) {
                return c2;
            }
            /*
             * We perform an unchecked cast because in theory T is the common super
             * class. However we don't know it at run time (because generic types are
             * implemented by erasure), which is why we are doing all this stuff. If
             * there is no logical error in our algorithm, the cast should be correct.
             */
            c1 = (Class) c1.getSuperclass();
            c2 = (Class) c2.getSuperclass();
        } while (c1 != null && c2 != null);
        return (Class) Object.class;
    }

    /**
     * Returns {@code true} if the two specified objects implements exactly the same set of
     * interfaces. Only interfaces assignable to {@code base} are compared. Declaration order
     * doesn't matter. For example in ISO 19111, different interfaces exist for different coordinate
     * system geometries ({@code CartesianCS}, {@code PolarCS}, etc.). We can check if two
     * CS implementations has the same geometry with the following code:
     *
     * {@preformat java
     *     if (sameInterfaces(cs1, cs2, CoordinateSystem.class))
     * }
     *
     * @param <T>     A common parent for both objects.
     * @param object1 The first object to check for interfaces.
     * @param object2 The second object to check for interfaces.
     * @param base    The parent of all interfaces to check.
     * @return        {@code true} if both objects implement the same set of interfaces,
     *                considering only sub-interfaces of {@code base}.
     */
    public static <T> boolean sameInterfaces(final Class<? extends T> object1,
                                             final Class<? extends T> object2,
                                             final Class<T> base)
    {
        if (object1 == object2) {
            return true;
        }
        if (object1==null || object2==null) {
            return false;
        }
        final Class<?>[] c1 = object1.getInterfaces();
        final Class<?>[] c2 = object2.getInterfaces();
        /*
         * Trim all interfaces that are not assignable to 'base' in the 'c2' array.
         * Doing this once will avoid to redo the same test many time in the inner
         * loops j=[0..n].
         */
        int n = 0;
        for (int i=0; i<c2.length; i++) {
            final Class<?> c = c2[i];
            if (base.isAssignableFrom(c)) {
                c2[n++] = c;
            }
        }
        /*
         * For each interface assignable to 'base' in the 'c1' array, check if
         * this interface exists also in the 'c2' array. Order doesn't matter.
         */
compare:for (int i=0; i<c1.length; i++) {
            final Class<?> c = c1[i];
            if (base.isAssignableFrom(c)) {
                for (int j=0; j<n; j++) {
                    if (c.equals(c2[j])) {
                        System.arraycopy(c2, j+1, c2, j, --n-j);
                        continue compare;
                    }
                }
                return false; // Interface not found in 'c2'.
            }
        }
        return n == 0; // If n>0, at least one interface was not found in 'c1'.
    }

    /**
     * Returns {@code true} if the given {@code type} is a floating point type.
     *
     * @param  type The type to test (may be {@code null}).
     * @return {@code true} if {@code type} is the primitive or wrapper class of
     *         {@link Float} or {@link Double}.
     */
    public static boolean isFloat(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isFloat;
    }

    /**
     * Returns {@code true} if the given {@code type} is an integer type.
     *
     * @param  type The type to test (may be {@code null}).
     * @return {@code true} if {@code type} is the primitive of wrapper class of
     *         {@link Long}, {@link Integer}, {@link Short} or {@link Byte}.
     */
    public static boolean isInteger(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isInteger;
    }

    /**
     * Returns the number of bits used by primitive of the specified type.
     * The given type must be a primitive type or its wrapper class.
     *
     * @param  type The primitive type (may be {@code null}).
     * @return The number of bits, or 0 if {@code type} if null.
     * @throws IllegalArgumentException if the given type is unknown.
     */
    public static int primitiveBitCount(final Class<?> type) throws IllegalArgumentException {
        final Classes mapping = MAPPING.get(type);
        if (mapping != null) {
            return mapping.size;
        }
        if (type == null) {
            return 0;
        }
        throw unknownType(type);
    }

    /**
     * Changes a primitive class to its wrapper (e.g. {@code int} to {@link Integer}).
     * If the specified class is not a primitive type, then it is returned unchanged.
     *
     * @param  type The primitive type (may be {@code null}).
     * @return The type as a wrapper.
     */
    public static Class<?> primitiveToWrapper(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) ? mapping.wrapper : type;
    }

    /**
     * Changes a wrapper class to its primitive (e.g. {@link Integer} to {@code int}).
     * If the specified class is not a wrapper type, then it is returned unchanged.
     *
     * @param  type The wrapper type (may be {@code null}).
     * @return The type as a primitive.
     */
    public static Class<?> wrapperToPrimitive(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) ? mapping.primitive : type;
    }

    /**
     * Returns the widest type of two numbers. Numbers {@code n1} and {@code n2} must be instance
     * of any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float} or
     * {@link Double} types. At most one of the arguments can be null.
     *
     * @param  n1 The first number.
     * @param  n2 The second number.
     * @return The widest type of the given numbers.
     * @throws IllegalArgumentException If a number is not of a known type.
     */
    public static Class<? extends Number> widestClass(final Number n1, final Number n2)
            throws IllegalArgumentException
    {
        return widestClass((n1 != null) ? n1.getClass() : null,
                           (n2 != null) ? n2.getClass() : null);
    }

    /**
     * Returns the widest of the given types. Classes {@code c1} and {@code c2}
     * must be of any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long},
     * {@link Float} or {@link Double} types. At most one of the arguments can be null.
     *
     * @param  c1 The first number type.
     * @param  c2 The second number type.
     * @return The widest of the given types.
     * @throws IllegalArgumentException If one of the given type is unknown.
     */
    public static Class<? extends Number> widestClass(final Class<? extends Number> c1,
                                                      final Class<? extends Number> c2)
            throws IllegalArgumentException
    {
        final Classes m1 = MAPPING.get(c1);
        if (m1 == null && c1 != null) {
            throw unknownType(c1);
        }
        final Classes m2 = MAPPING.get(c2);
        if (m2 == null && c2 != null) {
            throw unknownType(c2);
        }
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return (m1.ordinal >= m2.ordinal) ? c1 : c2;
    }

    /**
     * Returns the finest type of two numbers. Numbers {@code n1} and {@code n2} must be instance
     * of any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float} or
     * {@link Double} types. At most one of the arguments can be null.
     *
     * @param  n1 The first number.
     * @param  n2 The second number.
     * @return The finest type of the given numbers.
     * @throws IllegalArgumentException If a number is not of a known type.
     */
    public static Class<? extends Number> finestClass(final Number n1, final Number n2)
            throws IllegalArgumentException
    {
        return finestClass((n1 != null) ? n1.getClass() : null,
                           (n2 != null) ? n2.getClass() : null);
    }

    /**
     * Returns the finest of the given types. Classes {@code c1} and {@code c2}
     * must be of any of {@link Byte}, {@link Short}, {@link Integer}, {@link Long},
     * {@link Float} or {@link Double} types. At most one of the arguments can be null.
     *
     * @param  c1 The first number type.
     * @param  c2 The second number type.
     * @return The widest of the given types.
     * @throws IllegalArgumentException If one of the given type is unknown.
     */
    public static Class<? extends Number> finestClass(final Class<? extends Number> c1,
                                                      final Class<? extends Number> c2)
            throws IllegalArgumentException
    {
        final Classes m1 = MAPPING.get(c1);
        if (m1 == null && c1 != null) {
            throw unknownType(c1);
        }
        final Classes m2 = MAPPING.get(c2);
        if (m2 == null && c2 != null) {
            throw unknownType(c2);
        }
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return (m1.ordinal < m2.ordinal) ? c1 : c2;
    }

    /**
     * Returns the smallest class capable to hold the specified value.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
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
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
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
     * Returns the smallest number capable to hold the specified value.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
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
     * Returns the smallest number capable to hold the specified value.
     *
     * @param  value The value to be wrapped in a {@link Number}.
     * @return The finest type capable to hold the given value.
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
        if (n==null || n.getClass().equals(c)) {
            return (N) n;
        }
        if (Byte   .class.equals(c)) return (N) Byte   .valueOf(n.  byteValue());
        if (Short  .class.equals(c)) return (N) Short  .valueOf(n. shortValue());
        if (Integer.class.equals(c)) return (N) Integer.valueOf(n.   intValue());
        if (Long   .class.equals(c)) return (N) Long   .valueOf(n.  longValue());
        if (Float  .class.equals(c)) return (N) Float  .valueOf(n. floatValue());
        if (Double .class.equals(c)) return (N) Double .valueOf(n.doubleValue());
        throw unknownType(c);
    }

    /**
     * Converts the specified string into a value object. The value object can be an instance of
     * {@link Double}, {@link Float}, {@link Long}, {@link Integer}, {@link Short}, {@link Byte},
     * {@link Boolean}, {@link Character} or {@link String} according the specified type. This
     * method is intentionnaly restricted to primitive types, with the addition of {@code String}
     * which can be though as an identity operation. Other types like {@link java.io.File} are
     * not the purpose of this method.
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
        if (Double .class.equals(type)) return (T) Double .valueOf(value);
        if (Float  .class.equals(type)) return (T) Float  .valueOf(value);
        if (Long   .class.equals(type)) return (T) Long   .valueOf(value);
        if (Integer.class.equals(type)) return (T) Integer.valueOf(value);
        if (Short  .class.equals(type)) return (T) Short  .valueOf(value);
        if (Byte   .class.equals(type)) return (T) Byte   .valueOf(value);
        if (Boolean.class.equals(type)) return (T) Boolean.valueOf(value);
        if (Character.class.equals(type)) {
            /*
             * If the string is empty, returns 0 which means "end of string" in C/C++
             * and NULL in Unicode standard. If non-empty, take only the first char.
             * This is somewhat consistent with Boolean.valueOf(...) which is quite
             * lenient about the parsing as well, and throwing a NumberFormatException
             * for those would not be appropriate.
             */
            return (T) Character.valueOf(value.length() != 0 ? value.charAt(0) : 0);
        }
        if (String.class.equals(type)) {
            return (T) value;
        }
        throw unknownType(type);
    }

    /**
     * Returns one of {@link #DOUBLE}, {@link #FLOAT}, {@link #LONG}, {@link #INTEGER},
     * {@link #SHORT}, {@link #BYTE}, {@link #CHARACTER}, {@link #BOOLEAN} or {@link #OTHER}
     * constants for the given type. This is a commodity for usage in {@code switch} statememnts.
     *
     * @param type A type (usually either a primitive type or its wrapper).
     * @return The constant for the given type, or {@link #OTHER} if unknow.
     */
    public static byte getEnumConstant(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) ? mapping.ordinal : OTHER;
    }

    /**
     * Returns a short class name for the specified class. This method will
     * omit the package name.  For example, it will return {@code "String"} instead
     * of {@code "java.lang.String"} for a {@link String} object. It will also name
     * array according Java language usage,  for example {@code "double[]"} instead
     * of {@code "[D"}.
     *
     * @param  classe The object class (may be {@code null}).
     * @return A short class name for the specified object.
     */
    public static String getShortName(Class<?> classe) {
        if (classe == null) {
            return "<*>";
        }
        String name = classe.getSimpleName();
        Class<?> enclosing = classe.getEnclosingClass();
        if (enclosing != null) {
            final StringBuilder buffer = new StringBuilder();
            do {
                buffer.insert(0, '.').insert(0, enclosing.getSimpleName());
            } while ((enclosing = enclosing.getEnclosingClass()) != null);
            name = buffer.append(name).toString();
        }
        return name;
    }

    /**
     * Returns a short class name for the specified object. This method will
     * omit the package name. For example, it will return {@code "String"}
     * instead of {@code "java.lang.String"} for a {@link String} object.
     *
     * @param  object The object (may be {@code null}).
     * @return A short class name for the specified object.
     */
    public static String getShortClassName(final Object object) {
        return getShortName(getClass(object));
    }

    /**
     * Returns an exception for an unkown type.
     */
    private static IllegalArgumentException unknownType(final Class<?> type) {
        return new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, type));
    }
}
