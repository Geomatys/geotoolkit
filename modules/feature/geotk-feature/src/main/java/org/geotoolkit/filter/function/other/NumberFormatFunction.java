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

import java.text.DecimalFormat;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


/**
 * Formats a number into a string given a certain pattern (specified in the format accepted
 * by {@link DecimalFormat}}
 * @author Andrea Aime - OpenGeo
 *
 * @module
 */
public class NumberFormatFunction extends AbstractFunction {

    public NumberFormatFunction(final Expression expr1, final Expression expr2) {
        super(OtherFunctionFactory.NUMBER_FORMAT, expr1, expr2);
    }

    @Override
    public Object apply(final Object feature) {
        String format = stringValue(feature, 0);
        double number = doubleValue(feature, 1);
        DecimalFormat numberFormat = new DecimalFormat(format);
        return numberFormat.format(number);
    }
}
