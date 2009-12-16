/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.internal.jaxb.uom;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter wrapping the date value in a {@code <gco:DateTime>} element,
 * for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class DateTimeAdapter extends XmlAdapter<DateTimeAdapter, Date> {
    /**
     * The date value.
     */
    @XmlElement(name = "DateTime")
    private Date dateTime;

    /**
     * The date value using the {@code "Date"} name. This is used during unmarshalling
     * for compatibility with element using that name. Only one of {@code date} and
     * {@link #dateTime} shall be non-null.
     */
    @XmlElement(name = "Date")
    private Date date;

    /**
     * Empty constructor for JAXB only.
     */
    public DateTimeAdapter() {
    }

    /**
     * Builds an adapter for {@link Date}.
     *
     * @param date The date to marshall.
     */
    private DateTimeAdapter(final Date date) {
        this.dateTime = date;
    }

    /**
     * Converts a date read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@linkplain Date date} which represents the metadata value.
     */
    @Override
    public Date unmarshal(final DateTimeAdapter value) {
        if (value == null) {
            return null;
        }
        if (value.dateTime != null) {
           return value.dateTime;
        } else {
            return value.date;
        }
    }

    /**
     * Converts a {@linkplain Date date} to the object to be marshalled in a XML
     * file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The date value.
     * @return The adapter for this date.
     */
    @Override
    public DateTimeAdapter marshal(final Date value) {
        if (value == null) {
            return null;
        }
        return new DateTimeAdapter(value);
    }
}
