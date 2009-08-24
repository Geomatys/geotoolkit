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
 * JAXB adapter wrapping the date value in a {@code <gco:Date>} element,
 * for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class DateAdapter extends XmlAdapter<DateAdapter, Date> {
    /**
     * The date value.
     */
    @XmlElement(name = "Date")
    public Date date;

    /**
     * Empty constructor for JAXB only.
     */
    public DateAdapter() {
    }

    /**
     * Builds an adapter for {@link Date}.
     *
     * @param date The date to marshall.
     */
    DateAdapter(final Date date) {
        this.date = date;
    }

    /**
     * Converts a date read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@linkplain Date date} which represents the metadata value.
     */
    @Override
    public Date unmarshal(final DateAdapter value) {
        if (value == null) {
            return null;
        }
        return value.date;
    }

    /**
     * Converts the {@linkplain Date date} to the object to be marshalled in a XML
     * file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The date value.
     * @return The adapter for this date.
     */
    @Override
    public DateAdapter marshal(final Date value) {
        if (value == null) {
            return null;
        }
        return new DateAdapter(value);
    }
}
