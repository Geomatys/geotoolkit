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
 * FeatureOfInterest
 */
public class FeatureOfInterest implements STSEntityResponse, DeltaComparable {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String name = null;

    private String description = null;

    private String encodingType = null;

    private Object feature = null;

    private Object properties = null;

    @JsonProperty("Observations")
    private List<Observation> observations = null;

    @JsonProperty("Observations@iot.navigationLink")
    private String observationsIotNavigationLink = null;

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

    public FeatureOfInterest name(String name) {
        this.name = name;
        return this;
    }

    public FeatureOfInterest iotId(String iotId) {
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

    public FeatureOfInterest iotSelfLink(String iotSelfLink) {
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

    public FeatureOfInterest description(String description) {
        this.description = description;
        return this;
    }

    /**
     * The description about the FeatureOfInterest.
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

    public FeatureOfInterest encodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the feature property. Its value is one of the
     * ValueCode enumeration (see Table 8-6 for the available ValueCode,
     * GeoJSON).
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

    public FeatureOfInterest feature(Object feature) {
        this.feature = feature;
        return this;
    }

    /**
     * The detailed description of the feature. The data type is defined by
     * encodingType.
     *
     * @return feature
     *
     */
    public Object getFeature() {
        return feature;
    }

    public void setFeature(Object feature) {
        this.feature = feature;
    }

    public FeatureOfInterest properties(Object properties) {
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

    public FeatureOfInterest observations(List<Observation> observations) {
        this.observations = observations;
        return this;
    }

    public FeatureOfInterest addObservationsItem(Observation observationsItem) {
        if (this.observations == null) {
            this.observations = new ArrayList<>();
        }
        this.observations.add(observationsItem);
        return this;
    }

    /**
     * An Observation observes on one-and-only-one FeatureOfInterest. One
     * FeatureOfInterest could be observed by zero-to-many Observations.
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

    public FeatureOfInterest observationsIotNavigationLink(String observationsIotNavigationLink) {
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
        FeatureOfInterest featureOfInterest = (FeatureOfInterest) o;
        return Objects.equals(this.iotId, featureOfInterest.iotId)
                && Objects.equals(this.iotSelfLink, featureOfInterest.iotSelfLink)
                && Objects.equals(this.description, featureOfInterest.description)
                && Objects.equals(this.encodingType, featureOfInterest.encodingType)
                && Objects.equals(this.feature, featureOfInterest.feature)
                && Objects.equals(this.properties, featureOfInterest.properties)
                && Objects.equals(this.name, featureOfInterest.name)
                && Objects.equals(this.observations, featureOfInterest.observations)
                && Objects.equals(this.observationsIotNavigationLink, featureOfInterest.observationsIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, description, encodingType, feature, properties, observations, observationsIotNavigationLink, name);
    }

    @Override
    public boolean equals(java.lang.Object o, float delta) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureOfInterest featureOfInterest = (FeatureOfInterest) o;
        return Objects.equals(this.iotId, featureOfInterest.iotId)
                && Objects.equals(this.iotSelfLink, featureOfInterest.iotSelfLink)
                && Objects.equals(this.description, featureOfInterest.description)
                && Objects.equals(this.encodingType, featureOfInterest.encodingType)
                && Objects.equals(this.properties, featureOfInterest.properties)
                && DeltaComparable.equals(this.feature, featureOfInterest.feature, delta)
                && Objects.equals(this.name, featureOfInterest.name)
                && DeltaComparable.equals(this.observations, featureOfInterest.observations, delta)
                && Objects.equals(this.observationsIotNavigationLink, featureOfInterest.observationsIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FeatureOfInterest {\n");
        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    encodingType: ").append(toIndentedString(encodingType)).append("\n");
        sb.append("    feature: ").append(toIndentedString(feature)).append("\n");
        sb.append("    properties: ").append(toIndentedString(properties)).append("\n");
        sb.append("    observations: ").append(toIndentedString(observations)).append("\n");
        sb.append("    observationsIotNavigationLink: ").append(toIndentedString(observationsIotNavigationLink)).append("\n");
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
