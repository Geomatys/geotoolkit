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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InterpolationMethodType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InterpolationMethodType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="nearest neighbor"/>
 *     &lt;enumeration value="bilinear"/>
 *     &lt;enumeration value="bicubic"/>
 *     &lt;enumeration value="lost area"/>
 *     &lt;enumeration value="barycentric"/>
 *     &lt;enumeration value="none"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InterpolationMethod")
@XmlEnum
public enum InterpolationMethod {

    @XmlEnumValue("nearest")
    NEAREST_NEIGHBOR("nearest"),
    @XmlEnumValue("linear")
    BILINEAR("linear"),
    @XmlEnumValue("cubic")
    BICUBIC("cubic"),
    @XmlEnumValue("lost area")
    LOST_AREA("lost area"),
    @XmlEnumValue("barycentric")
    BARYCENTRIC("barycentric"),

    /**
     * No interpolation. 
     * 
     */
    @XmlEnumValue("none")
    NONE("none");
    private final String value;

    InterpolationMethod(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InterpolationMethod fromValue(String v) {
        if (v!= null) {
            for (InterpolationMethod c: InterpolationMethod.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
        }
        return null;
    }
}
