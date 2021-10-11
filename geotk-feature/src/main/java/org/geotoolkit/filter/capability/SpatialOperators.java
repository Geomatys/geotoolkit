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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutalbe spatial operators.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class SpatialOperators {

    private final Map<String,SpatialOperator> operators;

    /** For JAXB. */
    protected SpatialOperators() {
        operators = Collections.emptyMap();
    }

    public SpatialOperators(final SpatialOperator[] operators) {
        if(operators == null || operators.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        this.operators = new HashMap<>();
        for(SpatialOperator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    public Collection<SpatialOperator> getOperators() {
        return operators.values();
    }

    public SpatialOperator getOperator(final String name) {
        return operators.get(name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SpatialOperators other = (SpatialOperators) obj;
        return operators.equals(other.operators);
    }

    @Override
    public int hashCode() {
        return (5*97) + operators.hashCode();
    }
}
