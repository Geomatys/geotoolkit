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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of variable which may inform correct interpretation and interpolation methods
 */
public enum VariableType {
    CONTINUOUS(String.valueOf("continuous")),
    NUMERICAL_ORDINAL(String.valueOf("numericalOrdinal")),
    NUMERICAL_NOMINAL(String.valueOf("numericalNominal")),
    CATEGORICAL_ORDINAL(String.valueOf("categoricalOrdinal")),
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
