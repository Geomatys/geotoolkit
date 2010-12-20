/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

package org.geotoolkit.geotnetcab;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.metadata.citation.ResponsibleParty;


/**
 * <p>Java class for GNC_UserDefinedMetadata_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_UserDefinedMetadata_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="dateStamp" type="{http://www.isotc211.org/2005/gco}Date_PropertyType"/>
 *         &lt;element name="feedbackStatement" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="userContact" type="{http://www.isotc211.org/2005/gmd}CI_ResponsibleParty_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_UserDefinedMetadata_Type", propOrder = {
    "dateStamp",
    "feedbackStatement",
    "userContact"
})
public class GNC_UserDefinedMetadata {

    @XmlElement(required = true)
    private Date dateStamp;
    @XmlElement(required = true)
    private String feedbackStatement;
    private List<ResponsibleParty> userContact;

    /**
     * Gets the value of the dateStamp property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getDateStamp() {
        return dateStamp;
    }

    /**
     * Sets the value of the dateStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setDateStamp(Date value) {
        this.dateStamp = value;
    }

    /**
     * Gets the value of the feedbackStatement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeedbackStatement() {
        return feedbackStatement;
    }

    /**
     * Sets the value of the feedbackStatement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeedbackStatement(String value) {
        this.feedbackStatement = value;
    }

    /**
     * Gets the value of the userContact property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link ResponsibleParty }
     * 
     * 
     */
    public List<ResponsibleParty> getUserContact() {
        if (userContact == null) {
            userContact = new ArrayList<ResponsibleParty>();
        }
        return this.userContact;
    }

    public void setUserContact(List<ResponsibleParty> userContact) {
        this.userContact = userContact;
    }

    public void setUserContact(ResponsibleParty userContact) {
        if (this.userContact == null) {
            this.userContact = new ArrayList<ResponsibleParty>();
        }
        this.userContact.add(userContact);
    }
}
