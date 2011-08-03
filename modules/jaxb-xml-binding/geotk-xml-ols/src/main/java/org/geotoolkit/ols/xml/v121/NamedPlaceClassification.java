/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NamedPlaceClassification.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NamedPlaceClassification">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CountrySubdivision"/>
 *     &lt;enumeration value="CountrySecondarySubdivision"/>
 *     &lt;enumeration value="Municipality"/>
 *     &lt;enumeration value="MunicipalitySubdivision"/>
 *     &lt;enumeration value="choume-banchi-go"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NamedPlaceClassification")
@XmlEnum
public enum NamedPlaceClassification {

    @XmlEnumValue("CountrySubdivision")
    COUNTRY_SUBDIVISION("CountrySubdivision"),
    @XmlEnumValue("CountrySecondarySubdivision")
    COUNTRY_SECONDARY_SUBDIVISION("CountrySecondarySubdivision"),
    @XmlEnumValue("Municipality")
    MUNICIPALITY("Municipality"),
    @XmlEnumValue("MunicipalitySubdivision")
    MUNICIPALITY_SUBDIVISION("MunicipalitySubdivision"),
    @XmlEnumValue("choume-banchi-go")
    CHOUME_BANCHI_GO("choume-banchi-go");
    private final String value;

    NamedPlaceClassification(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NamedPlaceClassification fromValue(String v) {
        for (NamedPlaceClassification c: NamedPlaceClassification.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
