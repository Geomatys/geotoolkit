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
package org.geotoolkit.filter.function.string;

import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.filter.function.other.StaticUtils;
import org.opengis.filter.Expression;


public class SubstringFunction extends AbstractFunction {

    public SubstringFunction(final Expression expr1, final  Expression expr2, final Expression expr3) {
        super(StringFunctionFactory.SUBSTRING, expr1, expr2, expr3);
    }

    @Override
    public Object apply(final Object feature) {
        final String[] args = stringValues(feature, 1);
        int arg1, arg2;
        try {
            arg1 = ((Number) parameters.get(1).apply(feature)).intValue();
            arg2 = ((Number) parameters.get(2).apply(feature)).intValue();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function strSubstring - expected type int");
        }
        return StaticUtils.strSubstring(args[0], arg1, arg2);
    }
}
