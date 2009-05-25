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
 */
package org.geotoolkit.internal.jaxb;

import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.geotoolkit.lang.Static;


/**
 * Utilities methods related to XML.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Static
public final class XmlUtilities {
    /**
     * The format to use for parsing and formatting XML dates. The {@link Locale#CANADA}
     * is used because it is close to the US locale while using a more ISO-like date format.
     */
    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA);
            // TODO: port here the code from gt_modified.
            return format;
        }
    };

    /**
     * Do not allow instantiation of this class.
     */
    private XmlUtilities() {
    }

    /**
     * Returns the format to use for parsing and formatting XML dates.
     * This method returns a shared instance - <strong>do not modify!</strong>
     *
     * @return The format to use for parsing and formatting XML dates.
     */
    public static DateFormat getDateFormat() {
        return DATE_FORMAT.get();
    }
}
