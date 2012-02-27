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
package org.geotoolkit.internal.jaxb;

import java.util.AbstractMap;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;


/**
 * An entry in the {@link IdentifierMap}. This class implements both the
 * {@link Map.Entry} interface (for inclusion in the set to be returned
 * by {@link IdentifierMapAdapter#entrySet()}) and the {@link Identifier}
 * interface (for inclusion in the {@link IdentifierMapAdapter#identifiers}
 * collection).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.19
 * @module
 */
final class IdentifierMapEntry extends AbstractMap.SimpleEntry<Citation,String> implements Identifier {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5484541090753985572L;

    /**
     * Creates a new entry for the given authority and code.
     */
    IdentifierMapEntry(final Citation authority, final String code) {
        super(authority, code);
    }

    /**
     * Returns the identifier namespace, which is the key of this entry.
     */
    @Override
    public Citation getAuthority() {
        return getKey();
    }

    /**
     * Returns the identifier code, which is the value of this entry.
     */
    @Override
    public String getCode() {
        return getValue();
    }

    /**
     * Same than the above, but as an immutable entry. We use this implementation when the
     * entry has been created on-the-fly at iteration time rather than being stored in the
     * identifier collection.
     */
    static final class Immutable extends AbstractMap.SimpleImmutableEntry<Citation,String> implements Identifier {
        private static final long serialVersionUID = -8179498861233498041L;
        Immutable(Citation authority, String code) {super(authority, code);}
        @Override public Citation getAuthority()   {return getKey();}
        @Override public String   getCode()        {return getValue();}
    }
}
