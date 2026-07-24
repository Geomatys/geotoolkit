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
package org.geotoolkit.ogcapi.dto.sensorthings;

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
import java.util.Objects;

/**
 * A snapshot of the set of Locations a Thing had at a given point in time.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.3.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#historicallocation
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    HistoricalLocation.JSON_PROPERTY_TIME,
    HistoricalLocation.JSON_PROPERTY_THING,
    HistoricalLocation.JSON_PROPERTY_LOCATIONS
})
@XmlRootElement(name = "HistoricalLocation")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "HistoricalLocation")
public final class HistoricalLocation extends SensorThingsEntity {

    public static final String JSON_PROPERTY_TIME = "time";
    @XmlElement(name = "time")
    @jakarta.annotation.Nonnull
    private String time;

    public static final String JSON_PROPERTY_THING = "Thing";
    @XmlElement(name = "thing")
    @jakarta.annotation.Nullable
    private Thing thing;

    public static final String JSON_PROPERTY_LOCATIONS = "Locations";
    @XmlElement(name = "locations")
    @jakarta.annotation.Nullable
    private java.util.List<Location> locations;

    public HistoricalLocation() {
    }

    public HistoricalLocation time(@jakarta.annotation.Nonnull String time) {
        this.time = time;
        return this;
    }

    /**
     * The ISO 8601 date-time this historical snapshot was taken. (required)
     *
     * @return the time
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "time")
    public String getTime() {
        return time;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "time")
    public void setTime(@jakarta.annotation.Nonnull String time) {
        this.time = time;
    }

    public HistoricalLocation thing(@jakarta.annotation.Nullable Thing thing) {
        this.thing = thing;
        return this;
    }

    /**
     * The Thing this historical location belongs to, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation, but omitted on
     * normal reads where it is exposed only as a navigation link)
     *
     * @return the thing
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Thing")
    public Thing getThing() {
        return thing;
    }

    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Thing")
    public void setThing(@jakarta.annotation.Nullable Thing thing) {
        this.thing = thing;
    }

    public HistoricalLocation locations(@jakarta.annotation.Nullable java.util.List<Location> locations) {
        this.locations = locations;
        return this;
    }

    public HistoricalLocation addLocationsItem(Location locationsItem) {
        if (this.locations == null) {
            this.locations = new java.util.ArrayList<>();
        }
        this.locations.add(locationsItem);
        return this;
    }

    /**
     * The Locations associated with this historical snapshot, either deep-inserted or linked by
     * {@code @iot.id}. (optional)
     *
     * @return the locations
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Locations")
    public java.util.List<Location> getLocations() {
        return locations;
    }

    @JsonProperty(JSON_PROPERTY_LOCATIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Locations")
    public void setLocations(@jakarta.annotation.Nullable java.util.List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoricalLocation other = (HistoricalLocation) o;
        return entityEquals(other)
                && Objects.equals(this.time, other.time)
                && Objects.equals(this.thing, other.thing)
                && Objects.equals(this.locations, other.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), time, thing, locations);
    }
}
