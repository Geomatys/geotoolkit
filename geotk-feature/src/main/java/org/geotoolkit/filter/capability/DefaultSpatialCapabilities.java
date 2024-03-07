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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.apache.sis.util.iso.Names;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;

/**
 * Immutable spatial capabilities
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultSpatialCapabilities implements SpatialCapabilities {

    private final List<GeometryOperand> operands;
    private final SpatialOperators operators;

    public DefaultSpatialCapabilities(final GeometryOperand[] operands, final SpatialOperators operators) {
        ensureNonNull("operands", operands);
        ensureNonNull("spatial operators", operators);
        if(operands.length == 0){
            throw new IllegalArgumentException("Operands must not be empty");
        }

        this.operands = UnmodifiableArrayList.wrap(operands);
        this.operators = operators;
    }

    private static List<ScopedName> getGeometryOperands(final Collection<GeometryOperand> operands) {
        final LocalName scope = Names.createLocalName(null, null, "geotk");
        final List<ScopedName> names = new ArrayList<>(operands.size());
        for (final GeometryOperand op : operands) {
            names.add(Names.createScopedName(scope, ":", op.identifier()));
        }
        return names;
    }

    @Override
    public Collection<ScopedName> getGeometryOperands() {
        return getGeometryOperands(operands);
    }

    @Deprecated
    public Collection<GeometryOperand> getGeometryOperands2() {
        return operands;
    }

    @Override
    public Map<SpatialOperatorName, List<? extends ScopedName>> getSpatialOperators() {
        final Map<SpatialOperatorName, List<? extends ScopedName>> names = new HashMap<>();
        for (final SpatialOperator op : operators.getOperators()) {
            final String name = op.getName();
            final SpatialOperatorName key = SpatialOperatorName.valueOf(SpatialOperatorName.class,
                    (c) -> name.equalsIgnoreCase(c.identifier()), null);
            if (key != null) {
                names.put(key, getGeometryOperands(op.getGeometryOperands()));
            }
        }
        return names;
    }

    @Deprecated
    public SpatialOperators getSpatialOperators2() {
        return operators;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultSpatialCapabilities other = (DefaultSpatialCapabilities) obj;
        if (this.operands != other.operands && (this.operands == null || !this.operands.equals(other.operands))) {
            return false;
        }
        if (this.operators != other.operators && (this.operators == null || !this.operators.equals(other.operators))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.operands != null ? this.operands.hashCode() : 0);
        hash = 71 * hash + (this.operators != null ? this.operators.hashCode() : 0);
        return hash;
    }
}
