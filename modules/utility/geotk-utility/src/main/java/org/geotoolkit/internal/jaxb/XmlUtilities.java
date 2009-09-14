/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
 */
package org.geotoolkit.internal.jaxb;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.factory.FactoryNotFoundException;


/**
 * Utilities methods related to XML.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
@Static
public final class XmlUtilities {
    /**
     * The factory for creating {@link javax.xml.datatype} objects.
     */
    private static DatatypeFactory factory;

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
    private static synchronized DatatypeFactory getDatatypeFactory() throws FactoryNotFoundException {
        if (factory == null) try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new FactoryNotFoundException(Errors.format(
                    Errors.Keys.FACTORY_NOT_FOUND_$1, DatatypeFactory.class), e);
        }
        return factory;
    }

    /**
     * Converts the given date to a XML gregorian calendar.
     *
     * @param  date The date to convert to a XML calendar, or {@code null}.
     * @return The XML calendar, or {@code null} if {@code date} was null.
     *
     * @since 3.03
     */
    public static XMLGregorianCalendar toXML(final Date date) {
        if (date != null) {
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return getDatatypeFactory().newXMLGregorianCalendar(calendar);
        }
        return null;
    }

    /**
     * Converts the given XML gregorian calendar to a date.
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
}
