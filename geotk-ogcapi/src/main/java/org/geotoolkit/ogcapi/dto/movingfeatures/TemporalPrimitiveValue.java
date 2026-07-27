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
package org.geotoolkit.ogcapi.dto.movingfeatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * MF-JSON {@code TemporalPrimitiveValue} object: a sequence of scalar values, each attached to
 * one of the given {@code datetimes}.
 *
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    TemporalPrimitiveValue.JSON_PROPERTY_DATETIMES,
    TemporalPrimitiveValue.JSON_PROPERTY_VALUES,
    TemporalPrimitiveValue.JSON_PROPERTY_INTERPOLATION
})
public final class TemporalPrimitiveValue extends DataTransferObject {

    public static final String JSON_PROPERTY_DATETIMES = "datetimes";
    private List<String> datetimes;

    public static final String JSON_PROPERTY_VALUES = "values";
    //ponytail: spec declares "values" as a bare oneOf(number,string,boolean) scalar, but every
    //example (and any real payload) sends an array of such scalars, one per datetime; modeled
    //as a raw list to match actual usage rather than the (apparently mistaken) schema.
    private List<Object> values;

    public static final String JSON_PROPERTY_INTERPOLATION = "interpolation";
    private String interpolation;

    public TemporalPrimitiveValue() {
    }

    public TemporalPrimitiveValue datetimes(List<String> datetimes) {
        this.datetimes = datetimes;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<String> getDatetimes() {
        return datetimes;
    }

    @JsonProperty(JSON_PROPERTY_DATETIMES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setDatetimes(List<String> datetimes) {
        this.datetimes = datetimes;
    }

    public TemporalPrimitiveValue values(List<Object> values) {
        this.values = values;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setValues(List<Object> values) {
        this.values = values;
    }

    public TemporalPrimitiveValue interpolation(String interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getInterpolation() {
        return interpolation;
    }

    @JsonProperty(JSON_PROPERTY_INTERPOLATION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setInterpolation(String interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemporalPrimitiveValue other = (TemporalPrimitiveValue) o;
        return Objects.equals(this.datetimes, other.datetimes)
                && Objects.equals(this.values, other.values)
                && Objects.equals(this.interpolation, other.interpolation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetimes, values, interpolation);
    }

}
