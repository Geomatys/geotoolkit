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
package org.geotoolkit.internal.simple;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.util.InternationalString;
import org.apache.sis.internal.simple.SimpleCitation;


/**
 * A trivial implementation of {@link ReferenceIdentifier}. This is defined as a subtype of
 * {@link SimpleCitation} only as an opportunist way (not something generally recommended).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class SimpleReferenceIdentifier extends SimpleCitation implements ReferenceIdentifier {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8773225498918423388L;

    /**
     * The code to be returned by {@link #getCode()}.
     */
    protected final String code;

    /**
     * Creates a new identifier for the given code space and code value.
     * The given code space is also used for constructing a simple authority.
     *
     * @param codespace The string to be returned by {@link #getCodeSpace()}.
     * @param code      The string to be returned by {@link #getCode()}.
     */
    public SimpleReferenceIdentifier(final String codespace, final String code) {
        super(codespace);
        this.code = code;
    }

    /**
     * Methods inherited from the {@link ReferenceIdentifier} interface
     * that we implement in a trivial way.
     */
    @Override public Citation getAuthority() {return this;}
    @Override public String   getCodeSpace() {return title;}
    @Override public String   getCode()      {return code;}
    @Override public String   getVersion()   {return null;}
    @Override public InternationalString getDescription() {return null;}

    /**
     * Returns a string representation of this identifier.
     */
    @Override
    public String toString() {
        return "ReferenceIdentifier[\"" + title + ':' + code + "\"]";
    }
}
