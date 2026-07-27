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
import java.util.List;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * A ControlStream resource, describing a stream of {@link Command}s that can be sent to a
 * system's input.
 *
 * <p>This same class is used for both read responses and create/replace request bodies.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class ControlStream extends BaseStream {

    public static final String JSON_PROPERTY_SYSTEM_LINK = "system@link";
    private Link systemLink;

    public static final String JSON_PROPERTY_INPUT_NAME = "inputName";
    private String inputName;

    public static final String JSON_PROPERTY_PROCEDURE_LINK = "procedure@link";
    private Link procedureLink;

    public static final String JSON_PROPERTY_DEPLOYMENT_LINK = "deployment@link";
    private Link deploymentLink;

    public static final String JSON_PROPERTY_FEATURE_OF_INTEREST_LINK = "featureOfInterest@link";
    private Link featureOfInterestLink;

    public static final String JSON_PROPERTY_SAMPLING_FEATURE_LINK = "samplingFeature@link";
    private Link samplingFeatureLink;

    public static final String JSON_PROPERTY_CONTROLLED_PROPERTIES = "controlledProperties";
    private List<ObservedPropertyRef> controlledProperties;

    public static final String JSON_PROPERTY_ISSUE_TIME = "issueTime";
    private TimePeriod issueTime;

    public static final String JSON_PROPERTY_EXECUTION_TIME = "executionTime";
    private TimePeriod executionTime;

    public static final String JSON_PROPERTY_LIVE = "live";
    private Boolean live;

    public static final String JSON_PROPERTY_ASYNC = "async";
    private Boolean async;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    private CommandSchema schema;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Link to the system that accepts commands from this stream. (required)
     */
    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public Link getSystemLink() {
        return systemLink;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public void setSystemLink(Link systemLink) {
        this.systemLink = systemLink;
    }

    public ControlStream systemLink(Link systemLink) {
        setSystemLink(systemLink);
        return this;
    }

    /**
     * Name of the system input that this stream is targeting. (optional)
     */
    @JsonProperty(JSON_PROPERTY_INPUT_NAME)
    public String getInputName() {
        return inputName;
    }

    @JsonProperty(JSON_PROPERTY_INPUT_NAME)
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public ControlStream inputName(String inputName) {
        setInputName(inputName);
        return this;
    }

    /**
     * Link to the procedure implemented by the system accepting commands. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public Link getProcedureLink() {
        return procedureLink;
    }

    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public void setProcedureLink(Link procedureLink) {
        this.procedureLink = procedureLink;
    }

    public ControlStream procedureLink(Link procedureLink) {
        setProcedureLink(procedureLink);
        return this;
    }

    /**
     * Link to the deployment during which commands are/were accepted. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEPLOYMENT_LINK)
    public Link getDeploymentLink() {
        return deploymentLink;
    }

    @JsonProperty(JSON_PROPERTY_DEPLOYMENT_LINK)
    public void setDeploymentLink(Link deploymentLink) {
        this.deploymentLink = deploymentLink;
    }

    public ControlStream deploymentLink(Link deploymentLink) {
        setDeploymentLink(deploymentLink);
        return this;
    }

    /**
     * Link to the feature of interest targeted by this stream's commands. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST_LINK)
    public Link getFeatureOfInterestLink() {
        return featureOfInterestLink;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST_LINK)
    public void setFeatureOfInterestLink(Link featureOfInterestLink) {
        this.featureOfInterestLink = featureOfInterestLink;
    }

    public ControlStream featureOfInterestLink(Link featureOfInterestLink) {
        setFeatureOfInterestLink(featureOfInterestLink);
        return this;
    }

    /**
     * Link to the sampling feature targeted by this stream's commands. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_LINK)
    public Link getSamplingFeatureLink() {
        return samplingFeatureLink;
    }

    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_LINK)
    public void setSamplingFeatureLink(Link samplingFeatureLink) {
        this.samplingFeatureLink = samplingFeatureLink;
    }

    public ControlStream samplingFeatureLink(Link samplingFeatureLink) {
        setSamplingFeatureLink(samplingFeatureLink);
        return this;
    }

    /**
     * Properties controlled by this stream. (required, but the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_CONTROLLED_PROPERTIES)
    public List<ObservedPropertyRef> getControlledProperties() {
        return controlledProperties;
    }

    @JsonProperty(JSON_PROPERTY_CONTROLLED_PROPERTIES)
    public void setControlledProperties(List<ObservedPropertyRef> controlledProperties) {
        this.controlledProperties = controlledProperties;
    }

    public ControlStream controlledProperties(List<ObservedPropertyRef> controlledProperties) {
        setControlledProperties(controlledProperties);
        return this;
    }

    /**
     * Overall time period spanned by the issue times of this stream's commands. (required, but
     * the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_ISSUE_TIME)
    public TimePeriod getIssueTime() {
        return issueTime;
    }

    @JsonProperty(JSON_PROPERTY_ISSUE_TIME)
    public void setIssueTime(TimePeriod issueTime) {
        this.issueTime = issueTime;
    }

    public ControlStream issueTime(TimePeriod issueTime) {
        setIssueTime(issueTime);
        return this;
    }

    /**
     * Overall time period spanned by the execution times of this stream's commands. (required,
     * but the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public TimePeriod getExecutionTime() {
        return executionTime;
    }

    @JsonProperty(JSON_PROPERTY_EXECUTION_TIME)
    public void setExecutionTime(TimePeriod executionTime) {
        this.executionTime = executionTime;
    }

    public ControlStream executionTime(TimePeriod executionTime) {
        setExecutionTime(executionTime);
        return this;
    }

    /**
     * Whether this stream is currently able to accept live commands. (required, but the value may
     * be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_LIVE)
    public Boolean getLive() {
        return live;
    }

    @JsonProperty(JSON_PROPERTY_LIVE)
    public void setLive(Boolean live) {
        this.live = live;
    }

    public ControlStream live(Boolean live) {
        setLive(live);
        return this;
    }

    /**
     * Whether commands sent through this stream are executed asynchronously. (required)
     */
    @JsonProperty(JSON_PROPERTY_ASYNC)
    public Boolean getAsync() {
        return async;
    }

    @JsonProperty(JSON_PROPERTY_ASYNC)
    public void setAsync(Boolean async) {
        this.async = async;
    }

    public ControlStream async(Boolean async) {
        setAsync(async);
        return this;
    }

    /**
     * Schema describing the format/structure of this stream's commands. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    public CommandSchema getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    public void setSchema(CommandSchema schema) {
        this.schema = schema;
    }

    public ControlStream schema(CommandSchema schema) {
        setSchema(schema);
        return this;
    }

    /**
     * Related links. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public ControlStream links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
