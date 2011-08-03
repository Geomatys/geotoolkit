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
 * <p>Java class for POIPropertyNameType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="POIPropertyNameType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ID"/>
 *     &lt;enumeration value="POIName"/>
 *     &lt;enumeration value="PhoneNumber"/>
 *     &lt;enumeration value="Keyword"/>
 *     &lt;enumeration value="NAICS_type"/>
 *     &lt;enumeration value="NAICS_subType"/>
 *     &lt;enumeration value="NAICS_category"/>
 *     &lt;enumeration value="SIC_type"/>
 *     &lt;enumeration value="SIC_subType"/>
 *     &lt;enumeration value="SIC_category"/>
 *     &lt;enumeration value="SIC_code"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "POIPropertyNameType")
@XmlEnum
public enum POIPropertyNameType {

    ID("ID"),
    @XmlEnumValue("POIName")
    POI_NAME("POIName"),
    @XmlEnumValue("PhoneNumber")
    PHONE_NUMBER("PhoneNumber"),
    @XmlEnumValue("Keyword")
    KEYWORD("Keyword"),
    @XmlEnumValue("NAICS_type")
    NAICS_TYPE("NAICS_type"),
    @XmlEnumValue("NAICS_subType")
    NAICS_SUB_TYPE("NAICS_subType"),
    @XmlEnumValue("NAICS_category")
    NAICS_CATEGORY("NAICS_category"),
    @XmlEnumValue("SIC_type")
    SIC_TYPE("SIC_type"),
    @XmlEnumValue("SIC_subType")
    SIC_SUB_TYPE("SIC_subType"),
    @XmlEnumValue("SIC_category")
    SIC_CATEGORY("SIC_category"),
    @XmlEnumValue("SIC_code")
    SIC_CODE("SIC_code"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    POIPropertyNameType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static POIPropertyNameType fromValue(String v) {
        for (POIPropertyNameType c: POIPropertyNameType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
