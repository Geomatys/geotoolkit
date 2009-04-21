/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.metadata;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.jaxb.text.AnchoredStringAdapter;


/**
 * An identification of a CRS object. This implementation duplicates
 * {@link org.geotoolkit.referencing.NamedIdentifier} with generic name
 * support removed. It is provided here for avoiding a direct dependency
 * to the referencing module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.6
 * @module
 */
@XmlRootElement(name = "RS_Identifier")
public final class ReferenceIdentifierMetadata implements ReferenceIdentifier, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7515516481965979199L;

    /**
     * Identifier code or name, optionally from a controlled list or pattern defined by a code space.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(AnchoredStringAdapter.class)
    private String code;

    /**
     * Name or identifier of the person or organization responsible for namespace.
     * This is often an abreviation of the authority name.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(AnchoredStringAdapter.class)
    private String codespace;

    /**
     * Organization or party responsible for definition and maintenance of the code space or code.
     */
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CitationAdapter.class)
    private Citation authority;

    /**
     * Empty constructor for JAXB.
     */
    private ReferenceIdentifierMetadata() {
    }

    /**
     * Creates a new identifier from the specified one.
     *
     * @param identifier The identifier to copy.
     */
    public ReferenceIdentifierMetadata(final ReferenceIdentifier identifier) {
        code      = identifier.getCode();
        codespace = identifier.getCodeSpace();
        authority = identifier.getAuthority();
    }

    /**
     * Creates a new identifier from the specified code and authority.
     *
     * @param authority
     *          Organization or party responsible for definition and maintenance of the code space or code.
     * @param codespace
     *          Name or identifier of the person or organization responsible for namespace.
     *          This is often an abreviation of the authority name.
     * @param code
     *          Identifier code or name, optionally from a controlled list or pattern defined by a code space.
     */
    public ReferenceIdentifierMetadata(final Citation authority, final String codespace, final String code) {
        this.code      = code;
        this.codespace = codespace;
        this.authority = authority;
    }

    /**
     * Returns the identifier code or name.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Returns the identifier codespace.
     */
    @Override
    public String getCodeSpace() {
        return codespace;
    }

    /**
     * Returns the authority.
     */
    @Override
    public Citation getAuthority() {
        return authority;
    }

    /**
     * Returns {@code null} since we do not marshall version numbers.
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(code, Utilities.hash(codespace, Utilities.hash(authority, 0)));
    }

    /**
     * Compares this object with the given one for equality.
     *
     * @param object The object to compare with this identifier.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ReferenceIdentifierMetadata) {
            final ReferenceIdentifierMetadata that = (ReferenceIdentifierMetadata) object;
            return Utilities.equals(code,      that.code) &&
                   Utilities.equals(codespace, that.codespace) &&
                   Utilities.equals(authority, that.authority);
        }
        return false;
    }

    /**
     * Returns a string representation of this identifier.
     */
    @Override
    public String toString() {
        return toString("IDENTIFIER", authority, codespace, code);
    }

    /**
     * Returns a pseudo-WKT representation.
     *
     * @param  type The WKT heading text.
     * @param  code The code to write in the {@code "AUTHORITY"} element, or {@code null} if none.
     * @param  authority The authority to write in the {@code "AUTHORITY"} element.
     * @return The pseudo-WKT.
     */
    static String toString(final String type, final Citation authority, final String codespace, final String code) {
        final StringBuilder buffer = new StringBuilder(type).append("[\"");
        if (codespace != null) {
            buffer.append(codespace).append(':');
        }
        buffer.append(code).append('"');
        if (authority != null) {
            buffer.append("AUTHORITY[\"").append(authority.getTitle()).append("\",\"").append(code).append("\"]");
        }
        return buffer.append(']').toString();
    }
}
