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
import org.geotoolkit.filter.function.geometry.StaticGeometry;
import org.opengis.filter.expression.Expression;


public class InFunction extends AbstractFunction {

    public InFunction(final Expression ... exprs) {
        super(OtherFunctionFactory.IN, exprs, null);
    }

    @Override
    public Object evaluate(final Object feature) {

        final Object arg0;
        final Object[] args = new Object[parameters.size()-1];

        try {
            // attempt to get value and perform conversion
            arg0 = (Object) parameters.get(0).evaluate(feature);
        } catch (Exception e){
            // probably a type error
            throw new IllegalArgumentException("Filter Function problem for function in10 argument #0 - expected type Object");
        }

        for(int i=1;i<parameters.size();i++){
            try {
                // attempt to get value and perform conversion
                args[i-1] = (Object) parameters.get(i).evaluate(feature);
            } catch (Exception e){
                // probably a type error
                throw new IllegalArgumentException("Filter Function problem for function in10 argument #"+ i+"- expected type Object");
            }
        }

        return StaticGeometry.in(arg0, args);
    }
}
