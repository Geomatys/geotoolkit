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

import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.util.Converters;
import org.opengis.filter.expression.Expression;


public class ConvertFunction extends AbstractFunction {

    public ConvertFunction(final Expression expr1, final Expression expr2) {
        super(OtherFunctionFactory.CONVERT, new Expression[]{expr1,expr2}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        try {
            Object arg = parameters.get(0).evaluate(feature);
            Class target = parameters.get(1).evaluate(feature, Class.class);
            return Converters.convert(arg, target);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function convert argument #1 - expected type Class");
        }
    }
}
