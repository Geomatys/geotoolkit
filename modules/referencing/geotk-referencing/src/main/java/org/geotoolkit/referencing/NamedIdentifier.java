/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing;

import java.util.Map;
import net.jcip.annotations.Immutable;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.parameter.InvalidParameterValueException;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.ImmutableIdentifier;


/**
 * An identification of a CRS object which is both an {@linkplain Identifier identifier}
 * and a {@linkplain GenericName name}. The main interface implemented by this class is
 * {@link ReferenceIdentifier}. However, this class also implements {@link GenericName}
 * in order to make it possible to reuse the same identifiers in the list of
 * {@linkplain AbstractIdentifiedObject#getAlias aliases}. Casting an alias
 * {@code GenericName} to an {@code ReferenceIdentifier} gives access to more
 * informations, like the URL of the authority.
 * <p>
 * The generic name will be inferred from {@code ReferenceIdentifier} attributes. More
 * specifically, a {@linkplain ScopedName scoped name} will be created using the shortest
 * authority's {@linkplain Citation#getAlternateTitles alternate titles} (or the
 * {@linkplain Citation#getTitle main title} if there is no alternate titles) as the
 * {@linkplain ScopedName#scope scope}, and the {@linkplain #getCode code} as the
 * {@linkplain ScopedName#tip tip}. This heuristic rule seems reasonable since,
 * according ISO 19115, the {@linkplain Citation#getAlternateTitles alternate titles}
 * often contains abbreviation (for example "DCW" as an alternative title for
 * "<cite>Digital Chart of the World</cite>").
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.NamedIdentifier}.
 */
@Immutable
@Deprecated
public class NamedIdentifier extends org.apache.sis.referencing.NamedIdentifier {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8474731565582774497L;

    /**
     * Creates a new identifier from the specified one. This is a copy constructor
     * which will get the code, codespace, authority, version and (if available)
     * the remarks from the given identifier.
     * <p>
     * If the given identifier implements the {@link GenericName} interface, then calls to
     * {@link #tip()}, {@link #head()}, {@link #scope()} and similar methods will delegates
     * to that name.
     *
     * @param identifier The identifier to copy.
     *
     * @since 3.16
     */
    public NamedIdentifier(final ReferenceIdentifier identifier) {
        super(identifier);
    }

    /**
     * Constructs an identifier from a set of properties. The content of the properties map is used
     * as described in the {@linkplain ImmutableIdentifier#ImmutableIdentifier(Map)
     * super-class constructor}.
     *
     * @param  properties The properties to be given to this identifier.
     * @throws InvalidParameterValueException if a property has an invalid value.
     * @throws IllegalArgumentException if a property is invalid for some other reason.
     */
    public NamedIdentifier(final Map<String,?> properties) throws IllegalArgumentException {
        super(properties);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority
     *          The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}),
     *          or {@code null} if not available.
     * @param code
     *          The code. The {@link InternationalString#toString(Locale) toString(null)} method
     *          is invoked for the code, and the complete international string is retained for
     *          the {@linkplain GenericName generic name}.
     */
    public NamedIdentifier(final Citation authority, final InternationalString code) {
        super(authority, code);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}),
     *                  or {@code null} if not available.
     * @param code      The code. This parameter is mandatory.
     */
    public NamedIdentifier(final Citation authority, final String code) {
        super(authority, code);
    }

    /**
     * Constructs an identifier from an authority and code informations. This is a convenience
     * constructor for commonly-used parameters. If more control are wanted (for example adding
     * remarks), use the {@linkplain #NamedIdentifier(Map) constructor with a properties map}.
     *
     * @param authority The authority (e.g. {@link Citations#OGC OGC} or {@link Citations#EPSG EPSG}),
     *                  or {@code null} if not available.
     * @param code      The code. This parameter is mandatory.
     * @param version   The version, or {@code null} if none.
     */
    public NamedIdentifier(final Citation authority, final String code, final String version) {
        super(authority, code, version);
    }
}
