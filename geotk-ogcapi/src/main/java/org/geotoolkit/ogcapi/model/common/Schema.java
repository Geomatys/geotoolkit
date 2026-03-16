/*
 * Geotoolkit - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2026, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchema;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchemaProperty;

/**
 * Schema that extends JsonSchema
 * Spec : https://docs.ogc.org/DRAFTS/23-058r1.html
 *
 * @author Quentin BIALOTA (Geomatys)
 */
@JsonPropertyOrder({
        Schema.JSON_PROPERTY_SCHEMA,
        Schema.JSON_PROPERTY_ID,
        Schema.JSON_PROPERTY_TITLE
})
@XmlRootElement(name = "Schema")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Schema")
public final class Schema extends JSONSchema implements CommonResponse {

    public static final String JSON_PROPERTY_SCHEMA = "$schema";
    @XmlElement(name = "schema")
    @jakarta.annotation.Nullable
    private String schemaUrl = "https://json-schema.org/draft/2020-12/schema";

    public static final String JSON_PROPERTY_ID = "$id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public Schema() {
        super();
    }

    public Schema(String title, Map<String, JSONSchemaProperty> properties) {
        super();
        this.title = title;
        this.setProperties(properties);
    }

    public Schema(String title, String id,  Map<String, JSONSchemaProperty> properties) {
        super();
        this.title = title;
        this.id = id;
        this.setProperties(properties);
    }

    public Schema schemaUrl(@jakarta.annotation.Nullable String schemaUrl) {
        this.schemaUrl = schemaUrl;
        return this;
    }

    /**
     * Get schemaUrl
     *
     * @return schemaUrl
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "$schema")
    public String getSchemaUrl() {
        return schemaUrl;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "$schema")
    public void setSchemaUrl(@jakarta.annotation.Nullable String schemaUrl) {
        this.schemaUrl = schemaUrl;
    }

    public Schema id(@jakarta.annotation.Nullable String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "$id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "$id")
    public void setId(@jakarta.annotation.Nullable String id) {
        this.id = id;
    }

    public Schema title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !super.equals(o)) {
            return false;
        }
        Schema schema = (Schema) o;
        return Objects.equals(this.schemaUrl, schema.schemaUrl)
                && Objects.equals(this.id, schema.id)
                && Objects.equals(this.title, schema.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), schemaUrl, id, title);
    }

}
