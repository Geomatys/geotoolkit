/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.citation;

import java.util.Date;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Reference date and event used to describe it.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "CI_Date_Type", propOrder={
    "date",
    "dateType"
})
@XmlRootElement(name = "CI_Date")
public class DefaultCitationDate extends MetadataEntity implements CitationDate {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2884791484254008454L;

    /**
     * Reference date for the cited resource in milliseconds elapsed sine January 1st, 1970,
     * or {@link Long#MIN_VALUE} if none.
     */
    private long date;

    /**
     * Event used for reference date.
     */
    private DateType dateType;

    /**
     * Constructs an initially empty citation date.
     */
    public DefaultCitationDate() {
        date = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source
     * @since 2.4
     */
    public DefaultCitationDate(final CitationDate source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (date == 0 && source.getDate() == null) {
                date = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Constructs a citation date initialized to the given date.
     *
     * @param date     The reference date for the cited resource.
     * @param dateType The event used for reference date.
     */
    public DefaultCitationDate(final Date date, final DateType dateType) {
        this(); // Initialize the date field.
        setDate    (date);
        setDateType(dateType);
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
    public static DefaultCitationDate castOrCopy(final CitationDate object) {
        return (object == null) || (object instanceof DefaultCitationDate)
                ? (DefaultCitationDate) object : new DefaultCitationDate(object);
    }

    /**
     * Returns the reference date for the cited resource.
     */
    @Override
    @XmlElement(name = "date", required = true)
    public synchronized Date getDate() {
        return (date != Long.MIN_VALUE) ? new Date(date) : null;
    }

    /**
     * Sets the reference date for the cited resource.
     *
     * @param newValue The new date.
     */
    public synchronized void setDate(final Date newValue) {
        checkWritePermission();
        date = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the event used for reference date.
     */
    @Override
    @XmlElement(name = "dateType", required = true)
    public synchronized DateType getDateType() {
        return dateType;
    }

    /**
     * Sets the event used for reference date.
     *
     * @param newValue The new event.
     */
    public synchronized void setDateType(final DateType newValue) {
        checkWritePermission();
        dateType = newValue;
    }
}
