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
import org.opengis.filter.expression.Expression;


public class MinFunction extends AbstractFunction {

    public MinFunction(final Expression expression1, final Expression expression2) {
        super("min", new Expression[] {expression1, expression2}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        final Number number1 = parameters.get(0).evaluate(feature, Number.class);
        if (number1 == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function min argument #0 - expected type double");
        }

        final Number number2 = parameters.get(1).evaluate(feature, Number.class);
        if (number2 == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function min argument #1 - expected type double");
        }
        if (number1 instanceof Integer && number2 instanceof Integer) {
            return Math.min(number1.intValue(), number2.intValue());
        }
        if (number1 instanceof Double && number2 instanceof Double) {
            return Math.min(number1.doubleValue(), number2.doubleValue());
        }
        if (number1 instanceof Float && number2 instanceof Float) {
            return Math.min(number1.floatValue(), number2.floatValue());
        }
        if (number1 instanceof Long && number2 instanceof Long) {
            return Math.min(number1.longValue(), number2.longValue());
        }
        if (number1 instanceof Short && number2 instanceof Short) {
            return Math.min(number1.shortValue(), number2.shortValue());
        }
        throw new IllegalArgumentException(
                    "Filter Function problem for function abs argument #0 - expected type number");
    }
}

