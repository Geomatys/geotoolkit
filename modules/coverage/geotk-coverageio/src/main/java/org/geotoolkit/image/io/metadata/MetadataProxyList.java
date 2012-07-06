/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Arrays;
import java.util.AbstractList;
import java.util.RandomAccess;
import java.util.logging.Level;

import org.geotoolkit.util.collection.CheckedCollection;

import static org.geotoolkit.util.ArgumentChecks.ensureValidIndex;
import static org.geotoolkit.internal.image.io.GridDomainAccessor.ARRAY_ATTRIBUTE_NAME;


/**
 * A list of {@link MetadataProxy} instances. This list is <cite>live</cite>: changes to the
 * backing {@link javax.imageio.metadata.IIOMetadata} are immediately reflected in this list.
 *
 * {@note Current implementation has a limitation, in that changes in existing elements are
 *        reflected by this view as expected, but <em>addition</em> or <em>removal</em> of
 *        elements are not visible if they are not performed by the <code>MetadataNodeParser</code>
 *        instance wrapped by this class.}
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
     * The proxy which is the parent of all elements in this list.
     */
    private final MetadataProxy<T> parent;

    /**
     * The proxies which have been created up to date. Elements in this array
     * may be null if the proxy at a given index has not yet been created.
     */
    private T[] elements;

    /**
     * Creates a new list.
     */
    static <T> MetadataProxyList<T> create(final Class<T> elementType, final MetadataNodeParser accessor) {
        return new MetadataProxyList<T>(elementType, accessor);
    }

    /**
     * Creates a new list.
     */
    private MetadataProxyList(final Class<T> elementType, final MetadataNodeParser accessor) {
        parent = new MetadataProxy<T>(elementType, accessor);
    }

    /**
     * Sets the logging level of all proxies created up to date. This will be executed
     * only if the level is different than the one used up to date, as a safety against
     * infinite recursivity.
     */
    final void setWarningLevel(final Level level) {
        if (!level.equals(parent.accessor.setWarningLevel(level))) {
            MetadataProxy.setWarningLevel(Arrays.asList(elements), level);
        }
    }

    /**
     * Returns the type of elements in this list.
     */
    @Override
    public Class<T> getElementType() {
        return parent.interfaceType;
    }

    /**
     * Returns the size of this list.
     */
    @Override
    public int size() {
        return parent.accessor.childCount();
    }

    /**
     * Returns the element at the given index. This method creates the proxies when first needed.
     */
    @Override
    public T get(final int index) {
        ensureValidIndex(size(), index);
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
            /*
             * If the object has not already been created, create it now. In usual situations,
             * we just create a new proxy element. However in a few cases, the element type is
             * not an other interface for which we can create a proxy. The main case we want to
             * support is the OffsetVector node which are elements having a single attribute
             * named "values":
             *
             * RectifiedGridDomain  : RectifiedGrid
             * └───OffsetVectors    : List<double[]>
             *     └───OffsetVector : double[]
             *         └───values
             *
             * Note that this practice (element with a single attribute called "value") is used
             * in the standard Image I/O metadata format. For now are interested only in arrays
             * of primitive ints, doubles or Strings, which is why "values" is plural.
             */
            final Class<T> type = parent.interfaceType;
            final Class<?> componentType = type.getComponentType();
            if (componentType == null) {
                element = parent.newProxyInstance(index);
            } else {
                final MetadataNodeParser accessor = parent.accessor;
                accessor.selectChild(index);
                final Object array;
                if (componentType.equals(Double.TYPE)) {
                    array = accessor.getAttributeAsDoubles(ARRAY_ATTRIBUTE_NAME, false);
                } else if (componentType.equals(Integer.TYPE)) {
                    array = accessor.getAttributeAsIntegers(ARRAY_ATTRIBUTE_NAME, false);
                } else {
                    array = accessor.getAttributeAsStrings(ARRAY_ATTRIBUTE_NAME, false);
                }
                element = type.cast(array);
            }
            elements[index] = element;
        }
        return element;
    }
}
