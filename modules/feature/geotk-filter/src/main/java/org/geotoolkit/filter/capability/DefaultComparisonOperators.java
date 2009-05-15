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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.Operator;

/**
 * Immutable comparison operators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultComparisonOperators implements ComparisonOperators {

    private final Map<String,Operator> operators = new HashMap<String,Operator>();

    public DefaultComparisonOperators(Operator[] operators) {
        if(operators == null || operators.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        for(Operator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Operator> getOperators() {
        return operators.values();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Operator getOperator(String name) {
        return operators.get(name);
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
        final DefaultComparisonOperators other = (DefaultComparisonOperators) obj;
        if (this.operators != other.operators && (this.operators == null || !this.operators.equals(other.operators))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.operators != null ? this.operators.hashCode() : 0);
        return hash;
    }
    
}
