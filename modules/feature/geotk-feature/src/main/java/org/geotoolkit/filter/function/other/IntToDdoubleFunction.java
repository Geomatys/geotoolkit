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
import org.opengis.filter.Expression;


public class IntToDdoubleFunction extends AbstractFunction {

    public IntToDdoubleFunction(final Expression expression) {
        super(OtherFunctionFactory.INT_TO_DDOUBLE, expression);
    }

    @Override
    public Object apply(final Object feature) {
        int arg0;

        try { // attempt to get value and perform conversion
            arg0 = ((Number) parameters.get(0).apply(feature)).intValue();
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function int2ddouble argument #0 - expected type int");
        }

        return StaticUtils.int2ddouble(arg0);
    }
}
