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
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.TemporalCapabilities;
import org.opengis.filter.capability.TemporalOperand;
import org.opengis.filter.capability.TemporalOperators;

/**
 * Immutable temporal capabilities
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTemporalCapabilities implements TemporalCapabilities{

    private final Collection<TemporalOperand> operands;
    private final TemporalOperators operators;
    
    public DefaultTemporalCapabilities(final TemporalOperand[] operands, final TemporalOperators operators) {
        ensureNonNull("operands", operands);
        ensureNonNull("spatial operators", operators);
        if(operands.length == 0){
            throw new IllegalArgumentException("Operands must not be empty");
        }
        
        this.operands = UnmodifiableArrayList.wrap(operands);
        this.operators = operators;
    }
    
    @Override
    public Collection<TemporalOperand> getTemporalOperands() {
        return operands;
    }

    @Override
    public TemporalOperators getTemporalOperators() {
        return operators;
    }
    
}
