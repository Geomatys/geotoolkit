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


public class IsNullFunction extends AbstractFunction {

    public IsNullFunction(final Expression expression) {
        super(OtherFunctionFactory.IS_NULL, expression);
    }

    @Override
    public Object apply(final Object feature) {
        Object arg0 = parameters.get(0).apply(feature);
        return StaticUtils.isNull(arg0);
    }
}
