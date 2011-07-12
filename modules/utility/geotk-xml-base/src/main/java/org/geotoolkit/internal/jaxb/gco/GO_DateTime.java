/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.internal.jaxb.XmlUtilities;

import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;


/**
 * JAXB adapter wrapping the date value in a {@code <gco:Date>} or {@code <gco:DateTime>} element,
 * for ISO-19139 compliance. Only one of {@code Date} or {@code DateTime} field shall be non-null.
 * At marshaling time, the choice is performed depending on whatever the given date contains
 * hour, minute or seconds information different than zero.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class GO_DateTime extends XmlAdapter<GO_DateTime, Date> {
    /**
     * The date and time value using the {@code code "DateTime"} name.
     * Only one of {@code date} and {@link #dateTime} shall be non-null.
     */
    @XmlElement(name = "DateTime")
    private XMLGregorianCalendar dateTime;

    /**
     * The date value using the {@code "Date"} name, used when there is no
     * hour, minutes or seconds to format.
     */
    @XmlElement(name = "Date")
    private XMLGregorianCalendar date;

    /**
     * Empty constructor for JAXB only.
     */
    public GO_DateTime() {
    }

    /**
     * Builds a wrapper for the given {@link Date}.
     *
     * @param date The date to marshal, or {@code null} for formating only an empty element.
     * @param auto {@code true} for detecting whatever using the {@code "DateTime"} field,
     *        or {@code false} for using {@code "Date"} field in every cases.
     */
    GO_DateTime(final Date date, boolean auto) {
        if (date != null) {
            final XMLGregorianCalendar gc = XmlUtilities.toXML(date);
            if (auto && gc.getHour() == 0 && gc.getMinute() == 0 &&
                    gc.getSecond() == 0 && gc.getMillisecond() == 0)
            {
                auto = false;
            }
            if (auto) {
                if (gc.getMillisecond() == 0) {
                    gc.setMillisecond(FIELD_UNDEFINED);
                }
                dateTime = gc;
            } else {
                gc.setHour(FIELD_UNDEFINED);
                gc.setMinute(FIELD_UNDEFINED);
                gc.setSecond(FIELD_UNDEFINED);
                gc.setMillisecond(FIELD_UNDEFINED);
                gc.setTimezone(FIELD_UNDEFINED);
                this.date = gc;
            }
        }
    }

    /**
     * Returns the current date, or {@code null} if none. IF both fields are defined,
     * then {@link #dateTime} has precedence since it is assumed more accurate.
     */
    final Date getDate() {
        return XmlUtilities.toDate(dateTime != null ? dateTime : date);
    }

    /**
     * Converts a date read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshaling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@linkplain Date date} which represents the metadata value.
     */
    @Override
    public Date unmarshal(final GO_DateTime value) {
        return (value != null) ? value.getDate() : null;
    }

    /**
     * Converts the {@linkplain Date date} to the object to be marshalled in a XML
     * file or stream. JAXB calls automatically this method at marshaling time.
     * The use of {@code <gco:Date>} or {@code <gco:DateTime>} is determined automatically.
     *
     * @param value The date value.
     * @return The adapter for this date.
     */
    @Override
    public GO_DateTime marshal(final Date value) {
        return (value != null) ? new GO_DateTime(value, true) : null;
    }
}
