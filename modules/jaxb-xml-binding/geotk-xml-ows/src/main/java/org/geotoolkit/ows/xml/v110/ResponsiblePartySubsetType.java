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
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * For OWS use in the ServiceProvider section of a service metadata document, the optional organizationName element was removed, since this type is always used with the ProviderName element which provides that information. The mandatory "role" element was changed to optional, since no clear use of this information is known in the ServiceProvider section. 
 * 
 * <p>Java class for ResponsiblePartySubsetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponsiblePartySubsetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}IndividualName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}PositionName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ContactInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Role" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @atuhor Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsiblePartySubsetType", propOrder = {
    "individualName",
    "positionName",
    "contactInfo",
    "role"
})
public class ResponsiblePartySubsetType {

    @XmlElement(name = "IndividualName")
    private String individualName;
    @XmlElement(name = "PositionName")
    private String positionName;
    @XmlElement(name = "ContactInfo")
    private ContactType contactInfo;
    @XmlElement(name = "Role")
    private CodeType role;

    /**
     * Empty constructor used by JAXB.
     */
    ResponsiblePartySubsetType(){
    }
    
    /**
     *Build a new Responsible party subset.
     */
    public ResponsiblePartySubsetType(String individualName, String positionName, ContactType contactInfo,
            CodeType role){
        this.contactInfo    = contactInfo;
        this.individualName = individualName;
        this.positionName   = positionName;
        this.role           = role;
    }
    
    
    /**
     * Gets the value of the individualName property.
     */
    public String getIndividualName() {
        return individualName;
    }

    /**
     * Gets the value of the positionName property.
     * 
     */
    public String getPositionName() {
        return positionName;
    }

    /**
     * Gets the value of the contactInfo property.
     * 
     */
    public ContactType getContactInfo() {
        return contactInfo;
    }

    /**
     * Gets the value of the role property.
     * 
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
        if (object instanceof ResponsiblePartySubsetType) {
            final ResponsiblePartySubsetType that = (ResponsiblePartySubsetType) object;

            return Utilities.equals(this.contactInfo,    that.contactInfo)    &&
                   Utilities.equals(this.individualName, that.individualName) &&
                   Utilities.equals(this.positionName,   that.positionName)   &&
                   Utilities.equals(this.role,           that.role);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.individualName != null ? this.individualName.hashCode() : 0);
        hash = 67 * hash + (this.positionName != null ? this.positionName.hashCode() : 0);
        hash = 67 * hash + (this.contactInfo != null ? this.contactInfo.hashCode() : 0);
        hash = 67 * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class:ResponsiblePartySubsetType").append('\n');
        s.append("individualName=").append(individualName).append('\n');
        s.append("positionName=").append(positionName).append('\n');
        if (contactInfo != null)
            s.append("contactInfo=").append(contactInfo.toString()).append('\n');
        if (role != null)
            s.append("role=").append(role.toString()).append('\n');
        return s.toString();
    }


}
