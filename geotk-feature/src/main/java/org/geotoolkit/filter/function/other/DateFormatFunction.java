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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


/**
 * Formats a date into a string given a certain pattern (specified in the format accepted
 * by {@link SimpleDateFormat}}
 * @see SimpleDateFormat
 * @author Andrea Aime - TOPP
 */
public class DateFormatFunction extends AbstractFunction {

    public DateFormatFunction(final Expression expr1, final Expression expr2) {
        super(OtherFunctionFactory.DATE_FORMAT, expr1, expr2);
    }

    @Override
    public Object apply(final Object feature) {
        String format = stringValue(feature, 0);
        Date date;
        try { // attempt to get value and perform conversion
            date = (Date) parameters.get(1).apply(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function dateFormat argument #1 - expected type java.util.Date");
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
