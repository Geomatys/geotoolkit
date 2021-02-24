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
import java.util.List;
import org.geotoolkit.util.DeltaComparable;

/**
 * Location
 */
public class Location implements STSResponse, DeltaComparable {

    @JsonProperty("@iot.id")
    private String iotId = null;

    private String name = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String description = null;

    private String encodingType = null;

    private Object location = null;

    @JsonProperty("Things")
    private List<Thing> things = null;

    @JsonProperty("HistoricalLocations")
    private List<HistoricalLocation> historicalLocations = null;

    @JsonProperty("Things@iot.navigationLink")
    private String thingsIotNavigationLink = null;

    @JsonProperty("HistoricalLocations@iot.navigationLink")
    private String historicalLocationsIotNavigationLink = null;

    public Location iotId(String iotId) {
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
    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public Location iotSelfLink(String iotSelfLink) {
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
    public String getIotSelfLink() {
        return iotSelfLink;
    }

    public void setIotSelfLink(String iotSelfLink) {
        this.iotSelfLink = iotSelfLink;
    }

    /**
     * A property provides a label for Sensor entity, commonly a descriptive
     * name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public Location name(String name) {
        this.name = name;
        return this;
    }


    public Location description(String description) {
        this.description = description;
        return this;
    }

    /**
     * This is the description of the thing entity. The content is open to
     * accommodate changes to SensorML and to support other description
     * languages.
     *
     * @return description
  *
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location encodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the location property. Its value is one of the
     * ValueCode enumeration (see Table 8-6; currently only
     * application/vnd.geo+json is supported).
     *
     * @return encodingType
  *
     */
    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Location location(Object location) {
        this.location = location;
        return this;
    }

    /**
     * The location type is defined by encodingType. (only GeoJSON is supported)
     *
     * @return location
  *
     */
    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Location things(List<Thing> things) {
        this.things = things;
        return this;
    }

    public Location addThingsItem(Thing thingsItem) {

        if (this.things == null) {
            this.things = new ArrayList<>();
        }

        this.things.add(thingsItem);
        return this;
    }

    /**
     * Multiple Things MAY locate at the same Location . A Thing MAY not have a
     * Location.
     *
     * @return things
  *
     */
    public List<Thing> getThings() {
        return things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }

    public Location historicalLocations(List<HistoricalLocation> historicalLocations) {
        this.historicalLocations = historicalLocations;
        return this;
    }

    public Location addHistoricalLocationsItem(HistoricalLocation historicalLocationsItem) {

        if (this.historicalLocations == null) {
            this.historicalLocations = new ArrayList<>();
        }

        this.historicalLocations.add(historicalLocationsItem);
        return this;
    }

    /**
     * A Location can have zero-to-many HistoricalLocations . One
     * HistoricalLocation SHALL have one or many Locations.
     *
     * @return historicalLocations
  *
     */
    public List<HistoricalLocation> getHistoricalLocations() {
        return historicalLocations;
    }

    public void setHistoricalLocations(List<HistoricalLocation> historicalLocations) {
        this.historicalLocations = historicalLocations;
    }

    public Location thingsIotNavigationLink(String thingsIotNavigationLink) {
        this.thingsIotNavigationLink = thingsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return thingsIotNavigationLink
  *
     */
    public String getThingsIotNavigationLink() {
        return thingsIotNavigationLink;
    }

    public void setThingsIotNavigationLink(String thingsIotNavigationLink) {
        this.thingsIotNavigationLink = thingsIotNavigationLink;
    }

    public Location historicalLocationsIotNavigationLink(String historicalLocationsIotNavigationLink) {
        this.historicalLocationsIotNavigationLink = historicalLocationsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return historicalLocationsIotNavigationLink
  *
     */
    public String getHistoricalLocationsIotNavigationLink() {
        return historicalLocationsIotNavigationLink;
    }

    public void setHistoricalLocationsIotNavigationLink(String historicalLocationsIotNavigationLink) {
        this.historicalLocationsIotNavigationLink = historicalLocationsIotNavigationLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(this.iotId, location.iotId)
                && Objects.equals(this.iotSelfLink, location.iotSelfLink)
                && Objects.equals(this.description, location.description)
                && Objects.equals(this.encodingType, location.encodingType)
                && Objects.equals(this.location, location.location)
                && Objects.equals(this.things, location.things)
                && Objects.equals(this.historicalLocations, location.historicalLocations)
                && Objects.equals(this.thingsIotNavigationLink, location.thingsIotNavigationLink)
                && Objects.equals(this.historicalLocationsIotNavigationLink, location.historicalLocationsIotNavigationLink);
    }

    @Override
    public boolean equals(java.lang.Object o, float delta) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(this.iotId, location.iotId)
                && Objects.equals(this.iotSelfLink, location.iotSelfLink)
                && Objects.equals(this.description, location.description)
                && Objects.equals(this.encodingType, location.encodingType)
                && DeltaComparable.equals(this.location, location.location, delta)
                && DeltaComparable.equals(this.things, location.things, delta)
                && DeltaComparable.equals(this.historicalLocations, location.historicalLocations, delta)
                && Objects.equals(this.thingsIotNavigationLink, location.thingsIotNavigationLink)
                && Objects.equals(this.historicalLocationsIotNavigationLink, location.historicalLocationsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, description, encodingType, location, things, historicalLocations, thingsIotNavigationLink, historicalLocationsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Location {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    encodingType: ").append(toIndentedString(encodingType)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
        sb.append("    things: ").append(toIndentedString(things)).append("\n");
        sb.append("    historicalLocations: ").append(toIndentedString(historicalLocations)).append("\n");
        sb.append("    thingsIotNavigationLink: ").append(toIndentedString(thingsIotNavigationLink)).append("\n");
        sb.append("    historicalLocationsIotNavigationLink: ").append(toIndentedString(historicalLocationsIotNavigationLink)).append("\n");
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

}
