/*
 * Geotoolkit - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2026, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.dto.jsonschema;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * RangeStatistics
 *
 * @author Quentin BIALOTA (Geomatys)
 */
@JsonPropertyOrder({
        RangeStatistics.JSON_PROPERTY_MIN,
        RangeStatistics.JSON_PROPERTY_MAX,
        RangeStatistics.JSON_PROPERTY_MEAN,
        RangeStatistics.JSON_PROPERTY_STD_DEV
})
public final class RangeStatistics extends DataTransferObject {

    public static final String JSON_PROPERTY_MIN = "min";
    private Double min;

    public static final String JSON_PROPERTY_MAX = "max";
    private Double max;

    public static final String JSON_PROPERTY_MEAN = "mean";
    private Double mean;

    public static final String JSON_PROPERTY_STD_DEV = "stdDev";
    private Double stdDev;

    public RangeStatistics() {
    }

    public RangeStatistics(Double min, Double max, Double mean, Double stdDev) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDev = stdDev;
    }

    public RangeStatistics min(Double min) {
        this.min = min;
        return this;
    }

    /**
     * Get min
     *
     * @return min
     */
    @JsonProperty(JSON_PROPERTY_MIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMin() {
        return min;
    }

    @JsonProperty(JSON_PROPERTY_MIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMin(Double min) {
        this.min = min;
    }

    public RangeStatistics max(Double max) {
        this.max = max;
        return this;
    }

    /**
     * Get max
     *
     * @return max
     */
    @JsonProperty(JSON_PROPERTY_MAX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMax() {
        return max;
    }

    @JsonProperty(JSON_PROPERTY_MAX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMax(Double max) {
        this.max = max;
    }

    public RangeStatistics mean(Double mean) {
        this.mean = mean;
        return this;
    }

    /**
     * Get mean
     *
     * @return mean
     */
    @JsonProperty(JSON_PROPERTY_MEAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMean() {
        return mean;
    }

    @JsonProperty(JSON_PROPERTY_MEAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMean(Double mean) {
        this.mean = mean;
    }

    public RangeStatistics stdDev(Double stdDev) {
        this.stdDev = stdDev;
        return this;
    }

    /**
     * Get stdDev
     *
     * @return stdDev
     */
    @JsonProperty(JSON_PROPERTY_STD_DEV)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getStdDev() {
        return stdDev;
    }

    @JsonProperty(JSON_PROPERTY_STD_DEV)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    /**
     * Return true if this RangeStatistics object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RangeStatistics rangeStatistics = (RangeStatistics) o;
        return Objects.equals(this.min, rangeStatistics.min)
                && Objects.equals(this.max, rangeStatistics.max)
                && Objects.equals(this.mean, rangeStatistics.mean)
                && Objects.equals(this.stdDev, rangeStatistics.stdDev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, mean, stdDev);
    }

}
