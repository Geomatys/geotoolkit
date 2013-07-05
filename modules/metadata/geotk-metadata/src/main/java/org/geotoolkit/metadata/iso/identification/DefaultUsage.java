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
package org.geotoolkit.metadata.iso.identification;

import java.util.Date;
import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.InternationalString;
import org.opengis.metadata.identification.Usage;
import org.opengis.metadata.citation.ResponsibleParty;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Brief description of ways in which the resource(s) is/are currently used.
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
@XmlType(name = "MD_Usage_Type", propOrder={
    "specificUsage",
    "usageDate",
    "userDeterminedLimitations",
    "userContactInfo"
})
@XmlRootElement(name = "MD_Usage")
public class DefaultUsage extends MetadataEntity implements Usage {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 4059324536168287490L;

    /**
     * Brief description of the resource and/or resource series usage.
     */
    private InternationalString specificUsage;

    /**
     * Date and time of the first use or range of uses of the resource and/or resource series.
     * Values are milliseconds elapsed since January 1st, 1970,
     * or {@link Long#MIN_VALUE} if this value is not set.
     */
    private long usageDate;

    /**
     * Applications, determined by the user for which the resource and/or resource series
     * is not suitable.
     */
    private InternationalString userDeterminedLimitations;

    /**
     * Identification of and means of communicating with person(s) and organization(s)
     * using the resource(s).
     */
    private Collection<ResponsibleParty> userContactInfo;

    /**
     * Constructs an initially empty usage.
     */
    public DefaultUsage() {
        usageDate = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultUsage(final Usage source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (usageDate == 0 && source.getUsageDate() == null) {
                usageDate = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Creates an usage initialized to the specified values.
     *
     * @param specificUsage   Brief description of the resource and/or resource series usage.
     * @param userContactInfo Means of communicating with person(s) and organization(s).
     */
    public DefaultUsage(final InternationalString specificUsage,
                        final Collection<ResponsibleParty> userContactInfo)
    {
        this(); // Initialize the date field.
        setSpecificUsage  (specificUsage  );
        setUserContactInfo(userContactInfo);
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
    public static DefaultUsage castOrCopy(final Usage object) {
        return (object == null) || (object instanceof DefaultUsage)
                ? (DefaultUsage) object : new DefaultUsage(object);
    }

    /**
     * Returns a brief description of the resource and/or resource series usage.
     */
    @Override
    @XmlElement(name = "specificUsage", required = true)
    public synchronized InternationalString getSpecificUsage() {
        return specificUsage;
    }

    /**
     * Sets a brief description of the resource and/or resource series usage.
     *
     * @param newValue The new specific usage.
     */
    public synchronized void setSpecificUsage(final InternationalString newValue) {
        checkWritePermission();
        specificUsage = newValue;
    }

    /**
     * Returns the date and time of the first use or range of uses
     * of the resource and/or resource series.
     */
    @Override
    @XmlElement(name = "usageDateTime")
    public synchronized Date getUsageDate() {
        return (usageDate != Long.MIN_VALUE) ? new Date(usageDate) : null;
    }

    /**
     * Sets the date and time of the first use.
     *
     * @param newValue The new usage date.
     */
    public synchronized void setUsageDate(final Date newValue)  {
        checkWritePermission();
        usageDate = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns applications, determined by the user for which the resource and/or resource series
     * is not suitable.
     */
    @Override
    @XmlElement(name = "userDeterminedLimitations")
    public synchronized InternationalString getUserDeterminedLimitations() {
        return userDeterminedLimitations;
    }

    /**
     * Sets applications, determined by the user for which the resource and/or resource series
     * is not suitable.
     *
     * @param newValue The new user determined limitations.
     */
    public synchronized void setUserDeterminedLimitations(final InternationalString newValue) {
        checkWritePermission();
        this.userDeterminedLimitations = newValue;
    }

    /**
     * Returns identification of and means of communicating with person(s) and organization(s)
     * using the resource(s).
     */
    @Override
    @XmlElement(name = "userContactInfo", required = true)
    public synchronized Collection<ResponsibleParty> getUserContactInfo() {
        return userContactInfo = nonNullCollection(userContactInfo, ResponsibleParty.class);
    }

    /**
     * Sets identification of and means of communicating with person(s) and organization(s)
     * using the resource(s).
     *
     * @param newValues The new user contact info.
     */
    public synchronized void setUserContactInfo(final Collection<? extends ResponsibleParty> newValues) {
        userContactInfo = copyCollection(newValues, userContactInfo, ResponsibleParty.class);
    }
}
