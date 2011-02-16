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
import java.util.TimeZone;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.XmlUtilities;

import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;


/**
 * JAXB adapter wrapping the date value in a {@code <gco:Date>} or {@code <gco:DateTime>} element,
 * for ISO-19139 compliance. Only one of {@code Date} or {@code DateTime} field shall be non-null.
 * At marshalling time, the choice is performed depending on whatever the given date contains
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
     *
     * @todo Replace by {@code XmlGregorianCalendar} type and set the timezone
     *       explicitely according the value provided by {@link MarshalContext}.
     */
    @XmlElement(name = "DateTime")
    private Date dateTime;

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
     * @param hasTime {@code true} for using the {@code "DateTime"} field
     *        or {@code false} for the {@code "Date"} field.
     */
    GO_DateTime(final Date date, final boolean hasTime) {
        if (hasTime) {
            dateTime = date;
        } else if (date != null) {
            final XMLGregorianCalendar gc = XmlUtilities.toXML(date);
            gc.setHour(FIELD_UNDEFINED);
            gc.setMinute(FIELD_UNDEFINED);
            gc.setSecond(FIELD_UNDEFINED);
            gc.setMillisecond(FIELD_UNDEFINED);
            gc.setTimezone(FIELD_UNDEFINED);
            this.date = gc;
        }
    }

    /**
     * Returns the current date, or {@code null} if none. IF both fields are defined,
     * then {@link #dateTime} has precedence since it is assumed more accurate.
     */
    final Date getDate() {
        if (dateTime == null && date != null) {
            return XmlUtilities.toDate(date);
        } else {
            return dateTime;
        }
    }

    /**
     * Converts a date read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
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
     * file or stream. JAXB calls automatically this method at marshalling time.
     * The use of {@code <gco:Date>} or {@code <gco:DateTime>} is determined automatically.
     *
     * @param value The date value.
     * @return The adapter for this date.
     */
    @Override
    public GO_DateTime marshal(final Date value) {
        if (value == null) {
            return null;
        }
        long time = value.getTime();
        final TimeZone tz = MarshalContext.getTimeZone();
        if (tz != null) {
            time += tz.getOffset(time);
        }
        return new GO_DateTime(value, time % (24*60*60*1000L) != 0);
    }
}
