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
package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.opengis.filter.capability.TemporalOperator;
import org.opengis.filter.capability.TemporalOperators;

/**
 * Immutable temporal operators.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTemporalOperators implements TemporalOperators {

    private final Map<String,TemporalOperator> operators = new HashMap<String, TemporalOperator>();

    public DefaultTemporalOperators(final TemporalOperator[] operators) {
        if(operators == null){
            throw new IllegalArgumentException("Operators must not be null");
        }
        for(TemporalOperator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<TemporalOperator> getOperators() {
        return operators.values();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalOperator getOperator(final String name) {
        return operators.get(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultTemporalOperators other = (DefaultTemporalOperators) obj;
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
        int hash = 5;
        hash = 97 * hash + (this.operators != null ? this.operators.hashCode() : 0);
        return hash;
    }

}
