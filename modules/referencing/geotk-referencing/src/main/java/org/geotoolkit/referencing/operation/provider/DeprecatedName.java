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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.metadata.citation.Citation;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.referencing.NamedIdentifier;


/**
 * A name which is deprecated (when associated to a given object) in the EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 */
@Immutable
final class DeprecatedName extends NamedIdentifier {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1792369861343798471L;

    /**
     * Creates a new deprecated name for the given code.
     */
    DeprecatedName(final Citation authority, final String code) {
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
