/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Code identifying an interpolation method (ISO 19123 Annex B), as encoded by
 * {@code owsAdditions.xsd/InterpolationMethodType} (private WCTS extension namespace).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlType(name = "InterpolationMethodType", namespace = "http://www.opengeospatial.net/ows")
@XmlEnum
public enum InterpolationMethodType {

    @XmlEnumValue("nearest neighbor")
    NEAREST_NEIGHBOR("nearest neighbor"),
    @XmlEnumValue("bilinear")
    BILINEAR("bilinear"),
    @XmlEnumValue("bicubic")
    BICUBIC("bicubic"),
    @XmlEnumValue("lost area")
    LOST_AREA("lost area"),
    @XmlEnumValue("barycentric")
    BARYCENTRIC("barycentric"),
    @XmlEnumValue("none")
    NONE("none");

    private final String value;

    InterpolationMethodType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static InterpolationMethodType fromValue(String v) {
        for (InterpolationMethodType c : InterpolationMethodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
