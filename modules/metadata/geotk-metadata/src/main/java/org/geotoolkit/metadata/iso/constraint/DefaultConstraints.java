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
package org.geotoolkit.metadata.iso.constraint;

import java.util.Collection;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.constraint.LegalConstraints;
import org.opengis.metadata.constraint.SecurityConstraints;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * Restrictions on the access and use of a resource or metadata.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MD_Constraints_Type", propOrder={
    "useLimitations"
})
@XmlRootElement(name = "MD_Constraints")
@XmlSeeAlso({
    DefaultLegalConstraints.class,
    DefaultSecurityConstraints.class
})
public class DefaultConstraints extends MetadataEntity implements Constraints {
    /**
     * Serial number for inter-operability with different versions.
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
     * Constructs a new constraints with the given {@linkplain #getUseLimitations() use limitation}.
     *
     * @param useLimitation The use limitation, or {@code null} if none.
     *
     * @since 3.20
     */
    public DefaultConstraints(final CharSequence useLimitation) {
        if (useLimitation != null) {
            getUseLimitations().add(SimpleInternationalString.wrap(useLimitation));
        }
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultConstraints(final Constraints source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link LegalConstraints} and {@link SecurityConstraints}
     * sub-interfaces. If one of those interfaces is found, then this method delegates to
     * the corresponding {@code castOrCopy} static method. If the given object implements more
     * than one of the above-cited interfaces, then the {@code castOrCopy} method to be used is
     * unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultConstraints castOrCopy(final Constraints object) {
        if (object instanceof LegalConstraints) {
            return DefaultLegalConstraints.castOrCopy((LegalConstraints) object);
        }
        if (object instanceof SecurityConstraints) {
            return DefaultSecurityConstraints.castOrCopy((SecurityConstraints) object);
        }
        return (object == null) || (object instanceof DefaultConstraints)
                ? (DefaultConstraints) object : new DefaultConstraints(object);
    }

    /**
     * Returns the limitation affecting the fitness for use of the resource.
     * Example: "not to be used for navigation".
     */
    @Override
    @XmlElement(name = "useLimitation")
    public synchronized Collection<InternationalString> getUseLimitations() {
        return useLimitations = nonNullCollection(useLimitations, InternationalString.class);
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
