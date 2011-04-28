/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * Miscellaneous static methods working on {@link Class} objects. The methods provided in this
 * class can be grouped in two categories:
 * <p>
 * <ul>
 *   <li>Methods specialized for handling the {@link Number} type and its subclasses.</li>
 *   <li>Generic methods for handling any kind of classes or interfaces.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
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
    // Note: This class assumes that DOUBLE is the greatest public constant.

    /**
     * Mapping between a primitive type and its wrapper, if any.
     */
    private static final Map<Class<?>,Classes> MAPPING = new HashMap<Class<?>,Classes>(16);
    static {
        new Classes(BigDecimal.class, true, false, (byte) (DOUBLE+2)); // Undocumented enum.
        new Classes(BigInteger.class, false, true, (byte) (DOUBLE+1)); // Undocumented enum.
        new Classes(Double   .TYPE, Double   .class, true,  false, (byte) Double   .SIZE, DOUBLE,    'D');
        new Classes(Float    .TYPE, Float    .class, true,  false, (byte) Float    .SIZE, FLOAT,     'F');
        new Classes(Long     .TYPE, Long     .class, false, true,  (byte) Long     .SIZE, LONG,      'J');
        new Classes(Integer  .TYPE, Integer  .class, false, true,  (byte) Integer  .SIZE, INTEGER,   'I');
        new Classes(Short    .TYPE, Short    .class, false, true,  (byte) Short    .SIZE, SHORT,     'S');
        new Classes(Byte     .TYPE, Byte     .class, false, true,  (byte) Byte     .SIZE, BYTE,      'B');
        new Classes(Character.TYPE, Character.class, false, false, (byte) Character.SIZE, CHARACTER, 'C');
        new Classes(Boolean  .TYPE, Boolean  .class, false, false, (byte) 1,              BOOLEAN,   'Z');
        new Classes(Void     .TYPE, Void     .class, false, false, (byte) 0,              OTHER,     'V');
    }

    /** The primitive type.                     */ private final Class<?> primitive;
    /** The wrapper for the primitive type.     */ private final Class<?> wrapper;
    /** {@code true} for floating point number. */ private final boolean  isFloat;
    /** {@code true} for integer number.        */ private final boolean  isInteger;
    /** The size in bytes.                      */ private final byte     size;
    /** Constant to be used in switch statement.*/ private final byte     ordinal;
    /** The internal form of the primitive name.*/ private final char     internal;

    /**
     * Creates an entry for a type which is not a primitive type.
     */
    private Classes(final Class<?> type, final boolean isFloat, final boolean isInteger, final byte ordinal) {
        primitive = wrapper = type;
        this.isFloat   = isFloat;
        this.isInteger = isInteger;
        this.size      = -1;
        this.ordinal   = ordinal;
        this.internal  = 'L'; // Defined by Java, and tested elsewhere in this class.
        if (MAPPING.put(type, this) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Creates a mapping between a primitive type and its wrapper.
     */
    private Classes(final Class<?> primitive, final Class<?> wrapper,
                    final boolean  isFloat,   final boolean  isInteger,
                    final byte     size,      final byte     ordinal,
                    final char     internal)
    {
        this.primitive = primitive;
        this.wrapper   = wrapper;
        this.isFloat   = isFloat;
        this.isInteger = isInteger;
        this.size      = size;
        this.ordinal   = ordinal;
        this.internal  = internal;
        if (MAPPING.put(primitive, this) != null || MAPPING.put(wrapper, this) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Changes the array dimension by the given amount. The given class can be a primitive type,
     * a Java object, or an array of the above. If the given {@code dimension} is positive, then
     * the array dimension will be increased by that amount. For example a change of dimension 1
     * will change a {@code int} class into {@code int[]}, and a {@code String[]} class into
     * {@code String[][]}. A change of dimension 2 is like applying a change of dimension 1 two
     * times.
     * <p>
     * The change of dimension can also be negative. For example a change of dimension -1 will
     * change a {@code String[]} class into a {@code String}. More specifically:
     * <p>
     * <ul>
     *   <li>If the given {@code element} is null, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given {@code dimension} change is 0, then the given {@code element}
     *       is returned unchanged.</li>
     *   <li>Otherwise if the given {@code dimension} change is negative, then
     *       {@link Class#getComponentType()} is invoked {@code abs(dimension)} times.
     *       The result is a {@code null} value if {@code abs(dimension)} is greater
     *       than the array dimension.</li>
     *   <li>Otherwise if {@code element} is {@link Void#TYPE}, then this method returns
     *       {@code Void.TYPE} since arrays of {@code void} don't exist.</li>
     *   <li>Otherwise this method returns a class that represents an array of the given
     *       class augmented by the given amount of dimensions.</li>
     * </ul>
     *
     * @param  element The type of elements in the array.
     * @param  dimension The change of dimension, as a negative or positive number.
     * @return The type of an array of the given element type augmented by the given
     *         number of dimensions (which may be negative), or {@code null}.
     *
     * @category type
     * @since 3.03
     */
    public static Class<?> changeArrayDimension(Class<?> element, int dimension) {
        if (dimension != 0 && element != null) {
            if (dimension < 0) {
                do element = element.getComponentType();
                while (element!=null && ++dimension != 0);
            } else if (!element.equals(Void.TYPE)) {
                final StringBuilder buffer = new StringBuilder();
                do buffer.insert(0, '[');
                while (--dimension != 0);
                if (element.isPrimitive()) {
                    buffer.append(MAPPING.get(element).internal);
                } else if (element.isArray()) {
                    buffer.append(element.getName());
                } else {
                    buffer.append('L').append(element.getName()).append(';');
                }
                final String name = buffer.toString();
                try {
                    element = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new TypeNotPresentException(name, e);
                    // Should never happen because we are creating an array of an existing class.
                }
            }
        }
        return element;
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
     *
     * @category type
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
     *         do not operate on an object of a parameterized type.
     *
     * @category type
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
     *
     * @category type
     */
    private static Class<?> getActualTypeArgument(Type type) {
        if (type instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) type).getActualTypeArguments();
            while (p != null && p.length == 1) {
                type = p[0];
                if (type instanceof WildcardType) {
                    p = ((WildcardType) type).getUpperBounds();
                    continue;
                }
                /*
                 * At this point we are not going to continue the loop anymore.
                 * Check if we have an array, then check the (component) class.
                 */
                int dimension = 0;
                while (type instanceof GenericArrayType) {
                    type = ((GenericArrayType) type).getGenericComponentType();
                    dimension++;
                }
                if (type instanceof Class<?>) {
                    return changeArrayDimension((Class<?>) type, dimension);
                }
                break; // Unknown type.
            }
        }
        return null;
    }

    /**
     * Casts the {@code type} class to represent a subclass of the class represented by the
     * {@code sub} argument. Checks that the cast is valid, and returns {@code null} if it
     * is not.
     * <p>
     * This method performs the same work than
     * <code>type.{@linkplain Class#asSubclass(Class) asSubclass}(sub)</code>,
     * except that {@code null} is returned instead than throwing an exception
     * if the cast is not valid or if any of the argument is {@code null}.
     *
     * @param  <U>  The compile-time bounds of the {@code sub} argument.
     * @param  type The class to cast to a sub-class, or {@code null}.
     * @param  sub  The subclass to cast to, or {@code null}.
     * @return The {@code type} argument casted to a subclass of the {@code sub} argument,
     *         or {@code null} if this cast can not be performed.
     *
     * @see Class#asSubclass(Class)
     *
     * @since 3.09
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <U> Class<? extends U> asSubclassOrNull(final Class<?> type, final Class<U> sub) {
        // Design note: We are required to return null if 'sub' is null (not to return 'type'
        // unchanged), because if we returned 'type', we would have an unsafe cast if this
        // method is invoked indirectly from a parameterized method.
        return (type != null && sub != null && sub.isAssignableFrom(type)) ? (Class) type : null;
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
     *
     * @category type
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(final T object) {
        return (object != null) ? (Class<? extends T>) object.getClass() : null;
    }

    /**
     * Returns the classes of all objects in the given collection. If the given collection
     * contains some null elements, then the returned set will contains a null element as well.
     * The returned set is modifiable and can be freely updated by the caller.
     * <p>
     * Note that interfaces are not included in the returned set.
     *
     * @param  <T> The base type of elements in the given collection.
     * @param  objects The collection of objects.
     * @return The set of classes of all objects in the given collection.
     *
     * @category type
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
     * Returns the set of every interfaces implemented by the given class or interface. This is
     * similar to {@link Class#getInterfaces()} except that this method searches recursively in
     * the super-interfaces. For example if the given type is {@link java.util.ArrayList}, then
     * the returned set will contains {@link java.util.List} (which is implemented directly)
     * together with its parent interfaces {@link Collection} and {@link Iterable}.
     *
     * @param  type The class or interface for which to get all implemented interfaces.
     * @return All implemented interfaces (not including the given {@code type} if it was an
     *         interface), or an empty set if none. Callers can freely modify the returned set.
     *
     * @category type
     * @since 3.01
     */
    public static Set<Class<?>> getAllInterfaces(final Class<?> type) {
        final Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        getAllInterfaces(type, interfaces);
        return interfaces;
    }

    /**
     * Adds to the given collection every interfaces implemented by the given class or interface.
     *
     * @category type
     */
    private static void getAllInterfaces(final Class<?> type, final Set<Class<?>> interfaces) {
        for (final Class<?> i : type.getInterfaces()) {
            if (interfaces.add(i)) {
                getAllInterfaces(i, interfaces);
            }
        }
    }

    /**
     * Returns the interface implemented by the given class and assignable to the given base
     * interface. Example:
     * <p>
     * <ul>
     *   <li>{@code getInterface(ArrayList.class, Collection.class)} returns {@code List.class}.</li>
     * </ul>
     *
     * @param  <T>  The type of the {@code baseInterface} class argument.
     * @param  type A class for which the implemented interface is desired.
     * @param  baseInterface The base type of the interface to search.
     * @return {@code null} if no interface matching the given criterion is found.
     *
     * @since 3.17
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getInterface(Class<?> type, final Class<T> baseInterface) {
        while (type != null) {
            for (final Class<?> i : type.getInterfaces()) {
                if (baseInterface.isAssignableFrom(i)) {
                    return (Class<T>) i;
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    /**
     * Returns the most specific class implemented by the objects in the given collection.
     * If there is more than one specialized class, returns their {@linkplain #findCommonClass
     * most specific common super class}.
     * <p>
     * This method searches for classes only, not interfaces.
     *
     * @param  objects A collection of objects. May contains duplicated values and null values.
     * @return The most specialized class, or {@code null} if the given collection does not contain
     *         at least one non-null element.
     *
     * @category type
     * @since 3.01 (derived from 2.5)
     */
    public static Class<?> findSpecializedClass(final Collection<?> objects) {
        final Set<Class<?>> types = getClasses(objects);
        types.remove(null);
        /*
         * Removes every classes in the types collection which are assignable from an other
         * class from the same collection. As a result, the collection should contains only
         * leaf classes.
         */
        for (final Iterator<Class<?>> it=types.iterator(); it.hasNext();) {
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
     * This method is not public in order to make sure that it contains only classes, not
     * interfaces, since our implementation is not designed for multi-inheritances.
     *
     * @param  types The collection where to search for a common parent.
     * @return The common parent, or {@code null} if the given collection is empty.
     *
     * @category type
     */
    private static Class<?> common(final Set<Class<?>> types) {
        final Iterator<Class<?>> it = types.iterator();
        if (!it.hasNext()) {
            return null;
        }
        Class<?> type = it.next();
        while (it.hasNext()) {
            type = findCommonClass(type, it.next());
        }
        return type;
    }

    /**
     * Returns the most specific class which {@linkplain Class#isAssignableFrom is assignable from}
     * the type of all given objects. If no element in the given collection has a type assignable
     * from the type of all other elements, then this method searches for a common
     * {@linkplain Class#getSuperclass super class}.
     * <p>
     * This method searches for classes only, not interfaces.
     *
     * @param  objects A collection of objects. May contains duplicated values and null values.
     * @return The most specific class common to all supplied objects, or {@code null} if the
     *         given collection does not contain at least one non-null element.
     *
     * @category type
     * @since 3.01 (derived from 2.5)
     */
    public static Class<?> findCommonClass(final Collection<?> objects) {
        final Set<Class<?>> types = getClasses(objects);
        types.remove(null);
        return common(types);
    }

    /**
     * Returns the most specific class which {@linkplain Class#isAssignableFrom is assignable from}
     * the given classes or a parent of those classes. This method returns either {@code c1},
     * {@code c2} or a common parent of {@code c1} and {@code c2}.
     * <p>
     * This method considers classes only, not the interfaces.
     *
     * @param  c1 The first class, or {@code null}.
     * @param  c2 The second class, or {@code null}.
     * @return The most specific class common to the supplied classes, or {@code null}
     *         if both {@code c1} and {@code c2} are null.
     *
     * @category type
     * @since 3.01 (derived from 3.00)
     */
    public static Class<?> findCommonClass(Class<?> c1, Class<?> c2) {
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        do {
            if (c1.isAssignableFrom(c2)) {
                return c1;
            }
            if (c2.isAssignableFrom(c1)) {
                return c2;
            }
            c1 = c1.getSuperclass();
            c2 = c2.getSuperclass();
        } while (c1 != null && c2 != null);
        return Object.class;
    }

    /**
     * Returns the interfaces which are implemented by the two given classes. The returned set
     * does not include the parent interfaces. For example if the two given objects implement the
     * {@link Collection} interface, then the returned set will contains the {@code Collection}
     * type but not the {@link Iterable} type, since it is implied by the collection type.
     *
     * @param  c1 The first class.
     * @param  c2 The second class.
     * @return The interfaces common to both classes, or an empty set if none.
     *         Callers can freely modify the returned set.
     *
     * @category type
     * @since 3.01
     */
    public static Set<Class<?>> findCommonInterfaces(final Class<?> c1, final Class<?> c2) {
        final Set<Class<?>> interfaces = getAllInterfaces(c1);
        final Set<Class<?>> buffer     = getAllInterfaces(c2); // To be recycled.
        interfaces.retainAll(buffer);
        for (Iterator<Class<?>> it=interfaces.iterator(); it.hasNext();) {
            final Class<?> candidate = it.next();
            buffer.clear();
            getAllInterfaces(candidate, buffer);
            if (interfaces.removeAll(buffer)) {
                it = interfaces.iterator();
            }
        }
        return interfaces;
    }

    /**
     * Returns {@code true} if the two specified objects implements exactly the same set of
     * interfaces. Only interfaces assignable to {@code base} are compared. Declaration order
     * doesn't matter. For example in ISO 19111, different interfaces exist for different coordinate
     * system geometries ({@code CartesianCS}, {@code PolarCS}, etc.). We can check if two
     * CS implementations has the same geometry with the following code:
     *
     * {@preformat java
     *     if (implementSameInterfaces(cs1, cs2, CoordinateSystem.class)) {
     *         // The two Coordinate System are of the same kind.
     *     }
     * }
     *
     * @param <T>     A common parent for both objects.
     * @param object1 The first object to check for interfaces.
     * @param object2 The second object to check for interfaces.
     * @param base    The parent of all interfaces to check.
     * @return        {@code true} if both objects implement the same set of interfaces,
     *                considering only sub-interfaces of {@code base}.
     *
     * @category type
     * @since 3.01 (derived from 2.5)
     */
    public static <T> boolean implementSameInterfaces(
            final Class<? extends T> object1,
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
     *
     * @see #isInteger(Class)
     *
     * @category number
     */
    public static boolean isFloat(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
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
     *
     * @category number
     */
    public static boolean isInteger(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
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
     *
     * @category number
     */
    private static boolean isPrimitiveInteger(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        return (mapping != null) && mapping.isInteger && (mapping.internal != 'L');
    }

    /**
     * Returns the number of bits used by primitive of the specified type.
     * The given type must be a primitive type or its wrapper class.
     *
     * @param  type The primitive type (may be {@code null}).
     * @return The number of bits, or 0 if {@code type} is null.
     * @throws IllegalArgumentException if the given type is unknown.
     *
     * @category number
     */
    public static int primitiveBitCount(final Class<?> type) throws IllegalArgumentException {
        final Classes mapping = MAPPING.get(type);
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
     *
     * @category number
     */
    public static Class<?> primitiveToWrapper(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
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
     *
     * @category number
     */
    public static Class<?> wrapperToPrimitive(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
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
     *
     * @category number
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
     *
     * @category number
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
     *
     * @category number
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
     *
     * @category number
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
     * Returns the smallest class capable to hold the specified value. If the given value is
     * {@code null}, then this method returns {@code null}. Otherwise this method delegates
     * to {@link #finestClass(double)} or {@link #finestClass(long)} depending on the value type.
     *
     * @param  value The value to be wrapped in a finer (if possible) {@link Number}.
     * @return The finest type capable to hold the given value.
     *
     * @see #finestNumber(Number)
     *
     * @category number
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
     *
     * @category number
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
     * @category number
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
     * @category number
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
     *
     * @category number
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
     * @category number
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
     * @category number
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
     *
     * @category number
     */
    @SuppressWarnings("unchecked")
    public static <N extends Number> N cast(final Number n, final Class<N> c)
            throws IllegalArgumentException
    {
        if (n == null || n.getClass().equals(c)) {
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
     *        <code>String</code> which can be though as an identity operation.. Other types
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
     *
     * @category number
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
            return (T) Character.valueOf(value.isEmpty() ? 0 : value.charAt(0));
        }
        if (String.class.equals(type)) {
            return (T) value;
        }
        throw unknownType(type);
    }

    /**
     * Returns one of {@link #DOUBLE}, {@link #FLOAT}, {@link #LONG}, {@link #INTEGER},
     * {@link #SHORT}, {@link #BYTE}, {@link #CHARACTER}, {@link #BOOLEAN} or {@link #OTHER}
     * constants for the given type. This is a commodity for usage in {@code switch} statements.
     *
     * @param type A type (usually either a primitive type or its wrapper).
     * @return The constant for the given type, or {@link #OTHER} if unknown.
     *
     * @category number
     */
    public static byte getEnumConstant(final Class<?> type) {
        final Classes mapping = MAPPING.get(type);
        if (mapping != null) {
            // Filter out the non-public enum for BigDecimal and BigInteger.
            if (mapping.size >= 0) {
                return mapping.ordinal;
            }
        }
        return OTHER;
    }

    /**
     * Returns a short class name for the specified class. This method will
     * omit the package name.  For example, it will return {@code "String"} instead
     * of {@code "java.lang.String"} for a {@link String} object. It will also name
     * array according Java language usage,  for example {@code "double[]"} instead
     * of {@code "[D"}.
     * <p>
     * This method is similar to the {@link Class#getSimpleName()} method, except that
     * if the given class is an inner class, then the returned value is prefixed with
     * the outer class name. For example this method returns {@code "Point2D.Double"}
     * instead of {@code "Double"}.
     *
     * @param  classe The object class (may be {@code null}).
     * @return A short class name for the specified object, or {@code "<*>"} if the
     *         given class was null.
     *
     * @see #getShortClassName(Object)
     * @see Class#getSimpleName()
     *
     * @category type
     */
    public static String getShortName(Class<?> classe) {
        if (classe == null) {
            return "<*>";
        }
        Class<?> enclosing = classe.getEnclosingClass();
        while (classe.isAnonymousClass()) {
            classe = classe.getSuperclass();
        }
        String name = classe.getSimpleName();
        if (enclosing != null) {
            name = getShortName(enclosing) + '.' + name;
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
     *
     * @see #getShortName(Class)
     *
     * @category type
     */
    public static String getShortClassName(final Object object) {
        return getShortName(getClass(object));
    }

    /**
     * Returns {@code true} if the given type is assignable to one of the given allowed types.
     * More specifically, if at least one {@code allowedTypes[i]} element exists for which
     * <code>allowedTypes[i].{@linkplain Class#isAssignableFrom(Class) isAssignableFrom}(type)</code>
     * returns {@code true}, then this method returns {@code true}.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If {@code type} is null, then this method returns {@code false}.</li>
     *   <li>If {@code allowedTypes} is null, then this method returns {@code true}.
     *       This is to be interpreted as "no restriction on the allowed types".</li>
     *   <li>Any null element in the {@code allowedTypes} array are silently ignored.</li>
     * </ul>
     *
     * @param  type The type to be tested, or {@code null}.
     * @param  allowedTypes The allowed types.
     * @return {@code true} if the given type is assignable to one of the allowed types.
     *
     * @since 3.12
     */
    public static boolean isAssignableTo(final Class<?> type, final Class<?>... allowedTypes) {
        if (type != null) {
            if (allowedTypes == null) {
                return true;
            }
            for (final Class<?> candidate : allowedTypes) {
                if (candidate != null && candidate.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns an exception for an unknown type.
     */
    private static IllegalArgumentException unknownType(final Class<?> type) {
        return new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, type));
    }
}
