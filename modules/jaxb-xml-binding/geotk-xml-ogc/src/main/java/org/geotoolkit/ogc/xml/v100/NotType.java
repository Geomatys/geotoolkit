/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.ogc.xml.v100;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.LogicalOperatorName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Not")
public class NotType extends UnaryLogicOpType {

    public NotType() {
    }

    @Override
    public LogicalOperatorName getOperatorType() {
        return LogicalOperatorName.NOT;
    }

    public NotType(final NotType that) {
        super(that);
    }

    @Override
    public LogicOpsType getClone() {
        return new NotType(this);
    }

    @Override
    public String getOperator() {
        return "Not";
    }

    @Override
    public List getOperands() {
        return Collections.singletonList(getChild());
    }

    @Override
    public boolean test(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
