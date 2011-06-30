/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.geotoolkit.xml.IdentifierSpace;


/**
 * Wraps a {@code XLink}, {@code UUID} or other objects as an identifier
 * in the {@link IdentifierMap}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
final class IdentifierAdapter<T> implements Identifier {
    /**
     * The authority.
     */
    final IdentifierSpace<T> authority;

    /**
     * The identifier value.
     */
    T value;

    /**
     * Creates a new adapter for the given authority and identifier value.
     */
    IdentifierAdapter(final IdentifierSpace<T> authority, final T value) {
        this.authority = authority;
        this.value = value;
    }

    /**
     * Returns the authority specified at construction time.
     */
    @Override
    public Citation getAuthority() {
        return authority;
    }

    /**
     * Returns a string representation of the identifier given at construction time.
     */
    @Override
    public String getCode() {
        return value.toString();
    }

    /**
     * Returns a string representation of this identifier.
     */
    @Override public String toString() {
        return "Identifier[\"" + authority + "\", \"" + value + "\"]";
    }
}
