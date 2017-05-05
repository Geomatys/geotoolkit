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
import org.geotoolkit.ows.xml.AbstractResponsiblePartySubset;


/**
 * For OWS use in the ServiceProvider section of a service
 *       metadata document, the optional organizationName element was removed,
 *       since this type is always used with the ProviderName element which
 *       provides that information. The mandatory "role" element was changed to
 *       optional, since no clear use of this information is known in the
 *       ServiceProvider section.
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
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}IndividualName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}PositionName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ContactInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Role" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsiblePartySubsetType", propOrder = {
    "individualName",
    "positionName",
    "contactInfo",
    "role"
})
public class ResponsiblePartySubsetType implements AbstractResponsiblePartySubset {

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
    public ResponsiblePartySubsetType(final String individualName, final String positionName, final ContactType contactInfo,
            final CodeType role){
        this.contactInfo    = contactInfo;
        this.individualName = individualName;
        this.positionName   = positionName;
        this.role           = role;
    }

    /**
     * Gets the value of the individualName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
     * Gets the value of the positionName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
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
    @Override
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
    @Override
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
