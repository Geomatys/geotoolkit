/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.util.collection;

import java.util.*;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.collection.Containers;


/**
 * Static methods working on {@link Collection} objects.
 * This is an extension to the Java {@link Collections} and {@link Containers} utility classes.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.22
 *
 * @since 3.10 (derived from 3.00)
 * @module
 */
public final class XCollections extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private XCollections() {
    }

    /**
     * Clears the given collection, if non-null. If the collection is null, then this method does
     * nothing. This is a convenience method when a null collection is a synonymous of empty.
     *
     * @param collection The collection to clear, or {@code null}.
     *
     * @since 3.18
     */
    public static void clear(final Collection<?> collection) {
        if (collection != null) {
            collection.clear();
        }
    }

    /**
     * Clears the given map, if non-null. If the map is null, then this method does nothing.
     * This is a convenience method when a null map is a synonymous of empty.
     *
     * @param map The map to clear, or {@code null}.
     *
     * @since 3.18
     */
    public static void clear(final Map<?,?> map) {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * Adds the given element to the given collection only if the element is non-null.
     * If any of the given argument is null, then this method does nothing.
     *
     * @param  <E>        The type of elements in the collection.
     * @param  collection The collection in which to add elements, or {@code null}.
     * @param  element    The element to add in the collection, or {@code null}.
     * @return {@code true} if the given element has been added, or {@code false} otherwise.
     *
     * @since 3.20
     */
    public static <E> boolean addIfNonNull(final Collection<E> collection, final E element) {
        return (collection != null && element != null) && collection.add(element);
    }
}
