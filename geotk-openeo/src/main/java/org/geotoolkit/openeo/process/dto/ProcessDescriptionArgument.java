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
package org.geotoolkit.openeo.process.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ProcessDescriptionArgument.JSON_PROPERTY_VALUE,
        ProcessDescriptionArgument.JSON_PROPERTY_TYPE
})
@XmlRootElement(name = "ProcessDescriptionArgument")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProcessDescriptionArgument")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDescriptionArgument extends DataTransferObject {

    public enum ArgumentType {
        VALUE(null),
        ARRAY(null),
        FROM_NODE("from_node"),
        FROM_PARAMETER("from_parameter"),
        PROCESS_GRAPH("process_graph");

        private final String value;

        ArgumentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ArgumentType fromValue(String value) {
            for (ArgumentType type : ArgumentType.values()) {
                if (type.getValue().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid argument type: " + value);
        }
    }

    public ProcessDescriptionArgument(Object value, ArgumentType type) {
        this.value = value;
        this.type = type;
    }

    public static final String JSON_PROPERTY_VALUE = "value";
    @XmlTransient
    @XmlElement(name = "value")
    @jakarta.annotation.Nullable
    private Object value;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private ArgumentType type;

    /**
     * Get value
     *
     * @return value
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "value")
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "value")
    public void setValue(@jakarta.annotation.Nullable Object value) {
        this.value = value;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public ArgumentType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable ArgumentType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessDescriptionArgument that = (ProcessDescriptionArgument) o;
        return Objects.equals(value, that.value) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
