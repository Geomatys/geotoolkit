/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collections;
import net.jcip.annotations.ThreadSafe;
import org.geotoolkit.util.Cloneable;


/**
 * A {@linkplain Collections#checkedSet checked} and {@linkplain Collections#synchronizedSet
 * synchronized} {@link java.util.Set}. Type checks are performed at run-time in addition of
 * compile-time checks. The synchronization lock can be modified at runtime by overriding the
 * {@link #getLock} method.
 * <p>
 * This class is similar to using the wrappers provided in {@link Collections}, minus the cost
 * of indirection levels and with the addition of overrideable methods.
 *
 * @param <E> The type of elements in the set.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Collections#checkedSet
 * @see Collections#synchronizedSet
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.collection.CheckedHashSet}.
 */
@ThreadSafe
@Deprecated
public class CheckedHashSet<E> extends org.apache.sis.util.collection.CheckedHashSet<E> implements CheckedCollection<E>, Cloneable {
    /**
     * Serial version UID for compatibility with different versions.
     */
    private static final long serialVersionUID = -9014541457174735097L;

    /**
     * Constructs a set of the specified type.
     *
     * @param type The element type (should not be null).
     */
    public CheckedHashSet(final Class<E> type) {
        super(type);
    }

    /**
     * Constructs a set of the specified type and initial capacity.
     *
     * @param type The element type (should not be null).
     * @param capacity The initial capacity.
     *
     * @since 2.4
     */
    public CheckedHashSet(final Class<E> type, final int capacity) {
        super(type, capacity);
    }

    /**
     * Checks the type of the specified object. The default implementation ensure
     * that the object is assignable to the type specified at construction time.
     *
     * @param  element the object to check, or {@code null}.
     * @throws IllegalArgumentException if the specified element is not of the expected type.
     */
    @Override
    protected final void ensureValid(final E element) throws IllegalArgumentException {
        ensureValidType(element);
    }

    /**
     * Checks the type of the specified object. The default implementation ensure
     * that the object is assignable to the type specified at construction time.
     *
     * @param  element the object to check, or {@code null}.
     * @throws IllegalArgumentException if the specified element is not of the expected type.
     *
     * @deprecated Renamed {@link #ensureValid(Object)}.
     */
    @Deprecated
    protected void ensureValidType(final E element) throws IllegalArgumentException {
        super.ensureValid(element);
    }

    /**
     * Returns a shallow copy of this set.
     *
     * @return A shallow copy of this set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public CheckedHashSet<E> clone() {
        return (CheckedHashSet<E>) super.clone();
    }
}
