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
package org.geotoolkit.ogcapi.dto.maps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A method parameter for a custom CRS projection operation method, identified by a dictionary key
 * (a safe CURIE if a registered URI exists for the parameter) to be used as a value for the
 * {@code crs-proj-params} query parameter.
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    ProjectionParameter.JSON_PROPERTY_TITLE,
    ProjectionParameter.JSON_PROPERTY_DESCRIPTION
})
public final class ProjectionParameter extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public ProjectionParameter() {
    }

    public ProjectionParameter title(String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this method parameter.
     *
     * @return title
     */
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

    public ProjectionParameter description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this method parameter.
     *
     * @return description
     */
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

    /**
     * Return true if this projectionParameter object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectionParameter other = (ProjectionParameter) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

}
