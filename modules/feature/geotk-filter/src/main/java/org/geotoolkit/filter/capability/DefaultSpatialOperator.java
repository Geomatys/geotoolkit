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
import java.util.List;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialOperator;

/**
 * Immutable spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSpatialOperator extends DefaultOperator implements SpatialOperator{

    private final List<GeometryOperand> operands;

    public DefaultSpatialOperator(final String name, final GeometryOperand[] operands) {
        super(name);
        
        if(operands == null || operands.length == 0){
            throw new IllegalArgumentException("Operands list can not be null or empty");
        }

        //use a threadsafe optimized immutable list
        this.operands = UnmodifiableArrayList.wrap(operands.clone());
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
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultSpatialOperator other = (DefaultSpatialOperator) obj;
        if (this.operands != other.operands && (this.operands == null || !this.operands.equals(other.operands))) {
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
        hash = 13 * hash + (this.operands != null ? this.operands.hashCode() : 0);
        return hash;
    }

}
