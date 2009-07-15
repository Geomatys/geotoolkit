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


public class In2Function extends AbstractFunction {

    public In2Function(Expression expr1, Expression expr2, Expression expr3) {
        super("in2", new Expression[]{expr1,expr2,expr3}, null);
    }

    @Override
    public Object evaluate(Object feature) {
        Object arg0;
        Object arg1;
        Object arg2;

        try { // attempt to get value and perform conversion
            arg0 = (Object) parameters.get(0).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function in10 argument #0 - expected type Object");
        }

        try { // attempt to get value and perform conversion
            arg1 = (Object) parameters.get(1).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function in10 argument #1 - expected type Object");
        }

        try { // attempt to get value and perform conversion
            arg2 = (Object) parameters.get(2).evaluate(feature);
        } catch (Exception e) // probably a type error
        {
            throw new IllegalArgumentException(
                    "Filter Function problem for function in10 argument #2 - expected type Object");
        }
        return new Boolean(StaticGeometry.in2(arg0, arg1, arg2));
    }
}
