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
package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.atom.xml.Link;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        FileFormat.JSON_PROPERTY_TITLE,
        FileFormat.JSON_PROPERTY_DESCRIPTION,
        FileFormat.JSON_PROPERTY_GIS_DATA_TYPES,
        FileFormat.JSON_PROPERTY_PARAMETERS,
        FileFormat.JSON_PROPERTY_LINKS
})
@XmlRootElement(name = "FileFormat")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FileFormat")
public class FileFormat extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    /**
     * Gets or Sets gisDataTypes
     */
    public enum GisDataTypesEnum {
        RASTER("raster"),
        VECTOR("vector"),
        TABLE("table"),
        OTHER("other");

        private String value;

        GisDataTypesEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static GisDataTypesEnum fromValue(String value) {
            for (GisDataTypesEnum b : GisDataTypesEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_GIS_DATA_TYPES = "gis_data_types";
    @XmlElementWrapper(name = "gisDataTypes")
    @XmlElement(name = "gisDataType")
    @JacksonXmlElementWrapper(localName = "gisDataTypes", useWrapping = false)
    @JacksonXmlProperty(localName = "gisDataType")
    @jakarta.annotation.Nonnull
    private List<GisDataTypesEnum> gisDataTypes = new ArrayList<>();

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    @XmlTransient
    @jakarta.annotation.Nullable
    private Map<String, Argument> parameters = null;

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @JacksonXmlElementWrapper(localName = "links", useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    @jakarta.annotation.Nullable
    private List<Link> links = null;

    public FileFormat title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
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

    public FileFormat description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
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

    public FileFormat gisDataTypes(List<GisDataTypesEnum> gisDataTypes) {
        this.gisDataTypes = gisDataTypes;
        return this;
    }

    public FileFormat addGisDataTypesItem(GisDataTypesEnum gisDataTypesItem) {
        this.gisDataTypes.add(gisDataTypesItem);
        return this;
    }

    /**
     * Get gisDataTypes
     *
     * @return gisDataTypes
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_GIS_DATA_TYPES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_GIS_DATA_TYPES)
    public List<GisDataTypesEnum> getGisDataTypes() {
        return gisDataTypes;
    }

    @JsonProperty(JSON_PROPERTY_GIS_DATA_TYPES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_GIS_DATA_TYPES)
    public void setGisDataTypes(@jakarta.annotation.Nonnull List<GisDataTypesEnum> gisDataTypes) {
        this.gisDataTypes = gisDataTypes;
    }

    public FileFormat parameters(Map<String, Argument> parameters) {
        this.parameters = parameters;
        return this;
    }

    public FileFormat putParametersItem(String key, Argument parametersItem) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * Get parameters
     *
     * @return parameters
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Argument> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameters(@jakarta.annotation.Nullable Map<String, Argument> parameters) {
        this.parameters = parameters;
    }

    public FileFormat links(List<Link> links) {
        this.links = links;
        return this;
    }

    public FileFormat addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_LINKS)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileFormat fileFormat = (FileFormat) o;
        return Objects.equals(this.title, fileFormat.title) &&
                Objects.equals(this.description, fileFormat.description) &&
                Objects.equals(this.gisDataTypes, fileFormat.gisDataTypes) &&
                Objects.equals(this.parameters, fileFormat.parameters) &&
                Objects.equals(this.links, fileFormat.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, gisDataTypes, parameters, links);
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
