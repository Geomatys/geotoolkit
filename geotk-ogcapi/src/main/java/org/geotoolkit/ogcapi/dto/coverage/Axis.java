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
package org.geotoolkit.ogcapi.dto.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        Axis.JSON_PROPERTY_TYPE,
        Axis.JSON_PROPERTY_AXIS_LABEL,
        Axis.JSON_PROPERTY_LOWER_BOUND,
        Axis.JSON_PROPERTY_UPPER_BOUND
})
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type")
@JsonSubTypes({
  @Type(value = RegularAxis.class, name = "RegularAxis"),
  @Type(value = IndexAxis.class, name = "IndexAxis"),
  @Type(value = IrregularAxis.class, name = "IrregularAxis")
})
public abstract class Axis extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_AXIS_LABEL = "axisLabel";
    private String axisLabel;

    public static final String JSON_PROPERTY_LOWER_BOUND = "lowerBound";
    private Object lowerBound;

    public static final String JSON_PROPERTY_UPPER_BOUND = "upperBound";
    private Object upperBound;

    public Axis() {}

    public Axis(String type, String axisLabel, Object lowerBound, Object upperBound) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getAxisLabel() {
        return axisLabel;
    }

    @JsonProperty(JSON_PROPERTY_AXIS_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setAxisLabel(String axisLabel) {
        this.axisLabel = axisLabel;
    }

    @JsonProperty(JSON_PROPERTY_LOWER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Object getLowerBound() {
        return lowerBound;
    }

    @JsonProperty(JSON_PROPERTY_LOWER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setLowerBound(Object lowerBound) {
        this.lowerBound = lowerBound;
    }

    @JsonProperty(JSON_PROPERTY_UPPER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Object getUpperBound() {
        return upperBound;
    }

    @JsonProperty(JSON_PROPERTY_UPPER_BOUND)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUpperBound(Object upperBound) {
        this.upperBound = upperBound;
    }
}
