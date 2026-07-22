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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.coveragejson.CrsObject;

/**
 * Metadata specific to an individual EDR data query (position, radius, area, cube,
 * trajectory, corridor, locations, items or instances). The EDR OpenAPI schema defines
 * one {@code *DataQuery} variant per query type, all identical except for a locked
 * {@code query_type} enum value, so they are flattened into this single class.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    DataQuery.JSON_PROPERTY_QUERY_TYPE,
    DataQuery.JSON_PROPERTY_TITLE,
    DataQuery.JSON_PROPERTY_DESCRIPTION,
    DataQuery.JSON_PROPERTY_OUTPUT_FORMATS,
    DataQuery.JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT,
    DataQuery.JSON_PROPERTY_CRS_DETAILS
})
@XmlRootElement(name = "DataQuery")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DataQuery")
public final class DataQuery extends DataTransferObject {

    public static final String JSON_PROPERTY_QUERY_TYPE = "query_type";
    @XmlElement(name = "query_type")
    @jakarta.annotation.Nullable
    private String queryType;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_OUTPUT_FORMATS = "output_formats";
    @XmlElement(name = "output_formats")
    @jakarta.annotation.Nullable
    private List<String> outputFormats;

    public static final String JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT = "default_output_format";
    @XmlElement(name = "default_output_format")
    @jakarta.annotation.Nullable
    private String defaultOutputFormat;

    public static final String JSON_PROPERTY_CRS_DETAILS = "crs_details";
    @XmlElement(name = "crs_details")
    @jakarta.annotation.Nullable
    private List<CrsObject> crsDetails;

    public DataQuery() {
    }

    public DataQuery queryType(@jakarta.annotation.Nullable String queryType) {
        this.queryType = queryType;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_QUERY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "query_type")
    public String getQueryType() {
        return queryType;
    }

    @JsonProperty(JSON_PROPERTY_QUERY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "query_type")
    public void setQueryType(@jakarta.annotation.Nullable String queryType) {
        this.queryType = queryType;
    }

    public DataQuery title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

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

    public DataQuery description(@jakarta.annotation.Nullable String description) {
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

    public DataQuery outputFormats(@jakarta.annotation.Nullable List<String> outputFormats) {
        this.outputFormats = outputFormats;
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

    public DataQuery defaultOutputFormat(@jakarta.annotation.Nullable String defaultOutputFormat) {
        this.defaultOutputFormat = defaultOutputFormat;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default_output_format")
    public String getDefaultOutputFormat() {
        return defaultOutputFormat;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default_output_format")
    public void setDefaultOutputFormat(@jakarta.annotation.Nullable String defaultOutputFormat) {
        this.defaultOutputFormat = defaultOutputFormat;
    }

    public DataQuery crsDetails(@jakarta.annotation.Nullable List<CrsObject> crsDetails) {
        this.crsDetails = crsDetails;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs_details")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<CrsObject> getCrsDetails() {
        return crsDetails;
    }

    @JsonProperty(JSON_PROPERTY_CRS_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs_details")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCrsDetails(@jakarta.annotation.Nullable List<CrsObject> crsDetails) {
        this.crsDetails = crsDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataQuery other = (DataQuery) o;
        return Objects.equals(this.queryType, other.queryType)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.outputFormats, other.outputFormats)
                && Objects.equals(this.defaultOutputFormat, other.defaultOutputFormat)
                && Objects.equals(this.crsDetails, other.crsDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryType, title, description, outputFormats, defaultOutputFormat, crsDetails);
    }

}
