/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.InternationalString;
import org.opengis.metadata.constraint.Constraints;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Restrictions on the access and use of a resource or metadata.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_Constraints", propOrder={
    "useLimitations"
})
@XmlSeeAlso({DefaultLegalConstraints.class, DefaultSecurityConstraints.class})
@XmlRootElement(name = "MD_Constraints")
public class DefaultConstraints extends MetadataEntity implements Constraints {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 1771854790746022204L;

    /**
     * Limitation affecting the fitness for use of the resource.
     * Example, "not to be used for navigation".
     */
    private Collection<InternationalString> useLimitations;

    /**
     * Constructs an initially empty constraints.
     */
    public DefaultConstraints() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultConstraints(final Constraints source) {
        super(source);
    }

    /**
     * Returns the limitation affecting the fitness for use of the resource.
     * Example: "not to be used for navigation".
     */
    @Override
    @XmlElement(name = "useLimitation")
    public synchronized Collection<InternationalString> getUseLimitations() {
        return xmlOptional(useLimitations = nonNullCollection(useLimitations, InternationalString.class));
    }

    /**
     * Sets the limitation affecting the fitness for use of the resource.
     * Example: "not to be used for navigation".
     *
     * @param newValues The new use limitation.
     */
    public synchronized void setUseLimitations(final Collection<? extends InternationalString> newValues) {
        useLimitations = copyCollection(newValues, useLimitations, InternationalString.class);
    }
}
