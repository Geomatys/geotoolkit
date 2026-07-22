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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "EdrCollection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "EdrCollection")
public final class EdrCollection extends CollectionDescription {

    public static final String JSON_PROPERTY_DATA_QUERIES = "data_queries";
    @XmlElement(name = "data_queries")
    @jakarta.annotation.Nullable
    private DataQueries dataQueries;

    public static final String JSON_PROPERTY_PARAMETER_NAMES = "parameter_names";
    @XmlElement(name = "parameter_names")
    @jakarta.annotation.Nullable
    private Map<String, Parameter> parameterNames;

    public static final String JSON_PROPERTY_OUTPUT_FORMATS = "output_formats";
    @XmlElement(name = "output_formats")
    @jakarta.annotation.Nullable
    private List<String> outputFormats;

    public EdrCollection() {
    }

    public EdrCollection dataQueries(@jakarta.annotation.Nullable DataQueries dataQueries) {
        this.dataQueries = dataQueries;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DATA_QUERIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "data_queries")
    public DataQueries getDataQueries() {
        return dataQueries;
    }

    @JsonProperty(JSON_PROPERTY_DATA_QUERIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "data_queries")
    public void setDataQueries(@jakarta.annotation.Nullable DataQueries dataQueries) {
        this.dataQueries = dataQueries;
    }

    public EdrCollection parameterNames(@jakarta.annotation.Nullable Map<String, Parameter> parameterNames) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARAMETER_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameter_names")
    public Map<String, Parameter> getParameterNames() {
        return parameterNames;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETER_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "parameter_names")
    public void setParameterNames(@jakarta.annotation.Nullable Map<String, Parameter> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public EdrCollection outputFormats(@jakarta.annotation.Nullable List<String> outputFormats) {
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

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OUTPUT_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "output_formats")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getOutputFormats() {
        return outputFormats;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_FORMATS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "output_formats")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setOutputFormats(@jakarta.annotation.Nullable List<String> outputFormats) {
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
