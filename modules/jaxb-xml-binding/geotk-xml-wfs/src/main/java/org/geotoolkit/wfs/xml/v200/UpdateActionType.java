/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateActionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UpdateActionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="replace"/>
 *     &lt;enumeration value="insertBefore"/>
 *     &lt;enumeration value="insertAfter"/>
 *     &lt;enumeration value="remove"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UpdateActionType")
@XmlEnum
public enum UpdateActionType {

    @XmlEnumValue("replace")
    REPLACE("replace"),
    @XmlEnumValue("insertBefore")
    INSERT_BEFORE("insertBefore"),
    @XmlEnumValue("insertAfter")
    INSERT_AFTER("insertAfter"),
    @XmlEnumValue("remove")
    REMOVE("remove");
    private final String value;

    UpdateActionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UpdateActionType fromValue(String v) {
        for (UpdateActionType c: UpdateActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
