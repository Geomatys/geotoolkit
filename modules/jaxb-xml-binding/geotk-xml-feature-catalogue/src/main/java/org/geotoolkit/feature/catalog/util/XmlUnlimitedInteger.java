/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.catalog.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author guilhem
 * @module
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "value"
})
@XmlRootElement(name = "UnlimitedInteger")
public class XmlUnlimitedInteger {

    @XmlValue
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer value;

    @XmlAttribute
    private Boolean isInfinite;

    @XmlAttribute(name = "nil", namespace="http://www.w3.org/2001/XMLSchema-instance")
    protected Boolean nil;

    public XmlUnlimitedInteger() {
    }

    public XmlUnlimitedInteger(final Integer multiplicity) {
        if (multiplicity != null) {
            this.isInfinite =  (Integer.MAX_VALUE == multiplicity);
            if (!isInfinite) {
                this.value = multiplicity;
            }
        } else {
            this.nil = true;
            this.isInfinite = false;
        }
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

    public Boolean isInfinite() {
        return isInfinite;
    }

    @Override
    public String toString() {
        return "[XmlUnlimitedInteger] is infinite: " + isInfinite + " nil: " + nil + " value: " + value;
    }

}
