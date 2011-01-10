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

package org.geotoolkit.filter.function.javascript;

import javax.script.ScriptException;
import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Factory registering javascript functions.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JavaScriptFunctionFactory implements FunctionFactory{

    public static final String JAVASCRIPT = "javascript";

    private static final String[] NAMES;

    static{
        NAMES = new String[]{JAVASCRIPT};
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
    public Function createFunction(final String name, final Literal fallback, final Expression... parameters) throws IllegalArgumentException {

        if(name.equals(JAVASCRIPT)){
            try {
                return new JavaScriptFunction(parameters[0]);
            } catch (ScriptException ex) {
                throw new IllegalArgumentException("Malformated Javascript function : "+ ex.getMessage(), ex);
            }
        }

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
