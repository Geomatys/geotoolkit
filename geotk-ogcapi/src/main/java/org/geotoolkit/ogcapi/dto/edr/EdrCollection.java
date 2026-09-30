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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.coveragejson.Parameter;

/**
 * A {@code collection} resource as returned by the EDR {@code /collections/{collectionId}}
 * endpoint: a {@link CollectionDescription} extended with the EDR-specific
 * {@code data_queries}/{@code parameter_names}/{@code output_formats} members.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    EdrCollection.JSON_PROPERTY_DATA_QUERIES,
    EdrCollection.JSON_PROPERTY_PARAMETER_NAMES,
    EdrCollection.JSON_PROPERTY_OUTPUT_FORMATS
})
public final class EdrCollection extends CollectionDescription {

    public static final String JSON_PROPERTY_DATA_QUERIES = "data_queries";
    private DataQueries dataQueries;

    public static final String JSON_PROPERTY_PARAMETER_NAMES = "parameter_names";
    private Map<String, Parameter> parameterNames;

    public static final String JSON_PROPERTY_OUTPUT_FORMATS = "output_formats";
    private List<String> outputFormats;

    public EdrCollection() {
        // defaults inherited from OGC API Common / Features, which are not part of the EDR collection schema.
        setItemType(null);
        setStorageCrs(null);
    }

    public EdrCollection dataQueries(DataQueries dataQueries) {
        this.dataQueries = dataQueries;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DATA_QUERIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public DataQueries getDataQueries() {
        return dataQueries;
    }

    @JsonProperty(JSON_PROPERTY_DATA_QUERIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDataQueries(DataQueries dataQueries) {
        this.dataQueries = dataQueries;
    }

    public EdrCollection parameterNames(Map<String, Parameter> parameterNames) {
        this.parameterNames = parameterNames;
        return this;
    }

    public EdrCollection putParameterNamesItem(String key, Parameter parameterNamesItem) {
        if (this.parameterNames == null) {
            this.parameterNames = new java.util.LinkedHashMap<>();
        }
        this.parameterNames.put(key, parameterNamesItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETER_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Parameter> getParameterNames() {
        return parameterNames;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETER_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameterNames(Map<String, Parameter> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public EdrCollection outputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
        return this;
    }

    public EdrCollection addOutputFormatsItem(String outputFormatsItem) {
        if (this.outputFormats == null) {
            this.outputFormats = new java.util.ArrayList<>();
        }
        this.outputFormats.add(outputFormatsItem);
        return this;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getOutputFormats() {
        return outputFormats;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setOutputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        EdrCollection other = (EdrCollection) o;
        return Objects.equals(this.dataQueries, other.dataQueries)
                && Objects.equals(this.parameterNames, other.parameterNames)
                && Objects.equals(this.outputFormats, other.outputFormats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dataQueries, parameterNames, outputFormats);
    }

}
