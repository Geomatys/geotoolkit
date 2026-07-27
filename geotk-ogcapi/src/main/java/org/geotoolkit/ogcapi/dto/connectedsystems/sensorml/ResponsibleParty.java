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
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Description of a person or organization responsible for a described object, one of the two
 * variants usable in {@link DescribedObject#getContacts()} (the other being {@link ContactLink}).
 *
 * <p>Structurally disambiguated from {@link ContactLink} by the presence of {@code role} together
 * with either {@code individualName} or {@code organisationName} (no shared discriminator is
 * defined by the standard). Exactly one of {@code individualName}/{@code organisationName} is
 * expected to be set, though this is not enforced in code.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class ResponsibleParty extends DataTransferObject {

    public static final String JSON_PROPERTY_INDIVIDUAL_NAME = "individualName";
    private String individualName;

    public static final String JSON_PROPERTY_ORGANISATION_NAME = "organisationName";
    private String organisationName;

    public static final String JSON_PROPERTY_POSITION_NAME = "positionName";
    private String positionName;

    public static final String JSON_PROPERTY_CONTACT_INFO = "contactInfo";
    private Contact contactInfo;

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    /**
     * Name of the individual. (optional; exactly one of individualName/organisationName expected)
     */
    @JsonProperty(JSON_PROPERTY_INDIVIDUAL_NAME)
    public String getIndividualName() {
        return individualName;
    }

    @JsonProperty(JSON_PROPERTY_INDIVIDUAL_NAME)
    public void setIndividualName(String individualName) {
        this.individualName = individualName;
    }

    public ResponsibleParty individualName(String individualName) {
        setIndividualName(individualName);
        return this;
    }

    /**
     * Name of the organisation. (optional; exactly one of individualName/organisationName expected)
     */
    @JsonProperty(JSON_PROPERTY_ORGANISATION_NAME)
    public String getOrganisationName() {
        return organisationName;
    }

    @JsonProperty(JSON_PROPERTY_ORGANISATION_NAME)
    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public ResponsibleParty organisationName(String organisationName) {
        setOrganisationName(organisationName);
        return this;
    }

    /**
     * Position/role name held within the organisation. (optional)
     */
    @JsonProperty(JSON_PROPERTY_POSITION_NAME)
    public String getPositionName() {
        return positionName;
    }

    @JsonProperty(JSON_PROPERTY_POSITION_NAME)
    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public ResponsibleParty positionName(String positionName) {
        setPositionName(positionName);
        return this;
    }

    /**
     * Contact information. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONTACT_INFO)
    public Contact getContactInfo() {
        return contactInfo;
    }

    @JsonProperty(JSON_PROPERTY_CONTACT_INFO)
    public void setContactInfo(Contact contactInfo) {
        this.contactInfo = contactInfo;
    }

    public ResponsibleParty contactInfo(Contact contactInfo) {
        setContactInfo(contactInfo);
        return this;
    }

    /**
     * URI qualifying the role of this party. (required)
     */
    @JsonProperty(JSON_PROPERTY_ROLE)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    public void setRole(String role) {
        this.role = role;
    }

    public ResponsibleParty role(String role) {
        setRole(role);
        return this;
    }

}
