/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.function.other;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


/**
 * Parses a date from a string given a certain pattern (specified in the format accepted
 * by {@link SimpleDateFormat}}
 * @see SimpleDateFormat
 * @author Andrea Aime - TOPP
 *
 * @module
 */
public class DateParseFunction extends AbstractFunction {

    public DateParseFunction(final Expression expr1, final Expression expr2) {
        super(OtherFunctionFactory.DATE_PARSE, expr1, expr2);
    }

    @Override
    public Object apply(final Object feature) {
        String format = stringValue(feature, 0);
        String date = stringValue(feature, 1);
        DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        try {
            return dateFormat.parse(date);
        } catch(ParseException e) {
            throw new IllegalArgumentException("Invalid date, could not parse", e);
        }
    }
}
