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
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A custom CRS projection operation method, identified by a dictionary key (a safe CURIE if a
 * registered URI exists for the method) to be used as a value for the {@code crs-proj-method} query
 * parameter.
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    ProjectionMethod.JSON_PROPERTY_TITLE,
    ProjectionMethod.JSON_PROPERTY_DESCRIPTION,
    ProjectionMethod.JSON_PROPERTY_PARAMETERS
})
@XmlRootElement(name = "ProjectionMethod")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ProjectionMethod")
public final class ProjectionMethod extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    @XmlElement(name = "parameters")
    @jakarta.annotation.Nullable
    private Map<String, ProjectionParameter> parameters;

    public ProjectionMethod() {
    }

    public ProjectionMethod title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * A title for this projection operation method.
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

    public ProjectionMethod description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this projection operation method.
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

    public ProjectionMethod parameters(@jakarta.annotation.Nullable Map<String, ProjectionParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public ProjectionMethod putParametersItem(String key, ProjectionParameter parametersItem) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * The list of available method parameters for the {@code crs-proj-params} query parameter of the
     * API when using this method. The keys of this dictionary are the available values, and correspond
     * to safe CURIEs if a URI is registered for the method.
     *
     * @return parameters
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameters")
    @JacksonXmlElementWrapper(useWrapping = false)
    public Map<String, ProjectionParameter> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameters")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setParameters(@jakarta.annotation.Nullable Map<String, ProjectionParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Return true if this projectionMethod object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectionMethod other = (ProjectionMethod) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.parameters, other.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, parameters);
    }

}
