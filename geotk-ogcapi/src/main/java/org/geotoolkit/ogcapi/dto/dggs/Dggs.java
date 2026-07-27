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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The hierarchical series of Discrete Global Grid upon which this DGGRS is based, including any parameters.
 */
@JsonPropertyOrder({
    Dggs.JSON_PROPERTY_DEFINITION,
    Dggs.JSON_PROPERTY_PARAMETERS
})
public final class Dggs extends DataTransferObject {

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private DggsDefinition definition;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private DggsParameters parameters;

    public Dggs() {
    }

    public Dggs definition(DggsDefinition definition) {
        this.definition = definition;
        return this;
    }

    /**
     * Get definition
     *
     * @return definition
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DggsDefinition getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDefinition(DggsDefinition definition) {
        this.definition = definition;
    }

    public Dggs parameters(DggsParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Get parameters
     *
     * @return parameters
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DggsParameters getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameters(DggsParameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Return true if this dggrs_definition_dggh object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dggs dggrsDefinitionDggh = (Dggs) o;
        return Objects.equals(this.definition, dggrsDefinitionDggh.definition)
                && Objects.equals(this.parameters, dggrsDefinitionDggh.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition, parameters);
    }

}
