/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.citygml.xml.v100.appearance;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WrapModeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WrapModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="wrap"/>
 *     &lt;enumeration value="mirror"/>
 *     &lt;enumeration value="clamp"/>
 *     &lt;enumeration value="border"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WrapModeType")
@XmlEnum
public enum WrapModeType {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("wrap")
    WRAP("wrap"),
    @XmlEnumValue("mirror")
    MIRROR("mirror"),
    @XmlEnumValue("clamp")
    CLAMP("clamp"),
    @XmlEnumValue("border")
    BORDER("border");
    private final String value;

    WrapModeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WrapModeType fromValue(String v) {
        for (WrapModeType c: WrapModeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
