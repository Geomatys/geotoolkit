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
package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Endpoint.JSON_PROPERTY_PATH,
        Endpoint.JSON_PROPERTY_METHODS
})
@XmlRootElement(name = "Endpoint")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Endpoint")
public class Endpoint extends DataTransferObject {

    public static final String JSON_PROPERTY_PATH = "path";
    @XmlElement(name = "path")
    @jakarta.annotation.Nonnull
    private String path;

    /**
     * Gets or Sets methods
     */
    public enum MethodsEnum {
        GET("GET"),
        POST("POST"),
        PATCH("PATCH"),
        PUT("PUT"),
        DELETE("DELETE");

        private String value;

        MethodsEnum(String value) {
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
        public static MethodsEnum fromValue(String value) {
            for (MethodsEnum b : MethodsEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_METHODS = "methods";
    @XmlElementWrapper(name = "methods")
    @XmlElement(name = "method")
    @JacksonXmlElementWrapper(localName = "methods", useWrapping = false)
    @JacksonXmlProperty(localName = "method")
    @jakarta.annotation.Nonnull
    private List<MethodsEnum> methods = new ArrayList<>();

    public Endpoint(String path, List<MethodsEnum> methods) {
        this.path = path;
        this.methods = methods;
    }

    public Endpoint path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Get path
     *
     * @return path
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PATH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "path")
    public String getPath() {
        return path;
    }

    @JsonProperty(JSON_PROPERTY_PATH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "path")
    public void setPath(@jakarta.annotation.Nonnull String path) {
        this.path = path;
    }

    public Endpoint methods(List<MethodsEnum> methods) {
        this.methods = methods;
        return this;
    }

    public Endpoint addMethodsItem(MethodsEnum methodsItem) {
        this.methods.add(methodsItem);
        return this;
    }

    /**
     * Get methods
     *
     * @return methods
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_METHODS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_METHODS)
    public List<MethodsEnum> getMethods() {
        return methods;
    }

    @JsonProperty(JSON_PROPERTY_METHODS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_METHODS)
    public void setMethods(@jakarta.annotation.Nonnull List<MethodsEnum> methods) {
        this.methods = methods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(this.path, endpoint.path) &&
                Objects.equals(this.methods, endpoint.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, methods);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
