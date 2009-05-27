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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultArithmeticOperators other = (DefaultArithmeticOperators) obj;
        if (this.simple != other.simple) {
            return false;
        }
        if (this.functions != other.functions && (this.functions == null || !this.functions.equals(other.functions))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.simple ? 1 : 0);
        hash = 53 * hash + (this.functions != null ? this.functions.hashCode() : 0);
        return hash;
    }

}
