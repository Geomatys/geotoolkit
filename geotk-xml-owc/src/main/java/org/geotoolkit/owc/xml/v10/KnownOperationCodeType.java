/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.04.20 at 07:08:32 PM CEST
//


package org.geotoolkit.owc.xml.v10;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KnownOperationCodeType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KnownOperationCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GetCapabilities"/>
 *     &lt;enumeration value="DescribeFeature"/>
 *     &lt;enumeration value="DescribeCoverage"/>
 *     &lt;enumeration value="GetMap"/>
 *     &lt;enumeration value="GetTile"/>
 *     &lt;enumeration value="GetFeature"/>
 *     &lt;enumeration value="GetFeatureInfo"/>
 *     &lt;enumeration value="GetCoverage"/>
 *     &lt;enumeration value="GetRecords"/>
 *     &lt;enumeration value="Execute"/>
 *     &lt;enumeration value="Transaction"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "KnownOperationCodeType")
@XmlEnum
public enum KnownOperationCodeType {

    @XmlEnumValue("GetCapabilities")
    GET_CAPABILITIES("GetCapabilities"),
    @XmlEnumValue("DescribeFeature")
    DESCRIBE_FEATURE("DescribeFeature"),
    @XmlEnumValue("DescribeCoverage")
    DESCRIBE_COVERAGE("DescribeCoverage"),
    @XmlEnumValue("GetMap")
    GET_MAP("GetMap"),
    @XmlEnumValue("GetTile")
    GET_TILE("GetTile"),
    @XmlEnumValue("GetFeature")
    GET_FEATURE("GetFeature"),
    @XmlEnumValue("GetFeatureInfo")
    GET_FEATURE_INFO("GetFeatureInfo"),
    @XmlEnumValue("GetCoverage")
    GET_COVERAGE("GetCoverage"),
    @XmlEnumValue("GetRecords")
    GET_RECORDS("GetRecords"),
    @XmlEnumValue("Execute")
    EXECUTE("Execute"),
    @XmlEnumValue("Transaction")
    TRANSACTION("Transaction");
    private final String value;

    KnownOperationCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KnownOperationCodeType fromValue(String v) {
        for (KnownOperationCodeType c: KnownOperationCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
