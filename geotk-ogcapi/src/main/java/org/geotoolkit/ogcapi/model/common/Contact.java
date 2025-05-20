/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Identification of, and means of communication with, person responsible for
 * the resource.
 */
@JsonPropertyOrder({
    Contact.JSON_PROPERTY_IDENTIFIER,
    Contact.JSON_PROPERTY_NAME,
    Contact.JSON_PROPERTY_POSITION,
    Contact.JSON_PROPERTY_ORGANIZATION,
    Contact.JSON_PROPERTY_LOGO,
    Contact.JSON_PROPERTY_PHONES,
    Contact.JSON_PROPERTY_EMAILS,
    Contact.JSON_PROPERTY_ADDRESSES,
    Contact.JSON_PROPERTY_LINKS,
    Contact.JSON_PROPERTY_HOURS_OF_SERVICE,
    Contact.JSON_PROPERTY_CONTACT_INSTRUCTIONS,
    Contact.JSON_PROPERTY_ROLES
})
@XmlRootElement(name = "Contact")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Contact")
public final class Contact extends DataTransferObject {

    public static final String JSON_PROPERTY_IDENTIFIER = "identifier";
    @XmlElement(name = JSON_PROPERTY_IDENTIFIER)
    @jakarta.annotation.Nullable
    private String identifier;

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = JSON_PROPERTY_NAME)
    @jakarta.annotation.Nullable
    private String name;

    public static final String JSON_PROPERTY_POSITION = "position";
    @XmlElement(name = JSON_PROPERTY_POSITION)
    @jakarta.annotation.Nullable
    private String position;

    public static final String JSON_PROPERTY_ORGANIZATION = "organization";
    @XmlElement(name = JSON_PROPERTY_ORGANIZATION)
    @jakarta.annotation.Nullable
    private String organization;

    public static final String JSON_PROPERTY_LOGO = "logo";
    @XmlElement(name = JSON_PROPERTY_LOGO)
    @jakarta.annotation.Nullable
    private Link logo;

    public static final String JSON_PROPERTY_PHONES = "phones";
    @XmlElement(name = "phones")
    @jakarta.annotation.Nullable
    private List<Phone> phones = new ArrayList<>();

    public static final String JSON_PROPERTY_EMAILS = "emails";
    @XmlElement(name = "emails")
    @jakarta.annotation.Nullable
    private List<Email> emails = new ArrayList<>();

    public static final String JSON_PROPERTY_ADDRESSES = "addresses";
    @XmlElement(name = "addresses")
    @jakarta.annotation.Nullable
    private List<Address> addresses = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_HOURS_OF_SERVICE = "hoursOfService";
    @XmlElement(name = "hoursOfService")
    @jakarta.annotation.Nullable
    private String hoursOfService;

    public static final String JSON_PROPERTY_CONTACT_INSTRUCTIONS = "contactInstructions";
    @XmlElement(name = "contactInstructions")
    @jakarta.annotation.Nullable
    private String contactInstructions;

    public static final String JSON_PROPERTY_ROLES = "roles";
    @XmlElement(name = "roles")
    @jakarta.annotation.Nullable
    private List<String> roles = new ArrayList<>();

    public Contact() {
    }

    public Contact identifier(@jakarta.annotation.Nullable String identifier) {
        this.identifier = identifier;
        return this;
    }

    /**
     * A value uniquely identifying a contact.
     *
     * @return identifier
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_IDENTIFIER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty(JSON_PROPERTY_IDENTIFIER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "identifier")
    public void setIdentifier(@jakarta.annotation.Nullable String identifier) {
        this.identifier = identifier;
    }

    public Contact name(@jakarta.annotation.Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * The name of the responsible person.
     *
     * @return name
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nullable String name) {
        this.name = name;
    }

    public Contact position(@jakarta.annotation.Nullable String position) {
        this.position = position;
        return this;
    }

    /**
     * The name of the role or position of the responsible person taken from the
     * organization&#39;s formal organizational hierarchy or chart.
     *
     * @return position
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "position")
    public String getPosition() {
        return position;
    }

    @JsonProperty(JSON_PROPERTY_POSITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "position")
    public void setPosition(@jakarta.annotation.Nullable String position) {
        this.position = position;
    }

    public Contact organization(@jakarta.annotation.Nullable String organization) {
        this.organization = organization;
        return this;
    }

    /**
     * Organization/affiliation of the contact.
     *
     * @return organization
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ORGANIZATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "organization")
    public String getOrganization() {
        return organization;
    }

    @JsonProperty(JSON_PROPERTY_ORGANIZATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "organization")
    public void setOrganization(@jakarta.annotation.Nullable String organization) {
        this.organization = organization;
    }

    public Contact logo(@jakarta.annotation.Nullable Link logo) {
        this.logo = logo;
        return this;
    }

    /**
     * Graphic identifying a contact. The link relation should be
     * &#x60;icon&#x60; and the media type should be an image media type.
     *
     * @return logo
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "logo")
    public Link getLogo() {
        return logo;
    }

    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "logo")
    public void setLogo(@jakarta.annotation.Nullable Link logo) {
        this.logo = logo;
    }

    public Contact phones(@jakarta.annotation.Nullable List<Phone> phones) {
        this.phones = phones;
        return this;
    }

    public Contact addPhonesItem(Phone phonesItem) {
        if (this.phones == null) {
            this.phones = new ArrayList<>();
        }
        this.phones.add(phonesItem);
        return this;
    }

    /**
     * Telephone numbers at which contact can be made.
     *
     * @return phones
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PHONES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "phones")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Phone> getPhones() {
        return phones;
    }

    @JsonProperty(JSON_PROPERTY_PHONES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "phones")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setPhones(@jakarta.annotation.Nullable List<Phone> phones) {
        this.phones = phones;
    }

    public Contact emails(@jakarta.annotation.Nullable List<Email> emails) {
        this.emails = emails;
        return this;
    }

    public Contact addEmailsItem(Email emailsItem) {
        if (this.emails == null) {
            this.emails = new ArrayList<>();
        }
        this.emails.add(emailsItem);
        return this;
    }

    /**
     * Email addresses at which contact can be made.
     *
     * @return emails
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EMAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "emails")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Email> getEmails() {
        return emails;
    }

    @JsonProperty(JSON_PROPERTY_EMAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "emails")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setEmails(@jakarta.annotation.Nullable List<Email> emails) {
        this.emails = emails;
    }

    public Contact addresses(@jakarta.annotation.Nullable List<Address> addresses) {
        this.addresses = addresses;
        return this;
    }

    public Contact addAddressesItem(Address addressesItem) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        this.addresses.add(addressesItem);
        return this;
    }

    /**
     * Physical location at which contact can be made.
     *
     * @return addresses
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ADDRESSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "addresses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Address> getAddresses() {
        return addresses;
    }

    @JsonProperty(JSON_PROPERTY_ADDRESSES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "addresses")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setAddresses(@jakarta.annotation.Nullable List<Address> addresses) {
        this.addresses = addresses;
    }

    public Contact links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    public Contact addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * On-line information about the contact.
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    public Contact hoursOfService(@jakarta.annotation.Nullable String hoursOfService) {
        this.hoursOfService = hoursOfService;
        return this;
    }

    /**
     * Time period when the contact can be contacted.
     *
     * @return hoursOfService
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_HOURS_OF_SERVICE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "hoursOfService")
    public String getHoursOfService() {
        return hoursOfService;
    }

    @JsonProperty(JSON_PROPERTY_HOURS_OF_SERVICE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "hoursOfService")
    public void setHoursOfService(@jakarta.annotation.Nullable String hoursOfService) {
        this.hoursOfService = hoursOfService;
    }

    public Contact contactInstructions(@jakarta.annotation.Nullable String contactInstructions) {
        this.contactInstructions = contactInstructions;
        return this;
    }

    /**
     * Supplemental instructions on how or when to contact the responsible
     * party.
     *
     * @return contactInstructions
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTACT_INSTRUCTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contactInstructions")
    public String getContactInstructions() {
        return contactInstructions;
    }

    @JsonProperty(JSON_PROPERTY_CONTACT_INSTRUCTIONS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contactInstructions")
    public void setContactInstructions(@jakarta.annotation.Nullable String contactInstructions) {
        this.contactInstructions = contactInstructions;
    }

    public Contact roles(@jakarta.annotation.Nullable List<String> roles) {
        this.roles = roles;
        return this;
    }

    public Contact addRolesItem(String rolesItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(rolesItem);
        return this;
    }

    /**
     * The list of duties, job functions or permissions assigned by the system
     * and associated with the context of this member.
     *
     * @return roles
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "roles")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getRoles() {
        return roles;
    }

    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "roles")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setRoles(@jakarta.annotation.Nullable List<String> roles) {
        this.roles = roles;
    }

    /**
     * Return true if this contact object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return Objects.equals(this.identifier, contact.identifier)
                && Objects.equals(this.name, contact.name)
                && Objects.equals(this.position, contact.position)
                && Objects.equals(this.organization, contact.organization)
                && Objects.equals(this.logo, contact.logo)
                && Objects.equals(this.phones, contact.phones)
                && Objects.equals(this.emails, contact.emails)
                && Objects.equals(this.addresses, contact.addresses)
                && Objects.equals(this.links, contact.links)
                && Objects.equals(this.hoursOfService, contact.hoursOfService)
                && Objects.equals(this.contactInstructions, contact.contactInstructions)
                && Objects.equals(this.roles, contact.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, name, position, organization, logo, phones, emails, addresses, links, hoursOfService, contactInstructions, roles);
    }

}
