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

import org.opengis.filter.Not;

/**
 *
 * @author guilhem
 * @module
 */
public class NotType extends UnaryLogicOpType implements Not {

    /**
     * An empty constructor used by JAXB
     */
     public NotType() {

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

}
