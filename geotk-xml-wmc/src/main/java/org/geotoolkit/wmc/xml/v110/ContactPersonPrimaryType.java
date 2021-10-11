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
package org.geotoolkit.wmc.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactPersonPrimaryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ContactPersonPrimaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ContactPerson" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactOrganization" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactPersonPrimaryType", propOrder = {
    "contactPerson",
    "contactOrganization"
})
public class ContactPersonPrimaryType {

    @XmlElement(name = "ContactPerson")
    protected String contactPerson;
    @XmlElement(name = "ContactOrganization")
    protected String contactOrganization;

    /**
     * Gets the value of the contactPerson property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Sets the value of the contactPerson property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContactPerson(final String value) {
        this.contactPerson = value;
    }

    /**
     * Gets the value of the contactOrganization property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContactOrganization() {
        return contactOrganization;
    }

    /**
     * Sets the value of the contactOrganization property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContactOrganization(final String value) {
        this.contactOrganization = value;
    }

}
