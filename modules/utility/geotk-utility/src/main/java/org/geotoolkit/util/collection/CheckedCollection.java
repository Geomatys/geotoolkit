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

import java.util.Collection;


/**
 * Collection that ensures that all elements are assignable to a given base type.
 * The base {@linkplain #getElementType type} is usually specified at collection
 * construction time.
 *
 * @param <E> The base type of elements in the collection.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by {@link org.apache.sis.util.collection.CheckedContainer}.
 */
@Deprecated
public interface CheckedCollection<E> extends Collection<E>, CheckedContainer<E> {
    /**
     * Returns the base type of all elements in this collection.
     */
    @Override
    Class<? extends E> getElementType();
}
