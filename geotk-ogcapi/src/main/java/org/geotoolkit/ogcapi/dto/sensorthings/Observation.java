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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A single measurement or reading produced by a Datastream (or MultiDatastream).
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.7.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#observation
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Observation.JSON_PROPERTY_PHENOMENON_TIME,
    Observation.JSON_PROPERTY_RESULT,
    Observation.JSON_PROPERTY_RESULT_TIME,
    Observation.JSON_PROPERTY_RESULT_QUALITY,
    Observation.JSON_PROPERTY_VALID_TIME,
    Observation.JSON_PROPERTY_PARAMETERS,
    Observation.JSON_PROPERTY_DATASTREAM,
    Observation.JSON_PROPERTY_MULTI_DATASTREAM,
    Observation.JSON_PROPERTY_FEATURE_OF_INTEREST
})
public final class Observation extends SensorThingsEntity {

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    private String phenomenonTime;

    public static final String JSON_PROPERTY_RESULT = "result";
    private Object result;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    private String resultTime;

    public static final String JSON_PROPERTY_RESULT_QUALITY = "resultQuality";
    private Object resultQuality;

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private String validTime;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private Map<String, Object> parameters = new HashMap<>();

    public static final String JSON_PROPERTY_DATASTREAM = "Datastream";
    private Datastream datastream;

    public static final String JSON_PROPERTY_MULTI_DATASTREAM = "MultiDatastream";
    private MultiDatastream multiDatastream;

    public static final String JSON_PROPERTY_FEATURE_OF_INTEREST = "FeatureOfInterest";
    private FeatureOfInterest featureOfInterest;

    public Observation() {
    }

    public Observation phenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    /**
     * The ISO 8601 date-time (or interval) the phenomenon was observed. (optional — defaults to
     * {@code resultTime} server-side when omitted)
     *
     * @return the phenomenonTime
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Observation result(Object result) {
        this.result = result;
        return this;
    }

    /**
     * The observed value, shaped according to the owning Datastream's {@code observationType}.
     * Modeled as {@code Object} since the JSON shape varies (number, string, boolean, or nested
     * object). (required)
     *
     * @return the result
     */
    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getResult() {
        return result;
    }

    @JsonProperty(JSON_PROPERTY_RESULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResult(Object result) {
        this.result = result;
    }

    public Observation resultTime(String resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * The ISO 8601 date-time the result was generated, e.g. by the sensor or an algorithm.
     * (optional)
     *
     * @return the resultTime
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public Observation resultQuality(Object resultQuality) {
        this.resultQuality = resultQuality;
        return this;
    }

    /**
     * A JSON object or array describing the quality of the result, e.g. a DQ_Element from
     * ISO 19157. (optional)
     *
     * @return the resultQuality
     */
    @JsonProperty(JSON_PROPERTY_RESULT_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getResultQuality() {
        return resultQuality;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_QUALITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResultQuality(Object resultQuality) {
        this.resultQuality = resultQuality;
    }

    public Observation validTime(String validTime) {
        this.validTime = validTime;
        return this;
    }

    /**
     * The ISO 8601 time interval during which the result is valid. (optional)
     *
     * @return the validTime
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public Observation parameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Observation putParametersItem(String key, Object parametersItem) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, parametersItem);
        return this;
    }

    /**
     * A JSON object containing additional parameters describing the observation, e.g. sensor
     * settings. (optional)
     *
     * @return the parameters
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Observation datastream(Datastream datastream) {
        this.datastream = datastream;
        return this;
    }

    /**
     * The Datastream this observation belongs to, either deep-inserted or linked by
     * {@code @iot.id}. Mutually exclusive with {@code multiDatastream}. (optional in the DTO —
     * one of {@code Datastream}/{@code MultiDatastream} is required by the spec on creation)
     *
     * @return the datastream
     */
    @JsonProperty(JSON_PROPERTY_DATASTREAM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Datastream getDatastream() {
        return datastream;
    }

    @JsonProperty(JSON_PROPERTY_DATASTREAM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDatastream(Datastream datastream) {
        this.datastream = datastream;
    }

    public Observation multiDatastream(MultiDatastream multiDatastream) {
        this.multiDatastream = multiDatastream;
        return this;
    }

    /**
     * The MultiDatastream this observation belongs to, either deep-inserted or linked by
     * {@code @iot.id}. Mutually exclusive with {@code datastream}. (optional)
     *
     * @return the multiDatastream
     */
    @JsonProperty(JSON_PROPERTY_MULTI_DATASTREAM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public MultiDatastream getMultiDatastream() {
        return multiDatastream;
    }

    @JsonProperty(JSON_PROPERTY_MULTI_DATASTREAM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMultiDatastream(MultiDatastream multiDatastream) {
        this.multiDatastream = multiDatastream;
    }

    public Observation featureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    /**
     * The feature this observation's result is ultimately about, either deep-inserted or linked
     * by {@code @iot.id}. Defaulted server-side from the owning Thing's Location when omitted.
     * (optional)
     *
     * @return the featureOfInterest
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Observation other = (Observation) o;
        return entityEquals(other)
                && Objects.equals(this.phenomenonTime, other.phenomenonTime)
                && Objects.equals(this.result, other.result)
                && Objects.equals(this.resultTime, other.resultTime)
                && Objects.equals(this.resultQuality, other.resultQuality)
                && Objects.equals(this.validTime, other.validTime)
                && Objects.equals(this.parameters, other.parameters)
                && Objects.equals(this.datastream, other.datastream)
                && Objects.equals(this.multiDatastream, other.multiDatastream)
                && Objects.equals(this.featureOfInterest, other.featureOfInterest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), phenomenonTime, result, resultTime, resultQuality,
                validTime, parameters, datastream, multiDatastream, featureOfInterest);
    }
}
