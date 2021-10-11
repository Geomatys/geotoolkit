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


public class AbsFunction extends AbstractFunction {

    public AbsFunction(final Expression expression) {
        super(MathFunctionFactory.ABS, expression);
    }

    @Override
    public Object apply(final Object feature) {
        final Object number = parameters.get(0).apply(feature);
        if (number instanceof Integer) {
            return Math.abs((Integer) number);
        }
        if (number instanceof Double) {
            return Math.abs((Double) number);
        }
        if (number instanceof Float) {
            return Math.abs((Float) number);
        }
        if (number instanceof Long) {
            return Math.abs((Long) number);
        }
        if (number instanceof Short) {
            return Math.abs((Short) number);
        }
        throw new IllegalArgumentException(
                    "Filter Function problem for function abs argument #0 - expected type number");
    }
}
