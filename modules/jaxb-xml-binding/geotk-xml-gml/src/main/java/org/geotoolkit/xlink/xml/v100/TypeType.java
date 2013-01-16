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

package org.geotoolkit.xlink.xml.v100;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="typeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="simple"/>
 *     &lt;enumeration value="extended"/>
 *     &lt;enumeration value="title"/>
 *     &lt;enumeration value="resource"/>
 *     &lt;enumeration value="locator"/>
 *     &lt;enumeration value="arc"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "typeType")
@XmlEnum
public enum TypeType {

    @XmlEnumValue("simple")
    SIMPLE("simple"),
    @XmlEnumValue("extended")
    EXTENDED("extended"),
    @XmlEnumValue("title")
    TITLE("title"),
    @XmlEnumValue("resource")
    RESOURCE("resource"),
    @XmlEnumValue("locator")
    LOCATOR("locator"),
    @XmlEnumValue("arc")
    ARC("arc");
    private final String value;

    TypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeType fromValue(String v) {
        for (TypeType c: TypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
