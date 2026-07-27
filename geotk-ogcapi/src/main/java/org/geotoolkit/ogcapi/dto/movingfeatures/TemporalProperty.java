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
package org.geotoolkit.ogcapi.dto.movingfeatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * MF-JSON {@code TemporalProperty} object: describes a single time-varying property (a
 * {@linkplain #getValueSequence() sequence} of {@link TemporalPrimitiveValue}s) of a moving
 * feature.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalProperty.JSON_PROPERTY_NAME,
    TemporalProperty.JSON_PROPERTY_TYPE,
    TemporalProperty.JSON_PROPERTY_FORM,
    TemporalProperty.JSON_PROPERTY_VALUE_SEQUENCE,
    TemporalProperty.JSON_PROPERTY_DESCRIPTION,
    TemporalProperty.JSON_PROPERTY_LINKS
})
public final class TemporalProperty extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_FORM = "form";
    private String form;

    public static final String JSON_PROPERTY_VALUE_SEQUENCE = "valueSequence";
    private List<TemporalPrimitiveValue> valueSequence;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    public TemporalProperty() {
    }

    public TemporalProperty name(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setName(String name) {
        this.name = name;
    }

    public TemporalProperty type(String type) {
        this.type = type;
        return this;
    }

    /**
     * One of TBoolean, TText, TInteger, TReal or TImage.
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    public TemporalProperty form(String form) {
        this.form = form;
        return this;
    }

    /**
     * A URI, or a 3-character unit/form code (e.g. "KMH", "MTR"), qualifying the values.
     * @return form
     */
    @JsonProperty(JSON_PROPERTY_FORM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getForm() {
        return form;
    }

    @JsonProperty(JSON_PROPERTY_FORM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setForm(String form) {
        this.form = form;
    }

    public TemporalProperty valueSequence(List<TemporalPrimitiveValue> valueSequence) {
        this.valueSequence = valueSequence;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<TemporalPrimitiveValue> getValueSequence() {
        return valueSequence;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setValueSequence(List<TemporalPrimitiveValue> valueSequence) {
        this.valueSequence = valueSequence;
    }

    public TemporalProperty description(String description) {
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

    public TemporalProperty links(List<Link> links) {
        this.links = links;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalProperty other = (TemporalProperty) o;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.form, other.form)
                && Objects.equals(this.valueSequence, other.valueSequence)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.links, other.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, form, valueSequence, description, links);
    }

}
