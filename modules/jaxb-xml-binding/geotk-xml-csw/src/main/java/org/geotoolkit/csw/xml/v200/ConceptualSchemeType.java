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
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConceptualSchemeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConceptualSchemeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Document" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="Authority" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
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
@XmlType(name = "ConceptualSchemeType", propOrder = {
    "name",
    "document",
    "authority"
})
public class ConceptualSchemeType {

    @XmlElement(name = "Name", required = true)
    private String name;
    @XmlElement(name = "Document", required = true)
    @XmlSchemaType(name = "anyURI")
    private String document;
    @XmlElement(name = "Authority", required = true)
    @XmlSchemaType(name = "anyURI")
    private String authority;

    /**
     * Gets the value of the name property.
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the document property.
     * 
     */
    public String getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     */
    public void setDocument(final String value) {
        this.document = value;
    }

    /**
     * Gets the value of the authority property.
     * 
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Sets the value of the authority property.
     * 
     */
    public void setAuthority(final String value) {
        this.authority = value;
    }

}
