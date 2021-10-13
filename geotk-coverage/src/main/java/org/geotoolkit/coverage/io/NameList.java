/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import java.util.List;
import org.opengis.util.LocalName;
import org.opengis.util.NameFactory;


/**
 * A list where each element is some base name completed by the index + 1.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.09
 * @module
 */
final class NameList extends AbstractList<LocalName> {
    /**
     * The name factory.
     */
    private final NameFactory factory;

    /**
     * The base name.
     */
    private final String base;

    /**
     * The names, created when first needed.
     */
    private final LocalName[] names;

    /**
     * The image names, or {@code null} if none.
     *
     * @since 3.20
     */
    private final List<String> imageNames;

    /**
     * Creates a new list wrapping the given list of image names.
     *
     * @since 3.20
     */
    NameList(final NameFactory factory, final List<String> imageNames) {
        this.factory    = factory;
        this.base       = null;
        this.names      = new LocalName[imageNames.size()];
        this.imageNames = imageNames;
    }

    /**
     * Creates a new list for the given base name repeated the given amount of time.
     */
    NameList(final NameFactory factory, final String base, final int size) {
        this.factory    = factory;
        this.base       = base;
        this.names      = new LocalName[size];
        this.imageNames = null;
    }

    /**
     * Returns the size of the list.
     */
    @Override
    public int size() {
        return names.length;
    }

    /**
     * Returns the element at the given index, or {@code null} if none.
     */
    @Override
    public LocalName get(final int index) {
        if (index < 0 || index >= names.length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        LocalName name = names[index];
        if (name == null) {
            final String imageName;
            if (imageNames != null) {
                imageName = imageNames.get(index);
            } else {
                imageName = base + " [" + (index + 1) + ']';
            }
            if (imageName != null) {
                name = factory.createLocalName(null, imageName);
                names[index] = name;
            }
        }
        return name;
    }
}
