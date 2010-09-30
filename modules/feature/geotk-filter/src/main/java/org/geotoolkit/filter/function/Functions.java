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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * @module pending
 */
public class Functions {

    private static final Collection<FunctionFactory> FACTORIES = new ArrayList<FunctionFactory>();
    private static final Map<String,FunctionFactory> MAPPING = new HashMap<String,FunctionFactory>();

    static{
        final FactoryRegistry fr = new FactoryRegistry(FunctionFactory.class);
        final Iterator<FunctionFactory> factories = fr.getServiceProviders(FunctionFactory.class, null, null, null);

        while(factories.hasNext()){
            final FunctionFactory ff = factories.next();
            FACTORIES.add(ff);
            for(String name : ff.getNames()){
                MAPPING.put(name, ff);
            }
        }

    }

    private Functions(){}

    /**
     * @return map of all function factories, key is the factory name.
     */
    public static Collection<FunctionFactory> getFactories(){
        return Collections.unmodifiableCollection(FACTORIES);
    }

    /**
     * Create a function.
     *
     * @param name : name of the desired function
     * @param fallback : fallback literal or null
     * @param parameters : parameters of the function
     * @return Function or null if no factory are able to create this function
     */
    public static Function function(String name, Literal fallback, Expression ... parameters){
        final FunctionFactory ff = MAPPING.get(name);
        if(ff != null){
            return ff.createFunction(name,fallback, parameters);
        }
        return null;
    }

}
