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
 *
 * @deprecated The {@link WeakHashSet} parent class is suffisient.
 */
@Deprecated
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
}
