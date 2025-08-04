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
package org.geotoolkit.ogcapi.model.jsonschema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Json property types.
 */
@XmlType(name = "TypeEnum")
@XmlEnum(value = String.class)
public enum JSONType {
    @XmlEnumValue(value = "array")
    ARRAY(String.valueOf("array")),
    @XmlEnumValue(value = "boolean")
    BOOLEAN(String.valueOf("boolean")),
    @XmlEnumValue(value = "integer")
    INTEGER(String.valueOf("integer")),
    @XmlEnumValue(value = "null")
    NULL(String.valueOf("null")),
    @XmlEnumValue(value = "number")
    NUMBER(String.valueOf("number")),
    @XmlEnumValue(value = "object")
    OBJECT(String.valueOf("object")),
    @XmlEnumValue(value = "string")
    STRING(String.valueOf("string"));
    private String value;

    JSONType(String value) {
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
    public static JSONType fromValue(String value) {
        for (JSONType b : JSONType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
