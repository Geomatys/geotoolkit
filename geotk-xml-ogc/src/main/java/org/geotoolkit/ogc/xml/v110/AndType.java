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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "And")
public class AndType extends BinaryLogicOpType {
    /**
     * An empty constructor used by JAXB
     */
    public AndType() {
    }

    /**
     * Build a new Binary logic operator
     */
    public AndType(final Object... operators) {
        super(operators);

    }

    public AndType(final BinaryLogicOpType that) {
        super(that);
    }

    @Override
    public LogicOpsType getClone() {
        return new AndType(this);
    }

    @Override
    public LogicalOperatorName getOperatorType() {
        return LogicalOperatorName.AND;
    }

    @Override
    public String getOperator() {
        return "AND";
    }
}
