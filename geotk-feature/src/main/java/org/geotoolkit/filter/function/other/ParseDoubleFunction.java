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


public class ParseDoubleFunction extends AbstractFunction {

    public ParseDoubleFunction(final Expression expression) {
        super(OtherFunctionFactory.PARSE_DOUBLE, expression);
    }

    @Override
    public Object apply(final Object feature) {
        String arg0 = stringValue(feature, 0);
        return StaticUtils.parseDouble(arg0);
    }
}
