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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour colorModeEnumType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="colorModeEnumType">
 *   &lt;restriction base="{http://www.opengis.net/kml/2.2}enumBaseType">
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="random"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "colorModeEnumType", namespace = "http://www.opengis.net/kml/2.2")
@XmlEnum
public enum ColorModeEnumType {

    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("random")
    RANDOM("random");
    private final String value;

    ColorModeEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorModeEnumType fromValue(String v) {
        for (ColorModeEnumType c: ColorModeEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
