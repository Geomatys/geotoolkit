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

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A datastream groups a collection of observations that are related in some
 * way. The one constraint is that the observations in a datastream must measure
 * the same observed property (i.e., one phenomenon).
 */
public class Datastream implements STSResponse {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String description = null;

    private Object unitOfMeasure = null;

    private String observationType = null;

    private Object observedArea = null;

    private String phenomenonTime = null;

    private String resultTime = null;

    @JsonProperty("Thing")
    private Thing thing = null;

    @JsonProperty("Sensor")
    private Sensor sensor = null;

    @JsonProperty("ObservedProperty")
    private ObservedProperty observedProperty = null;

    @JsonProperty("Observations")
    protected List<Observation> observations = null;

    @JsonProperty("Thing@iot.navigationLink")
    private String thingIotNavigationLink = null;

    @JsonProperty("Sensor@iot.navigationLink")
    private String sensorIotNavigationLink = null;

    @JsonProperty("ObservedProperty@iot.navigationLink")
    private String observedPropertyIotNavigationLink = null;

    @JsonProperty("Observations@iot.navigationLink")
    private String observationsIotNavigationLink = null;

    public Datastream iotId(String iotId) {
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

    public Datastream iotSelfLink(String iotSelfLink) {
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

    public Datastream description(String description) {
        this.description = description;
        return this;
    }

    /**
     * This is the description of the datastream entity. The content is open to
     * support other description languages.
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

    public Datastream unitOfMeasure(Object unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
        return this;
    }

    /**
     * A JSON Object containing three key- value pairs. The name property
     * presents the full name of the unitOfMeasurement; the symbol property
     * shows the textual form of the unit symbol; and the definition contains
     * the IRI defining the unitOfMeasurement. The values of these properties
     * SHOULD follow the Unified Code for Unit of Measure (UCUM).
     *
     * @return unitOfMeasure
  *
     */
    public Object getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(Object unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Datastream observationType(String observationType) {
        this.observationType = observationType;
        return this;
    }

    /**
     * The type of Observation (with unique result type), which is used by the
     * service to encode observations. E.g.
     * http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement
     *
     * @return observationType
  *
     */
    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public Datastream observedArea(Object observedArea) {
        this.observedArea = observedArea;
        return this;
    }

    /**
     * The spatial bounding box of the spatial extent of all FeaturesOfInterest
     * that belong to the Observations associated with this Datastream.
     * Typically a GeoJSON Polygon
     *
     * @return observedArea
  *
     */
    public Object getObservedArea() {
        return observedArea;
    }

    public void setObservedArea(Object observedArea) {
        this.observedArea = observedArea;
    }

    public Datastream phenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    /**
     * The temporal bounding box of the phenomenon times of all observations
     * belonging to this Datastream. TM_Period(ISO 8601 Time Interval)
     *
     * @return phenomenonTime
  *
     */
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Datastream resultTime(String resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * The temporal bounding box of the result times of all observations
     * belonging to this Datastream. TM_Period (ISO 8601 Time Interval)
     *
     * @return resultTime
  *
     */
    public String getResultTime() {
        return resultTime;
    }

    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public Datastream thing(Thing thing) {
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

    public Datastream sensor(Sensor sensor) {
        this.sensor = sensor;
        return this;
    }

    /**
     * Get sensor
     *
     * @return sensor
  *
     */
    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Datastream observedProperty(ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    /**
     * Get observedProperty
     *
     * @return observedProperty
  *
     */
    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
    }

    public Datastream observations(List<Observation> observations) {
        this.observations = observations;
        return this;
    }

    public Datastream addObservationsItem(Observation observationsItem) {

        if (this.observations == null) {
            this.observations = new ArrayList<>();
        }

        this.observations.add(observationsItem);
        return this;
    }

    public Datastream addObservationsItems(List<Observation> observationsItems) {
        if (observationsItems != null) {

            if (this.observations == null) {
                this.observations = new ArrayList<>();
            }

            this.observations.addAll(observationsItems);
        }
        return this;
    }

    /**
     * A datastream has zero-to-many observations. One observation SHALL occur
     * in one and only one Datastream.
     *
     * @return observations
  *
     */
    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    public Datastream thingIotNavigationLink(String thingIotNavigationLink) {
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

    public Datastream sensorIotNavigationLink(String sensorIotNavigationLink) {
        this.sensorIotNavigationLink = sensorIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return sensorIotNavigationLink
  *
     */
    public String getSensorIotNavigationLink() {
        return sensorIotNavigationLink;
    }

    public void setSensorIotNavigationLink(String sensorIotNavigationLink) {
        this.sensorIotNavigationLink = sensorIotNavigationLink;
    }

    public Datastream observedPropertyIotNavigationLink(String observedPropertyIotNavigationLink) {
        this.observedPropertyIotNavigationLink = observedPropertyIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return observedPropertyIotNavigationLink
  *
     */
    public String getObservedPropertyIotNavigationLink() {
        return observedPropertyIotNavigationLink;
    }

    public void setObservedPropertyIotNavigationLink(String observedPropertyIotNavigationLink) {
        this.observedPropertyIotNavigationLink = observedPropertyIotNavigationLink;
    }

    public Datastream observationsIotNavigationLink(String observationsIotNavigationLink) {
        this.observationsIotNavigationLink = observationsIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return observationsIotNavigationLink
  *
     */
    public String getObservationsIotNavigationLink() {
        return observationsIotNavigationLink;
    }

    public void setObservationsIotNavigationLink(String observationsIotNavigationLink) {
        this.observationsIotNavigationLink = observationsIotNavigationLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Datastream datastream = (Datastream) o;
        return Objects.equals(this.iotId, datastream.iotId)
                && Objects.equals(this.iotSelfLink, datastream.iotSelfLink)
                && Objects.equals(this.description, datastream.description)
                && Objects.equals(this.unitOfMeasure, datastream.unitOfMeasure)
                && Objects.equals(this.observationType, datastream.observationType)
                && Objects.equals(this.observedArea, datastream.observedArea)
                && Objects.equals(this.phenomenonTime, datastream.phenomenonTime)
                && Objects.equals(this.resultTime, datastream.resultTime)
                && Objects.equals(this.thing, datastream.thing)
                && Objects.equals(this.sensor, datastream.sensor)
                && Objects.equals(this.observedProperty, datastream.observedProperty)
                && Objects.equals(this.observations, datastream.observations)
                && Objects.equals(this.thingIotNavigationLink, datastream.thingIotNavigationLink)
                && Objects.equals(this.sensorIotNavigationLink, datastream.sensorIotNavigationLink)
                && Objects.equals(this.observedPropertyIotNavigationLink, datastream.observedPropertyIotNavigationLink)
                && Objects.equals(this.observationsIotNavigationLink, datastream.observationsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, description, unitOfMeasure, observationType, observedArea, phenomenonTime, resultTime, thing, sensor, observedProperty, observations, thingIotNavigationLink, sensorIotNavigationLink, observedPropertyIotNavigationLink, observationsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Datastream {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    unitOfMeasure: ").append(toIndentedString(unitOfMeasure)).append("\n");
        sb.append("    observationType: ").append(toIndentedString(observationType)).append("\n");
        sb.append("    observedArea: ").append(toIndentedString(observedArea)).append("\n");
        sb.append("    phenomenonTime: ").append(toIndentedString(phenomenonTime)).append("\n");
        sb.append("    resultTime: ").append(toIndentedString(resultTime)).append("\n");
        sb.append("    thing: ").append(toIndentedString(thing)).append("\n");
        sb.append("    sensor: ").append(toIndentedString(sensor)).append("\n");
        sb.append("    observedProperty: ").append(toIndentedString(observedProperty)).append("\n");
        sb.append("    observations: ").append(toIndentedString(observations)).append("\n");
        sb.append("    thingIotNavigationLink: ").append(toIndentedString(thingIotNavigationLink)).append("\n");
        sb.append("    sensorIotNavigationLink: ").append(toIndentedString(sensorIotNavigationLink)).append("\n");
        sb.append("    observedPropertyIotNavigationLink: ").append(toIndentedString(observedPropertyIotNavigationLink)).append("\n");
        sb.append("    observationsIotNavigationLink: ").append(toIndentedString(observationsIotNavigationLink)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    protected String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
