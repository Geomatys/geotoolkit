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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Concepts
 */
@JsonPropertyOrder({
    Concepts.JSON_PROPERTY_ID,
    Concepts.JSON_PROPERTY_TITLE,
    Concepts.JSON_PROPERTY_DESCRIPTION,
    Concepts.JSON_PROPERTY_URL
})
@XmlRootElement(name = "Concepts")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Concepts")
public final class Concepts extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nonnull
    private String id;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_URL = "url";
    @XmlElement(name = "url")
    @jakarta.annotation.Nullable
    private URI url;

    public Concepts() {
    }

    public Concepts id(@jakarta.annotation.Nonnull String id) {
        this.id = id;
        return this;
    }

    /**
     * An identifier for the concept.
     *
     * @return id
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nonnull String id) {
        this.id = id;
    }

    public Concepts title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * A human readable title for the concept.
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

    public Concepts description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * A human readable description for the concept.
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

    public Concepts url(@jakarta.annotation.Nullable URI url) {
        this.url = url;
        return this;
    }

    /**
     * A URI providing further description of the concept.
     *
     * @return url
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "url")
    public URI getUrl() {
        return url;
    }

    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "url")
    public void setUrl(@jakarta.annotation.Nullable URI url) {
        this.url = url;
    }

    /**
     * Return true if this theme_concepts_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Concepts themeConceptsInner = (Concepts) o;
        return Objects.equals(this.id, themeConceptsInner.id)
                && Objects.equals(this.title, themeConceptsInner.title)
                && Objects.equals(this.description, themeConceptsInner.description)
                && Objects.equals(this.url, themeConceptsInner.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, url);
    }

}
