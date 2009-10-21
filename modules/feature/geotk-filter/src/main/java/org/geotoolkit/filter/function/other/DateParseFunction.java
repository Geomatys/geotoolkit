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
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;


/**
 * Parses a date from a string given a certain pattern (specified in the format accepted
 * by {@link SimpleDateFormat}}
 * @see SimpleDateFormat
 * @author Andrea Aime - TOPP
 *
 * @module pending
 */
public class DateParseFunction extends AbstractFunction {

    public DateParseFunction(final Expression expr1, final Expression expr2) {
        super(OtherFunctionFactory.DATE_PARSE, new Expression[]{expr1,expr2}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        String format;
        String date;

        try {
            // attempt to get value and perform conversion
            format  = parameters.get(0).evaluate(feature, String.class);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function dateParse argument #0 - expected type String");
        }

        try { // attempt to get value and perform conversion
            date = parameters.get(1).evaluate(feature, String.class);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function dateParse argument #1 - expected type String");
        }

        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(date);
        } catch(ParseException e) {
            throw new IllegalArgumentException("Invalid date, could not parse", e);
        }
    }



}
