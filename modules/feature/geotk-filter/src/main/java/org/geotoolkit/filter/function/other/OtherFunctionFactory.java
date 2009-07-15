/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;


/**
 * Factory registering the various functions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class OtherFunctionFactory implements FunctionFactory{

    public static final String EXPRESSION_VALUE_LENGHT  = "length";
    public static final String PROPERTY_EXISTS          = "PropertyExists";
    private static final String[] NAMES;

    static {
        NAMES = new String[] {
                    EXPRESSION_VALUE_LENGHT,
                    PROPERTY_EXISTS};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getNames() {
        return NAMES;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {

        if(name.equals(EXPRESSION_VALUE_LENGHT))   return new LengthFunction((PropertyName) parameters[0]);
        if(name.equals(PROPERTY_EXISTS))           return new PropertyExistsFunction(parameters[0]);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
