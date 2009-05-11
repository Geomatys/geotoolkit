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
import java.util.List;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperators;

/**
 * Immutable spatial capabilities
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSpatialCapabilities implements SpatialCapabilities{

    private final List<GeometryOperand> operands;
    private final SpatialOperators operators;

    public DefaultSpatialCapabilities(GeometryOperand[] operands, SpatialOperators operators) {
        if(operands == null || operands.length == 0){
            throw new IllegalArgumentException("Operands must not be null or empty");
        }
        if(operators == null){
            throw new NullPointerException("SpatialOperators can not be null");
        }

        this.operands = UnmodifiableArrayList.wrap(operands);
        this.operators = operators;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<GeometryOperand> getGeometryOperands() {
        return operands;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialOperators getSpatialOperators() {
        return operators;
    }

}
