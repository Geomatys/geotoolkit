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
public final class InputDescription extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    private List<Metadata> metadata;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    private JSONSchemaProperty schema;

    public static final String JSON_PROPERTY_MIN_OCCURS = "minOccurs";
    private Integer minOccurs = 1;

    /**
     * Either an {@link Integer} or the literal string {@code "unbounded"}.
     */
    public static final String JSON_PROPERTY_MAX_OCCURS = "maxOccurs";
    private Object maxOccurs = 1;

    public static final String JSON_PROPERTY_VALUE_PASSING = "valuePassing";
    private List<String> valuePassing;

    public InputDescription() {
    }

    public InputDescription title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public InputDescription description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public InputDescription keywords(List<String> keywords) {
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

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public InputDescription metadata(List<Metadata> metadata) {
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

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Metadata> getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public InputDescription schema(JSONSchemaProperty schema) {
        this.schema = schema;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public JSONSchemaProperty getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSchema(JSONSchemaProperty schema) {
        this.schema = schema;
    }

    public InputDescription minOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_MIN_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getMinOccurs() {
        return minOccurs;
    }

    @JsonProperty(JSON_PROPERTY_MIN_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public InputDescription maxOccurs(Object maxOccurs) {
        this.maxOccurs = maxOccurs;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_MAX_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getMaxOccurs() {
        return maxOccurs;
    }

    @JsonProperty(JSON_PROPERTY_MAX_OCCURS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxOccurs(Object maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public InputDescription valuePassing(List<String> valuePassing) {
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

    @JsonProperty(JSON_PROPERTY_VALUE_PASSING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getValuePassing() {
        return valuePassing;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_PASSING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setValuePassing(List<String> valuePassing) {
        this.valuePassing = valuePassing;
    }
}
