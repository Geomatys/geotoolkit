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
 * Type of transformation problem detected by a WCTS server, returned by {@code IsTransformable}
 * when its {@code transformable} attribute is {@code false}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlType(name = "ProblemType")
@XmlEnum
public enum ProblemType {

    @XmlEnumValue("source CRS")
    SOURCE_CRS("source CRS"),
    @XmlEnumValue("target CRS")
    TARGET_CRS("target CRS"),
    @XmlEnumValue("geometry type")
    GEOMETRY_TYPE("geometry type"),
    @XmlEnumValue("coverage type")
    COVERAGE_TYPE("coverage type"),
    @XmlEnumValue("interpolation method")
    INTERPOLATION_METHOD("interpolation method"),
    @XmlEnumValue("other")
    OTHER("other");

    private final String value;

    ProblemType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ProblemType fromValue(String v) {
        for (ProblemType c : ProblemType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
