/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.util.Arrays;
import net.jcip.annotations.ThreadSafe;


/**
 * A set of objects hold by weak references. An entry in a {@code WeakHashSet} will automatically
 * be removed when it is no longer in ordinary use. More precisely, the presence of an entry will
 * not prevent the entry from being discarded by the garbage collector, that is, made finalizable,
 * finalized, and then reclaimed. When an entry has been discarded it is effectively removed from
 * the set, so this class behaves somewhat differently than other {@link java.util.Set} implementations.
 * <p>
 * If the elements stored in this set are arrays like {@code int[]}, {@code float[]} or
 * {@code Object[]}, then the hash code computations and the comparisons are performed using
 * the static {@code hashCode(a)} and {@code equals(a1, a2)} methods defined in the {@link Arrays}
 * class.
 *
 * {@section Optimizing memory use in factory implementations}
 * The {@code WeakHashSet} class has a {@link #get} method that is not part of the {@link java.util.Set}
 * interface. This {@code get} method retrieves an entry from this set that is equals to the supplied
 * object. The {@link #unique} method combines a {@code get} followed by a {@code add} operation if
 * the specified object was not in the set. This is similar in spirit to the {@link String#intern}
 * method. The following example shows a convenient way to use {@code WeakHashSet} as an internal
 * pool of immutable objects.
 *
 * {@preformat java
 *     private final WeakHashSet<Foo> pool = WeakHashSet.newInstance(Foo.class);
 *
 *     public Foo create(String definition) {
 *         Foo created = new Foo(definition);
 *         return pool.unique(created);
 *     }
 * }
 *
 * Thus, {@code WeakHashSet} can be used inside a factory to prevent creating duplicate
 * immutable objects.
 *
 * @param <E> The type of elements in the set.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.05
 *
 * @see java.util.WeakHashMap
 *
 * @since 1.0
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.collection.WeakHashSet}.
 */
@ThreadSafe
@Deprecated
public class WeakHashSet<E> extends org.apache.sis.util.collection.WeakHashSet<E> implements CheckedCollection<E> {
    /**
     * Constructs a {@code WeakHashSet} for elements of the specified type.
     *
     * @param <E>  The type of elements in the set.
     * @param type The type of elements in the set.
     * @return An initially empty set for elements of the given type.
     *
     * @since 2.5
     */
    public static <E> WeakHashSet<E> newInstance(final Class<E> type) {
        return new WeakHashSet<>(type);
    }

    /**
     * Constructs a {@code WeakHashSet} for elements of the specified type.
     *
     * @param type The type of the element to be included in this set.
     *
     * @since 2.5
     */
    protected WeakHashSet(final Class<E> type) {
        super(type);
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
     *          elements that are {@linkplain Object#equals(Object) equal}, but where every
     *          reference to an instance already presents in this set has been replaced by
     *          a reference to the existing instance.
     */
    public synchronized void uniques(final E[] objects) {
        for (int i=0; i<objects.length; i++) {
            objects[i] = unique(objects[i]);
        }
    }
}
