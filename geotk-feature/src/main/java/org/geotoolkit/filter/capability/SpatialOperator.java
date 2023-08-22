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
import java.util.List;
import org.apache.sis.util.internal.UnmodifiableArrayList;
import org.opengis.filter.capability.GeometryOperand;

/**
 * Immutable spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class SpatialOperator extends Operator {

    private final List<GeometryOperand> operands;

    protected SpatialOperator(final String name) {
        super(name);
        operands = Collections.emptyList();
    }

    public SpatialOperator(final String name, final GeometryOperand[] operands) {
        super(name);
        if (operands == null) {
            this.operands = Collections.emptyList();
        } else if (operands.length != 0) {
            this.operands = UnmodifiableArrayList.wrap(operands.clone());
        } else {
            throw new IllegalArgumentException("Operands list can not be empty");
        }
    }

    public Collection<GeometryOperand> getGeometryOperands() {
        return operands;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SpatialOperator other = (SpatialOperator) obj;
        return operands.equals(other.operands);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + operands.hashCode();
    }
}
