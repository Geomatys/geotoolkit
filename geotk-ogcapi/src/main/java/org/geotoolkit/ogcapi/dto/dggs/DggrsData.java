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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.jsonschema.JSONSchema;

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
public final class DggrsData extends DataTransferObject {

    public static final String JSON_PROPERTY_DGGRS = "dggrs";
    private String dggrs;

    public static final String JSON_PROPERTY_ZONE_ID = "zoneId";
    private String zoneId;

    public static final String JSON_PROPERTY_DEPTHS = "depths";
    private List<Integer> depths = new ArrayList<>();

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    private JSONSchema schema;

    public static final String JSON_PROPERTY_DIMENSIONS = "dimensions";
    private List<Dimension> dimensions = new ArrayList<>();

    public static final String JSON_PROPERTY_VALUES = "values";
    private Map<String, List<DggrsDataValue>> values = new HashMap<>();

    public DggrsData() {
    }

    public DggrsData dggrs(String dggrs) {
        this.dggrs = dggrs;
        return this;
    }

    /**
     * Get dggrs
     *
     * @return dggrs
     */
    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getDggrs() {
        return dggrs;
    }

    @JsonProperty(JSON_PROPERTY_DGGRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDggrs(String dggrs) {
        this.dggrs = dggrs;
    }

    public DggrsData zoneId(String zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    /**
     * Get zoneId
     *
     * @return zoneId
     */
    @JsonProperty(JSON_PROPERTY_ZONE_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getZoneId() {
        return zoneId;
    }

    @JsonProperty(JSON_PROPERTY_ZONE_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public DggrsData depths(List<Integer> depths) {
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
    @JsonProperty(JSON_PROPERTY_DEPTHS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Integer> getDepths() {
        return depths;
    }

    @JsonProperty(JSON_PROPERTY_DEPTHS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDepths(List<Integer> depths) {
        this.depths = depths;
    }

    public DggrsData schema(JSONSchema schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Get schema
     *
     * @return schema
     */
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public JSONSchema getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSchema(JSONSchema schema) {
        this.schema = schema;
    }

    public DggrsData dimensions(List<Dimension> dimensions) {
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
    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Dimension> getDimensions() {
        return dimensions;
    }

    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public DggrsData values(Map<String, List<DggrsDataValue>> values) {
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
    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, List<DggrsDataValue>> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setValues(Map<String, List<DggrsDataValue>> values) {
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
