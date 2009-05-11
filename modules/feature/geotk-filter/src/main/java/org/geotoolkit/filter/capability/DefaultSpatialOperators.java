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
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;

/**
 * Immutalbe spatial operators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSpatialOperators implements SpatialOperators {

    private final Map<String,SpatialOperator> operators = new HashMap<String, SpatialOperator>();

    public DefaultSpatialOperators(SpatialOperator[] operators) {
        if(operators == null || operators.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        for(SpatialOperator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<SpatialOperator> getOperators() {
        return operators.values();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperator getOperator(String name) {
        return operators.get(name);
    }

}
