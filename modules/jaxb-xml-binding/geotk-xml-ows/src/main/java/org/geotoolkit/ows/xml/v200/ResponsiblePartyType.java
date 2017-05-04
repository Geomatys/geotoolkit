/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.ows.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Identification of, and means of communication with,
 *       person responsible for the server. At least one of IndividualName,
 *       OrganisationName, or PositionName shall be included.
 *
 * <p>Java class for ResponsiblePartyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ResponsiblePartyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}IndividualName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}OrganisationName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}PositionName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ContactInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Role"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsiblePartyType", propOrder = {
    "individualName",
    "organisationName",
    "positionName",
    "contactInfo",
    "role"
})
public class ResponsiblePartyType {

    @XmlElement(name = "IndividualName")
    private String individualName;
    @XmlElement(name = "OrganisationName")
    private String organisationName;
    @XmlElement(name = "PositionName")
    private String positionName;
    @XmlElement(name = "ContactInfo")
    private ContactType contactInfo;
    @XmlElement(name = "Role", required = true)
    private CodeType role;

    /**
     * Gets the value of the individualName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * Sets the value of the individualName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndividualName(String value) {
        this.individualName = value;
    }

    /**
     * Gets the value of the organisationName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * Sets the value of the organisationName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrganisationName(String value) {
        this.organisationName = value;
    }

    /**
     * Gets the value of the positionName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Sets the value of the positionName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPositionName(String value) {
        this.positionName = value;
    }

    /**
     * Gets the value of the contactInfo property.
     *
     * @return
     *     possible object is
     *     {@link ContactType }
     *
     */
    public ContactType getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the value of the contactInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *
     */
    public void setContactInfo(ContactType value) {
        this.contactInfo = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setRole(CodeType value) {
        this.role = value;
    }

}
