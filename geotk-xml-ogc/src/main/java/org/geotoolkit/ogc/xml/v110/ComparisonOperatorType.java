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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for ComparisonOperatorType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ComparisonOperatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LessThan"/>
 *     &lt;enumeration value="GreaterThan"/>
 *     &lt;enumeration value="LessThanEqualTo"/>
 *     &lt;enumeration value="GreaterThanEqualTo"/>
 *     &lt;enumeration value="EqualTo"/>
 *     &lt;enumeration value="NotEqualTo"/>
 *     &lt;enumeration value="Like"/>
 *     &lt;enumeration value="Between"/>
 *     &lt;enumeration value="NullCheck"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlEnum
public enum ComparisonOperatorType {

    @XmlEnumValue("LessThan")
    LESS_THAN("LessThan"),
    @XmlEnumValue("GreaterThan")
    GREATER_THAN("GreaterThan"),
    @XmlEnumValue("LessThanEqualTo")
    LESS_THAN_EQUAL_TO("LessThanEqualTo"),
    @XmlEnumValue("GreaterThanEqualTo")
    GREATER_THAN_EQUAL_TO("GreaterThanEqualTo"),
    @XmlEnumValue("EqualTo")
    EQUAL_TO("EqualTo"),
    @XmlEnumValue("NotEqualTo")
    NOT_EQUAL_TO("NotEqualTo"),
    @XmlEnumValue("Like")
    LIKE("Like"),
    @XmlEnumValue("Between")
    BETWEEN("Between"),
    @XmlEnumValue("NullCheck")
    NULL_CHECK("NullCheck");
    private final String value;

    ComparisonOperatorType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ComparisonOperatorType fromValue(final String v) {
        for (ComparisonOperatorType c: ComparisonOperatorType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }
}
