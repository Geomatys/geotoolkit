/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sts.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.util.DeltaComparable;

/**
 * HistoricalLocation
 */
public class HistoricalLocation implements STSEntityResponse, Comparable<HistoricalLocation>, DeltaComparable {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    @JsonProperty("time")
    private Date time = null;

    @JsonProperty("Locations")
    private List<Location> locations = null;

    @JsonProperty("Thing")
    private Thing thing = null;

    @JsonProperty("Locations@iot.navigationLink")
    private String locationsIotNavigationLink = null;

    @JsonProperty("Thing@iot.navigationLink")
    private String thingIotNavigationLink = null;

    public HistoricalLocation iotId(String iotId) {
        this.iotId = iotId;
        return this;
    }

    /**
     * ID is the system-generated identifier of an entity. ID is unique among
     * the entities of the same entity type.
     *
     * @return iotId
     *
     */
    @Override
    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public HistoricalLocation iotSelfLink(String iotSelfLink) {
        this.iotSelfLink = iotSelfLink;
        return this;
    }

    /**
     * Self-Link is the absolute URL of an entity which is unique among all
     * other entities.
     *
     * @return iotSelfLink
     *
     */
    @Override
    public String getIotSelfLink() {
        return iotSelfLink;
    }

    public void setIotSelfLink(String iotSelfLink) {
        this.iotSelfLink = iotSelfLink;
    }

    public HistoricalLocation time(Date time) {
        this.time = time;
        return this;
    }

    /**
     * The time when the Thing is known at the Location. Datatype TM_Instant
     * (ISO-8601 Time String)
     *
     * @return time
  *
     */
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public HistoricalLocation locations(List<Location> locations) {
        this.locations = locations;
        return this;
    }

    public HistoricalLocation addLocationsItem(Location locationsItem) {

        if (this.locations == null) {
            this.locations = new ArrayList<>();
        }

        this.locations.add(locationsItem);
        return this;
    }

    /**
     * A Location can have zero-to-many HistoricalLocations. One
     * HistoricalLocation SHALL have one or many Locations.
     *
     * @return locations
  *
     */
    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public HistoricalLocation thing(Thing thing) {
        this.thing = thing;
        return this;
    }

    /**
     * Get thing
     *
     * @return thing
  *
     */
    public Thing getThing() {
        return thing;
    }

    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public HistoricalLocation locationsIotNavigationLink(String locationsIotNavigationLink) {
        this.locationsIotNavigationLink = locationsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return locationsIotNavigationLink
  *
     */
    public String getLocationsIotNavigationLink() {
        return locationsIotNavigationLink;
    }

    public void setLocationsIotNavigationLink(String locationsIotNavigationLink) {
        this.locationsIotNavigationLink = locationsIotNavigationLink;
    }

    public HistoricalLocation thingIotNavigationLink(String thingIotNavigationLink) {
        this.thingIotNavigationLink = thingIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return thingIotNavigationLink
  *
     */
    public String getThingIotNavigationLink() {
        return thingIotNavigationLink;
    }

    public void setThingIotNavigationLink(String thingIotNavigationLink) {
        this.thingIotNavigationLink = thingIotNavigationLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoricalLocation historicalLocation = (HistoricalLocation) o;
        return Objects.equals(this.iotId, historicalLocation.iotId)
                && Objects.equals(this.iotSelfLink, historicalLocation.iotSelfLink)
                && Objects.equals(this.time, historicalLocation.time)
                && Objects.equals(this.locations, historicalLocation.locations)
                && Objects.equals(this.thing, historicalLocation.thing)
                && Objects.equals(this.locationsIotNavigationLink, historicalLocation.locationsIotNavigationLink)
                && Objects.equals(this.thingIotNavigationLink, historicalLocation.thingIotNavigationLink);
    }

    @Override
    public boolean equals(java.lang.Object o, float delta) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoricalLocation historicalLocation = (HistoricalLocation) o;
        return Objects.equals(this.iotId, historicalLocation.iotId)
                && Objects.equals(this.iotSelfLink, historicalLocation.iotSelfLink)
                && Objects.equals(this.time, historicalLocation.time)
                && DeltaComparable.equals(this.locations, historicalLocation.locations, delta)
                && DeltaComparable.equals(this.thing, historicalLocation.thing, delta)
                && Objects.equals(this.locationsIotNavigationLink, historicalLocation.locationsIotNavigationLink)
                && Objects.equals(this.thingIotNavigationLink, historicalLocation.thingIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, time, locations, thing, locationsIotNavigationLink, thingIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class HistoricalLocation {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    time: ").append(toIndentedString(time)).append("\n");
        sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
        sb.append("    thing: ").append(toIndentedString(thing)).append("\n");
        sb.append("    locationsIotNavigationLink: ").append(toIndentedString(locationsIotNavigationLink)).append("\n");
        sb.append("    thingIotNavigationLink: ").append(toIndentedString(thingIotNavigationLink)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    @Override
    public int compareTo(HistoricalLocation o) {
        return this.time.compareTo(o.time);
    }
}
