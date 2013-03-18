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

import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;


/**
 * Miscellaneous static methods working on {@link Class} objects.
 * This class defines helper methods for working with reflection.
 * Some functionalities are:
 * <p>
 * <ul>
 *   <li>Add or remove dimension to an array type
 *       ({@link #changeArrayDimension(Class, int) changeArrayDimension})</li>
 *   <li>Find the common parent of two or more classes
 *       ({@link #findCommonClass(Class, Class) findCommonClass},
 *       ({@link #findCommonInterfaces(Class, Class) findCommonInterfaces})</li>
 *   <li>Getting the bounds of a parameterized field or method
 *       ({@link #boundOfParameterizedProperty(Method) boundOfParameterizedProperty})</li>
 *   <li>Getting a short class name ({@link #getShortName(Class) getShortName},
 *       {@link #getShortClassName(Object) getShortClassName})</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.5
 * @module
 *
 * @deprecated Moved to {@link org.apache.sis.util.Classes}.
 */
@Deprecated
public final class Classes extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Classes() {
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
     * @since 3.03
     *
     * @deprecated Moved to {@link org.apache.sis.util.Classes#changeArrayDimension(java.lang.Class, int)}.
     */
    @Deprecated
    public static Class<?> changeArrayDimension(Class<?> element, int dimension) {
        return org.apache.sis.util.Classes.changeArrayDimension(element, dimension);
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
     * @deprecated Moved to {@link org.apache.sis.util.Classes#boundOfParameterizedProperty(Field)}.
     */
    @Deprecated
    public static Class<?> boundOfParameterizedAttribute(final Field field) {
        return org.apache.sis.util.Classes.boundOfParameterizedProperty(field);
    }

    /**
     * If the given method is a getter or a setter for a parameterized attribute, returns the
     * upper bounds of the parameterized type. Otherwise returns {@code null}. This method
     * provides the same semantic than {@link #boundOfParameterizedProperty(Field)}, but
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
     * @deprecated Moved to {@link org.apache.sis.util.Classes#boundOfParameterizedProperty(Method)}.
     */
    @Deprecated
    public static Class<?> boundOfParameterizedAttribute(final Method method) {
        return org.apache.sis.util.Classes.boundOfParameterizedProperty(method);
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
     * @deprecated No replacement.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
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
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(final T object) {
        return org.apache.sis.util.Classes.getClass(object);
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
     * @since 3.00
     *
     * @deprecated No replacement.
     */
    public static <T> Set<Class<? extends T>> getClasses(final Collection<? extends T> objects) {
        final Set<Class<? extends T>> types = new LinkedHashSet<>();
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
     * @since 3.01
     */
    public static Set<Class<?>> getAllInterfaces(Class<?> type) {
        return new LinkedHashSet<Class<?>>(Arrays.asList(org.apache.sis.util.Classes.getAllInterfaces(type)));
    }

    /**
     * Returns the interfaces implemented by the given class and assignable to the given base
     * interface, or {@code null} if none. If more than one interface extends the given base,
     * then the most specialized interfaces are returned. For example if the given class
     * implements both the {@link Set} and {@link Collection} interfaces, then the returned
     * array contains only the {@code Set} interface.
     *
     * {@section Example}
     * {@code getLeafInterfaces(ArrayList.class, Collection.class)} returns an array of length 1
     * containing {@code List.class}.
     *
     * @param  <T>  The type of the {@code baseInterface} class argument.
     * @param  type A class for which the implemented interface is desired.
     * @param  baseInterface The base type of the interface to search.
     * @return The leaf interfaces matching the given criterion, or {@code null} if none.
     *         If non-null, than the array is guaranteed to contain at least one element.
     *
     * @since 3.18
     */
    /*
     * Warning: contract differs from SIS: the later returns an empty array instead of null.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T>[] getLeafInterfaces(Class<?> type, final Class<T> baseInterface) {
        int count = 0;
        Class<?>[] types = null;
        while (type != null) {
            final Class<?>[] candidates = type.getInterfaces();
next:       for (final Class<?> candidate : candidates) {
                if (baseInterface == null || baseInterface.isAssignableFrom(candidate)) {
                    /*
                     * At this point, we have an interface to be included in the returned array.
                     * If a more specialized interface existed before 'candidate', forget the
                     * candidate.
                     */
                    for (int i=0; i<count; i++) {
                        final Class<?> old = types[i];
                        if (candidate.isAssignableFrom(old)) {
                            continue next; // A more specialized interface already exists.
                        }
                        if (old.isAssignableFrom(candidate)) {
                            types[i] = candidate; // This interface specializes a previous interface.
                            continue next;
                        }
                    }
                    if (types == null) {
                        types = candidates;
                    }
                    if (count >= types.length) {
                        types = Arrays.copyOf(types, types.length + candidates.length);
                    }
                    types[count++] = candidate;
                }
            }
            type = type.getSuperclass();
        }
        return (Class[]) ArraysExt.resize(types, count);
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
     * @since 3.01 (derived from 2.5)
     */
    public static Class<?> findSpecializedClass(final Collection<?> objects) {
        return org.apache.sis.util.Classes.findSpecializedClass(objects);
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
     * @since 3.01 (derived from 2.5)
     */
    public static Class<?> findCommonClass(final Collection<?> objects) {
        return org.apache.sis.util.Classes.findCommonClass(objects);
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
     * @since 3.01 (derived from 3.00)
     */
    public static Class<?> findCommonClass(Class<?> c1, Class<?> c2) {
        return org.apache.sis.util.Classes.findCommonClass(c1, c2);
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
     * @since 3.01
     */
    public static Set<Class<?>> findCommonInterfaces(final Class<?> c1, final Class<?> c2) {
        return org.apache.sis.util.Classes.findCommonInterfaces(c1, c2);
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
     * @param object1 The first object to check for interfaces.
     * @param object2 The second object to check for interfaces.
     * @param base    The parent of all interfaces to check.
     * @return        {@code true} if both objects implement the same set of interfaces,
     *                considering only sub-interfaces of {@code base}.
     *
     * @since 3.01 (derived from 2.5)
     */
    public static boolean implementSameInterfaces(final Class<?> object1, final Class<?> object2, final Class<?> base) {
        return org.apache.sis.util.Classes.implementSameInterfaces(object1, object2, base);
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
     */
    public static String getShortName(Class<?> classe) {
        return org.apache.sis.util.Classes.getShortName(classe);
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
     */
    public static String getShortClassName(final Object object) {
        return org.apache.sis.util.Classes.getShortClassName(object);
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
        return org.apache.sis.util.Classes.isAssignableToAny(type, allowedTypes);
    }

    /**
     * Returns {@code true} if the given method may possibly be the getter method for an attribute.
     * This method implements the algorithm used by Geotk in order to identify getter methods in
     * {@linkplain org.opengis.metadata metadata} interfaces. We do not rely on naming convention
     * (method names starting with "{@code get}" or "{@code is}" prefixes) because not every methods
     * follow such convention (e.g. {@link org.opengis.metadata.quality.ConformanceResult#pass()}).
     *
     * <p>The current implementation returns {@code true} if the given method meets all the
     * following conditions. Note that a {@code true} value is not a guaranteed that the given
     * method is really a getter. The caller is encouraged to perform additional checks if
     * possible.</p>
     *
     * <ul>
     *   <li>The method does no expect any argument.</li>
     *   <li>The method returns a value (anything except {@code void}).</li>
     *   <li>The method name is not {@link Object#clone() clone}, {@link Object#getClass() getClass},
     *       {@link Object#hashCode() hashCode}, {@link Object#toString() toString} or
     *       {@link org.opengis.referencing.IdentifiedObject#toWKT() toWKT}.</li>
     *   <li>The method is not {@linkplain Method#isSynthetic() synthetic}.</li>
     *   <li>The method is not {@linkplain Deprecated deprecated}.</li>
     * </ul>
     *
     * <p>Those conditions may be updated in any future Geotk version.</p>
     *
     * @param  method The method to inspect.
     * @return {@code true} if the given method may possibly be a non-deprecated getter method.
     *
     * @since 3.20
     */
    public static boolean isPossibleGetter(final Method method) {
        return org.apache.sis.util.Classes.isPossibleGetter(method);
    }
}
