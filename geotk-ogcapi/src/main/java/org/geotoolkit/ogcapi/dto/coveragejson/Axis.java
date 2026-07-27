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
public final class Axis extends DataTransferObject {

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    private String dataType;

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    private List<String> coordinates;

    public static final String JSON_PROPERTY_VALUES = "values";
    private List<Object> values;

    public static final String JSON_PROPERTY_BOUNDS = "bounds";
    private List<Object> bounds;

    public static final String JSON_PROPERTY_START = "start";
    private Double start;

    public static final String JSON_PROPERTY_STOP = "stop";
    private Double stop;

    public static final String JSON_PROPERTY_NUM = "num";
    private Integer num;

    public Axis() {
    }

    public Axis dataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * {@code "primitive"}, {@code "tuple"} or {@code "polygon"}; only set for a {@code tupleValuesAxis}.
     *
     * @return dataType
     */
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Axis coordinates(List<String> coordinates) {
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
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public Axis values(List<Object> values) {
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
    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setValues(List<Object> values) {
        this.values = values;
    }

    public Axis bounds(List<Object> bounds) {
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

    @JsonProperty(JSON_PROPERTY_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<Object> getBounds() {
        return bounds;
    }

    @JsonProperty(JSON_PROPERTY_BOUNDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setBounds(List<Object> bounds) {
        this.bounds = bounds;
    }

    public Axis start(Double start) {
        this.start = start;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_START)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getStart() {
        return start;
    }

    @JsonProperty(JSON_PROPERTY_START)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStart(Double start) {
        this.start = start;
    }

    public Axis stop(Double stop) {
        this.stop = stop;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_STOP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getStop() {
        return stop;
    }

    @JsonProperty(JSON_PROPERTY_STOP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStop(Double stop) {
        this.stop = stop;
    }

    public Axis num(Integer num) {
        this.num = num;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_NUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getNum() {
        return num;
    }

    @JsonProperty(JSON_PROPERTY_NUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setNum(Integer num) {
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
