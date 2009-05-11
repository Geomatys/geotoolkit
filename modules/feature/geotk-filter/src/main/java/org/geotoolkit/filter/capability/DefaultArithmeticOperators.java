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

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.Functions;

/**
 * Immutable arithmetic operators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultArithmeticOperators implements ArithmeticOperators {

    private final boolean simple;
    private final Functions functions;

    public DefaultArithmeticOperators(boolean simple, Functions functions) {
        if(functions == null){
            throw new NullPointerException("Functions can not be null");
        }
        this.simple = simple;
        this.functions = functions;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasSimpleArithmetic() {
        return simple;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Functions getFunctions() {
        return functions;
    }

}
