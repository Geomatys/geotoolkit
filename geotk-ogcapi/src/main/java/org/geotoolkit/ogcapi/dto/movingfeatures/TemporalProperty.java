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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "TemporalProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TemporalProperty")
public final class TemporalProperty extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = JSON_PROPERTY_NAME)
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = JSON_PROPERTY_TYPE)
    @jakarta.annotation.Nonnull
    private String type;

    public static final String JSON_PROPERTY_FORM = "form";
    @XmlElement(name = JSON_PROPERTY_FORM)
    @jakarta.annotation.Nullable
    private String form;

    public static final String JSON_PROPERTY_VALUE_SEQUENCE = "valueSequence";
    @XmlElement(name = JSON_PROPERTY_VALUE_SEQUENCE)
    @jakarta.annotation.Nullable
    private List<TemporalPrimitiveValue> valueSequence;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = JSON_PROPERTY_DESCRIPTION)
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = JSON_PROPERTY_LINKS)
    @jakarta.annotation.Nullable
    private List<Link> links;

    public TemporalProperty() {
    }

    public TemporalProperty name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

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

    public TemporalProperty type(@jakarta.annotation.Nonnull String type) {
        this.type = type;
        return this;
    }

    /**
     * One of TBoolean, TText, TInteger, TReal or TImage.
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public TemporalProperty form(@jakarta.annotation.Nullable String form) {
        this.form = form;
        return this;
    }

    /**
     * A URI, or a 3-character unit/form code (e.g. "KMH", "MTR"), qualifying the values.
     * @return form
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FORM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "form")
    public String getForm() {
        return form;
    }

    @JsonProperty(JSON_PROPERTY_FORM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "form")
    public void setForm(@jakarta.annotation.Nullable String form) {
        this.form = form;
    }

    public TemporalProperty valueSequence(@jakarta.annotation.Nullable List<TemporalPrimitiveValue> valueSequence) {
        this.valueSequence = valueSequence;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "valueSequence")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<TemporalPrimitiveValue> getValueSequence() {
        return valueSequence;
    }

    @JsonProperty(JSON_PROPERTY_VALUE_SEQUENCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "valueSequence")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setValueSequence(@jakarta.annotation.Nullable List<TemporalPrimitiveValue> valueSequence) {
        this.valueSequence = valueSequence;
    }

    public TemporalProperty description(@jakarta.annotation.Nullable String description) {
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

    public TemporalProperty links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
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
