/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.filter.function.groovy;

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Factory registering groovy functions.
 * Groovy dependency is heavy, so it is provided scope.
 * Explicitly add the dependency if you which groovy functions.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GroovyFunctionFactory implements FunctionFactory{

    public static final String GROOVY = "groovy";

    private static final String[] NAMES;

    static{
        NAMES = new String[]{GROOVY};
    }

    @Override
    public String getIdentifier() {
        return GROOVY;
    }
    
    /**
     * Test if groovy shell is available.
     * @return true if available.
     */
    private static boolean isAvailable(){
        try{
            Class.forName("groovy.lang.GroovyShell");
            return true;
        }catch(ClassNotFoundException ex){
            return false;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getNames() {
        if(isAvailable()){
            return NAMES;
        }else{
            return new String[0];
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(final String name, final Literal fallback, final Expression... parameters) throws IllegalArgumentException {
        if(!isAvailable()){
            throw new IllegalArgumentException("Unknowed function name : "+ name);
        }
        
        if(name.equals(GROOVY)){
            try {
                return new GroovyFunction(parameters[0]);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Malformated groovy function : "+ ex.getMessage(), ex);
            }
        }

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
