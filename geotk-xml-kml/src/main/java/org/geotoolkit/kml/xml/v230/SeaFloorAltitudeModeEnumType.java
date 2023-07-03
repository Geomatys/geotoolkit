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
 * <p>Classe Java pour seaFloorAltitudeModeEnumType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="seaFloorAltitudeModeEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="clampToSeaFloor"/>
 *     &lt;enumeration value="relativeToSeaFloor"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "seaFloorAltitudeModeEnumType", namespace = "http://www.opengis.net/kml/2.2")
@XmlEnum
public enum SeaFloorAltitudeModeEnumType {

    @XmlEnumValue("clampToSeaFloor")
    CLAMP_TO_SEA_FLOOR("clampToSeaFloor"),
    @XmlEnumValue("relativeToSeaFloor")
    RELATIVE_TO_SEA_FLOOR("relativeToSeaFloor");
    private final String value;

    SeaFloorAltitudeModeEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SeaFloorAltitudeModeEnumType fromValue(String v) {
        for (SeaFloorAltitudeModeEnumType c: SeaFloorAltitudeModeEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
