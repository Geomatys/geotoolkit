/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.temporal.precision;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Truncates a date on the given precision.
 * For example the date '2007-04-01 12:45:26.152' truncated on {@link DatePrecision#DAY}
 * will return '2007-04-01'.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class TruncatedDateFormat extends DateFormat {
    private final SimpleDateFormat format;

    public TruncatedDateFormat(final String pattern, final DatePrecision precision) {
        String finalPattern = pattern;
        if (finalPattern.indexOf(precision.pattern) != -1) {
            final DatePrecision[] precisions = DatePrecision.values();
            //final int nbDatePrecision = precisions.length;
            for (int i=precision.ordinal() - 1; i>=0; i--) {
                finalPattern = finalPattern.replaceAll("(\\W)*("+ precisions[i].pattern +")+(\\W)*", "");
            }
        }
        format = new SimpleDateFormat(finalPattern);
    }

    @Override
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        return format.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public Date parse(final String source, final ParsePosition pos) {
        return format.parse(source, pos);
    }

}
