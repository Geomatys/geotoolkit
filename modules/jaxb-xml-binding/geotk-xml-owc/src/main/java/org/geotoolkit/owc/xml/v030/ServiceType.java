/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.owc.xml.v030;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="serviceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="urn:ogc:serviceType:WMS"/>
 *     &lt;enumeration value="urn:ogc:serviceType:WFS"/>
 *     &lt;enumeration value="urn:ogc:serviceType:WCS"/>
 *     &lt;enumeration value="urn:ogc:serviceType:GML"/>
 *     &lt;enumeration value="urn:ogc:serviceType:SLD"/>
 *     &lt;enumeration value="urn:ogc:serviceType:FES"/>
 *     &lt;enumeration value="urn:ogc:serviceType:KML"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "serviceType")
@XmlEnum
public enum ServiceType {

    @XmlEnumValue("urn:ogc:serviceType:WMS")
    URN_OGC_SERVICE_TYPE_WMS("urn:ogc:serviceType:WMS"),
    @XmlEnumValue("urn:ogc:serviceType:WFS")
    URN_OGC_SERVICE_TYPE_WFS("urn:ogc:serviceType:WFS"),
    @XmlEnumValue("urn:ogc:serviceType:WCS")
    URN_OGC_SERVICE_TYPE_WCS("urn:ogc:serviceType:WCS"),
    @XmlEnumValue("urn:ogc:serviceType:GML")
    URN_OGC_SERVICE_TYPE_GML("urn:ogc:serviceType:GML"),
    @XmlEnumValue("urn:ogc:serviceType:SLD")
    URN_OGC_SERVICE_TYPE_SLD("urn:ogc:serviceType:SLD"),
    @XmlEnumValue("urn:ogc:serviceType:FES")
    URN_OGC_SERVICE_TYPE_FES("urn:ogc:serviceType:FES"),
    @XmlEnumValue("urn:ogc:serviceType:KML")
    URN_OGC_SERVICE_TYPE_KML("urn:ogc:serviceType:KML");
    private final String value;

    ServiceType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceType fromValue(final String v) {
        for (ServiceType c: ServiceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
