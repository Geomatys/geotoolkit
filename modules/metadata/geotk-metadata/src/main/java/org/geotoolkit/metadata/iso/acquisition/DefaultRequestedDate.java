/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.acquisition;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.acquisition.RequestedDate;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Range of date validity.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MI_RequestedDate_Type", propOrder={
    "requestedDateOfCollection",
    "latestAcceptableDate"
})
@XmlRootElement(name = "MI_RequestedDate")
public class DefaultRequestedDate extends MetadataEntity implements RequestedDate {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8884795189934200802L;

    /**
     * Preferred date and time of collection,
     * or {@link Long#MIN_VALUE} if none.
     */
    private long requestedDateOfCollection;

    /**
     * Latest date and time collection must be completed,
     * or {@link Long#MIN_VALUE} if none.
     */
    private long latestAcceptableDate;

    /**
     * Constructs an initially empty requested date.
     */
    public DefaultRequestedDate() {
        requestedDateOfCollection = Long.MIN_VALUE;
        latestAcceptableDate = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultRequestedDate(final RequestedDate source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (requestedDateOfCollection == 0 && source.getRequestedDateOfCollection() == null) {
                requestedDateOfCollection = Long.MIN_VALUE;
            }
            if (latestAcceptableDate == 0 && source.getLatestAcceptableDate() == null) {
                latestAcceptableDate = Long.MIN_VALUE;
            }
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
    public static DefaultRequestedDate castOrCopy(final RequestedDate object) {
        return (object == null) || (object instanceof DefaultRequestedDate)
                ? (DefaultRequestedDate) object : new DefaultRequestedDate(object);
    }

    /**
     * Returns the preferred date and time of collection.
     */
    @Override
    @XmlElement(name = "requestedDateOfCollection", required = true)
    public synchronized Date getRequestedDateOfCollection() {
        final long date = this.requestedDateOfCollection;
        return (date != Long.MIN_VALUE) ? new Date(date) : null;
    }

    /**
     * Sets the preferred date and time of collection.
     *
     * @param newValue The new requested date of collection value.
     */
    public synchronized void setRequestedDateOfCollection(final Date newValue) {
        checkWritePermission();
        requestedDateOfCollection = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the latest date and time collection must be completed.
     */
    @Override
    @XmlElement(name = "latestAcceptableDate", required = true)
    public synchronized Date getLatestAcceptableDate() {
        final long date = this.latestAcceptableDate;
        return (date != Long.MIN_VALUE) ? new Date(date) : null;
    }

    /**
     * Sets the latest date and time collection must be completed.
     *
     * @param newValue The new latest acceptable data value.
     */
    public synchronized void setLatestAcceptableDate(final Date newValue) {
        checkWritePermission();
        latestAcceptableDate = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }
}
