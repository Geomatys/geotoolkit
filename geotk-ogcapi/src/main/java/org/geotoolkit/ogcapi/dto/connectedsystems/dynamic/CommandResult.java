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
 * The result of a {@link Command}.
 *
 * <p>This is a flat class with every alternative-result field nullable: exactly one of {@link
 * #getData()}, {@link #getObservationLink()}, {@link #getObservationSetLink()}, {@link
 * #getDatastreamLink()} (with {@link #getDatastreamLinkResultTime()}) or {@link
 * #getExternalLink()} is expected to be populated per the OGC examples, though the spec is not
 * strict about which combination appears.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class CommandResult extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_COMMAND_ID = "command@id";
    private String commandId;

    public static final String JSON_PROPERTY_DATA = "data";
    private JsonNode data;

    public static final String JSON_PROPERTY_OBSERVATION_LINK = "observation@link";
    private Link observationLink;

    public static final String JSON_PROPERTY_OBSERVATION_SET_LINK = "observationSet@link";
    private Link observationSetLink;

    public static final String JSON_PROPERTY_DATASTREAM_LINK = "datastream@link";
    private Link datastreamLink;

    public static final String JSON_PROPERTY_DATASTREAM_LINK_RESULT_TIME = "datastreamLinkResultTime";
    private TimePeriod datastreamLinkResultTime;

    public static final String JSON_PROPERTY_EXTERNAL_LINK = "external@link";
    private Link externalLink;

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

    public CommandResult id(String id) {
        setId(id);
        return this;
    }

    /**
     * Identifier of the command this result belongs to. (required)
     */
    @JsonProperty(JSON_PROPERTY_COMMAND_ID)
    public String getCommandId() {
        return commandId;
    }

    @JsonProperty(JSON_PROPERTY_COMMAND_ID)
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public CommandResult commandId(String commandId) {
        setCommandId(commandId);
        return this;
    }

    /**
     * Inline result data; shape defined by the parent ControlStream's schema. (optional, see
     * class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_DATA)
    public JsonNode getData() {
        return data;
    }

    @JsonProperty(JSON_PROPERTY_DATA)
    public void setData(JsonNode data) {
        this.data = data;
    }

    public CommandResult data(JsonNode data) {
        setData(data);
        return this;
    }

    /**
     * Link to a single observation produced by this command. (optional, see class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_OBSERVATION_LINK)
    public Link getObservationLink() {
        return observationLink;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVATION_LINK)
    public void setObservationLink(Link observationLink) {
        this.observationLink = observationLink;
    }

    public CommandResult observationLink(Link observationLink) {
        setObservationLink(observationLink);
        return this;
    }

    /**
     * Link to a set of observations produced by this command. (optional, see class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_OBSERVATION_SET_LINK)
    public Link getObservationSetLink() {
        return observationSetLink;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVATION_SET_LINK)
    public void setObservationSetLink(Link observationSetLink) {
        this.observationSetLink = observationSetLink;
    }

    public CommandResult observationSetLink(Link observationSetLink) {
        setObservationSetLink(observationSetLink);
        return this;
    }

    /**
     * Link to the DataStream carrying the observations produced by this command. (optional, see
     * class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_DATASTREAM_LINK)
    public Link getDatastreamLink() {
        return datastreamLink;
    }

    @JsonProperty(JSON_PROPERTY_DATASTREAM_LINK)
    public void setDatastreamLink(Link datastreamLink) {
        this.datastreamLink = datastreamLink;
    }

    public CommandResult datastreamLink(Link datastreamLink) {
        setDatastreamLink(datastreamLink);
        return this;
    }

    /**
     * Result time period qualifying {@link #getDatastreamLink()}: identifies which observations
     * of the linked DataStream were produced by this command. Modeled as a sibling field rather
     * than nested inside the link since {@link Link} has no {@code resultTime} field. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DATASTREAM_LINK_RESULT_TIME)
    public TimePeriod getDatastreamLinkResultTime() {
        return datastreamLinkResultTime;
    }

    @JsonProperty(JSON_PROPERTY_DATASTREAM_LINK_RESULT_TIME)
    public void setDatastreamLinkResultTime(TimePeriod datastreamLinkResultTime) {
        this.datastreamLinkResultTime = datastreamLinkResultTime;
    }

    public CommandResult datastreamLinkResultTime(TimePeriod datastreamLinkResultTime) {
        setDatastreamLinkResultTime(datastreamLinkResultTime);
        return this;
    }

    /**
     * Link to an external, out-of-band result. (optional, see class javadoc)
     */
    @JsonProperty(JSON_PROPERTY_EXTERNAL_LINK)
    public Link getExternalLink() {
        return externalLink;
    }

    @JsonProperty(JSON_PROPERTY_EXTERNAL_LINK)
    public void setExternalLink(Link externalLink) {
        this.externalLink = externalLink;
    }

    public CommandResult externalLink(Link externalLink) {
        setExternalLink(externalLink);
        return this;
    }

}
