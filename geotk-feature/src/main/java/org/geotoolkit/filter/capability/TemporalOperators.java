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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable temporal operators.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class TemporalOperators {

    private final Map<String,TemporalOperator> operators;

    /** For JAXB. */
    protected TemporalOperators() {
        operators = Collections.emptyMap();
    }

    public TemporalOperators(final TemporalOperator[] operators) {
        if(operators == null){
            throw new IllegalArgumentException("Operators must not be null");
        }
        this.operators = new HashMap<String, TemporalOperator>();
        for(TemporalOperator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    public Collection<TemporalOperator> getOperators() {
        return operators.values();
    }

    public TemporalOperator getOperator(final String name) {
        return operators.get(name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TemporalOperators other = (TemporalOperators) obj;
        return operators.equals(other.operators);
    }

    @Override
    public int hashCode() {
        return (5*97) + operators.hashCode();
    }
}
