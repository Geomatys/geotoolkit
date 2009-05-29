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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Identification of, and means of communication with,
 * person(s) and organizations associated with the server. 
 *       
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
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="individualName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *             &lt;element name="organisationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="organisationName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="positionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contactInfo" type="{http://www.opengis.net/wcs}ContactType" minOccurs="0"/>
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
    "content"
})
public class ResponsiblePartyType {

    @XmlElementRefs({
        @XmlElementRef(name = "positionName",     namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "individualName",   namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "organisationName", namespace = "http://www.opengis.net/wcs", type = JAXBElement.class),
        @XmlElementRef(name = "contactInfo",      namespace = "http://www.opengis.net/wcs", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> content;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();
    
    /**
     * empty construtor used by JAXB
     */
    ResponsiblePartyType() {
        
    }
    
    /**
     * Build a new Responsible Party
     */
    public ResponsiblePartyType(String individualName, String positionName, String organisationName, ContactType contactInfo) {
         content = new ArrayList<JAXBElement<?>>();
         content.add(factory.createResponsiblePartyTypeIndividualName(individualName));
         content.add(factory.createResponsiblePartyTypePositionName(positionName));
         content.add(factory.createResponsiblePartyTypeOrganisationName(organisationName));
         content.add(factory.createResponsiblePartyTypeContactInfo(contactInfo));
    }
    /**
     * Gets the rest of the content model.
     * (unModifiable) 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return Collections.unmodifiableList(content);
    }

}
