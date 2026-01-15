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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Discovery">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        ProcessParameter.JSON_PROPERTY_NAME,
        ProcessParameter.JSON_PROPERTY_DESCRIPTION,
        ProcessParameter.JSON_PROPERTY_SCHEMA,
        ProcessParameter.JSON_PROPERTY_OPTIONAL,
        ProcessParameter.JSON_PROPERTY_DEFAULT
})
@XmlRootElement(name = "ProcessParameter")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProcessParameter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessParameter extends DataTransferObject {

    public ProcessParameter() {}

    public ProcessParameter(String name, String description, DataTypeSchema[] schema) {
        this.name = name;
        this.description = description;
        this.schema = schema;
    }

    public ProcessParameter(String name, String description, DataTypeSchema[] schema, boolean optional, Object defaultObject) {
        this.name = name;
        this.description = description;
        this.schema = schema;
        this.optional = optional;
        this.defaultObject = defaultObject;
    }

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    @XmlElementWrapper(name = "schema")
    @XmlElement(name = "dataTypeSchema")
    @JacksonXmlElementWrapper(localName = "schema", useWrapping = true)
    @JacksonXmlProperty(localName = "dataTypeSchema")
    @jakarta.annotation.Nonnull
    private DataTypeSchema[] schema;

    public static final String JSON_PROPERTY_OPTIONAL = "optional";
    @XmlElement(name = "optional")
    @jakarta.annotation.Nonnull
    private boolean optional = false;

    public static final String JSON_PROPERTY_DEFAULT = "default";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Object defaultObject = null;

    /**
     * Get name
     *
     * @return name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    /**
     * Get schema
     *
     * @return schema
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SCHEMA)
    public DataTypeSchema[] getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_SCHEMA)
    public void setSchema(@jakarta.annotation.Nonnull DataTypeSchema[] schema) {
        this.schema = schema;
    }

    /**
     * Get optional
     *
     * @return optional
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_OPTIONAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "optional")
    public boolean isOptional() {
        return optional;
    }

    @JsonProperty(JSON_PROPERTY_OPTIONAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "optional")
    public void setOptional(@jakarta.annotation.Nonnull boolean optional) {
        this.optional = optional;
    }

    /**
     * Get defaultObject
     *
     * @return defaultObject
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getDefaultObject() {
        return defaultObject;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDefaultObject(@jakarta.annotation.Nullable Object defaultObject) {
        this.defaultObject = defaultObject;
    }

    @JsonIgnore
    public boolean isValid() {
        return !((optional && defaultObject == null) || (!optional && defaultObject != null));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessParameter that = (ProcessParameter) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Arrays.equals(schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, Arrays.hashCode(schema));
    }
}
