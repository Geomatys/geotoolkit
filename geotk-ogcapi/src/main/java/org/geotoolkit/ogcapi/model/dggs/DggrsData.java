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
package org.geotoolkit.ogcapi.model.dggs;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * DggsJson
 */
@JsonPropertyOrder({
    DggrsData.JSON_PROPERTY_DGGRS,
    DggrsData.JSON_PROPERTY_ZONE_ID,
    DggrsData.JSON_PROPERTY_DEPTHS,
    DggrsData.JSON_PROPERTY_SCHEMA,
    DggrsData.JSON_PROPERTY_DIMENSIONS,
    DggrsData.JSON_PROPERTY_VALUES
})
@XmlRootElement(name = "DggsJson")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJson")
public final class DggrsData extends DataTransferObject {

    public static final String JSON_PROPERTY_DGGRS = "dggrs";
    @XmlElement(name = "dggrs")
    @jakarta.annotation.Nonnull
    private URI dggrs;

    public static final String JSON_PROPERTY_ZONE_ID = "zoneId";
    @XmlElement(name = "zoneId")
    @jakarta.annotation.Nonnull
    private String zoneId;

    public static final String JSON_PROPERTY_DEPTHS = "depths";
    @XmlElement(name = "depths")
    @jakarta.annotation.Nonnull
    private List<Integer> depths = new ArrayList<>();

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    @XmlElement(name = "schema")
    @jakarta.annotation.Nullable
    private Schema schema;

    public static final String JSON_PROPERTY_DIMENSIONS = "dimensions";
    @XmlElement(name = "dimensions")
    @jakarta.annotation.Nullable
    private List<Dimension> dimensions = new ArrayList<>();

    public static final String JSON_PROPERTY_VALUES = "values";
    @XmlElement(name = "values")
    @jakarta.annotation.Nonnull
    private Map<String, List<DggrsDataValue>> values = new HashMap<>();

    public DggrsData() {
    }

    public DggrsData dggrs(@jakarta.annotation.Nonnull URI dggrs) {
        this.dggrs = dggrs;
        return this;
    }

    /**
     * Get dggrs
     *
     * @return dggrs
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dggrs")
    public URI getDggrs() {
        return dggrs;
    }

    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dggrs")
    public void setDggrs(@jakarta.annotation.Nonnull URI dggrs) {
        this.dggrs = dggrs;
    }

    public DggrsData zoneId(@jakarta.annotation.Nonnull String zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    /**
     * Get zoneId
     *
     * @return zoneId
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ZONE_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zoneId")
    public String getZoneId() {
        return zoneId;
    }

    @JsonProperty(JSON_PROPERTY_ZONE_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zoneId")
    public void setZoneId(@jakarta.annotation.Nonnull String zoneId) {
        this.zoneId = zoneId;
    }

    public DggrsData depths(@jakarta.annotation.Nonnull List<Integer> depths) {
        this.depths = depths;
        return this;
    }

    public DggrsData addDepthsItem(Integer depthsItem) {
        if (this.depths == null) {
            this.depths = new ArrayList<>();
        }
        this.depths.add(depthsItem);
        return this;
    }

    /**
     * Get depths
     *
     * @return depths
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DEPTHS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "depths")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Integer> getDepths() {
        return depths;
    }

    @JsonProperty(JSON_PROPERTY_DEPTHS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "depths")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setDepths(@jakarta.annotation.Nonnull List<Integer> depths) {
        this.depths = depths;
    }

    public DggrsData schema(@jakarta.annotation.Nullable Schema schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Get schema
     *
     * @return schema
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public Schema getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public void setSchema(@jakarta.annotation.Nullable Schema schema) {
        this.schema = schema;
    }

    public DggrsData dimensions(@jakarta.annotation.Nullable List<Dimension> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public DggrsData addDimensionsItem(Dimension dimensionsItem) {
        if (this.dimensions == null) {
            this.dimensions = new ArrayList<>();
        }
        this.dimensions.add(dimensionsItem);
        return this;
    }

    /**
     * Get dimensions
     *
     * @return dimensions
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dimensions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Dimension> getDimensions() {
        return dimensions;
    }

    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dimensions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setDimensions(@jakarta.annotation.Nullable List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public DggrsData values(@jakarta.annotation.Nonnull Map<String, List<DggrsDataValue>> values) {
        this.values = values;
        return this;
    }

    public DggrsData putValuesItem(String key, List<DggrsDataValue> valuesItem) {
        if (this.values == null) {
            this.values = new HashMap<>();
        }
        this.values.put(key, valuesItem);
        return this;
    }

    /**
     * Get values
     *
     * @return values
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public Map<String, List<DggrsDataValue>> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setValues(@jakarta.annotation.Nonnull Map<String, List<DggrsDataValue>> values) {
        this.values = values;
    }

    /**
     * Return true if this dggs-json object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsData dggsJson = (DggrsData) o;
        return Objects.equals(this.dggrs, dggsJson.dggrs)
                && Objects.equals(this.zoneId, dggsJson.zoneId)
                && Objects.equals(this.depths, dggsJson.depths)
                && Objects.equals(this.schema, dggsJson.schema)
                && Objects.equals(this.dimensions, dggsJson.dimensions)
                && Objects.equals(this.values, dggsJson.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dggrs, zoneId, depths, schema, dimensions, values);
    }

}
