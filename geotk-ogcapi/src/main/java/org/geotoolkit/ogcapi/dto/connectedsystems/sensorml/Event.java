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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;

/**
 * An event that occurred in the life of a described object (e.g. a maintenance operation, a
 * configuration change), used by {@link DescribedObject#getHistory()}.
 *
 * <p>Part 2's {@code dynamic.SystemEvent} resource is defined as this type plus an additional
 * {@code links} field; that combination is not modeled as a separate class here since Part 2 is
 * out of scope for this package.</p>
 *
 * <p>{@link #getTime()} is exposed as a raw {@link JsonNode} since the standard allows either a
 * single time instant (string) or a {@link
 * org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod} (2-element array) with no shared
 * discriminator; callers should inspect the node's shape to disambiguate.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Event extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_IDENTIFIERS = "identifiers";
    private List<Term> identifiers;

    public static final String JSON_PROPERTY_CLASSIFIERS = "classifiers";
    private List<Term> classifiers;

    public static final String JSON_PROPERTY_CONTACTS = "contacts";
    private List<ResponsibleParty> contacts;

    public static final String JSON_PROPERTY_DOCUMENTATION = "documentation";
    private List<Document> documentation;

    public static final String JSON_PROPERTY_TIME = "time";
    private JsonNode time;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private List<AbstractDataComponent> properties;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    private Settings configuration;

    /**
     * Local identifier, typically read-only/server-assigned. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public Event id(String id) {
        setId(id);
        return this;
    }

    /**
     * Human readable label of the event. (required)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public Event label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Description of the event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public Event description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * URI defining the semantics/type of this event. (required)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Event definition(String definition) {
        setDefinition(definition);
        return this;
    }

    /**
     * Identifier terms attached to this event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_IDENTIFIERS)
    public List<Term> getIdentifiers() {
        return identifiers;
    }

    @JsonProperty(JSON_PROPERTY_IDENTIFIERS)
    public void setIdentifiers(List<Term> identifiers) {
        this.identifiers = identifiers;
    }

    public Event identifiers(List<Term> identifiers) {
        setIdentifiers(identifiers);
        return this;
    }

    /**
     * Classifier terms attached to this event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CLASSIFIERS)
    public List<Term> getClassifiers() {
        return classifiers;
    }

    @JsonProperty(JSON_PROPERTY_CLASSIFIERS)
    public void setClassifiers(List<Term> classifiers) {
        this.classifiers = classifiers;
    }

    public Event classifiers(List<Term> classifiers) {
        setClassifiers(classifiers);
        return this;
    }

    /**
     * Parties responsible for/related to this event. Unlike {@link
     * DescribedObject#getContacts()}, only {@link ResponsibleParty} is allowed here (no {@link
     * ContactLink} variant). (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONTACTS)
    public List<ResponsibleParty> getContacts() {
        return contacts;
    }

    @JsonProperty(JSON_PROPERTY_CONTACTS)
    public void setContacts(List<ResponsibleParty> contacts) {
        this.contacts = contacts;
    }

    public Event contacts(List<ResponsibleParty> contacts) {
        setContacts(contacts);
        return this;
    }

    /**
     * Documents related to this event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DOCUMENTATION)
    public List<Document> getDocumentation() {
        return documentation;
    }

    @JsonProperty(JSON_PROPERTY_DOCUMENTATION)
    public void setDocumentation(List<Document> documentation) {
        this.documentation = documentation;
    }

    public Event documentation(List<Document> documentation) {
        setDocumentation(documentation);
        return this;
    }

    /**
     * Time of occurrence, either a time instant (string) or a time period; see class javadoc.
     * (optional)
     */
    @JsonProperty(JSON_PROPERTY_TIME)
    public JsonNode getTime() {
        return time;
    }

    @JsonProperty(JSON_PROPERTY_TIME)
    public void setTime(JsonNode time) {
        this.time = time;
    }

    public Event time(JsonNode time) {
        setTime(time);
        return this;
    }

    /**
     * Properties describing this event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public List<AbstractDataComponent> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    public void setProperties(List<AbstractDataComponent> properties) {
        this.properties = properties;
    }

    public Event properties(List<AbstractDataComponent> properties) {
        setProperties(properties);
        return this;
    }

    /**
     * Configuration/settings resulting from this event. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public Settings getConfiguration() {
        return configuration;
    }

    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(Settings configuration) {
        this.configuration = configuration;
    }

    public Event configuration(Settings configuration) {
        setConfiguration(configuration);
        return this;
    }

}
