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


package org.geotoolkit.opt.xml.v201;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour PercentageCoverQuotationModeValueEnumerationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="PercentageCoverQuotationModeValueEnumerationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MANUAL"/>
 *     &lt;enumeration value="AUTOMATIC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "PercentageCoverQuotationModeValueEnumerationType")
@XmlEnum
public enum PercentageCoverQuotationModeValueEnumerationType {


    /**
     * Cloud cover percentage has been calculated manually
     *
     */
    MANUAL,

    /**
     * Cloud cover percentage has been calculated automatically
     *
     */
    AUTOMATIC;

    public String value() {
        return name();
    }

    public static PercentageCoverQuotationModeValueEnumerationType fromValue(String v) {
        return valueOf(v);
    }

}
