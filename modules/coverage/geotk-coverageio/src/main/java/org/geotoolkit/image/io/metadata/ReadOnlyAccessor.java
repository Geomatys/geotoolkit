/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.util.NoSuchElementException;


/**
 * A read-only metadata accessor. Used by {@link MetadataProxy} in order to explore an
 * {@link SpatialMetadata} object without creating new nodes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
final class ReadOnlyAccessor extends MetadataAccessor {
    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative
     * to the given parent. This constructor auto-detects whatever the given node has children.
     *
     * @param parent The accessor for which the {@code path} is relative.
     * @param path   The path to the {@linkplain Node node} of interest.
     *
     * @throws NoSuchElementException If the given metadata doesn't contains a node for the
     *         element to fetch.
     */
    ReadOnlyAccessor(final MetadataAccessor parent, final String path) throws NoSuchElementException {
        super(parent, path, "#auto");
    }

    /**
     * Unconditionally declares this accessor as read-only.
     */
    @Override
    public boolean isReadOnly() {
        return true;
    }
}
