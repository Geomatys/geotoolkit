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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * DggsJsonValuesValueInnerShape
 */
@JsonPropertyOrder({
    DggrsDataValueShape.JSON_PROPERTY_COUNT,
    DggrsDataValueShape.JSON_PROPERTY_SUB_ZONES,
    DggrsDataValueShape.JSON_PROPERTY_DIMENSIONS
})
@XmlRootElement(name = "DggsJsonValuesValueInnerShape")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJsonValuesValueInnerShape")
public final class DggrsDataValueShape extends DataTransferObject {

    public static final String JSON_PROPERTY_COUNT = "count";
    @XmlElement(name = "count")
    @jakarta.annotation.Nonnull
    private Integer count;

    public static final String JSON_PROPERTY_SUB_ZONES = "subZones";
    @XmlElement(name = "subZones")
    @jakarta.annotation.Nonnull
    private Integer subZones;

    public static final String JSON_PROPERTY_DIMENSIONS = "dimensions";
    @XmlElement(name = "dimensions")
    @jakarta.annotation.Nullable
    private Map<String, Integer> dimensions = new HashMap<>();

    public DggrsDataValueShape() {
    }

    public DggrsDataValueShape count(@jakarta.annotation.Nonnull Integer count) {
        this.count = count;
        return this;
    }

    /**
     * Get count
     *
     * @return count
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COUNT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "count")
    public Integer getCount() {
        return count;
    }

    @JsonProperty(JSON_PROPERTY_COUNT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "count")
    public void setCount(@jakarta.annotation.Nonnull Integer count) {
        this.count = count;
    }

    public DggrsDataValueShape subZones(@jakarta.annotation.Nonnull Integer subZones) {
        this.subZones = subZones;
        return this;
    }

    /**
     * Get subZones
     *
     * @return subZones
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SUB_ZONES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "subZones")
    public Integer getSubZones() {
        return subZones;
    }

    @JsonProperty(JSON_PROPERTY_SUB_ZONES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "subZones")
    public void setSubZones(@jakarta.annotation.Nonnull Integer subZones) {
        this.subZones = subZones;
    }

    public DggrsDataValueShape dimensions(@jakarta.annotation.Nullable Map<String, Integer> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public DggrsDataValueShape putDimensionsItem(String key, Integer dimensionsItem) {
        if (this.dimensions == null) {
            this.dimensions = new HashMap<>();
        }
        this.dimensions.put(key, dimensionsItem);
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
    public Map<String, Integer> getDimensions() {
        return dimensions;
    }

    @JsonProperty(JSON_PROPERTY_DIMENSIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dimensions")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setDimensions(@jakarta.annotation.Nullable Map<String, Integer> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Return true if this dggs_json_values_value_inner_shape object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsDataValueShape dggsJsonValuesValueInnerShape = (DggrsDataValueShape) o;
        return Objects.equals(this.count, dggsJsonValuesValueInnerShape.count)
                && Objects.equals(this.subZones, dggsJsonValuesValueInnerShape.subZones)
                && Objects.equals(this.dimensions, dggsJsonValuesValueInnerShape.dimensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, subZones, dimensions);
    }

}
