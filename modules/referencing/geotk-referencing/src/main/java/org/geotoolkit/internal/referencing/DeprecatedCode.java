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

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.apache.sis.util.Deprecable;


/**
 * An identifier which is deprecated (when associated to a given object) in the EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.16)
 * @module
 */
public final class DeprecatedCode extends ImmutableIdentifier implements Deprecable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2608807286088731814L;

    /**
     * Creates a new deprecated name for the given code.
     *
     * @param authority
     *          Organization or party responsible for definition and maintenance of the code
     *          space or code.
     * @param codeSpace
     *          Name or identifier of the person or organization responsible for namespace.
     *          This is often an abbreviation of the authority name.
     * @param code
     *          Identifier code or name, optionally from a controlled list or pattern defined by
     *          a code space. The code can not be null.
     * @param version
     *          The version of the associated code space or code as specified by the code authority,
     *          or {@code null} if none.
     * @param remarks
     *          Comments on or information about this identifier, or {@code null} if none.
     */
    public DeprecatedCode(final Citation authority, final String codeSpace, final String code,
            final String version, final InternationalString remarks)
    {
        super(authority, codeSpace, code, version, remarks);
    }

    /**
     * Returns {@code true} since this code is deprecated.
     */
    @Override
    public boolean isDeprecated() {
        return true;
    }
}
