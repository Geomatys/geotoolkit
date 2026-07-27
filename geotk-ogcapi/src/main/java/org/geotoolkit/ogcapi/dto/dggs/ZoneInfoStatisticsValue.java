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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * ZoneInfoStatisticsValue
 */
@JsonPropertyOrder({
    ZoneInfoStatisticsValue.JSON_PROPERTY_MINIMUM,
    ZoneInfoStatisticsValue.JSON_PROPERTY_MAXIMUM,
    ZoneInfoStatisticsValue.JSON_PROPERTY_AVERAGE,
    ZoneInfoStatisticsValue.JSON_PROPERTY_STD_DEV
})
public final class ZoneInfoStatisticsValue extends DataTransferObject {

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    private Double minimum;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    private Double maximum;

    public static final String JSON_PROPERTY_AVERAGE = "average";
    private Double average;

    public static final String JSON_PROPERTY_STD_DEV = "stdDev";
    private Double stdDev;

    public ZoneInfoStatisticsValue() {
    }

    public ZoneInfoStatisticsValue minimum(Double minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get minimum
     *
     * @return minimum
     */
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public ZoneInfoStatisticsValue maximum(Double maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Get maximum
     *
     * @return maximum
     */
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public ZoneInfoStatisticsValue average(Double average) {
        this.average = average;
        return this;
    }

    /**
     * Get average
     *
     * @return average
     */
    @JsonProperty(JSON_PROPERTY_AVERAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getAverage() {
        return average;
    }

    @JsonProperty(JSON_PROPERTY_AVERAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAverage(Double average) {
        this.average = average;
    }

    public ZoneInfoStatisticsValue stdDev(Double stdDev) {
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
     * Return true if this zone_info_statistics_value object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZoneInfoStatisticsValue zoneInfoStatisticsValue = (ZoneInfoStatisticsValue) o;
        return Objects.equals(this.minimum, zoneInfoStatisticsValue.minimum)
                && Objects.equals(this.maximum, zoneInfoStatisticsValue.maximum)
                && Objects.equals(this.average, zoneInfoStatisticsValue.average)
                && Objects.equals(this.stdDev, zoneInfoStatisticsValue.stdDev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum, average, stdDev);
    }

}
