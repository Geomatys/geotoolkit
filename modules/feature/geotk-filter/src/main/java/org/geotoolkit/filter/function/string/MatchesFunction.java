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
import org.geotoolkit.filter.function.geometry.StaticGeometry;
import org.opengis.filter.expression.Expression;


public class MatchesFunction extends AbstractFunction {

    public MatchesFunction(final Expression expr1, final Expression expr2) {
        super(StringFunctionFactory.MATCHES, new Expression[]{expr1,expr2}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        String arg0;
        String arg1;

        try { // attempt to get value and perform conversion
            arg0 = parameters.get(0).evaluate(feature, String.class); // extra
                                                                    // protection
                                                                    // for
                                                                    // strings
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function strMatches argument #0 - expected type String");
        }

        try { // attempt to get value and perform conversion
            arg1 = parameters.get(1).evaluate(feature, String.class); // extra
                                                                    // protection
                                                                    // for
                                                                    // strings
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function strMatches argument #1 - expected type String");
        }

        return StaticGeometry.strMatches(arg0, arg1);
    }
}
