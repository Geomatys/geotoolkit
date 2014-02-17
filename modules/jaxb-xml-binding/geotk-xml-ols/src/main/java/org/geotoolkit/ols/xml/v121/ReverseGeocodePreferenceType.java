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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReverseGeocodePreferenceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReverseGeocodePreferenceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="StreetAddress"/>
 *     &lt;enumeration value="IntersectionAddress"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReverseGeocodePreferenceType")
@XmlEnum
public enum ReverseGeocodePreferenceType {

    @XmlEnumValue("StreetAddress")
    STREET_ADDRESS("StreetAddress"),
    @XmlEnumValue("IntersectionAddress")
    INTERSECTION_ADDRESS("IntersectionAddress");
    private final String value;

    ReverseGeocodePreferenceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReverseGeocodePreferenceType fromValue(String v) {
        for (ReverseGeocodePreferenceType c: ReverseGeocodePreferenceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
