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
package org.geotoolkit.ogcapi.dto.coveragejson;

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

/**
 * Describes the physical quantity measured by a {@link Parameter}, per the CoverageJSON
 * {@code observedProperty} object.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    ObservedProperty.JSON_PROPERTY_ID,
    ObservedProperty.JSON_PROPERTY_LABEL,
    ObservedProperty.JSON_PROPERTY_DESCRIPTION,
    ObservedProperty.JSON_PROPERTY_CATEGORIES
})
@XmlRootElement(name = "ObservedProperty")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ObservedProperty")
public final class ObservedProperty extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    @XmlElement(name = "label")
    @jakarta.annotation.Nullable
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_CATEGORIES = "categories";
    @XmlElement(name = "categories")
    @jakarta.annotation.Nullable
    private List<Category> categories;

    public ObservedProperty() {
    }

    public ObservedProperty id(@jakarta.annotation.Nullable String id) {
        this.id = id;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "id")
    public void setId(@jakarta.annotation.Nullable String id) {
        this.id = id;
    }

    public ObservedProperty label(@jakarta.annotation.Nullable String label) {
        this.label = label;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "label")
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "label")
    public void setLabel(@jakarta.annotation.Nullable String label) {
        this.label = label;
    }

    public ObservedProperty description(@jakarta.annotation.Nullable String description) {
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

    public ObservedProperty categories(@jakarta.annotation.Nullable List<Category> categories) {
        this.categories = categories;
        return this;
    }

    public ObservedProperty addCategoriesItem(Category categoriesItem) {
        if (this.categories == null) {
            this.categories = new java.util.ArrayList<>();
        }
        this.categories.add(categoriesItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CATEGORIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "categories")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty(JSON_PROPERTY_CATEGORIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "categories")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCategories(@jakarta.annotation.Nullable List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservedProperty other = (ObservedProperty) o;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.label, other.label)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.categories, other.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, description, categories);
    }

}
