/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Arrays;
import java.util.AbstractList;
import java.util.RandomAccess;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.CheckedCollection;


/**
 * A list of {@link MetadataProxy} instances. This list is <cite>live</cite>: changes to the
 * backing {@link javax.imageio.metadata.IIOMetadata} are immediately reflected in this list.
 *
 * @param <T> The type of elements in this list.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 * @module
 */
final class MetadataProxyList<T> extends AbstractList<T> implements CheckedCollection<T>, RandomAccess {
    /**
     * The type of elements in this list.
     */
    private final Class<T> elementType;

    /**
     * The metadata accessor to be given to every proxy to be created.
     */
    private final MetadataAccessor accessor;

    /**
     * The proxies which have been created up to date. Elements in this array
     * may be null if the proxy at a given index has not yet been created.
     */
    private T[] elements;

    /**
     * Creates a new list.
     */
    static <T> MetadataProxyList<T> create(final Class<T> elementType, final MetadataAccessor accessor) {
        return new MetadataProxyList<T>(elementType, accessor);
    }

    /**
     * Creates a new list.
     */
    private MetadataProxyList(final Class<T> elementType, final MetadataAccessor accessor) {
        this.elementType = elementType;
        this.accessor    = accessor;
    }

    /**
     * Returns the type of elements in this list.
     */
    @Override
    public Class<T> getElementType() {
        return elementType;
    }

    /**
     * Returns the size of this list.
     */
    @Override
    public int size() {
        return accessor.childCount();
    }

    /**
     * Returns the element at the given index. This method creates the proxies when first needed.
     */
    @Override
    public T get(final int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, index));
        }
        T[] elements = this.elements;
        if (elements == null) {
            @SuppressWarnings("unchecked")
            final T[] unsafe = (T[]) new Object[Math.max(4, index+1)];
            this.elements = elements = unsafe;
        } else {
            final int length = elements.length;
            if (index >= length) {
                this.elements = elements = Arrays.copyOf(elements, Math.max(index, length*2));
            }
        }
        T element = elements[index];
        if (element == null) {
            element = MetadataProxy.newProxyInstance(elementType, accessor, index);
            elements[index] = element;
        }
        return element;
    }
}
