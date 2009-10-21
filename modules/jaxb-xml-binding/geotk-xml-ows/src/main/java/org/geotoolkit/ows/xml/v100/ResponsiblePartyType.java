/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ows.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Identification of, and means of communication with, person responsible for the server. At least one of IndividualName, OrganisationName, or PositionName shall be included. 
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
 *         &lt;element ref="{http://www.opengis.net/ows}IndividualName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}OrganisationName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}PositionName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}ContactInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Role"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
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
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * Gets the value of the organisationName property.
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * Gets the value of the positionName property.
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Gets the value of the contactInfo property.
     */
    public ContactType getContactInfo() {
        return contactInfo;
    }

    /**
     * Gets the value of the role property.
     */
    public CodeType getRole() {
        return role;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ResponsiblePartyType) {
            final ResponsiblePartyType that = (ResponsiblePartyType) object;
            return Utilities.equals(this.contactInfo,      that.contactInfo)    &&
                   Utilities.equals(this.individualName,   that.individualName) &&
                   Utilities.equals(this.positionName,     that.positionName)   &&
                   Utilities.equals(this.role,             that.role)           &&
                   Utilities.equals(this.organisationName, that.organisationName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.individualName != null ? this.individualName.hashCode() : 0);
        hash = 37 * hash + (this.organisationName != null ? this.organisationName.hashCode() : 0);
        hash = 37 * hash + (this.positionName != null ? this.positionName.hashCode() : 0);
        hash = 37 * hash + (this.contactInfo != null ? this.contactInfo.hashCode() : 0);
        hash = 37 * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }
}
