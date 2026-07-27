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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Enumeration
 */
@JsonPropertyOrder({
    Enumeration.JSON_PROPERTY_TYPE,
    Enumeration.JSON_PROPERTY_ENUM
})
public final class Enumeration extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        ENUM(String.valueOf("enum"));

        private String value;

        TypeEnum(String value) {
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
        public static TypeEnum fromValue(String value) {
            for (TypeEnum b : TypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_TYPE = "type";
    private TypeEnum type;

    public static final String JSON_PROPERTY_ENUM = "enum";
    private List<String> _enum = new ArrayList<>();

    public Enumeration() {
    }

    public Enumeration type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(TypeEnum type) {
        this.type = type;
    }

    public Enumeration _enum(List<String> _enum) {
        this._enum = _enum;
        return this;
    }

    public Enumeration addEnumItem(String _enumItem) {
        if (this._enum == null) {
            this._enum = new ArrayList<>();
        }
        this._enum.add(_enumItem);
        return this;
    }

    /**
     * Get _enum
     *
     * @return _enum
     */
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getEnum() {
        return _enum;
    }

    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setEnum(List<String> _enum) {
        this._enum = _enum;
    }

    /**
     * Return true if this enumeration object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Enumeration enumeration = (Enumeration) o;
        return Objects.equals(this.type, enumeration.type)
                && Objects.equals(this._enum, enumeration._enum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, _enum);
    }

}
