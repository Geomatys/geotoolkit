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
 * Sensor
 */
public class Sensor implements STSResponse {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String description = null;

    private String name = null;

    private String encodingType = null;

    private String metadata = null;

    @JsonProperty("Datastreams")
    private List<Datastream> datastreams = null;

    @JsonProperty("Datastreams@iot.navigationLink")
    private String datastreamsIotNavigationLink = null;

    @JsonProperty("MultiDatastreams")
    private List<MultiDatastream> multiDatastreams = null;

    @JsonProperty("MultiDatastreams@iot.navigationLink")
    private String multiDatastreamsIotNavigationLink = null;

    public Sensor iotId(String iotId) {
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

    public Sensor name(String name) {
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

    public Sensor iotSelfLink(String iotSelfLink) {
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

    public Sensor description(String description) {
        this.description = description;
        return this;
    }

    /**
     * The description of the Sensor entity.
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

    public Sensor encodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the metadata property. Its value is one of the
     * ValueCode enumeration (see Table 8-14 for the available ValueCode:
     * application/pdf or http://www.opengis.net/doc/IS/SensorML/2.0).
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

    public Sensor metadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * The detailed description of the sensor or system. The content is open to
     * accommodate changes to SensorML or to support other description
     * languages.
     *
     * @return metadata
  *
     */
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Sensor datastreams(List<Datastream> datastreams) {
        this.datastreams = datastreams;
        return this;
    }

    public Sensor addDatastreamsItem(Datastream datastreamsItem) {

        if (this.datastreams == null) {
            this.datastreams = new ArrayList<>();
        }

        this.datastreams.add(datastreamsItem);
        return this;
    }

    /**
     * The Observations of a Datastream are measured with the same Sensor. One
     * Sensor MAY produce zero-to-many Observations in different Datastreams.
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

    public Sensor datastreamsIotNavigationLink(String datastreamsIotNavigationLink) {
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


    public Sensor addMultiDatastreamsItem(MultiDatastream multiDatastreamsItem) {
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

    public Sensor multiDatastreamsIotNavigationLink(String multiDatastreamsIotNavigationLink) {
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
        Sensor sensor = (Sensor) o;
        return Objects.equals(this.iotId, sensor.iotId)
                && Objects.equals(this.iotSelfLink, sensor.iotSelfLink)
                && Objects.equals(this.description, sensor.description)
                && Objects.equals(this.encodingType, sensor.encodingType)
                && Objects.equals(this.metadata, sensor.metadata)
                && Objects.equals(this.name, sensor.name)
                && Objects.equals(this.datastreams, sensor.datastreams)
                && Objects.equals(this.datastreamsIotNavigationLink, sensor.datastreamsIotNavigationLink)
                && Objects.equals(this.multiDatastreams, sensor.multiDatastreams)
                && Objects.equals(this.multiDatastreamsIotNavigationLink, sensor.multiDatastreamsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, description, encodingType, metadata, datastreams, datastreamsIotNavigationLink, name, multiDatastreams, multiDatastreamsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Sensor {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    encodingType: ").append(toIndentedString(encodingType)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    datastreams: ").append(toIndentedString(datastreams)).append("\n");
        sb.append("    datastreamsIotNavigationLink: ").append(toIndentedString(datastreamsIotNavigationLink)).append("\n");
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
