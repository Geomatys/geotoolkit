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
package org.geotoolkit.ogcapi.model.feature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Gets or Sets type
 */
@XmlType(name = "TypeEnum")
@XmlEnum(value = String.class)
public enum ValueType {
    @XmlEnumValue(value = "string")
    STRING(String.valueOf("string")),
    @XmlEnumValue(value = "number")
    NUMBER(String.valueOf("number")),
    @XmlEnumValue(value = "integer")
    INTEGER(String.valueOf("integer")),
    @XmlEnumValue(value = "datetime")
    DATETIME(String.valueOf("datetime")),
    @XmlEnumValue(value = "geometry")
    GEOMETRY(String.valueOf("geometry")),
    @XmlEnumValue(value = "boolean")
    BOOLEAN(String.valueOf("boolean"));
    private String value;

    ValueType(String value) {
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
    public static ValueType fromValue(String value) {
        for (ValueType b : ValueType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
