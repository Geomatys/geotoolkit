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


public class CosFunction extends AbstractFunction {

    public CosFunction(final Expression expression) {
        super(DefaultMathFunctionFactory.COS, new Expression[] {expression}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        final Number number = parameters.get(0).evaluate(feature, Number.class);
        if (number == null) {
            throw new IllegalArgumentException(
                    "Filter Function problem for function cos argument #0 - expected type double");
        }

        return Math.cos(number.doubleValue());
    }
}
