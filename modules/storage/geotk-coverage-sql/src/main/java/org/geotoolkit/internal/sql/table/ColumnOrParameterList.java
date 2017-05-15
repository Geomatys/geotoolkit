/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.AbstractSequentialList;
import java.util.ListIterator;


/**
 * A list wrapping an array of {@link ColumnOrParameter}. This list filters the elements
 * in order to return only the ones of the {@link QueryType} given to the constructor.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class ColumnOrParameterList<E extends ColumnOrParameter> extends AbstractSequentialList<E> {
    /**
     * The query type for which this list is created.
     */
    private final QueryType type;

    /**
     * {@link Query#columns} or {@link Query#parameters} at the time this list has been created.
     */
    private final E[] elements;

    /**
     * Creates a list for the given query type.
     */
    ColumnOrParameterList(final QueryType type, final E[] elements) {
        this.type = type;
        this.elements = elements;
    }

    /**
     * Returns {@code true} if this collection contains no elements.
     */
    @Override
    public boolean isEmpty() {
        for (final E c : elements) {
            if (c.indexOf(type) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the number of elements in this collection.
     */
    @Override
    public int size() {
        int count = 0;
        for (final E c : elements) {
            if (c.indexOf(type) != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a list iterator over the elements in this list.
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        return new ColumnOrParameterIterator<>(type, elements, index);
    }
}
