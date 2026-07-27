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
public final class DataQuery extends DataTransferObject {

    public static final String JSON_PROPERTY_QUERY_TYPE = "query_type";
    private String queryType;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_OUTPUT_FORMATS = "output_formats";
    private List<String> outputFormats;

    public static final String JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT = "default_output_format";
    private String defaultOutputFormat;

    public static final String JSON_PROPERTY_CRS_DETAILS = "crs_details";
    private List<CrsObject> crsDetails;

    public DataQuery() {
    }

    public DataQuery queryType(String queryType) {
        this.queryType = queryType;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_QUERY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getQueryType() {
        return queryType;
    }

    @JsonProperty(JSON_PROPERTY_QUERY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public DataQuery title(String title) {
        this.title = title;
        return this;
    }

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

    public DataQuery description(String description) {
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

    public DataQuery outputFormats(List<String> outputFormats) {
        this.outputFormats = outputFormats;
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

    public DataQuery defaultOutputFormat(String defaultOutputFormat) {
        this.defaultOutputFormat = defaultOutputFormat;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDefaultOutputFormat() {
        return defaultOutputFormat;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_OUTPUT_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDefaultOutputFormat(String defaultOutputFormat) {
        this.defaultOutputFormat = defaultOutputFormat;
    }

    public DataQuery crsDetails(List<CrsObject> crsDetails) {
        this.crsDetails = crsDetails;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_CRS_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<CrsObject> getCrsDetails() {
        return crsDetails;
    }

    @JsonProperty(JSON_PROPERTY_CRS_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrsDetails(List<CrsObject> crsDetails) {
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
