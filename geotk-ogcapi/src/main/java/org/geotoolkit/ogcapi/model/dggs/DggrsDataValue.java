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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * DggsJsonValuesValueInner
 */
@JsonPropertyOrder({
    DggrsDataValue.JSON_PROPERTY_DEPTH,
    DggrsDataValue.JSON_PROPERTY_SHAPE,
    DggrsDataValue.JSON_PROPERTY_DATA
})
@XmlRootElement(name = "DggsJsonValuesValueInner")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJsonValuesValueInner")
public final class DggrsDataValue extends DataTransferObject {

    public static final String JSON_PROPERTY_DEPTH = "depth";
    @XmlElement(name = "depth")
    @jakarta.annotation.Nullable
    private Integer depth;

    public static final String JSON_PROPERTY_SHAPE = "shape";
    @XmlElement(name = "shape")
    @jakarta.annotation.Nullable
    private DggrsDataValueShape shape;

    /**
     * Specification says it should be a Double, we extent that to support other types.
     */
    public static final String JSON_PROPERTY_DATA = "data";
    @XmlElement(name = "data")
    @jakarta.annotation.Nonnull
    private List<Object> data = new ArrayList<>();

    public DggrsDataValue() {
    }

    public DggrsDataValue depth(@jakarta.annotation.Nullable Integer depth) {
        this.depth = depth;
        return this;
    }

    /**
     * Get depth
     *
     * @return depth
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "depth")
    public Integer getDepth() {
        return depth;
    }

    @JsonProperty(JSON_PROPERTY_DEPTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "depth")
    public void setDepth(@jakarta.annotation.Nullable Integer depth) {
        this.depth = depth;
    }

    public DggrsDataValue shape(@jakarta.annotation.Nullable DggrsDataValueShape shape) {
        this.shape = shape;
        return this;
    }

    /**
     * Get shape
     *
     * @return shape
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SHAPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "shape")
    public DggrsDataValueShape getShape() {
        return shape;
    }

    @JsonProperty(JSON_PROPERTY_SHAPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "shape")
    public void setShape(@jakarta.annotation.Nullable DggrsDataValueShape shape) {
        this.shape = shape;
    }

    public DggrsDataValue data(@jakarta.annotation.Nonnull List<Object> data) {
        this.data = data;
        return this;
    }

    public DggrsDataValue addDataItem(Object dataItem) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     *
     * @return data
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DATA)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "data")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getData() {
        return data;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "data")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setData(@jakarta.annotation.Nonnull List<Object> data) {
        this.data = data;
    }

    /**
     * Return true if this dggs_json_values_value_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsDataValue dggsJsonValuesValueInner = (DggrsDataValue) o;
        return Objects.equals(this.depth, dggsJsonValuesValueInner.depth)
                && Objects.equals(this.shape, dggsJsonValuesValueInner.shape)
                && Objects.equals(this.data, dggsJsonValuesValueInner.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depth, shape, data);
    }

}
