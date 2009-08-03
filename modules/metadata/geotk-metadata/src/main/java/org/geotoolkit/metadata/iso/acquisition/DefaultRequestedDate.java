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
package org.geotoolkit.metadata.iso.acquisition;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.acquisition.RequestedDate;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Range of date validity.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "requestedDateOfCollection",
    "latestAcceptableDate"
})
@XmlRootElement(name = "MI_RequestedDate")
public class DefaultRequestedDate extends MetadataEntity implements RequestedDate {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8884795189934200802L;

    /**
     * Preferred date and time of collection.
     */
    private Date requestedDateOfCollection;

    /**
     * Latest date and time collection must be completed.
     */
    private Date latestAcceptableDate;

    /**
     * Constructs an initially empty requested date.
     */
    public DefaultRequestedDate() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultRequestedDate(final RequestedDate source) {
        super(source);
    }

    /**
     * Returns the preferred date and time of collection.
     */
    @Override
    @XmlElement(name = "requestedDateOfCollection")
    public Date getRequestedDateOfCollection() {
        return requestedDateOfCollection;
    }

    /**
     * Sets the preferred date and time of collection.
     *
     * @param newValue The new requested date of collection value.
     */
    public synchronized void setRequestedDateOfCollection(final Date newValue) {
        checkWritePermission();
        requestedDateOfCollection = newValue;
    }

    /**
     * Returns the latest date and time collection must be completed.
     */
    @Override
    @XmlElement(name = "latestAcceptableDate")
    public Date getLatestAcceptableDate() {
        return latestAcceptableDate;
    }

    /**
     * Sets the latest date and time collection must be completed.
     *
     * @param newValue The new latest acceptable data value.
     */
    public synchronized void setLatestAcceptableDate(final Date newValue) {
        checkWritePermission();
        latestAcceptableDate = newValue;
    }
}
