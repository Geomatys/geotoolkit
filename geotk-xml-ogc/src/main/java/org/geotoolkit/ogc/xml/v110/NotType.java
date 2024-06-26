/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogc.xml.v110;

import java.util.Collections;
import java.util.List;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author guilhem
 */
public class NotType extends UnaryLogicOpType {
    /**
     * An empty constructor used by JAXB
     */
    public NotType() {
    }

    @Override
    public LogicalOperatorName getOperatorType() {
        return LogicalOperatorName.NOT;
    }

    /**
     * Build a new Unary logic operator of type NOT
     */
    public NotType(final Object operators) {
        super(operators);
    }

    public NotType(final UnaryLogicOpType that) {
        super(that);
    }

    @Override
    public LogicOpsType getClone() {
        return new NotType(this);
    }

    @Override
    public String getOperator() {
        return "NOT";
    }

    @Override
    public List getOperands() {
        return Collections.singletonList(getFilter());
    }
}
