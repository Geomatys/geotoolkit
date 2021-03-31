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

import java.util.Objects;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;


public class TableFunction extends AbstractFunction {

    public TableFunction(final Expression ... exprs) {
        super(OtherFunctionFactory.TABLE, exprs, null);
    }

    @Override
    public Object apply(final Object feature) {
        final Object arg0 = parameters.get(0).apply(feature);
        for (int i=2; i<parameters.size(); i+=2) {
            try {
                // attempt to get value and perform conversion
                Object key = (Object) parameters.get(i).apply(feature);
                if (Objects.equals(arg0,key)) {
                    return parameters.get(i+1).apply(feature);
                }
            } catch (Exception e){
                // probably a type error
                throw new IllegalArgumentException("Filter Function problem for function in10 argument #"+ i+"- expected type Object");
            }
        }
        return  parameters.get(1).apply(feature);
    }
}
