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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.TimePeriod;

/**
 * Root of the SensorML 3.0 "described object" hierarchy: common metadata shared by every process
 * and deployment description.
 *
 * <p>The {@code type} discriminator fans out, through {@link AbstractProcess}, to the concrete
 * process leaves {@link SimpleProcess}, {@link AggregateProcess}, {@link PhysicalComponent} and
 * {@link PhysicalSystem}. {@link Deployment} is a sibling root that also extends this class but
 * is deliberately NOT part of this polymorphic union (its {@code type} is always the constant
 * {@code "Deployment"} and it is never resolved through this dispatch).</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = SimpleProcess.class, name = "SimpleProcess"),
    @Type(value = AggregateProcess.class, name = "AggregateProcess"),
    @Type(value = PhysicalComponent.class, name = "PhysicalComponent"),
    @Type(value = PhysicalSystem.class, name = "PhysicalSystem")
})
public class DescribedObject extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_UNIQUE_ID = "uniqueId";
    private String uniqueId;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_LANG = "lang";
    private String lang;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords;

    public static final String JSON_PROPERTY_IDENTIFIERS = "identifiers";
    private List<Term> identifiers;

    public static final String JSON_PROPERTY_CLASSIFIERS = "classifiers";
    private List<Term> classifiers;

    public static final String JSON_PROPERTY_VALID_TIME = "validTime";
    private TimePeriod validTime;

    public static final String JSON_PROPERTY_SECURITY_CONSTRAINTS = "securityConstraints";
    private List<JsonNode> securityConstraints;

    public static final String JSON_PROPERTY_LEGAL_CONSTRAINTS = "legalConstraints";
    private List<LegalConstraint> legalConstraints;

    public static final String JSON_PROPERTY_CHARACTERISTICS = "characteristics";
    private List<CharacteristicList> characteristics;

    public static final String JSON_PROPERTY_CAPABILITIES = "capabilities";
    private List<CapabilityList> capabilities;

    public static final String JSON_PROPERTY_CONTACTS = "contacts";
    private List<JsonNode> contacts;

    public static final String JSON_PROPERTY_DOCUMENTS = "documents";
    private List<Document> documents;

    public static final String JSON_PROPERTY_HISTORY = "history";
    private List<Event> history;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Discriminator identifying the concrete kind of described object. (required)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    public DescribedObject type(String type) {
        setType(type);
        return this;
    }

    /**
     * Local identifier. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public DescribedObject id(String id) {
        setId(id);
        return this;
    }

    /**
     * Description. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public DescribedObject description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Globally unique identifier (URI) of this object. (required)
     */
    @JsonProperty(JSON_PROPERTY_UNIQUE_ID)
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty(JSON_PROPERTY_UNIQUE_ID)
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public DescribedObject uniqueId(String uniqueId) {
        setUniqueId(uniqueId);
        return this;
    }

    /**
     * Human readable label. (required)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public DescribedObject label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Language of the textual content (label, description, ...), as an IETF BCP 47 tag. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LANG)
    public String getLang() {
        return lang;
    }

    @JsonProperty(JSON_PROPERTY_LANG)
    public void setLang(String lang) {
        this.lang = lang;
    }

    public DescribedObject lang(String lang) {
        setLang(lang);
        return this;
    }

    /**
     * Free text keywords. (optional)
     */
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public DescribedObject keywords(List<String> keywords) {
        setKeywords(keywords);
        return this;
    }

    /**
     * Identifier terms (e.g. serial number, model number). (optional)
     */
    @JsonProperty(JSON_PROPERTY_IDENTIFIERS)
    public List<Term> getIdentifiers() {
        return identifiers;
    }

    @JsonProperty(JSON_PROPERTY_IDENTIFIERS)
    public void setIdentifiers(List<Term> identifiers) {
        this.identifiers = identifiers;
    }

    public DescribedObject identifiers(List<Term> identifiers) {
        setIdentifiers(identifiers);
        return this;
    }

    /**
     * Classifier terms (e.g. intended application, sensor type). (optional)
     */
    @JsonProperty(JSON_PROPERTY_CLASSIFIERS)
    public List<Term> getClassifiers() {
        return classifiers;
    }

    @JsonProperty(JSON_PROPERTY_CLASSIFIERS)
    public void setClassifiers(List<Term> classifiers) {
        this.classifiers = classifiers;
    }

    public DescribedObject classifiers(List<Term> classifiers) {
        setClassifiers(classifiers);
        return this;
    }

    /**
     * Period of validity of this description. (optional)
     */
    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public TimePeriod getValidTime() {
        return validTime;
    }

    @JsonProperty(JSON_PROPERTY_VALID_TIME)
    public void setValidTime(TimePeriod validTime) {
        this.validTime = validTime;
    }

    public DescribedObject validTime(TimePeriod validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * Security constraints; kept raw since the standard leaves their content open-ended.
     * (optional)
     */
    @JsonProperty(JSON_PROPERTY_SECURITY_CONSTRAINTS)
    public List<JsonNode> getSecurityConstraints() {
        return securityConstraints;
    }

    @JsonProperty(JSON_PROPERTY_SECURITY_CONSTRAINTS)
    public void setSecurityConstraints(List<JsonNode> securityConstraints) {
        this.securityConstraints = securityConstraints;
    }

    public DescribedObject securityConstraints(List<JsonNode> securityConstraints) {
        setSecurityConstraints(securityConstraints);
        return this;
    }

    /**
     * Legal (access/use) constraints. (optional)
     */
    @JsonProperty(JSON_PROPERTY_LEGAL_CONSTRAINTS)
    public List<LegalConstraint> getLegalConstraints() {
        return legalConstraints;
    }

    @JsonProperty(JSON_PROPERTY_LEGAL_CONSTRAINTS)
    public void setLegalConstraints(List<LegalConstraint> legalConstraints) {
        this.legalConstraints = legalConstraints;
    }

    public DescribedObject legalConstraints(List<LegalConstraint> legalConstraints) {
        setLegalConstraints(legalConstraints);
        return this;
    }

    /**
     * Named lists of intrinsic characteristics. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CHARACTERISTICS)
    public List<CharacteristicList> getCharacteristics() {
        return characteristics;
    }

    @JsonProperty(JSON_PROPERTY_CHARACTERISTICS)
    public void setCharacteristics(List<CharacteristicList> characteristics) {
        this.characteristics = characteristics;
    }

    public DescribedObject characteristics(List<CharacteristicList> characteristics) {
        setCharacteristics(characteristics);
        return this;
    }

    /**
     * Named lists of functional/performance capabilities. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CAPABILITIES)
    public List<CapabilityList> getCapabilities() {
        return capabilities;
    }

    @JsonProperty(JSON_PROPERTY_CAPABILITIES)
    public void setCapabilities(List<CapabilityList> capabilities) {
        this.capabilities = capabilities;
    }

    public DescribedObject capabilities(List<CapabilityList> capabilities) {
        setCapabilities(capabilities);
        return this;
    }

    /**
     * Contact information. Each entry is either a {@link ResponsibleParty} (identifiable by its
     * {@code role} plus one of {@code individualName}/{@code organisationName}) or a {@link
     * ContactLink} (identifiable by its {@code name} plus {@code link}); the two share no
     * discriminator, so entries are kept raw and should be manually mapped by callers. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONTACTS)
    public List<JsonNode> getContacts() {
        return contacts;
    }

    @JsonProperty(JSON_PROPERTY_CONTACTS)
    public void setContacts(List<JsonNode> contacts) {
        this.contacts = contacts;
    }

    public DescribedObject contacts(List<JsonNode> contacts) {
        setContacts(contacts);
        return this;
    }

    /**
     * Related external documents. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DOCUMENTS)
    public List<Document> getDocuments() {
        return documents;
    }

    @JsonProperty(JSON_PROPERTY_DOCUMENTS)
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public DescribedObject documents(List<Document> documents) {
        setDocuments(documents);
        return this;
    }

    /**
     * History of events in the life of this object. (optional)
     */
    @JsonProperty(JSON_PROPERTY_HISTORY)
    public List<Event> getHistory() {
        return history;
    }

    @JsonProperty(JSON_PROPERTY_HISTORY)
    public void setHistory(List<Event> history) {
        this.history = history;
    }

    public DescribedObject history(List<Event> history) {
        setHistory(history);
        return this;
    }

    /**
     * Web links related to this resource. This field is not part of the base SensorML 3.0 model:
     * it is added by the OGC API - Connected Systems Part 1 encoding of Systems, Procedures and
     * Deployments. (optional)
     *
     * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public DescribedObject links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
