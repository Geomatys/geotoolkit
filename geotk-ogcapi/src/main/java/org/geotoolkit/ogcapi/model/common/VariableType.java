/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * The type of variable which may inform correct interpretation and interpolation methods
 */
@XmlType(name = "VariableTypeEnum")
@XmlEnum(value = String.class)
public enum VariableType {
    @XmlEnumValue(value = "continuous")
    CONTINUOUS(String.valueOf("continuous")),
    @XmlEnumValue(value = "numericalOrdinal")
    NUMERICAL_ORDINAL(String.valueOf("numericalOrdinal")),
    @XmlEnumValue(value = "numericalNominal")
    NUMERICAL_NOMINAL(String.valueOf("numericalNominal")),
    @XmlEnumValue(value = "categoricalOrdinal")
    CATEGORICAL_ORDINAL(String.valueOf("categoricalOrdinal")),
    @XmlEnumValue(value = "categoricalNominal")
    CATEGORICAL_NOMINAL(String.valueOf("categoricalNominal"));
    private String value;

    VariableType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static VariableType fromValue(String value) {
        for (VariableType b : VariableType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
