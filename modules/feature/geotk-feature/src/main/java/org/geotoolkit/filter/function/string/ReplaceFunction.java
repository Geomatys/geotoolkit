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


public class ReplaceFunction extends AbstractFunction {

    public ReplaceFunction(final Expression expr1, final Expression expr2, final Expression expr3, final Expression expr4) {
        super(StringFunctionFactory.REPLACE, expr1, expr2, expr3, expr4);
    }

    @Override
    public Object apply(final Object feature) {
        final String[] args = stringValues(feature, 3);
        Boolean arg3 = Boolean.FALSE;
        try {
            arg3 = (Boolean) parameters.get(3).apply(feature);
        } catch (Exception e) {
            // TODO: really ignore?
        }
        return StaticUtils.strReplace(args[0], args[1], args[2], arg3);
    }
}
