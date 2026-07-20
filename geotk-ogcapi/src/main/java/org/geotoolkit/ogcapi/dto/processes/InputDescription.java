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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.jsonschema.JSONSchemaProperty;

/**
 * Description of one process input, keyed by input id in
 * {@link ProcessDescription#getInputs()}.
 * Reuses {@link JSONSchemaProperty} (already used by the DGGS bindings) for
 * the {@code schema} field rather than introducing a second flattened
 * JSON-Schema-subset type; its recursive {@code items}/{@code properties}
 * cases fall through to {@link DataTransferObject#otherFields()}.
 */
@JsonPropertyOrder({
    InputDescription.JSON_PROPERTY_TITLE,
    InputDescription.JSON_PROPERTY_DESCRIPTION,
    InputDescription.JSON_PROPERTY_KEYWORDS,
    InputDescription.JSON_PROPERTY_METADATA,
    InputDescription.JSON_PROPERTY_SCHEMA,
    InputDescription.JSON_PROPERTY_MIN_OCCURS,
    InputDescription.JSON_PROPERTY_MAX_OCCURS,
    InputDescription.JSON_PROPERTY_VALUE_PASSING
})
@XmlRootElement(name = "InputDescription")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "InputDescription")
public final class InputDescription extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    @XmlElement(name = "keywords")
    @jakarta.annotation.Nullable
    private List<String> keywords;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    @XmlElement(name = "metadata")
    @jakarta.annotation.Nullable
    private List<Metadata> metadata;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    @XmlElement(name = "schema")
    @jakarta.annotation.Nullable
    private JSONSchemaProperty schema;

    public static final String JSON_PROPERTY_MIN_OCCURS = "minOccurs";
    @XmlElement(name = "minOccurs")
    @jakarta.annotation.Nullable
    private Integer minOccurs = 1;

    /**
     * Either an {@link Integer} or the literal string {@code "unbounded"}.
     */
    public static final String JSON_PROPERTY_MAX_OCCURS = "maxOccurs";
    @XmlElement(name = "maxOccurs")
    @jakarta.annotation.Nullable
    private Object maxOccurs = 1;

    public static final String JSON_PROPERTY_VALUE_PASSING = "valuePassing";
    @XmlElement(name = "valuePassing")
    @jakarta.annotation.Nullable
    private List<String> valuePassing;

    public InputDescription() {
    }

    public InputDescription title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

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

    public InputDescription description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

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

    public InputDescription keywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public InputDescription addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "keywords")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setKeywords(@jakarta.annotation.Nullable List<String> keywords) {
        this.keywords = keywords;
    }

    public InputDescription metadata(@jakarta.annotation.Nullable List<Metadata> metadata) {
        this.metadata = metadata;
        return this;
    }

    public InputDescription addMetadataItem(Metadata metadataItem) {
        if (this.metadata == null) {
            this.metadata = new ArrayList<>();
        }
        this.metadata.add(metadataItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Metadata> getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setMetadata(@jakarta.annotation.Nullable List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public InputDescription schema(@jakarta.annotation.Nullable JSONSchemaProperty schema) {
        this.schema = schema;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public JSONSchemaProperty getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public void setSchema(@jakarta.annotation.Nullable JSONSchemaProperty schema) {
        this.schema = schema;
    }

    public InputDescription minOccurs(@jakarta.annotation.Nullable Integer minOccurs) {
        this.minOccurs = minOccurs;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minOccurs")
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @JsonProperty(JSON_PROPERTY_MIN_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minOccurs")
    public void setMinOccurs(@jakarta.annotation.Nullable Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public InputDescription maxOccurs(@jakarta.annotation.Nullable Object maxOccurs) {
        this.maxOccurs = maxOccurs;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxOccurs")
    public Object getMaxOccurs() {
        return maxOccurs;
    }

    @JsonProperty(JSON_PROPERTY_MAX_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxOccurs")
    public void setMaxOccurs(@jakarta.annotation.Nullable Object maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public InputDescription valuePassing(@jakarta.annotation.Nullable List<String> valuePassing) {
        this.valuePassing = valuePassing;
        return this;
    }

    public InputDescription addValuePassingItem(String valuePassingItem) {
        if (this.valuePassing == null) {
            this.valuePassing = new ArrayList<>();
        }
        this.valuePassing.add(valuePassingItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_PASSING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "valuePassing")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getValuePassing() {
        return valuePassing;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_PASSING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "valuePassing")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setValuePassing(@jakarta.annotation.Nullable List<String> valuePassing) {
        this.valuePassing = valuePassing;
    }
}
