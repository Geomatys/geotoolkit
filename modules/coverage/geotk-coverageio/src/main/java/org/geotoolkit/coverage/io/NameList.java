/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.coverage.io;

import java.util.AbstractList;


/**
 * A list where each element is some base name completed by the index + 1.
 *
 * @todo In a future version, we should provide the capability to increase the size
 *       only when we more to the next image (for supporting the image format where
 *       getting the image count is costly). <code>IOException</code> will need to
 *       be wrapped in <code>BackingStoreException</code>.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
final class NameList extends AbstractList<String> {
    /**
     * The base name.
     */
    private final String base;

    /**
     * The size of the list.
     */
    private final int size;

    /**
     * Creates a new list for the given base name repeated the given amount of time.
     */
    NameList(final String base, final int size) {
        this.base = base;
        this.size = size;
    }

    /**
     * Returns the size of the list.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the element at the given index.
     */
    @Override
    public String get(final int index) {
        if (index >= 0 && index < size) {
            return base + " [" + (index + 1) + ']';
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }
}
