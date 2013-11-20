/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.style.function;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;
import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Factory registering the default Symbology Encoding functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StyleFunctionFactory extends AbstractFunctionFactory{

    public static final String INTERPOLATE = DefaultInterpolate.NAME.getName();
    public static final String CATEGORIZE = DefaultCategorize.NAME.getName();

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static{
        FUNCTIONS.put(INTERPOLATE, DefaultInterpolate.class);
        FUNCTIONS.put(CATEGORIZE, DefaultCategorize.class);
    }

    public StyleFunctionFactory() {
        super("style", FUNCTIONS);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(final String name, final Literal fallback, final Expression... parameters) throws IllegalArgumentException {

        if(name.equals(INTERPOLATE))        return new DefaultInterpolate(parameters);
        else if(name.equals(CATEGORIZE))    return new DefaultCategorize(parameters);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
