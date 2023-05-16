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

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SpectralRangeValueEnumerationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="SpectralRangeValueEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="VISIBLE"/>
 *     &lt;enumeration value="UV"/>
 *     &lt;enumeration value="INFRARED"/>
 *     &lt;enumeration value="NEAR-INFRARED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "SpectralRangeValueEnumerationType")
@XmlEnum
public enum SpectralRangeValueEnumerationType {


    /**
     * Other spectral range
     *
     */
    OTHER("OTHER"),

    /**
     * Visible Range
     *
     */
    VISIBLE("VISIBLE"),

    /**
     * Ultra Violet Range
     *
     */
    UV("UV"),

    /**
     * Infrared range
     *
     */
    INFRARED("INFRARED"),

    /**
     * Near infrared Range
     *
     */
    @XmlEnumValue("NEAR-INFRARED")
    NEAR_INFRARED("NEAR-INFRARED");
    private final String value;

    SpectralRangeValueEnumerationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SpectralRangeValueEnumerationType fromValue(String v) {
        for (SpectralRangeValueEnumerationType c: SpectralRangeValueEnumerationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
