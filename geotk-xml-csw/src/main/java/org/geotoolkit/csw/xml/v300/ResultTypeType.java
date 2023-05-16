/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour ResultTypeType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ResultTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="possible"/>
 *     &lt;enumeration value="available"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ResultTypeType")
@XmlEnum
public enum ResultTypeType {


    /**
     *
     *                   Returns the set of supported possible values
     *                   for the specified data component.
     *
     *
     */
    @XmlEnumValue("possible")
    POSSIBLE("possible"),

    /**
     *
     *                   Returns the set of available values for the
     *                   specified data component.  This is typically
     *                   a subset of the list of possible values.
     *
     *
     */
    @XmlEnumValue("available")
    AVAILABLE("available");
    private final String value;

    ResultTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResultTypeType fromValue(String v) {
        for (ResultTypeType c: ResultTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
