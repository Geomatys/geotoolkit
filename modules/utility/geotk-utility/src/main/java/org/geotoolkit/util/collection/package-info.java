/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

/**
 * Addition to the collection framework. Most classes in this package implement interfaces
 * from the <cite>Java Collection Framework</cite> defined in the {@link java.util} package.
 * <ul>
 *   <li><p>
 *     {@link org.geotoolkit.util.collection.WeakHashSet} provides a way to ensure that
 *     a factory returns unique instances for all values that are equal in the sense of
 *     {@link java.lang.Object#equals Object.equals(Object)}. The values that were created
 *     in previous factory operations are retained by {@linkplain java.lang.ref.WeakReference
 *     weak references} for reuse.
 *   </p></li><li><p>
 *     {@link org.geotoolkit.util.collection.Cache} and
 *     {@link org.geotoolkit.util.collection.WeakValueHashMap} are {@link java.util.Map java.util.Map}
 *     implementations that may be used for some caching or pseudo-caching functionalities. The
 *     {@link org.geotoolkit.util.collection.Cache} implementation is the most full-featured one
 *     and supports concurrency, while the other implementations are more lightweight, sometime
 *     thread-safe but without concurrency support.
 *   </p></li><li><p>
 *     {@link org.geotoolkit.util.collection.CheckedCollection},
 *     {@link org.geotoolkit.util.collection.CheckedArrayList},
 *     {@link org.geotoolkit.util.collection.CheckedHashSet} and
 *     {@link org.geotoolkit.util.collection.CheckedHashMap} can be used for combining <em>runtime</em>
 *     type safety with thread-safety (without concurrency). They are similar in functionalities to
 *     the wrappers provided by the standard {@link java.util.Collections} methods, except that they
 *     combine both functionalities in a single class (so reducing the amount of indirection), provide
 *     a hook for making the collections read-only and allow the caller to specify the synchronization
 *     lock of his choice.
 *   </p></li><li><p>
 *     {@link org.geotoolkit.util.collection.DerivedMap} and
 *     {@link org.geotoolkit.util.collection.DerivedSet} are wrapper collections in which the
 *     keys or the values are derived on-the-fly from the content of an other collection.
 *   </p></li><li><p>
 *     {@link org.geotoolkit.util.collection.IntegerList} and
 *   </p></li><li><p>
 *     {@link org.geotoolkit.util.collection.DisjointSet},
 *     {@link org.geotoolkit.util.collection.KeySortedList} and
 *     {@link org.geotoolkit.util.collection.FrequencySortedSet} provides specialized ways to
 *     organize their elements.
 *   </p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 1.0
 * @module
 */
package org.geotoolkit.util.collection;
