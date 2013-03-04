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


/**
 * A container that ensures that all elements are assignable to a given base type.
 * Checked containers are usually {@linkplain CheckedCollection checked collections},
 * but are not limited to collections.
 *
 * @param <E> The base type of elements in the container.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 *
 * @deprecated Replaced by {@link org.apache.sis.util.collection.CheckedContainer}.
 */
@Deprecated
public interface CheckedContainer<E> {
    /**
     * Returns the base type of all elements in this container.
     *
     * @return The element type.
     */
    Class<? extends E> getElementType();
}
