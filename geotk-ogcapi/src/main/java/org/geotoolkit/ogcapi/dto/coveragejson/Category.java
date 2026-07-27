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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * One entry of an {@link ObservedProperty}'s category list, for categorical/classified parameters.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Category.JSON_PROPERTY_ID,
    Category.JSON_PROPERTY_LABEL,
    Category.JSON_PROPERTY_DESCRIPTION
})
public final class Category extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public Category() {
    }

    public Category id(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(String id) {
        this.id = id;
    }

    public Category label(String label) {
        this.label = label;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLabel(String label) {
        this.label = label;
    }

    public Category description(String description) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category other = (Category) o;
        return Objects.equals(this.id, other.id)
                && Objects.equals(this.label, other.label)
                && Objects.equals(this.description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, description);
    }

}
