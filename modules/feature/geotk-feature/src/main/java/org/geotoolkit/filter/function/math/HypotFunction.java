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
import org.opengis.filter.Expression;

/**
 *
 * @author Johann Sorel (Geomatys
 */
public class HypotFunction extends AbstractFunction {

    public HypotFunction(final Expression expression1,final Expression expression2) {
        super(MathFunctionFactory.HYPOT, expression1, expression2);
    }

    @Override
    public Object apply(final Object feature) {
        return Math.hypot(doubleValue(feature, 0), doubleValue(feature, 1));
    }
}
