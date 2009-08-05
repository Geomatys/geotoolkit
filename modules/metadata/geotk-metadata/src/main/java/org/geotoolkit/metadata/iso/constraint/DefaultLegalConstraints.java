/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.constraint;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.InternationalString;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.constraint.LegalConstraints;


/**
 * Restrictions and legal prerequisites for accessing and using the resource.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlType(name = "MD_LegalConstraints", propOrder={
    "accessConstraints",
    "useConstraints",
    "otherConstraints"
})
@XmlRootElement(name = "MD_LegalConstraints")
public class DefaultLegalConstraints extends DefaultConstraints implements LegalConstraints {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -2891061818279024901L;

    /**
     * Access constraints applied to assure the protection of privacy or intellectual property,
     * and any special restrictions or limitations on obtaining the resource.
     */
    private Collection<Restriction> accessConstraints;

    /**
     * Constraints applied to assure the protection of privacy or intellectual property, and any
     * special restrictions or limitations or warnings on using the resource.
     */
    private Collection<Restriction> useConstraints;

    /**
     * Other restrictions and legal prerequisites for accessing and using the resource.
     * This method should returns a non-empty value only if {@linkplain #getAccessConstraints
     * access constraints} or {@linkplain #getUseConstraints use constraints} declares
     * {@linkplain Restriction#OTHER_RESTRICTIONS other restrictions}.
     */
    private Collection<InternationalString> otherConstraints;

    /**
     * Constructs an initially empty constraints.
     */
    public DefaultLegalConstraints() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultLegalConstraints(final LegalConstraints source) {
        super(source);
    }

    /**
     * Returns the access constraints applied to assure the protection of privacy or intellectual property,
     * and any special restrictions or limitations on obtaining the resource.
     */
    @Override
    @XmlElement(name = "accessConstraints")
    public synchronized Collection<Restriction> getAccessConstraints() {
        return xmlOptional(accessConstraints = nonNullCollection(accessConstraints, Restriction.class));
    }

    /**
     * Sets the access constraints applied to assure the protection of privacy or intellectual property,
     * and any special restrictions or limitations on obtaining the resource.
     *
     * @param newValues The new access constraints.
     */
    public synchronized void setAccessConstraints(
            final Collection<? extends Restriction> newValues)
    {
        accessConstraints = copyCollection(newValues, accessConstraints, Restriction.class);
    }

    /**
     * Returns the constraints applied to assure the protection of privacy or intellectual property, and any
     * special restrictions or limitations or warnings on using the resource.
     */
    @Override
    @XmlElement(name = "useConstraints")
    public synchronized Collection<Restriction> getUseConstraints() {
        return xmlOptional(useConstraints = nonNullCollection(useConstraints, Restriction.class));
    }

    /**
     * Sets the constraints applied to assure the protection of privacy or intellectual property, and any
     * special restrictions or limitations or warnings on using the resource.
     *
     * @param newValues The new use constraints.
     */
    public synchronized void setUseConstraints(
            final Collection<? extends Restriction> newValues)
    {
        useConstraints = copyCollection(newValues, useConstraints, Restriction.class);
    }

    /**
     * Returns the other restrictions and legal prerequisites for accessing and using the resource.
     * This method should returns a non-empty value only if {@linkplain #getAccessConstraints
     * access constraints} or {@linkplain #getUseConstraints use constraints} declares
     * {@linkplain Restriction#OTHER_RESTRICTIONS other restrictions}.
     */
    @Override
    @XmlElement(name = "otherConstraints")
    public synchronized Collection<InternationalString> getOtherConstraints() {
        return xmlOptional(otherConstraints = nonNullCollection(otherConstraints, InternationalString.class));
    }

    /**
     * Sets the other restrictions and legal prerequisites for accessing and using the resource.
     *
     * @param newValues Other constraints.
     */
    public synchronized void setOtherConstraints(
            final Collection<? extends InternationalString> newValues)
    {
        otherConstraints = copyCollection(newValues, otherConstraints, InternationalString.class);
    }
}
