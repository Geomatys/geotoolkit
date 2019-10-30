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

/**
 * ObservedProperty
 */
public class ObservedProperty {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String name = null;

    private String definition = null;

    private String description = null;

    @JsonProperty("Datastreams")
    private List<Datastream> datastreams = null;

    @JsonProperty("Datastreams@iot.navigationLink")
    private String datastreamsIotNavigationLink = null;

    public ObservedProperty iotId(String iotId) {
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

    public ObservedProperty iotSelfLink(String iotSelfLink) {
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

    public ObservedProperty name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The name of the ObservedProperty.
     *
     * @return name
  *
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservedProperty definition(String definition) {
        this.definition = definition;
        return this;
    }

    /**
     * The IRI of the ObservedProperty. Dereferencing this IRI SHOULD result in
     * a representation of the definition of the ObservedProperty.
     *
     * @return definition
  *
     */
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public ObservedProperty description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A description about the ObservedProperty.
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

    public ObservedProperty datastreams(List<Datastream> datastreams) {
        this.datastreams = datastreams;
        return this;
    }

    public ObservedProperty addDatastreamsItem(Datastream datastreamsItem) {

        if (this.datastreams == null) {
            this.datastreams = new ArrayList<>();
        }

        this.datastreams.add(datastreamsItem);
        return this;
    }

    /**
     * The Observations of a Datastream observe the same ObservedProperty . The
     * Observations of different Datastreams MAY observe the same
     * ObservedProperty.
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

    public ObservedProperty datastreamsIotNavigationLink(String datastreamsIotNavigationLink) {
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservedProperty observedProperty = (ObservedProperty) o;
        return Objects.equals(this.iotId, observedProperty.iotId)
                && Objects.equals(this.iotSelfLink, observedProperty.iotSelfLink)
                && Objects.equals(this.name, observedProperty.name)
                && Objects.equals(this.definition, observedProperty.definition)
                && Objects.equals(this.description, observedProperty.description)
                && Objects.equals(this.datastreams, observedProperty.datastreams)
                && Objects.equals(this.datastreamsIotNavigationLink, observedProperty.datastreamsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, name, definition, description, datastreams, datastreamsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ObservedProperty {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    datastreams: ").append(toIndentedString(datastreams)).append("\n");
        sb.append("    datastreamsIotNavigationLink: ").append(toIndentedString(datastreamsIotNavigationLink)).append("\n");
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
