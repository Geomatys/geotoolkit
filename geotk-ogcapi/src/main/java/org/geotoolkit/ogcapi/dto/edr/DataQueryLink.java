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
package org.geotoolkit.ogcapi.dto.edr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.common.Link;

/**
 * A {@link Link} extended with a {@code variables} member describing the query parameters
 * accepted by the linked {@link DataQuery}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlRootElement(name = "DataQueryLink")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DataQueryLink")
@JsonPropertyOrder({
    DataQueryLink.JSON_PROPERTY_VARIABLES
})
public final class DataQueryLink extends Link {

    public static final String JSON_PROPERTY_VARIABLES = "variables";
    @XmlElement(name = "variables")
    @jakarta.annotation.Nullable
    private DataQuery variables;

    public DataQueryLink() {
    }

    /**
     * @param variables the variables to set
     * @see #getVariables()
     */
    public DataQueryLink variables(@jakarta.annotation.Nullable DataQuery variables) {
        setVariables(variables);
        return this;
    }

    /**
     * The query parameters accepted by the linked {@link DataQuery}. (optional)
     *
     * @return the variables
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VARIABLES)
    @JacksonXmlProperty(localName = "variables")
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQuery getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     * @see #getVariables()
     */
    @JsonProperty(JSON_PROPERTY_VARIABLES)
    @JacksonXmlProperty(localName = "variables")
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVariables(@jakarta.annotation.Nullable DataQuery variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        DataQueryLink other = (DataQueryLink) o;
        return Objects.equals(this.variables, other.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), variables);
    }
}
