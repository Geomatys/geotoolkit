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
import org.geotoolkit.util.DeltaComparable;

/**
 * An Observation is act of measuring or otherwise determining the value of a
 * property [OGC and ISO 19156:2011]
 */
public class Observation implements STSResponse, DeltaComparable {

    @JsonProperty("@iot.id")
    private String iotId = null;

    @JsonProperty("@iot.selfLink")
    private String iotSelfLink = null;

    private String phenomenonTime = null;

    private Object result = null;

    private String resultTime = null;

    private String resultQuality = null;

    private String validTime = null;

    private Object parameters = null;

    @JsonProperty("Datastream")
    private Datastream datastream = null;

    @JsonProperty("FeatureOfInterest")
    private FeatureOfInterest featureOfInterest = null;

    @JsonProperty("Datastream@iot.navigationLink")
    private String datastreamIotNavigationLink = null;

    @JsonProperty("FeatureOfInterest@iot.navigationLink")
    private String featureOfInterestIotNavigationLink = null;

    @JsonProperty("MultiDatastream")
    private MultiDatastream multiDatastream = null;

    @JsonProperty("MultiDatastream@iot.navigationLink")
    private String multiDatastreamIotNavigationLink = null;

    public Observation iotId(String iotId) {
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

    public Observation iotSelfLink(String iotSelfLink) {
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

    public Observation phenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    /**
     * The time instant or period of when the Observation happens. Note: Many
     * resource-constrained sensing devices do not have a clock. As a result, a
     * client may omit phenonmenonTime when POST new Observations, even though
     * phenonmenonTime is a mandatory property. When a SensorThings service
     * receives a POST Observations without phenonmenonTime, the service SHALL
     * assign the current server time to the value of the phenomenonTime.
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

    public Observation result(Object result) {
        this.result = result;
        return this;
    }

    /**
     * The estimated value of an ObservedProperty from the Observation.
     *
     * @return result
  *
     */
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Observation resultTime(String resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * The time of the Observation&#x27;s result was generated. Note: Many
     * resource-constrained sensing devices do not have a clock. As a result, a
     * client may omit resultTime when POST new Observations, even though
     * resultTime is a mandatory property. When a SensorThings service receives
     * a POST Observations without resultTime, the service SHALL assign a null
     * value to the resultTime.
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

    public Observation resultQuality(String resultQuality) {
        this.resultQuality = resultQuality;
        return this;
    }

    /**
     * Describes the quality of the result.
     *
     * @return resultQuality
  *
     */
    public String getResultQuality() {
        return resultQuality;
    }

    public void setResultQuality(String resultQuality) {
        this.resultQuality = resultQuality;
    }

    public Observation validTime(String validTime) {
        this.validTime = validTime;
        return this;
    }

    /**
     * The time period during which the result may be used. TM_Period (ISO 8601
     * Time Interval string)
     *
     * @return validTime
  *
     */
    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public Observation parameters(Object parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Key-value pairs showing the environmental conditions during measurement.
     *
     * @return parameters
  *
     */
    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public Observation datastream(Datastream datastream) {
        this.datastream = datastream;
        return this;
    }

    /**
     * Get datastream
     *
     * @return datastream
  *
     */
    public Datastream getDatastream() {
        return datastream;
    }

    public void setDatastream(Datastream datastream) {
        this.datastream = datastream;
    }

    public Observation featureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    /**
     * Get featureOfInterest
     *
     * @return featureOfInterest
  *
     */
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    public Observation datastreamIotNavigationLink(String datastreamIotNavigationLink) {
        this.datastreamIotNavigationLink = datastreamIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return datastreamIotNavigationLink
  *
     */
    public String getDatastreamIotNavigationLink() {
        return datastreamIotNavigationLink;
    }

    public void setDatastreamIotNavigationLink(String datastreamIotNavigationLink) {
        this.datastreamIotNavigationLink = datastreamIotNavigationLink;
    }

    public Observation featureOfInterestIotNavigationLink(String featureOfInterestIotNavigationLink) {
        this.featureOfInterestIotNavigationLink = featureOfInterestIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return featureOfInterestIotNavigationLink
  *
     */
    public String getFeatureOfInterestIotNavigationLink() {
        return featureOfInterestIotNavigationLink;
    }

    public void setFeatureOfInterestIotNavigationLink(String featureOfInterestIotNavigationLink) {
        this.featureOfInterestIotNavigationLink = featureOfInterestIotNavigationLink;
    }


    public MultiDatastream getMultiDatastream() {
        return multiDatastream;
    }

    public void setMultiDatastream(MultiDatastream multiDatastream) {
        this.multiDatastream = multiDatastream;
    }

    public Observation multiDatastreamIotNavigationLink(String multiDatastreamIotNavigationLink) {
        this.multiDatastreamIotNavigationLink = multiDatastreamIotNavigationLink;
        return this;
    }

    /**
     * link to related entities
     *
     * @return datastreamsIotNavigationLink
  *
     */
    public String getMultiDatastreamIotNavigationLink() {
        return multiDatastreamIotNavigationLink;
    }

    public void setMultiDatastreamIotNavigationLink(String multiDatastreamsIotNavigationLink) {
        this.multiDatastreamIotNavigationLink = multiDatastreamsIotNavigationLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Observation observation = (Observation) o;
        return Objects.equals(this.iotId, observation.iotId)
                && Objects.equals(this.iotSelfLink, observation.iotSelfLink)
                && Objects.equals(this.phenomenonTime, observation.phenomenonTime)
                && Objects.equals(this.result, observation.result)
                && Objects.equals(this.resultTime, observation.resultTime)
                && Objects.equals(this.resultQuality, observation.resultQuality)
                && Objects.equals(this.validTime, observation.validTime)
                && Objects.equals(this.parameters, observation.parameters)
                && Objects.equals(this.datastream, observation.datastream)
                && Objects.equals(this.featureOfInterest, observation.featureOfInterest)
                && Objects.equals(this.datastreamIotNavigationLink, observation.datastreamIotNavigationLink)
                && Objects.equals(this.featureOfInterestIotNavigationLink, observation.featureOfInterestIotNavigationLink)
                && Objects.equals(this.multiDatastream, observation.multiDatastream)
                && Objects.equals(this.multiDatastreamIotNavigationLink, observation.multiDatastreamIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotId, iotSelfLink, phenomenonTime, result, resultTime, resultQuality, validTime, parameters, datastream, featureOfInterest, datastreamIotNavigationLink, featureOfInterestIotNavigationLink, multiDatastream, multiDatastreamIotNavigationLink);
    }

    @Override
    public boolean equals(java.lang.Object o, float delta) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Observation observation = (Observation) o;
        return Objects.equals(this.iotId, observation.iotId)
                && Objects.equals(this.iotSelfLink, observation.iotSelfLink)
                && Objects.equals(this.phenomenonTime, observation.phenomenonTime)
                && Objects.equals(this.result, observation.result)
                && Objects.equals(this.resultTime, observation.resultTime)
                && Objects.equals(this.resultQuality, observation.resultQuality)
                && Objects.equals(this.validTime, observation.validTime)
                && Objects.equals(this.parameters, observation.parameters)
                && DeltaComparable.equals(this.datastream, observation.datastream, delta)
                && DeltaComparable.equals(this.featureOfInterest, observation.featureOfInterest, delta)
                && Objects.equals(this.datastreamIotNavigationLink, observation.datastreamIotNavigationLink)
                && Objects.equals(this.featureOfInterestIotNavigationLink, observation.featureOfInterestIotNavigationLink)
                && DeltaComparable.equals(this.multiDatastream, observation.multiDatastream, delta)
                && Objects.equals(this.multiDatastreamIotNavigationLink, observation.multiDatastreamIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Observation {\n");

        sb.append("    iotId: ").append(toIndentedString(iotId)).append("\n");
        sb.append("    iotSelfLink: ").append(toIndentedString(iotSelfLink)).append("\n");
        sb.append("    phenomenonTime: ").append(toIndentedString(phenomenonTime)).append("\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
        sb.append("    resultTime: ").append(toIndentedString(resultTime)).append("\n");
        sb.append("    resultQuality: ").append(toIndentedString(resultQuality)).append("\n");
        sb.append("    validTime: ").append(toIndentedString(validTime)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
        sb.append("    datastream: ").append(toIndentedString(datastream)).append("\n");
        sb.append("    featureOfInterest: ").append(toIndentedString(featureOfInterest)).append("\n");
        sb.append("    datastreamIotNavigationLink: ").append(toIndentedString(datastreamIotNavigationLink)).append("\n");
        sb.append("    featureOfInterestIotNavigationLink: ").append(toIndentedString(featureOfInterestIotNavigationLink)).append("\n");
        sb.append("    multiDatastream: ").append(toIndentedString(multiDatastream)).append("\n");
        sb.append("    multiDatastreamIotNavigationLink: ").append(toIndentedString(multiDatastreamIotNavigationLink)).append("\n");
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
