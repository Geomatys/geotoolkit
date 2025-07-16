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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * ZoneInfoStatisticsValue
 */
@JsonPropertyOrder({
    ZoneInfoStatisticsValue.JSON_PROPERTY_MINIMUM,
    ZoneInfoStatisticsValue.JSON_PROPERTY_MAXIMUM,
    ZoneInfoStatisticsValue.JSON_PROPERTY_AVERAGE,
    ZoneInfoStatisticsValue.JSON_PROPERTY_STD_DEV
})
@XmlRootElement(name = "ZoneInfoStatisticsValue")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "ZoneInfoStatisticsValue")
public final class ZoneInfoStatisticsValue extends DataTransferObject {

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    @XmlElement(name = "minimum")
    @jakarta.annotation.Nullable
    private Double minimum;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    @XmlElement(name = "maximum")
    @jakarta.annotation.Nullable
    private Double maximum;

    public static final String JSON_PROPERTY_AVERAGE = "average";
    @XmlElement(name = "average")
    @jakarta.annotation.Nullable
    private Double average;

    public static final String JSON_PROPERTY_STD_DEV = "stdDev";
    @XmlElement(name = "stdDev")
    @jakarta.annotation.Nullable
    private Double stdDev;

    public ZoneInfoStatisticsValue() {
    }

    public ZoneInfoStatisticsValue minimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get minimum
     *
     * @return minimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public Double getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public void setMinimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
    }

    public ZoneInfoStatisticsValue maximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Get maximum
     *
     * @return maximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public Double getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public void setMaximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
    }

    public ZoneInfoStatisticsValue average(@jakarta.annotation.Nullable Double average) {
        this.average = average;
        return this;
    }

    /**
     * Get average
     *
     * @return average
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_AVERAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "average")
    public Double getAverage() {
        return average;
    }

    @JsonProperty(JSON_PROPERTY_AVERAGE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "average")
    public void setAverage(@jakarta.annotation.Nullable Double average) {
        this.average = average;
    }

    public ZoneInfoStatisticsValue stdDev(@jakarta.annotation.Nullable Double stdDev) {
        this.stdDev = stdDev;
        return this;
    }

    /**
     * Get stdDev
     *
     * @return stdDev
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STD_DEV)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stdDev")
    public Double getStdDev() {
        return stdDev;
    }

    @JsonProperty(JSON_PROPERTY_STD_DEV)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "stdDev")
    public void setStdDev(@jakarta.annotation.Nullable Double stdDev) {
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
