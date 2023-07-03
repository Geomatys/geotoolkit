/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

package org.geotoolkit.kml.xml.v230;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour itemIconStateEnumType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="itemIconStateEnumType">
 *   &lt;restriction base="{http://www.opengis.net/kml/2.2}enumBaseType">
 *     &lt;enumeration value="open"/>
 *     &lt;enumeration value="closed"/>
 *     &lt;enumeration value="error"/>
 *     &lt;enumeration value="fetching0"/>
 *     &lt;enumeration value="fetching1"/>
 *     &lt;enumeration value="fetching2"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "itemIconStateEnumType", namespace = "http://www.opengis.net/kml/2.2")
@XmlEnum
public enum ItemIconStateEnumType {

    @XmlEnumValue("open")
    OPEN("open"),
    @XmlEnumValue("closed")
    CLOSED("closed"),
    @XmlEnumValue("error")
    ERROR("error"),
    @XmlEnumValue("fetching0")
    FETCHING_0("fetching0"),
    @XmlEnumValue("fetching1")
    FETCHING_1("fetching1"),
    @XmlEnumValue("fetching2")
    FETCHING_2("fetching2");
    private final String value;

    ItemIconStateEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ItemIconStateEnumType fromValue(String v) {
        for (ItemIconStateEnumType c: ItemIconStateEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
