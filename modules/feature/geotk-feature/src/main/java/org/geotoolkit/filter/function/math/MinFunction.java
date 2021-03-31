/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.function.math;

import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


public class MinFunction extends AbstractFunction {

    public MinFunction(final Expression expression1, final Expression expression2) {
        super(MathFunctionFactory.MIN, expression1, expression2);
    }

    @Override
    public Object apply(final Object feature) {
        final Object number1 = parameters.get(0).apply(feature);
        final Object number2 = parameters.get(1).apply(feature);
        if (number1 instanceof Integer && number2 instanceof Integer) {
            return Math.min((Integer) number1, (Integer) number2);
        }
        if (number1 instanceof Double && number2 instanceof Double) {
            return Math.min((Double) number1, (Double) number2);
        }
        if (number1 instanceof Float && number2 instanceof Float) {
            return Math.min((Float) number1, (Float) number2);
        }
        if (number1 instanceof Long && number2 instanceof Long) {
            return Math.min((Long) number1, (Long) number2);
        }
        if (number1 instanceof Short && number2 instanceof Short) {
            return Math.min((Short) number1, (Short) number2);
        }
        throw new IllegalArgumentException(
                    "Filter Function problem for function abs argument #0 - expected type number");
    }
}

