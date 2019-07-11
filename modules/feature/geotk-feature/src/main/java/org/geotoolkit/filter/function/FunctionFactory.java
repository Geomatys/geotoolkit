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

import java.util.Set;
import org.apache.sis.internal.feature.FunctionRegister;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Function factories are capable to create function given
 * a specific number and order of expressions.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface FunctionFactory extends FunctionRegister {

    /**
     * Factory name.
     * @return String
     */
    String getIdentifier();

    /**
     * @return Names of the created functions.
     */
    Set<String> getNames();

    /**
     * Create a function with the given parameters.
     *
     * @param fallback : fallback literal or null
     * @param parameters : list of parameters
     * @return Function
     * @throws java.lang.IllegalArgumentException if some arguments are missing or incorrect
     */
    Function createFunction(String name,Literal fallback, Expression ... parameters) throws IllegalArgumentException;

    /**
     * Get a description of the function parameters and results.
     *
     * @param name
     * @return OperationType
     * @throws IllegalArgumentException
     */
    ParameterDescriptorGroup describeFunction(String name) throws IllegalArgumentException;

}
