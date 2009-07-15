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
package org.geotoolkit.filter.function.geometry;

import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;


public class GeomFromWKTFunction extends AbstractFunction {

    public GeomFromWKTFunction(final Expression expression) {
        super(GeometryFunctionFactory.GEOM_FROM_WKT, new Expression[]{expression}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        String arg0;

        try { // attempt to get value and perform conversion
            arg0 = (String) parameters.get(0).evaluate(feature, String.class); // extra
                                                                    // protection
                                                                    // for
                                                                    // strings
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function geomFromWKT argument #0 - expected type String");
        }

        return (StaticGeometry.geomFromWKT(arg0));
    }
}
