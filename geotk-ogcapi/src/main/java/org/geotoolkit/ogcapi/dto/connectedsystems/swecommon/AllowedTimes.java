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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Value constraint for temporal components (Time(Range)): an enumeration of allowed date-times
 * and/or a list of allowed intervals.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class AllowedTimes extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type = "AllowedTimes";

    public static final String JSON_PROPERTY_VALUES = "values";
    private List<Object> values;

    public static final String JSON_PROPERTY_INTERVALS = "intervals";
    private List<List<Object>> intervals;

    public static final String JSON_PROPERTY_SIGNIFICANT_FIGURES = "significantFigures";
    private Integer significantFigures;

    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Enumeration of allowed date-time values (ISO 8601 string, or a special numeric token). (one of values/intervals)
     */
    @JsonProperty(JSON_PROPERTY_VALUES)
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    public void setValues(List<Object> values) {
        this.values = values;
    }

    /**
     * List of [min, max] allowed intervals. (one of values/intervals)
     */
    @JsonProperty(JSON_PROPERTY_INTERVALS)
    public List<List<Object>> getIntervals() {
        return intervals;
    }

    @JsonProperty(JSON_PROPERTY_INTERVALS)
    public void setIntervals(List<List<Object>> intervals) {
        this.intervals = intervals;
    }

    /**
     * Number of significant figures values must be rounded to. (optional, 1-40)
     */
    @JsonProperty(JSON_PROPERTY_SIGNIFICANT_FIGURES)
    public Integer getSignificantFigures() {
        return significantFigures;
    }

    @JsonProperty(JSON_PROPERTY_SIGNIFICANT_FIGURES)
    public void setSignificantFigures(Integer significantFigures) {
        this.significantFigures = significantFigures;
    }

}
