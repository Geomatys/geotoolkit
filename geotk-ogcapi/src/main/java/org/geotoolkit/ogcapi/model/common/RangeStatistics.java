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
package org.geotoolkit.ogcapi.model.common;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

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
@XmlRootElement(name = "RangeStatistics")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "RangeStatistics")
public final class RangeStatistics extends DataTransferObject {

    public static final String JSON_PROPERTY_MIN = "min";
    @XmlElement(name = "min")
    @jakarta.annotation.Nullable
    private Double min;

    public static final String JSON_PROPERTY_MAX = "max";
    @XmlElement(name = "max")
    @jakarta.annotation.Nullable
    private Double max;

    public static final String JSON_PROPERTY_MEAN = "mean";
    @XmlElement(name = "mean")
    @jakarta.annotation.Nullable
    private Double mean;

    public static final String JSON_PROPERTY_STD_DEV = "stdDev";
    @XmlElement(name = "stdDev")
    @jakarta.annotation.Nullable
    private Double stdDev;

    public RangeStatistics() {
    }

    public RangeStatistics(Double min, Double max, Double mean, Double stdDev) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.stdDev = stdDev;
    }

    public RangeStatistics min(@jakarta.annotation.Nullable Double min) {
        this.min = min;
        return this;
    }

    /**
     * Get min
     *
     * @return min
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "min")
    public Double getMin() {
        return min;
    }

    @JsonProperty(JSON_PROPERTY_MIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "min")
    public void setMin(@jakarta.annotation.Nullable Double min) {
        this.min = min;
    }

    public RangeStatistics max(@jakarta.annotation.Nullable Double max) {
        this.max = max;
        return this;
    }

    /**
     * Get max
     *
     * @return max
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "max")
    public Double getMax() {
        return max;
    }

    @JsonProperty(JSON_PROPERTY_MAX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "max")
    public void setMax(@jakarta.annotation.Nullable Double max) {
        this.max = max;
    }

    public RangeStatistics mean(@jakarta.annotation.Nullable Double mean) {
        this.mean = mean;
        return this;
    }

    /**
     * Get mean
     *
     * @return mean
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MEAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mean")
    public Double getMean() {
        return mean;
    }

    @JsonProperty(JSON_PROPERTY_MEAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mean")
    public void setMean(@jakarta.annotation.Nullable Double mean) {
        this.mean = mean;
    }

    public RangeStatistics stdDev(@jakarta.annotation.Nullable Double stdDev) {
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