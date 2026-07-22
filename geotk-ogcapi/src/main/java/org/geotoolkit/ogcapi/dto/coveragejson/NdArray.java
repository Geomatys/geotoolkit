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
package org.geotoolkit.ogcapi.dto.coveragejson;

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

/**
 * A flattened multidimensional array of values, per the CoverageJSON {@code NdArray} object,
 * used as the value type of a {@link CoverageJson}'s {@code ranges} map.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    NdArray.JSON_PROPERTY_TYPE,
    NdArray.JSON_PROPERTY_DATA_TYPE,
    NdArray.JSON_PROPERTY_AXIS_NAMES,
    NdArray.JSON_PROPERTY_SHAPE,
    NdArray.JSON_PROPERTY_VALUES
})
@XmlRootElement(name = "NdArray")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "NdArray")
public final class NdArray extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    @jakarta.annotation.Nullable
    private String dataType;

    public static final String JSON_PROPERTY_AXIS_NAMES = "axisNames";
    @XmlElement(name = "axisNames")
    @jakarta.annotation.Nullable
    private List<String> axisNames;

    public static final String JSON_PROPERTY_SHAPE = "shape";
    @XmlElement(name = "shape")
    @jakarta.annotation.Nullable
    private List<Integer> shape;

    public static final String JSON_PROPERTY_VALUES = "values";
    @XmlElement(name = "values")
    @jakarta.annotation.Nullable
    private List<Object> values;

    public NdArray() {
    }

    public NdArray type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * Always {@code "NdArray"}.
     *
     * @return type
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable String type) {
        this.type = type;
    }

    public NdArray dataType(@jakarta.annotation.Nullable String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * {@code "float"}, {@code "integer"} or {@code "string"}.
     *
     * @return dataType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dataType")
    public void setDataType(@jakarta.annotation.Nullable String dataType) {
        this.dataType = dataType;
    }

    public NdArray axisNames(@jakarta.annotation.Nullable List<String> axisNames) {
        this.axisNames = axisNames;
        return this;
    }

    public NdArray addAxisNamesItem(String axisNamesItem) {
        if (this.axisNames == null) {
            this.axisNames = new java.util.ArrayList<>();
        }
        this.axisNames.add(axisNamesItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AXIS_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "axisNames")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getAxisNames() {
        return axisNames;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_NAMES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "axisNames")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setAxisNames(@jakarta.annotation.Nullable List<String> axisNames) {
        this.axisNames = axisNames;
    }

    public NdArray shape(@jakarta.annotation.Nullable List<Integer> shape) {
        this.shape = shape;
        return this;
    }

    public NdArray addShapeItem(Integer shapeItem) {
        if (this.shape == null) {
            this.shape = new java.util.ArrayList<>();
        }
        this.shape.add(shapeItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SHAPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "shape")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Integer> getShape() {
        return shape;
    }

    @JsonProperty(JSON_PROPERTY_SHAPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "shape")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setShape(@jakarta.annotation.Nullable List<Integer> shape) {
        this.shape = shape;
    }

    public NdArray values(@jakarta.annotation.Nullable List<Object> values) {
        this.values = values;
        return this;
    }

    public NdArray addValuesItem(Object valuesItem) {
        if (this.values == null) {
            this.values = new java.util.ArrayList<>();
        }
        this.values.add(valuesItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "values")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setValues(@jakarta.annotation.Nullable List<Object> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NdArray other = (NdArray) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.dataType, other.dataType)
                && Objects.equals(this.axisNames, other.axisNames)
                && Objects.equals(this.shape, other.shape)
                && Objects.equals(this.values, other.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, dataType, axisNames, shape, values);
    }

}
