/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.wps.xml.v200;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ExecutionUnit {

    private Reference reference;
    private Object unit;

    public ExecutionUnit() {

    }

    public ExecutionUnit(Reference reference, String unit) {
        this.reference = reference;
        this.unit = unit;
    }

    public ExecutionUnit(org.geotoolkit.wps.json.ExecutionUnit unit) {
        if (unit != null) {
            this.unit = unit.getUnit();

            this.reference = new Reference(unit.getHref(),
                                           unit.getMimeType(),
                                           unit.getEncoding(),
                                           unit.getSchema());

        }
    }

    /**
     * @return the reference
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    /**
     * @return the unit
     */
    public Object getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(Object unit) {
        this.unit = unit;
    }
}
