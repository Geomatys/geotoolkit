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
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * A single command belonging to a {@link ControlStream}.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class Command extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_CONTROLSTREAM_ID = "controlstream@id";
    private String controlstreamId;

    public static final String JSON_PROPERTY_SAMPLING_FEATURE_ID = "samplingFeature@id";
    private String samplingFeatureId;

    public static final String JSON_PROPERTY_PROCEDURE_LINK = "procedure@link";
    private Link procedureLink;

    public static final String JSON_PROPERTY_ISSUE_TIME = "issueTime";
    private String issueTime;

    public static final String JSON_PROPERTY_EXECUTION_TIME = "executionTime";
    private TimePeriod executionTime;

    public static final String JSON_PROPERTY_SENDER = "sender";
    private String sender;

    public static final String JSON_PROPERTY_CURRENT_STATUS = "currentStatus";
    private CommandStatusCode currentStatus;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private JsonNode parameters;

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

    public Command id(String id) {
        setId(id);
        return this;
    }

    /**
     * Identifier of the parent {@link ControlStream}. (required)
     */
    @JsonProperty(JSON_PROPERTY_CONTROLSTREAM_ID)
    public String getControlstreamId() {
        return controlstreamId;
    }

    @JsonProperty(JSON_PROPERTY_CONTROLSTREAM_ID)
    public void setControlstreamId(String controlstreamId) {
        this.controlstreamId = controlstreamId;
    }

    public Command controlstreamId(String controlstreamId) {
        setControlstreamId(controlstreamId);
        return this;
    }

    /**
     * Identifier of the sampling feature targeted by this command. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_ID)
    public String getSamplingFeatureId() {
        return samplingFeatureId;
    }

    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_ID)
    public void setSamplingFeatureId(String samplingFeatureId) {
        this.samplingFeatureId = samplingFeatureId;
    }

    public Command samplingFeatureId(String samplingFeatureId) {
        setSamplingFeatureId(samplingFeatureId);
        return this;
    }

    /**
     * Link to the procedure used to execute this command. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public Link getProcedureLink() {
        return procedureLink;
    }

    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public void setProcedureLink(Link procedureLink) {
        this.procedureLink = procedureLink;
    }

    public Command procedureLink(Link procedureLink) {
        setProcedureLink(procedureLink);
        return this;
    }

    /**
     * Time at which the command was issued, as an ISO 8601 date-time. (required)
     */
    @JsonProperty(JSON_PROPERTY_ISSUE_TIME)
    public String getIssueTime() {
        return issueTime;
    }

    @JsonProperty(JSON_PROPERTY_ISSUE_TIME)
    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public Command issueTime(String issueTime) {
        setIssueTime(issueTime);
        return this;
    }

    /**
     * Time period during which the command was/is expected to execute. (optional)
     */
    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public TimePeriod getExecutionTime() {
        return executionTime;
    }

    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public void setExecutionTime(TimePeriod executionTime) {
        this.executionTime = executionTime;
    }

    public Command executionTime(TimePeriod executionTime) {
        setExecutionTime(executionTime);
        return this;
    }

    /**
     * Identifier of the entity that issued this command. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SENDER)
    public String getSender() {
        return sender;
    }

    @JsonProperty(JSON_PROPERTY_SENDER)
    public void setSender(String sender) {
        this.sender = sender;
    }

    public Command sender(String sender) {
        setSender(sender);
        return this;
    }

    /**
     * Most recent status of this command. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CURRENT_STATUS)
    public CommandStatusCode getCurrentStatus() {
        return currentStatus;
    }

    @JsonProperty(JSON_PROPERTY_CURRENT_STATUS)
    public void setCurrentStatus(CommandStatusCode currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Command currentStatus(CommandStatusCode currentStatus) {
        setCurrentStatus(currentStatus);
        return this;
    }

    /**
     * Parameters of this command; shape defined by the parent ControlStream's schema. (required)
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public JsonNode getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public void setParameters(JsonNode parameters) {
        this.parameters = parameters;
    }

    public Command parameters(JsonNode parameters) {
        setParameters(parameters);
        return this;
    }

}
