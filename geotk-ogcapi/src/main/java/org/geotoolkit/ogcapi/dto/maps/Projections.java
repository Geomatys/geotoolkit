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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The list of available custom CRS projection operation methods, parameters and datums, as returned by
 * the {@code /projectionsAndDatums} resource.
 *
 * @author Johann Sorel (Geomatys)
 */
@JsonPropertyOrder({
    Projections.JSON_PROPERTY_METHODS,
    Projections.JSON_PROPERTY_DATUMS,
    Projections.JSON_PROPERTY_PARAMETERS
})
public final class Projections extends DataTransferObject {

    public static final String JSON_PROPERTY_METHODS = "methods";
    private Map<String, ProjectionMethod> methods = new HashMap<>();

    public static final String JSON_PROPERTY_DATUMS = "datums";
    private Map<String, ProjectionDatum> datums = new HashMap<>();

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private Map<String, ProjectionParameter> parameters;

    public Projections() {
    }

    public Projections methods(Map<String, ProjectionMethod> methods) {
        this.methods = methods;
        return this;
    }

    public Projections putMethodsItem(String key, ProjectionMethod methodsItem) {
        if (this.methods == null) {
            this.methods = new HashMap<>();
        }
        this.methods.put(key, methodsItem);
        return this;
    }

    /**
     * The list of available projection operation methods for the {@code crs-proj-method} query
     * parameter of the API. The keys of this dictionary are the available values, and correspond to
     * safe CURIEs if a URI is registered for the method.
     *
     * @return methods
     */
    @JsonProperty(JSON_PROPERTY_METHODS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, ProjectionMethod> getMethods() {
        return methods;
    }

    @JsonProperty(JSON_PROPERTY_METHODS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMethods(Map<String, ProjectionMethod> methods) {
        this.methods = methods;
    }

    public Projections datums(Map<String, ProjectionDatum> datums) {
        this.datums = datums;
        return this;
    }

    public Projections putDatumsItem(String key, ProjectionDatum datumsItem) {
        if (this.datums == null) {
            this.datums = new HashMap<>();
        }
        this.datums.put(key, datumsItem);
        return this;
    }

    /**
     * The list of available datums for the {@code crs-datum} query parameter of the API. The keys of
     * this dictionary are the available values, and correspond to safe CURIEs if a URI is registered
     * for the datum.
     *
     * @return datums
     */
    @JsonProperty(JSON_PROPERTY_DATUMS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, ProjectionDatum> getDatums() {
        return datums;
    }

    @JsonProperty(JSON_PROPERTY_DATUMS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDatums(Map<String, ProjectionDatum> datums) {
        this.datums = datums;
    }

    public Projections parameters(Map<String, ProjectionParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Projections putParametersItem(String key, ProjectionParameter parametersItem) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * A list of global method parameters that can be referenced from individual methods supporting
     * them using a JSON Pointer ({@code $ref}) mechanism.
     *
     * @return parameters
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, ProjectionParameter> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameters(Map<String, ProjectionParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Return true if this projections object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Projections other = (Projections) o;
        return Objects.equals(this.methods, other.methods)
                && Objects.equals(this.datums, other.datums)
                && Objects.equals(this.parameters, other.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methods, datums, parameters);
    }

}
