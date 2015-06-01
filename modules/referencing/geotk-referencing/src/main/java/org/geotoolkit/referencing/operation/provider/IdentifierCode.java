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
package org.geotoolkit.referencing.operation.provider;


import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;

import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.metadata.iso.ImmutableIdentifier;


/**
 * A reference identifier for EPSG or GeoTiff codes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
final class IdentifierCode extends ImmutableIdentifier {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 357222258307746767L;

    /**
     * If this identifier is deprecated, the identifier that supersede this one.
     * Otherwise {@code 0}.
     */
    final int supersededBy;

    /**
     * Creates a new identifier for the given authority.
     *
     * @param authority Organization for definition and maintenance of the code space or code.
     * @param code Identifier code from the authority.
     */
    IdentifierCode(final Citation authority, final int code) {
        this(authority, code, 0);
    }

    /**
     * Creates a deprecated identifier for the given authority.
     *
     * @param authority Organization for definition and maintenance of the code space or code.
     * @param code Identifier code from the authority.
     * @param supersededBy The code that replace this one.
     */
    IdentifierCode(final Citation authority, final int code, final int supersededBy) {
        super(authority, codespace(authority), Integer.toString(code), null, remarks(supersededBy));
        this.supersededBy = supersededBy;
    }

    /**
     * Returns the code space for the given authority.
     */
    private static String codespace(final Citation authority) {
        if (authority == org.apache.sis.metadata.iso.citation.Citations.EPSG) { // Temporary hack.
            return "EPSG";
        }
        return authority.getIdentifiers().iterator().next().getCode();
    }

    /**
     * formats a "Superseded by" international string.
     */
    private static InternationalString remarks(final int supersededBy) {
        if (supersededBy == 0) {
            return null;
        }
        return Vocabulary.formatInternational(Vocabulary.Keys.SUPERSEDED_BY_1, supersededBy);
    }

    /**
     * Returns {@code true} if this code is deprecated.
     */
    @Override
    public boolean isDeprecated() {
        return supersededBy != 0;
    }
}
