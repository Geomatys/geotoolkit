/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geotoolkit.factory.FactoryRegistry;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Utility class to create functions.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Functions {

    private static final Map<String,FunctionFactory> FACTORIES = new HashMap<String,FunctionFactory>();

    static{
        final FactoryRegistry fr = new FactoryRegistry(FunctionFactory.class);
        final Iterator<FunctionFactory> factories = fr.getServiceProviders(FunctionFactory.class, null, null, null);

        while(factories.hasNext()){
            final FunctionFactory ff = factories.next();
            for(String name : ff.getNames()){
                FACTORIES.put(name, ff);
            }
        }

    }

    private Functions(){}

    /**
     * Create a function.
     *
     * @param name : name of the desired function
     * @param fallback : fallback literal or null
     * @param parameters : parameters of the function
     * @return Function or null if no factory are able to create this function
     */
    public static final Function function(String name, Literal fallback, Expression ... parameters){
        final FunctionFactory ff = FACTORIES.get(name);
        if(ff != null){
            return ff.createFunction(name,fallback, parameters);
        }
        return null;
    }

}
