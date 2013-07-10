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
package org.geotoolkit.metadata.sql;

import java.util.Objects;
import net.jcip.annotations.Immutable;


/**
 * The key for an entry in the {@link MetadataSource} cache.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Immutable
final class CacheKey {
    /**
     * The metadata interface to be implemented.
     */
    private final Class<?> type;

    /**
     * The primary key for the entry in the table.
     */
    private final String identifier;

    /**
     * Creates a new key.
     */
    CacheKey(final Class<?> type, final String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    /**
     * Compares the given object with thie key for equality.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof CacheKey) {
            final CacheKey that = (CacheKey) other;
            return Objects.equals(this.type,       that.type) &&
                   Objects.equals(this.identifier, that.identifier);
        }
        return false;
    }

    /**
     * Returns a hash code for this key.
     */
    @Override
    public int hashCode() {
        return type.hashCode() ^ identifier.hashCode();
    }
}
