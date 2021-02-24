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
 * Thing
 */
public class Thing implements STSResponse, DeltaComparable {

    @JsonProperty("@iot.id")
    private String iotId = null;

    private String name = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String description = null;

    private Object properties = null;

    @JsonProperty("Locations")
    private List<Location> locations = null;

    @JsonProperty("HistoricalLocations")
    private List<HistoricalLocation> historicalLocations = null;

    @JsonProperty("Datastreams")
    private List<Datastream> datastreams = null;

    @JsonProperty("HistoricalLocations@iot.navigationLink")
    private String historicalLocationsIotNavigationLink = null;

    @JsonProperty("Datastreams@iot.navigationLink")
    private String datastreamsIotNavigationLink = null;

    @JsonProperty("Locations@iot.navigationLink")
    private String locationsIotNavigationLink = null;

    @JsonProperty("MultiDatastreams")
    private List<MultiDatastream> multiDatastreams = null;

    @JsonProperty("MultiDatastreams@iot.navigationLink")
    private String multiDatastreamsIotNavigationLink = null;

    public Thing iotId(String iotId) {
        this.iotId = iotId;
        return this;
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

    public Thing name(String name) {
        this.name = name;
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

    public Thing iotSelfLink(String iotSelfLink) {
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

    public Thing description(String description) {
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

    public Thing properties(Object properties) {
        this.properties = properties;
        return this;
    }

    /**
     * a set of additional properties specified for the object in the form
     * \&quot;name\&quot;:\&quot;value\&quot; pair
     *
     * @return properties
  *
     */
    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public Thing locations(List<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Thing addLocationsItem(Location locationsItem) {

        if (this.locations == null) {
            this.locations = new ArrayList<>();
        }

        this.locations.add(locationsItem);
        return this;
    }

    /**
     * The Location entity locates the Thing. Multiple Things MAY be located at
     * the same Location. A Thing MAY not have a Location. A Thing SHOULD have
     * only one Location. However, in some complex use cases, a Thing MAY have
     * more than one Location representations. In such case, the Thing MAY have
     * more than one Locations. These Locations SHALL have different
     * encodingTypes and the encodingTypes SHOULD be in different spaces (e.g.,
     * one encodingType in Geometrical space and one encodingType in Topological
     * space). (many-to-many)
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

    public Thing historicalLocations(List<HistoricalLocation> historicalLocations) {
        this.historicalLocations = historicalLocations;
        return this;
    }

    public Thing addHistoricalLocationsItem(HistoricalLocation historicalLocationsItem) {

        if (this.historicalLocations == null) {
            this.historicalLocations = new ArrayList<>();
        }

        this.historicalLocations.add(historicalLocationsItem);
        return this;
    }

    /**
     * A Thing has zero-to-many HistoricalLocations. A HistoricalLocation has
     * one-and-only-one Thing
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

    public Thing datastreams(List<Datastream> datastreams) {
        this.datastreams = datastreams;
        return this;
    }

    public Thing addDatastreamsItem(Datastream datastreamsItem) {

        if (this.datastreams == null) {
            this.datastreams = new ArrayList<>();
        }

        this.datastreams.add(datastreamsItem);
        return this;
    }

    /**
     * A thing MAY have zero-to-many datastreams.\&quot;
     *
     * @return datastreams
  *
     */
    public List<Datastream> getDatastreams() {
        return datastreams;
    }

    public void setDatastreams(List<Datastream> datastreams) {
        this.datastreams = datastreams;
    }

    public Thing historicalLocationsIotNavigationLink(String historicalLocationsIotNavigationLink) {
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

    public Thing datastreamsIotNavigationLink(String datastreamsIotNavigationLink) {
        this.datastreamsIotNavigationLink = datastreamsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return datastreamsIotNavigationLink
  *
     */
    public String getDatastreamsIotNavigationLink() {
        return datastreamsIotNavigationLink;
    }

    public void setDatastreamsIotNavigationLink(String datastreamsIotNavigationLink) {
        this.datastreamsIotNavigationLink = datastreamsIotNavigationLink;
    }

    public Thing locationsIotNavigationLink(String locationsIotNavigationLink) {
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

    public Thing addMultiDatastreamsItem(MultiDatastream multiDatastreamsItem) {
        if (this.multiDatastreams == null) {
            this.multiDatastreams = new ArrayList<>();
        }

        this.multiDatastreams.add(multiDatastreamsItem);
        return this;
    }

    /**
     * The Observations of a Datastream are measured with the same Sensor. One
     * Sensor MAY produce zero-to-many Observations in different Datastreams.
     *
     * @return datastreams
     *
     */
    public List<MultiDatastream> getMultiDatastreams() {
        return multiDatastreams;
    }

    public void setMultiDatastreams(List<MultiDatastream> multiDatastreams) {
        this.multiDatastreams = multiDatastreams;
    }

    public Thing multiDatastreamsIotNavigationLink(String multiDatastreamsIotNavigationLink) {
        this.multiDatastreamsIotNavigationLink = multiDatastreamsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return datastreamsIotNavigationLink
     *
     */
    public String getMultiDatastreamsIotNavigationLink() {
        return multiDatastreamsIotNavigationLink;
    }

    public void setMultiDatastreamsIotNavigationLink(String multiDatastreamsIotNavigationLink) {
        this.multiDatastreamsIotNavigationLink = multiDatastreamsIotNavigationLink;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Thing thing = (Thing) o;
        return Objects.equals(this.iotId, thing.iotId)
                && Objects.equals(this.iotSelfLink, thing.iotSelfLink)
                && Objects.equals(this.description, thing.description)
                && Objects.equals(this.properties, thing.properties)
                && Objects.equals(this.locations, thing.locations)
                && Objects.equals(this.historicalLocations, thing.historicalLocations)
                && Objects.equals(this.historicalLocationsIotNavigationLink, thing.historicalLocationsIotNavigationLink)
                && Objects.equals(this.datastreams, thing.datastreams)
                && Objects.equals(this.datastreamsIotNavigationLink, thing.datastreamsIotNavigationLink)
                && Objects.equals(this.multiDatastreams, thing.multiDatastreams)
                && Objects.equals(this.multiDatastreamsIotNavigationLink, thing.multiDatastreamsIotNavigationLink)
                && Objects.equals(this.locationsIotNavigationLink, thing.locationsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, description, properties, locations, historicalLocations, datastreams, historicalLocationsIotNavigationLink, datastreamsIotNavigationLink, locationsIotNavigationLink, multiDatastreams, multiDatastreamsIotNavigationLink);
    }

    @Override
    public boolean equals(java.lang.Object o, float delta) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Thing thing = (Thing) o;
        return Objects.equals(this.iotId, thing.iotId)
                && Objects.equals(this.iotSelfLink, thing.iotSelfLink)
                && Objects.equals(this.description, thing.description)
                && Objects.equals(this.properties, thing.properties)
                && DeltaComparable.equals(this.locations, thing.locations, delta)
                && DeltaComparable.equals(this.historicalLocations, thing.historicalLocations, delta)
                && Objects.equals(this.historicalLocationsIotNavigationLink, thing.historicalLocationsIotNavigationLink)
                && DeltaComparable.equals(this.datastreams, thing.datastreams, delta)
                && Objects.equals(this.datastreamsIotNavigationLink, thing.datastreamsIotNavigationLink)
                && DeltaComparable.equals(this.multiDatastreams, thing.multiDatastreams, delta)
                && Objects.equals(this.multiDatastreamsIotNavigationLink, thing.multiDatastreamsIotNavigationLink)
                && Objects.equals(this.locationsIotNavigationLink, thing.locationsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Thing {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
        sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
        sb.append("    historicalLocations: ").append(toIndentedString(historicalLocations)).append("\n");
        sb.append("    datastreams: ").append(toIndentedString(datastreams)).append("\n");
        sb.append("    historicalLocationsIotNavigationLink: ").append(toIndentedString(historicalLocationsIotNavigationLink)).append("\n");
        sb.append("    datastreamsIotNavigationLink: ").append(toIndentedString(datastreamsIotNavigationLink)).append("\n");
        sb.append("    locationsIotNavigationLink: ").append(toIndentedString(locationsIotNavigationLink)).append("\n");
        sb.append("    multiDatastreams: ").append(toIndentedString(multiDatastreams)).append("\n");
        sb.append("    multiDatastreamsIotNavigationLink: ").append(toIndentedString(multiDatastreamsIotNavigationLink)).append("\n");
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
