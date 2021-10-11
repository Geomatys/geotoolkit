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
package org.geotoolkit.ogc.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author guilhem
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Or")
public class OrType extends BinaryLogicOpType {
    /**
     * An empty constructor used by JAXB
     */
    public OrType() {
    }

    /**
     * Build a new Binary logic operator
     */
    public OrType(final Object... operators) {
        super(operators);

    }

    public OrType(final BinaryLogicOpType that) {
        super(that);
    }

    @Override
    public LogicOpsType getClone() {
        return new OrType(this);
    }

    @Override
    public LogicalOperatorName getOperatorType() {
        return LogicalOperatorName.OR;
    }

    @Override
    public String getOperator() {
        return "OR";
    }
}
