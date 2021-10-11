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


public class ExpFunction extends AbstractFunction {

    public ExpFunction(final Expression expression) {
        super(MathFunctionFactory.EXP, expression);
    }

    @Override
    public Object apply(final Object feature) {
        return Math.exp(doubleValue(feature));
    }
}
