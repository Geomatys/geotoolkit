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
 * <p>Classe Java pour StatusValueEnumerationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusValueEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REJECTED"/>
 *     &lt;enumeration value="POTENTIAL"/>
 *     &lt;enumeration value="PLANNED"/>
 *     &lt;enumeration value="FAILED"/>
 *     &lt;enumeration value="CANCELLED"/>
 *     &lt;enumeration value="ACQUIRED"/>
 *     &lt;enumeration value="ARCHIVED"/>
 *     &lt;enumeration value="QUALITYDEGRADED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "StatusValueEnumerationType")
@XmlEnum
public enum StatusValueEnumerationType {


    /**
     * Data acquisition has been rejected for whatever reason
     *
     */
    REJECTED,

    /**
     * Potential data acquisition
     *
     */
    POTENTIAL,

    /**
     * Data acquisition planned
     *
     */
    PLANNED,

    /**
     * Data acquisition failed
     *
     */
    FAILED,

    /**
     * Data acquisition cancelled
     *
     */
    CANCELLED,

    /**
     * Data acquired
     *
     */
    ACQUIRED,

    /**
     * Data acquisition archived
     *
     */
    ARCHIVED,

    /**
     * Data acquisition with known quality problems.
     * Deprecated.
     *
     */
    QUALITYDEGRADED;

    public String value() {
        return name();
    }

    public static StatusValueEnumerationType fromValue(String v) {
        return valueOf(v);
    }

}
