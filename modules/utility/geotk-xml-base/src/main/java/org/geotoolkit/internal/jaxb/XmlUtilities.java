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
 */
package org.geotoolkit.internal.jaxb;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.factory.FactoryNotFoundException;

import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;


/**
 * Utilities methods related to XML.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class XmlUtilities extends Static {
    /**
     * The factory for creating {@link javax.xml.datatype} objects.
     */
    private static DatatypeFactory factory;

    /**
     * The Gregorian calendar to use for {@link #printDateTime}.
     *
     * @since 3.06
     */
    private static final ThreadLocal<Calendar> CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.CANADA);
        }
    };

    /**
     * Do not allow instantiation of this class.
     */
    private XmlUtilities() {
    }

    /**
     * Returns the factory for creating {@link javax.xml.datatype} objects.
     *
     * @return The factory (never {@code null}).
     * @throws FactoryNotFoundException If the factory has not been found.
     *
     * @since 3.03
     */
    public static synchronized DatatypeFactory getDatatypeFactory() throws FactoryNotFoundException {
        if (factory == null) try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new FactoryNotFoundException(Errors.format(
                    Errors.Keys.FACTORY_NOT_FOUND_1, DatatypeFactory.class), e);
        }
        return factory;
    }

    /**
     * Trims the time components of the given calendar if their values are zero, or leave
     * them unchanged otherwise (except for milliseconds). More specifically:
     * <p>
     * <ul>
     *   <li>If the {@code force} argument is {@code false}, then:
     *     <ul>
     *       <li>If every time components (hour, minute, seconds and milliseconds) are zero, set
     *           them to {@code FIELD_UNDEFINED} in order to prevent them from being formatted
     *           at XML marshalling time. Then returns {@code true}.</li>
     *       <li>Otherwise returns {@code false}. But before doing so, still set the milliseconds
     *           to {@code FIELD_UNDEFINED} if its value was 0.</li>
     *     </ul></li>
     *   <li>Otherwise (if the {@code force} argument is {@code false}), then the temporal
     *       part is set to {@code FIELD_UNDEFINED} unconditionally and this method returns
     *       {@code true}.</li>
     * </ul>
     * <p>
     * <strong>WARNING: The timezone information may be lost!</strong> This method is used mostly
     * when the Gregorian Calendar were created from a {@link Date}, in which case we don't know
     * if the time is really 0 or just unspecified. This method should be invoked only when we
     * want to assume that a time of zero means "unspecified".
     * <p>
     * This method should be deprecated after we implemented ISO 19108 in Geotk.
     *
     * @param  gc The date to modify in-place.
     * @param  force {@code true} for forcing the temporal components to be removed without any check.
     * @return {@code true} if the time part has been completely removed, {@code false} otherwise.
     *
     * @since 3.20
     */
    public static boolean trimTime(final XMLGregorianCalendar gc, final boolean force) {
        if (force || gc.getMillisecond() == 0) {
            gc.setMillisecond(FIELD_UNDEFINED);
            if (force || (gc.getHour() == 0 && gc.getMinute() == 0 && gc.getSecond() == 0)) {
                gc.setHour(FIELD_UNDEFINED);
                gc.setMinute(FIELD_UNDEFINED);
                gc.setSecond(FIELD_UNDEFINED);
                gc.setTimezone(FIELD_UNDEFINED);
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the given date to a XML Gregorian calendar using the locale and timezone
     * from the current {@linkplain MarshalContext marshalling context}.
     *
     * @param  date The date to convert to a XML calendar, or {@code null}.
     * @return The XML calendar, or {@code null} if {@code date} was null.
     *
     * @since 3.03
     */
    public static XMLGregorianCalendar toXML(final Date date) {
        if (date != null) {
            final GregorianCalendar calendar = MarshalContext.createGregorianCalendar();
            calendar.setTime(date);
            return getDatatypeFactory().newXMLGregorianCalendar(calendar);
        }
        return null;
    }

    /**
     * Converts the given XML Gregorian calendar to a date.
     *
     * @param  xml The XML calendar to convert to a date, or {@code null}.
     * @return The date, or {@code null} if {@code xml} was null.
     *
     * @since 3.03
     */
    public static Date toDate(final XMLGregorianCalendar xml) {
        if (xml != null) {
            return xml.toGregorianCalendar().getTime();
        }
        return null;
    }

    /**
     * Parses a date value from a string.
     * This method should be used only for occasional parsing.
     *
     * @param  date The date to parse, or {@code null}.
     * @return The parsed date, or {@code null} if the given string was null.
     * @throws IllegalArgumentException If string parameter does not conform to
     *         XML Schema Part 2: Datatypes for {@code xsd:dateTime}.
     *
     * @see DatatypeConverter#parseDateTime(String)
     *
     * @since 3.06
     *
     * @deprecated Moved to SIS as {@link org.apache.sis.internal.jdk8.JDK8#parseDateTime(String, boolean)}.
     */
    @Deprecated
    public static Date parseDateTime(final String date) throws IllegalArgumentException {
        return org.apache.sis.internal.jdk8.JDK8.parseDateTime(date, false);
    }

    /**
     * Formats a date value in a string, assuming UTC timezone and Canada locale.
     * This method should be used only for occasional formatting.
     *
     * @param  date The date to format, or {@code null}.
     * @return The formatted date, or {@code null} if the given date was null.
     *
     * @see DatatypeConverter#printDateTime(Calendar)
     *
     * @since 3.06
     */
    public static String printDateTime(final Date date) {
        if (date == null) {
            return null;
        }
        final Calendar calendar = CALENDAR.get();
        calendar.setTime(date);
        return DatatypeConverter.printDateTime(calendar);
    }
}
