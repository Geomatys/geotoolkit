/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.metadata.CitationAdapter;
import org.geotoolkit.internal.jaxb.text.AnchoredStringAdapter;
import org.geotoolkit.internal.jaxb.metadata.ReferenceSystemMetadata;


/**
 * An identification of a CRS object.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.6)
 * @module
 */
@XmlRootElement(name = "RS_Identifier", namespace = Namespaces.GMD)
public class DefaultReferenceIdentifier implements ReferenceIdentifier, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7515516481965979199L;

    /**
     * Identifier code or name, optionally from a controlled list or pattern defined by a code space.
     *
     * @see #getCode
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(AnchoredStringAdapter.class)
    String code;

    /**
     * Name or identifier of the person or organization responsible for namespace.
     * This is often an abreviation of the authority name.
     *
     * @see #getCodeSpace
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(AnchoredStringAdapter.class)
    String codespace;

    /**
     * Organization or party responsible for definition and maintenance of the code space or code.
     *
     * @see #getAuthority
     */
    @XmlElement(required = true, namespace = Namespaces.GMD)
    @XmlJavaTypeAdapter(CitationAdapter.class)
    Citation authority;

    /**
     * Empty constructor for JAXB.
     */
    DefaultReferenceIdentifier() {
    }

    /**
     * Creates a new identifier from the specified one.
     *
     * @param identifier The identifier to copy.
     */
    public DefaultReferenceIdentifier(final ReferenceIdentifier identifier) {
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
    public DefaultReferenceIdentifier(final Citation authority, final String codespace, final String code) {
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
     * Returns {@code null} by default, since this attribute is not part of the standard
     * XML schema. Note that the {@link NamedIdentifier} subclass overrides this method.
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
        if (object!=null && object.getClass().equals(getClass())) {
            final DefaultReferenceIdentifier that = (DefaultReferenceIdentifier) object;
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
        return ReferenceSystemMetadata.toString("IDENTIFIER", authority, codespace, code);
    }
}
