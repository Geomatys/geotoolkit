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
package org.geotoolkit.internal.referencing;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.Identifier;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.Deprecable;


/**
 * A name which is deprecated (when associated to a given object) in the EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 * @module
 */
public final class DeprecatedName extends NamedIdentifier implements Deprecable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1792369861343798471L;

    /**
     * Creates a new deprecated name with the same authority, code, version and remarks
     * than the identifier.
     *
     * @param identifier The identifier.
     */
    public DeprecatedName(final Identifier identifier) {
        super(identifier);
    }

    /**
     * Creates a new deprecated name for the given code.
     *
     * @param authority The authority, or {@code null} if not available.
     * @param code      The code.
     */
    public DeprecatedName(final Citation authority, final String code) {
        super(authority, code);
    }

    /**
     * Returns {@code true} since this name is deprecated.
     */
    @Override
    public boolean isDeprecated() {
        return true;
    }
}
