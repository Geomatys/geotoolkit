/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.eop.xml.v201;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour StatusSubTypeValueEnumerationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusSubTypeValueEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ON-LINE"/>
 *     &lt;enumeration value="OFF-LINE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "StatusSubTypeValueEnumerationType")
@XmlEnum
public enum StatusSubTypeValueEnumerationType {


    /**
     * Product available ON-LINE
     *
     */
    @XmlEnumValue("ON-LINE")
    ON_LINE("ON-LINE"),

    /**
     * Product not available ON-LINE
     *
     */
    @XmlEnumValue("OFF-LINE")
    OFF_LINE("OFF-LINE");
    private final String value;

    StatusSubTypeValueEnumerationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StatusSubTypeValueEnumerationType fromValue(String v) {
        for (StatusSubTypeValueEnumerationType c: StatusSubTypeValueEnumerationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
