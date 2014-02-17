/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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


public class GetZFunction extends AbstractFunction {

    public GetZFunction(final Expression expression) {
        super(GeometryFunctionFactory.GET_Z, new Expression[]{expression}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        Geometry  arg0;


        try{  //attempt to get value and perform conversion
            arg0 = (Geometry) parameters.get(0).evaluate(feature);
        }
        catch (Exception e) // probably a type error
        {
              throw new IllegalArgumentException("Filter Function problem for function getZ argument #0 - expected type Geometry");
        }

        return Double.valueOf(arg0.getCentroid().getCoordinate().z);

    }
}


