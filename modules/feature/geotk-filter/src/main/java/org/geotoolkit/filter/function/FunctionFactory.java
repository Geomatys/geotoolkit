/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Function factories are capable to create function given
 * a specific number and order of expressions.
 * Each created function shall have the same name as it's parent factory.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface FunctionFactory {

    /**
     * @return Name of the created functions.
     */
    String getName();

    /**
     * Create a function with the given parameters.
     *
     * @param fallback : fallback literal or null
     * @param parameters : list of parameters
     * @return Function
     * @throws java.lang.IllegalArgumentException if some arguments are missing or incorrect
     */
    Function createFunction(Literal fallback, Expression ... parameters) throws IllegalArgumentException;

}
