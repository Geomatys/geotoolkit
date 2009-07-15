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

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;


public class PointNFunction extends AbstractFunction {

    public PointNFunction(final Expression expr1, final Expression expr2) {
        super(GeometryFunctionFactory.POINT_N, new Expression[]{expr1,expr2}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        Geometry arg0;
        int arg1;

        try { // attempt to get value and perform conversion
            arg0 = (Geometry) parameters.get(0).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function pointN argument #0 - expected type Geometry");
        }

        try { // attempt to get value and perform conversion
            arg1 = ((Number) parameters.get(1).evaluate(feature)).intValue();
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function pointN argument #1 - expected type int");
        }

        return (StaticGeometry.pointN(arg0, arg1));
    }
}
