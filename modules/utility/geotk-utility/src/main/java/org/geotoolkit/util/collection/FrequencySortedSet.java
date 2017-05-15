/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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


/**
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.util.collection.FrequencySortedSet}.
 */
@Deprecated
public class FrequencySortedSet<E> extends org.apache.sis.util.collection.FrequencySortedSet<E> {
    /**
     * Creates an initially empty set with less frequent elements first.
     */
    public FrequencySortedSet() {
    }

    /**
     * Creates an initially empty set with the default initial capacity.
     *
     * @param reversed {@code true} if the elements should be sorted in reverse order
     *        (most frequent element first, less frequent last).
     */
    public FrequencySortedSet(final boolean reversed) {
        super(reversed);
    }

    /**
     * Creates an initially empty set with the specified initial capacity.
     *
     * @param initialCapacity The initial capacity.
     * @param reversed {@code true} if the elements should be sorted in reverse order
     *        (most frequent element first, less frequent last).
     */
    public FrequencySortedSet(final int initialCapacity, final boolean reversed) {
        super(initialCapacity, reversed);
    }
}
