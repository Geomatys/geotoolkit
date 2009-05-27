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
package org.geotoolkit.ebrim.xml.v250;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * TelephoneNumber is the mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for TelephoneNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TelephoneNumberType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="areaCode" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String8" />
 *       &lt;attribute name="countryCode" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String8" />
 *       &lt;attribute name="extension" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String8" />
 *       &lt;attribute name="number" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String16" />
 *       &lt;attribute name="phoneType" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String32" />
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelephoneNumberType")
public class TelephoneNumberType {

    @XmlAttribute
    private String areaCode;
    @XmlAttribute
    private String countryCode;
    @XmlAttribute
    private String extension;
    @XmlAttribute
    private String number;
    @XmlAttribute
    private String phoneType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String url;

    /**
     * Gets the value of the areaCode property.
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the value of the areaCode property.
     */
    public void setAreaCode(String value) {
        this.areaCode = value;
    }

    /**
     * Gets the value of the countryCode property.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
    */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the extension property.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the number property.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the phoneType property.
     */
    public String getPhoneType() {
        return phoneType;
    }

    /**
     * Sets the value of the phoneType property.
     */
    public void setPhoneType(String value) {
        this.phoneType = value;
    }

    /**
     * Gets the value of the url property.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     */
    public void setUrl(String value) {
        this.url = value;
    }

}
