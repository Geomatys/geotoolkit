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
package org.geotoolkit.metadata.iso;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;


/**
 * Value uniquely identifying an object within a namespace.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_Identifier_Type", propOrder={
    "code",
    "authority"
})
@XmlRootElement(name = "MD_Identifier")
public class DefaultIdentifier extends MetadataEntity implements Identifier {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7459062382170865919L;

    /**
     * Alphanumeric value identifying an instance in the namespace.
     */
    private String code;

    /**
     * Identifier of the version of the associated code space or code, as specified
     * by the code space or code authority. This version is included only when the
     * {@linkplain #getCode code} uses versions. When appropriate, the edition is
     * identified by the effective date, coded using ISO 8601 date format.
     */
    private String version;

    /**
     * Organization or party responsible for definition and maintenance of the
     * {@linkplain #getCode code}.
     */
    private Citation authority;

    /**
     * Construct an initially empty identifier.
     */
    public DefaultIdentifier() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultIdentifier(final Identifier source) {
        super(source);
    }

    /**
     * Creates an identifier initialized to the given code.
     *
     * @param code The alphanumeric value identifying an instance in the namespace,
     *             or {@code null} if none.
     */
    public DefaultIdentifier(final String code) {
        if (code != null) {
            setCode(code);
        }
    }

    /**
     * Creates an identifier initialized to the given authority and code.
     *
     * @param authority The organization or party responsible for definition and maintenance
     *                  of the code, or {@code null} if none.
     * @param code      The alphanumeric value identifying an instance in the namespace,
     *                  or {@code null} if none.
     *
     * @since 2.2
     */
    public DefaultIdentifier(final Citation authority, final String code) {
        this(code);
        if (authority != null) {
            setAuthority(authority);
        }
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultIdentifier castOrCopy(final Identifier object) {
        return (object == null) || (object instanceof DefaultIdentifier)
                ? (DefaultIdentifier) object : new DefaultIdentifier(object);
    }

    /**
     * Alphanumeric value identifying an instance in the namespace.
     *
     * @return The code.
     */
    @Override
    @XmlElement(name = "code", required = true)
    public synchronized String getCode() {
        return code;
    }

    /**
     * Sets the alphanumeric value identifying an instance in the namespace.
     *
     * @param newValue The new code
     */
    public synchronized void setCode(final String newValue) {
        checkWritePermission();
        code = newValue;
    }

    /**
     * Identifier of the version of the associated code, as specified by the code space or
     * code authority. This version is included only when the {@linkplain #getCode code}
     * uses versions. When appropriate, the edition is identified by the effective date,
     * coded using ISO 8601 date format.
     *
     * @return The version, or {@code null} if not available.
     */
    public synchronized String getVersion() {
        return version;
    }

    /**
     * Sets an identifier of the version of the associated code.
     *
     * @param newValue The new version.
     */
    public synchronized void setVersion(final String newValue) {
        checkWritePermission();
        version = newValue;
    }

    /**
     * Organization or party responsible for definition and maintenance of the
     * {@linkplain #getCode code}.
     *
     * @return The authority, or {@code null} if not available.
     */
    @Override
    @XmlElement(name = "authority")
    public synchronized Citation getAuthority() {
        return authority;
    }

    /**
     * Sets the organization or party responsible for definition and maintenance of the
     * {@linkplain #getCode code}.
     *
     * @param newValue The new authority.
     */
    public synchronized void setAuthority(final Citation newValue) {
        checkWritePermission();
        authority = newValue;
    }
}
