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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.apache.sis.util.iso.Names;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.util.CodeList;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;

/**
 * Immutable temporal capabilities
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class DefaultTemporalCapabilities implements TemporalCapabilities {

    private final Collection<CodeList<?>> operands;
    private final TemporalOperators operators;

    public DefaultTemporalCapabilities(final CodeList<?>[] operands, final TemporalOperators operators) {
        ensureNonNull("operands", operands);
        ensureNonNull("temporal operators", operators);
        if(operands == null){
            throw new IllegalArgumentException("Operands must not be null");
        }

        this.operands = UnmodifiableArrayList.wrap(operands);
        this.operators = operators;
    }

    private static List<ScopedName> getTemporalOperands(final Collection<CodeList<?>> operands) {
        final LocalName scope = Names.createLocalName(null, null, "geotk");
        final List<ScopedName> names = new ArrayList<>(operands.size());
        for (final CodeList<?> op : operands) {
            names.add(Names.createScopedName(scope, ":", op.identifier()));
        }
        return names;
    }

    @Override
    public Collection<? extends ScopedName> getTemporalOperands() {
        return getTemporalOperands(operands);
    }

    public Collection<CodeList<?>> getTemporalOperands2() {
        return operands;
    }

    @Override
    public Map<TemporalOperatorName, List<? extends ScopedName>> getTemporalOperators() {
        final Map<TemporalOperatorName, List<? extends ScopedName>> names = new HashMap<>();
        for (final TemporalOperator op : operators.getOperators()) {
            final String name = op.getName();
            final TemporalOperatorName key = TemporalOperatorName.valueOf(TemporalOperatorName.class,
                    (c) -> name.equalsIgnoreCase(c.identifier()), null);
            if (key != null) {
                names.put(key, getTemporalOperands(op.getTemporalOperands()));
            }
        }
        return names;
    }

    public TemporalOperators getTemporalOperators2() {
        return operators;
    }
}
