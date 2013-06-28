/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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

/**
 * 
 * @author Johann Sorel (Geomatys
 */
public class HypotFunction extends AbstractFunction {

    public HypotFunction(final Expression expression1,final Expression expression2) {
        super(MathFunctionFactory.HYPOT, new Expression[] {expression1,expression2}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        final Number number1 = parameters.get(0).evaluate(feature, Number.class);
        final Number number2 = parameters.get(1).evaluate(feature, Number.class);
        if (number1 == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function ceil argument #0 - expected type double");
        }
        if (number2 == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function ceil argument #1 - expected type double");
        }

        return Math.hypot(number1.doubleValue(),number2.doubleValue());
    }
}
