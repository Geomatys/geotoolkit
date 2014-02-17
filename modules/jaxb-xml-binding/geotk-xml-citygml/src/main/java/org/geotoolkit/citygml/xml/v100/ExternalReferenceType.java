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
package org.geotoolkit.citygml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Type describing the reference to an corresponding object in an other information system, 
 * for example in the german cadastre ALKIS, the german topographic information system or ATKIS, or the OS MasterMap.
 * The reference consists of the name of the external information system, represented by an URI,
 * and the reference of the external object, given either by a string or by an URI.
 * If the informationSystem element is missing in the ExternalReference, the ExternalObjectReference must be an URI,
 * which contains an indication of the informationSystem.
 * 
 * <p>Java class for ExternalReferenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExternalReferenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="informationSystem" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="externalObject" type="{http://www.opengis.net/citygml/1.0}ExternalObjectReferenceType"/>
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
@XmlType(name = "ExternalReferenceType", propOrder = {
    "informationSystem",
    "externalObject"
})
public class ExternalReferenceType {

    @XmlSchemaType(name = "anyURI")
    private String informationSystem;
    @XmlElement(required = true)
    private ExternalObjectReferenceType externalObject;

    /**
     * Gets the value of the informationSystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformationSystem() {
        return informationSystem;
    }

    /**
     * Sets the value of the informationSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformationSystem(final String value) {
        this.informationSystem = value;
    }

    /**
     * Gets the value of the externalObject property.
     * 
     * @return
     *     possible object is
     *     {@link ExternalObjectReferenceType }
     *     
     */
    public ExternalObjectReferenceType getExternalObject() {
        return externalObject;
    }

    /**
     * Sets the value of the externalObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalObjectReferenceType }
     *     
     */
    public void setExternalObject(final ExternalObjectReferenceType value) {
        this.externalObject = value;
    }

}
