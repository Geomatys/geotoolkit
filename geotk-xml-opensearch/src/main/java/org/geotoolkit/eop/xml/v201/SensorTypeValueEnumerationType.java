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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour SensorTypeValueEnumerationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="SensorTypeValueEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LIMB"/>
 *     &lt;enumeration value="RADAR"/>
 *     &lt;enumeration value="OPTICAL"/>
 *     &lt;enumeration value="ALTIMETRIC"/>
 *     &lt;enumeration value="ATMOSPHERIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "SensorTypeValueEnumerationType")
@XmlEnum
public enum SensorTypeValueEnumerationType {


    /**
     * Limb Looking/Sounding Sensor
     *
     */
    LIMB,

    /**
     * Radar Sensor
     *
     */
    RADAR,

    /**
     * Optical Sensor
     *
     */
    OPTICAL,

    /**
     * Altimetric Sensor
     *
     */
    ALTIMETRIC,

    /**
     * Atmospheric Sensor
     *
     */
    ATMOSPHERIC;

    public String value() {
        return name();
    }

    public static SensorTypeValueEnumerationType fromValue(String v) {
        return valueOf(v);
    }

}
