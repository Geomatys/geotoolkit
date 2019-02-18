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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour AcquisitionTypeValueType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="AcquisitionTypeValueType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="CALIBRATION"/>
 *     &lt;enumeration value="NOMINAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AcquisitionTypeValueType")
@XmlEnum
public enum AcquisitionTypeValueType {


    /**
     * Indicates some other type of acquisition
     *
     */
    OTHER,

    /**
     * Indicates the acquisition is a calibration product
     *
     */
    CALIBRATION,

    /**
     * Indicates the acquisition is a nominal acquisition
     *
     */
    NOMINAL;

    public String value() {
        return name();
    }

    public static AcquisitionTypeValueType fromValue(String v) {
        return valueOf(v);
    }

}
