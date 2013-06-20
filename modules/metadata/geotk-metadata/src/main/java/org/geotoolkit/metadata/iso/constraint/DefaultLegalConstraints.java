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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.constraint.LegalConstraints;


/**
 * Restrictions and legal prerequisites for accessing and using the resource.
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
@Deprecated
@ThreadSafe
@XmlType(name = "MD_LegalConstraints_Type", propOrder={
    "accessConstraints",
    "useConstraints",
    "otherConstraints"
})
@XmlRootElement(name = "MD_LegalConstraints")
public class DefaultLegalConstraints extends DefaultConstraints implements LegalConstraints {
    /**
     * Serial number for inter-operability with different versions.
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
     * Constructs a new constraints with the given {@linkplain #getUseLimitations() use limitation}.
     *
     * @param useLimitation The use limitation, or {@code null} if none.
     *
     * @since 3.20
     */
    public DefaultLegalConstraints(final CharSequence useLimitation) {
        super(useLimitation);
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultLegalConstraints(final LegalConstraints source) {
        super(source);
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
    public static DefaultLegalConstraints castOrCopy(final LegalConstraints object) {
        return (object == null) || (object instanceof DefaultLegalConstraints)
                ? (DefaultLegalConstraints) object : new DefaultLegalConstraints(object);
    }

    /**
     * Returns the access constraints applied to assure the protection of privacy or intellectual property,
     * and any special restrictions or limitations on obtaining the resource.
     */
    @Override
    @XmlElement(name = "accessConstraints")
    public synchronized Collection<Restriction> getAccessConstraints() {
        return accessConstraints = nonNullCollection(accessConstraints, Restriction.class);
    }

    /**
     * Sets the access constraints applied to assure the protection of privacy or intellectual property,
     * and any special restrictions or limitations on obtaining the resource.
     *
     * @param newValues The new access constraints.
     */
    public synchronized void setAccessConstraints(final Collection<? extends Restriction> newValues) {
        accessConstraints = copyCollection(newValues, accessConstraints, Restriction.class);
    }

    /**
     * Returns the constraints applied to assure the protection of privacy or intellectual property, and any
     * special restrictions or limitations or warnings on using the resource.
     */
    @Override
    @XmlElement(name = "useConstraints")
    public synchronized Collection<Restriction> getUseConstraints() {
        return useConstraints = nonNullCollection(useConstraints, Restriction.class);
    }

    /**
     * Sets the constraints applied to assure the protection of privacy or intellectual property, and any
     * special restrictions or limitations or warnings on using the resource.
     *
     * @param newValues The new use constraints.
     */
    public synchronized void setUseConstraints(final Collection<? extends Restriction> newValues) {
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
        return otherConstraints = nonNullCollection(otherConstraints, InternationalString.class);
    }

    /**
     * Sets the other restrictions and legal prerequisites for accessing and using the resource.
     *
     * @param newValues Other constraints.
     */
    public synchronized void setOtherConstraints(final Collection<? extends InternationalString> newValues) {
        otherConstraints = copyCollection(newValues, otherConstraints, InternationalString.class);
    }
}
