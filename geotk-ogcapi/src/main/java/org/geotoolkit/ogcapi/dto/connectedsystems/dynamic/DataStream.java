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
 * A DataStream resource, describing a stream of {@link Observation}s produced by a system's
 * output.
 *
 * <p>This same class is used for both read responses and create/replace request bodies (the
 * spec's "create" variant only differs by which fields are server-vs-client populated, which is
 * a server-side concern and not modeled as a separate Java type here).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class DataStream extends BaseStream {

    public static final String JSON_PROPERTY_SYSTEM_LINK = "system@link";
    private Link systemLink;

    public static final String JSON_PROPERTY_OUTPUT_NAME = "outputName";
    private String outputName;

    public static final String JSON_PROPERTY_PROCEDURE_LINK = "procedure@link";
    private Link procedureLink;

    public static final String JSON_PROPERTY_DEPLOYMENT_LINK = "deployment@link";
    private Link deploymentLink;

    public static final String JSON_PROPERTY_FEATURE_OF_INTEREST_LINK = "featureOfInterest@link";
    private Link featureOfInterestLink;

    public static final String JSON_PROPERTY_SAMPLING_FEATURE_LINK = "samplingFeature@link";
    private Link samplingFeatureLink;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTIES = "observedProperties";
    private List<ObservedPropertyRef> observedProperties;

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    private TimePeriod phenomenonTime;

    public static final String JSON_PROPERTY_PHENOMENON_TIME_INTERVAL = "phenomenonTimeInterval";
    private String phenomenonTimeInterval;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    private TimePeriod resultTime;

    public static final String JSON_PROPERTY_RESULT_TIME_INTERVAL = "resultTimeInterval";
    private String resultTimeInterval;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_RESULT_TYPE = "resultType";
    private String resultType;

    public static final String JSON_PROPERTY_LIVE = "live";
    private Boolean live;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    private ObservationSchema schema;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Link to the system that produces this stream's output. (required)
     */
    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public Link getSystemLink() {
        return systemLink;
    }

    @JsonProperty(JSON_PROPERTY_SYSTEM_LINK)
    public void setSystemLink(Link systemLink) {
        this.systemLink = systemLink;
    }

    public DataStream systemLink(Link systemLink) {
        setSystemLink(systemLink);
        return this;
    }

    /**
     * Name of the system output that this stream is derived from. (optional)
     */
    @JsonProperty(JSON_PROPERTY_OUTPUT_NAME)
    public String getOutputName() {
        return outputName;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUT_NAME)
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public DataStream outputName(String outputName) {
        setOutputName(outputName);
        return this;
    }

    /**
     * Link to the procedure implemented by the system producing this stream. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public Link getProcedureLink() {
        return procedureLink;
    }

    @JsonProperty(JSON_PROPERTY_PROCEDURE_LINK)
    public void setProcedureLink(Link procedureLink) {
        this.procedureLink = procedureLink;
    }

    public DataStream procedureLink(Link procedureLink) {
        setProcedureLink(procedureLink);
        return this;
    }

    /**
     * Link to the deployment during which this stream's data is/was produced. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEPLOYMENT_LINK)
    public Link getDeploymentLink() {
        return deploymentLink;
    }

    @JsonProperty(JSON_PROPERTY_DEPLOYMENT_LINK)
    public void setDeploymentLink(Link deploymentLink) {
        this.deploymentLink = deploymentLink;
    }

    public DataStream deploymentLink(Link deploymentLink) {
        setDeploymentLink(deploymentLink);
        return this;
    }

    /**
     * Link to the feature of interest targeted by this stream's observations. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST_LINK)
    public Link getFeatureOfInterestLink() {
        return featureOfInterestLink;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST_LINK)
    public void setFeatureOfInterestLink(Link featureOfInterestLink) {
        this.featureOfInterestLink = featureOfInterestLink;
    }

    public DataStream featureOfInterestLink(Link featureOfInterestLink) {
        setFeatureOfInterestLink(featureOfInterestLink);
        return this;
    }

    /**
     * Link to the sampling feature targeted by this stream's observations. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_LINK)
    public Link getSamplingFeatureLink() {
        return samplingFeatureLink;
    }

    @JsonProperty(JSON_PROPERTY_SAMPLING_FEATURE_LINK)
    public void setSamplingFeatureLink(Link samplingFeatureLink) {
        this.samplingFeatureLink = samplingFeatureLink;
    }

    public DataStream samplingFeatureLink(Link samplingFeatureLink) {
        setSamplingFeatureLink(samplingFeatureLink);
        return this;
    }

    /**
     * Properties observed by this stream. (required, but the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTIES)
    public List<ObservedPropertyRef> getObservedProperties() {
        return observedProperties;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTIES)
    public void setObservedProperties(List<ObservedPropertyRef> observedProperties) {
        this.observedProperties = observedProperties;
    }

    public DataStream observedProperties(List<ObservedPropertyRef> observedProperties) {
        setObservedProperties(observedProperties);
        return this;
    }

    /**
     * Overall time period spanned by the phenomenon times of this stream's observations.
     * (required, but the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public TimePeriod getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public void setPhenomenonTime(TimePeriod phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public DataStream phenomenonTime(TimePeriod phenomenonTime) {
        setPhenomenonTime(phenomenonTime);
        return this;
    }

    /**
     * Typical interval between successive phenomenon times, as an ISO 8601 duration. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME_INTERVAL)
    public String getPhenomenonTimeInterval() {
        return phenomenonTimeInterval;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME_INTERVAL)
    public void setPhenomenonTimeInterval(String phenomenonTimeInterval) {
        this.phenomenonTimeInterval = phenomenonTimeInterval;
    }

    public DataStream phenomenonTimeInterval(String phenomenonTimeInterval) {
        setPhenomenonTimeInterval(phenomenonTimeInterval);
        return this;
    }

    /**
     * Overall time period spanned by the result times of this stream's observations. (required,
     * but the value may be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public TimePeriod getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public void setResultTime(TimePeriod resultTime) {
        this.resultTime = resultTime;
    }

    public DataStream resultTime(TimePeriod resultTime) {
        setResultTime(resultTime);
        return this;
    }

    /**
     * Typical interval between successive result times, as an ISO 8601 duration. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME_INTERVAL)
    public String getResultTimeInterval() {
        return resultTimeInterval;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME_INTERVAL)
    public void setResultTimeInterval(String resultTimeInterval) {
        this.resultTimeInterval = resultTimeInterval;
    }

    public DataStream resultTimeInterval(String resultTimeInterval) {
        setResultTimeInterval(resultTimeInterval);
        return this;
    }

    /**
     * Kind of data carried by this stream, one of {@code status} or {@code observation}.
     * (optional)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    public DataStream type(String type) {
        setType(type);
        return this;
    }

    /**
     * Kind of result carried by this stream's observations, one of {@code measure}, {@code
     * vector}, {@code record}, {@code coverage} or {@code complex}. (required, but the value may
     * be JSON null)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TYPE)
    public String getResultType() {
        return resultType;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TYPE)
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public DataStream resultType(String resultType) {
        setResultType(resultType);
        return this;
    }

    /**
     * Whether this stream is currently receiving live data. (required, but the value may be JSON
     * null)
     */
    @JsonProperty(JSON_PROPERTY_LIVE)
    public Boolean getLive() {
        return live;
    }

    @JsonProperty(JSON_PROPERTY_LIVE)
    public void setLive(Boolean live) {
        this.live = live;
    }

    public DataStream live(Boolean live) {
        setLive(live);
        return this;
    }

    /**
     * Schema describing the format/structure of this stream's observation results. (optional)
     */
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    public ObservationSchema getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    public void setSchema(ObservationSchema schema) {
        this.schema = schema;
    }

    public DataStream schema(ObservationSchema schema) {
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

    public DataStream links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
