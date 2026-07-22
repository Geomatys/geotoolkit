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
 * One axis of a {@link Domain}. The CoverageJSON spec defines four axis schema variants
 * ({@code numericValuesAxis}, {@code numericRegularlySpacedAxis}, {@code stringValuesAxis},
 * {@code tupleValuesAxis}) which are flattened into this single class.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    Axis.JSON_PROPERTY_DATA_TYPE,
    Axis.JSON_PROPERTY_COORDINATES,
    Axis.JSON_PROPERTY_VALUES,
    Axis.JSON_PROPERTY_BOUNDS,
    Axis.JSON_PROPERTY_START,
    Axis.JSON_PROPERTY_STOP,
    Axis.JSON_PROPERTY_NUM
})
@XmlRootElement(name = "Axis")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Axis")
public final class Axis extends DataTransferObject {

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    @jakarta.annotation.Nullable
    private String dataType;

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nullable
    private List<String> coordinates;

    public static final String JSON_PROPERTY_VALUES = "values";
    @XmlElement(name = "values")
    @jakarta.annotation.Nullable
    private List<Object> values;

    public static final String JSON_PROPERTY_BOUNDS = "bounds";
    @XmlElement(name = "bounds")
    @jakarta.annotation.Nullable
    private List<Object> bounds;

    public static final String JSON_PROPERTY_START = "start";
    @XmlElement(name = "start")
    @jakarta.annotation.Nullable
    private Double start;

    public static final String JSON_PROPERTY_STOP = "stop";
    @XmlElement(name = "stop")
    @jakarta.annotation.Nullable
    private Double stop;

    public static final String JSON_PROPERTY_NUM = "num";
    @XmlElement(name = "num")
    @jakarta.annotation.Nullable
    private Integer num;

    public Axis() {
    }

    public Axis dataType(@jakarta.annotation.Nullable String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * {@code "primitive"}, {@code "tuple"} or {@code "polygon"}; only set for a {@code tupleValuesAxis}.
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

    public Axis coordinates(@jakarta.annotation.Nullable List<String> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public Axis addCoordinatesItem(String coordinatesItem) {
        if (this.coordinates == null) {
            this.coordinates = new java.util.ArrayList<>();
        }
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * Names of the component axes; only set for a {@code tupleValuesAxis}.
     *
     * @return coordinates
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nullable List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public Axis values(@jakarta.annotation.Nullable List<Object> values) {
        this.values = values;
        return this;
    }

    public Axis addValuesItem(Object valuesItem) {
        if (this.values == null) {
            this.values = new java.util.ArrayList<>();
        }
        this.values.add(valuesItem);
        return this;
    }

    /**
     * Explicit axis values (numbers, strings, or coordinate tuples/rings depending on
     * {@link #getDataType()}); absent for a {@code numericRegularlySpacedAxis} which uses
     * {@link #getStart()}/{@link #getStop()}/{@link #getNum()} instead.
     *
     * @return values
     */
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

    public Axis bounds(@jakarta.annotation.Nullable List<Object> bounds) {
        this.bounds = bounds;
        return this;
    }

    public Axis addBoundsItem(Object boundsItem) {
        if (this.bounds == null) {
            this.bounds = new java.util.ArrayList<>();
        }
        this.bounds.add(boundsItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bounds")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Object> getBounds() {
        return bounds;
    }

    @JsonProperty(JSON_PROPERTY_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bounds")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBounds(@jakarta.annotation.Nullable List<Object> bounds) {
        this.bounds = bounds;
    }

    public Axis start(@jakarta.annotation.Nullable Double start) {
        this.start = start;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_START)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "start")
    public Double getStart() {
        return start;
    }

    @JsonProperty(JSON_PROPERTY_START)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "start")
    public void setStart(@jakarta.annotation.Nullable Double start) {
        this.start = start;
    }

    public Axis stop(@jakarta.annotation.Nullable Double stop) {
        this.stop = stop;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STOP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stop")
    public Double getStop() {
        return stop;
    }

    @JsonProperty(JSON_PROPERTY_STOP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stop")
    public void setStop(@jakarta.annotation.Nullable Double stop) {
        this.stop = stop;
    }

    public Axis num(@jakarta.annotation.Nullable Integer num) {
        this.num = num;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "num")
    public Integer getNum() {
        return num;
    }

    @JsonProperty(JSON_PROPERTY_NUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "num")
    public void setNum(@jakarta.annotation.Nullable Integer num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Axis other = (Axis) o;
        return Objects.equals(this.dataType, other.dataType)
                && Objects.equals(this.coordinates, other.coordinates)
                && Objects.equals(this.values, other.values)
                && Objects.equals(this.bounds, other.bounds)
                && Objects.equals(this.start, other.start)
                && Objects.equals(this.stop, other.stop)
                && Objects.equals(this.num, other.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, coordinates, values, bounds, start, stop, num);
    }

}
