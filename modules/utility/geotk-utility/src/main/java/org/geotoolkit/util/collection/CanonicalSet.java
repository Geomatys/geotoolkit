/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;


/**
 * A canonical set of objects, used to optimize memory use.
 * The operation of this set is similar in spirit to the {@link String#intern} method.
 * The following example shows a convenient way to use {@code CanonicalSet} as an
 * internal pool of immutable objects.
 *
 * {@preformat java
 *     public Foo create(String definition) {
 *         Foo created = new Foo(definition);
 *         return canonicalSet.unique(created);
 *     }
 * }
 *
 * The {@code CanonicalSet} has a {@link #get} method that is not part of the {@link java.util.Set}
 * interface. This {@code get} method retrieves an entry from this set that is equal to
 * the supplied object. The {@link #unique} method combines a {@code get} followed by a
 * {@code put} operation if the specified object was not in the set.
 * <p>
 * The set of objects is held by weak references as explained in {@link WeakHashSet}.
 * The {@code CanonicalSet} class is thread-safe.
 *
 * @param <E> The type of elements in the set.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public class CanonicalSet<E> extends WeakHashSet<E> {
    /**
     * Constructs a {@code CanonicalSet} for elements of the specified type.
     *
     * @param type The type of elements in the set.
     *
     * @since 2.5
     */
    protected CanonicalSet(final Class<E> type) {
        super(type);
    }

    /**
     * Constructs a {@code CanonicalSet} for elements of the specified type.
     *
     * @param <E>  The type of elements in the set.
     * @param type The type of elements in the set.
     * @return An initially empty set for elements of the given type.
     *
     * @since 2.5
     */
    public static <E> CanonicalSet<E> newInstance(final Class<E> type) {
        return new CanonicalSet<E>(type);
    }

    /**
     * Returns an object equal to the specified object, if present. If
     * this set doesn't contains any object equal to {@code object},
     * then this method returns {@code null}.
     *
     * @param <T> The type of the element to get.
     * @param object The element to get.
     * @return An element equal to the given one if already presents in the set,
     *         or {@code null} otherwise.
     *
     * @see #unique(Object)
     */
    public synchronized <T extends E> T get(final T object) {
        return intern(object, GET);
    }

    /**
     * Returns an object equal to {@code object} if such an object already exist in this
     * {@code CanonicalSet}. Otherwise, adds {@code object} to this {@code CanonicalSet}.
     * This method is equivalents to the following code:
     *
     * {@preformat java
     *     if (object != null) {
     *         Object current = get(object);
     *         if (current != null) {
     *             return current;
     *         } else {
     *             add(object);
     *         }
     *     }
     *     return object;
     * }
     *
     * @param <T> The type of the element to get.
     * @param object The element to get or to add in the set if not already presents.
     * @return An element equal to the given one if already presents in the set,
     *         or the given {@code object} otherwise.
     */
    public synchronized <T extends E> T unique(final T object) {
        return intern(object, INTERN);
    }

    /**
     * Iteratively call {@link #unique(Object)} for an array of objects.
     * This method is equivalents to the following code:
     *
     * {@preformat java
     *     for (int i=0; i<objects.length; i++) {
     *         objects[i] = unique(objects[i]);
     *     }
     * }
     *
     * @param objects
     *          On input, the objects to add to this set if not already present. On output,
     *          elements that are {@linkplain Object#equals equal}, but where every reference
     *          to an instance already presents in this set has been replaced by a reference
     *          to the existing instance.
     */
    public synchronized void uniques(final E[] objects) {
        for (int i=0; i<objects.length; i++) {
            objects[i] = intern(objects[i], INTERN);
        }
    }
}
