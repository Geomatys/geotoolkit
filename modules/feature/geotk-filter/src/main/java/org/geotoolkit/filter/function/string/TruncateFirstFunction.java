/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.filter.function.string;

import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.filter.function.geometry.StaticGeometry;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TruncateFirstFunction extends AbstractFunction {

    public TruncateFirstFunction(final Expression expression, final Expression lenghtExp) {
        super(StringFunctionFactory.TRUNCATE_FIRST, new Expression[]{expression,lenghtExp}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        String arg0;

        try {
            // attempt to get value and perform conversion
            arg0 = parameters.get(0).evaluate(feature, String.class);
            // extra protection forstrings
        } catch (Exception e) {
            // probably a type error
            throw new IllegalArgumentException(
                    "Filter Function problem for function strTruncate argument #0 - expected type String");
        }

        final int lenght = parameters.get(1).evaluate(feature, Integer.class);

        return StaticGeometry.strTruncateFirst(arg0, lenght);
    }
}
