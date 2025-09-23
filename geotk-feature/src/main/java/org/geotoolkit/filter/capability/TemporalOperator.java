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
import java.util.List;
import org.apache.sis.util.internal.shared.UnmodifiableArrayList;
import org.opengis.util.CodeList;

/**
 * Immutable temporal operator.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
public class TemporalOperator extends Operator {

    private final List<CodeList<?>> operands;

    /** For JAXB. */
    protected TemporalOperator(final String name) {
        super(name);
        operands = Collections.emptyList();
    }

    public TemporalOperator(final String name, final CodeList<?>[] operands) {
        super(name);
        if(operands == null || operands.length == 0){
            throw new IllegalArgumentException("Operands list can not be null or empty");
        }
        //use a threadsafe optimized immutable list
        this.operands = UnmodifiableArrayList.wrap(operands.clone());
    }

    public Collection<CodeList<?>> getTemporalOperands() {
        return operands;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final TemporalOperator other = (TemporalOperator) obj;
        return operands.equals(other.operands);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + operands.hashCode();
    }
}
