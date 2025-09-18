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
package org.geotoolkit.ogcapi.model.geojson;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import org.geotoolkit.ogcapi.model.DataTransferObject;


/**
 * FeatureTime
 */
@JsonPropertyOrder({
    JSONFGTime.PROPERTY_DATE,
    JSONFGTime.PROPERTY_TIMESTAMP,
    JSONFGTime.PROPERTY_INTERVAL
})
@XmlRootElement(name = "FeatureTime")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FeatureTime")
public class JSONFGTime extends DataTransferObject {

    public static final String PROPERTY_DATE = "date";
    public static final String PROPERTY_TIMESTAMP = "timestamp";
    public static final String PROPERTY_INTERVAL = "interval";

    @XmlElement(name = PROPERTY_DATE)
    @jakarta.annotation.Nullable
    private String date;

    @XmlElement(name = "timestamp")
    @jakarta.annotation.Nullable
    private String timestamp;

    @XmlElement(name = "interval")
    @jakarta.annotation.Nullable
    private List<String> interval = new ArrayList<>();

    public JSONFGTime() {
    }

    /**
     * Get date
     *
     * @return date
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_DATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_DATE)
    public String getDate() {
        return date;
    }

    @JsonProperty(PROPERTY_DATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_DATE)
    public void setDate(@jakarta.annotation.Nullable String date) {
        this.date = date;
    }

    /**
     * Get timestamp
     *
     * @return timestamp
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_TIMESTAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_TIMESTAMP)
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty(PROPERTY_TIMESTAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_TIMESTAMP)
    public void setTimestamp(@jakarta.annotation.Nullable String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get interval
     *
     * @return interval
     */
    @jakarta.annotation.Nullable
    @JsonProperty(PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_INTERVAL)
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getInterval() {
        return interval;
    }

    @JsonProperty(PROPERTY_INTERVAL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = PROPERTY_INTERVAL)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setInterval(@jakarta.annotation.Nullable List<String> interval) {
        this.interval = interval;
    }

    /**
     * Return true if this Feature_time object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONFGTime other = (JSONFGTime) o;
        return Objects.equals(this.date, other.date)
                && Objects.equals(this.timestamp, other.timestamp)
                && Objects.equals(this.interval, other.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, timestamp, interval);
    }

}
