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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;

/**
 * A single observation belonging to a {@link DataStream}.
 *
 * <p>Exactly one of {@link #getResult()}/{@link #getResultLink()} is expected to be populated.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class Observation extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_DATASTREAM_ID = "datastream@id";
    private String datastreamId;

    public static final String JSON_PROPERTY_SAMPLING_FEATURE_ID = "samplingFeature@id";
    private String samplingFeatureId;

    public static final String JSON_PROPERTY_PROCEDURE_LINK = "procedure@link";
    private Link procedureLink;

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    private String phenomenonTime;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    private String resultTime;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private JsonNode parameters;

    public static final String JSON_PROPERTY_RESULT = "result";
    private JsonNode result;

    public static final String JSON_PROPERTY_RESULT_LINK = "result@link";
    private Link resultLink;

    /**
     * Local identifier, typically read-only/server-assigned. (required)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public Observation id(String id) {
        setId(id);
        return this;
    }

    /**
     * Identifier of the parent {@link DataStream}. (required)
     */
    @JsonProperty(JSON_PROPERTY_DATASTREAM_ID)
    public String getDatastreamId() {
        return datastreamId;
    }

    @JsonProperty(JSON_PROPERTY_DATASTREAM_ID)
    public void setDatastreamId(String datastreamId) {
        this.datastreamId = datastreamId;
    }

    public Observation datastreamId(String datastreamId) {
        setDatastreamId(datastreamId);
        return this;
    }

    /**
     * Identifier of the sampling feature targeted by this observation. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_ID)
    public String getSamplingFeatureId() {
        return samplingFeatureId;
    }

    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_ID)
    public void setSamplingFeatureId(String samplingFeatureId) {
        this.samplingFeatureId = samplingFeatureId;
    }

    public Observation samplingFeatureId(String samplingFeatureId) {
        setSamplingFeatureId(samplingFeatureId);
        return this;
    }

    /**
     * Link to the procedure used to produce this observation. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public Link getProcedureLink() {
        return procedureLink;
    }

    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public void setProcedureLink(Link procedureLink) {
        this.procedureLink = procedureLink;
    }

    public Observation procedureLink(Link procedureLink) {
        setProcedureLink(procedureLink);
        return this;
    }

    /**
     * Time at which the observed phenomenon occurred, as an ISO 8601 date-time. Defaults to
     * {@link #getResultTime()} if absent. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Observation phenomenonTime(String phenomenonTime) {
        setPhenomenonTime(phenomenonTime);
        return this;
    }

    /**
     * Time at which the result was generated, as an ISO 8601 date-time. (required)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public String getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public Observation resultTime(String resultTime) {
        setResultTime(resultTime);
        return this;
    }

    /**
     * Parameters associated with this observation; shape defined by the parent DataStream's
     * schema. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public JsonNode getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public void setParameters(JsonNode parameters) {
        this.parameters = parameters;
    }

    public Observation parameters(JsonNode parameters) {
        setParameters(parameters);
        return this;
    }

    /**
     * Inline observation result; shape defined by the parent DataStream's schema. (optional, see
     * class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_RESULT)
    public JsonNode getResult() {
        return result;
    }

    @JsonProperty(JSON_PROPERTY_RESULT)
    public void setResult(JsonNode result) {
        this.result = result;
    }

    public Observation result(JsonNode result) {
        setResult(result);
        return this;
    }

    /**
     * Link to an out-of-band observation result. (optional, see class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_LINK)
    public Link getResultLink() {
        return resultLink;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_LINK)
    public void setResultLink(Link resultLink) {
        this.resultLink = resultLink;
    }

    public Observation resultLink(Link resultLink) {
        setResultLink(resultLink);
        return this;
    }

}
