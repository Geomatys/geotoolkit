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
 * <p>Java class for GeneralType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeneralType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Window" type="{http://www.opengis.net/context}WindowType" minOccurs="0"/>
 *         &lt;element name="BoundingBox" type="{http://www.opengis.net/context}BoundingBoxType"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="KeywordList" type="{http://www.opengis.net/context}KeywordListType" minOccurs="0"/>
 *         &lt;element name="Abstract" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LogoURL" type="{http://www.opengis.net/context}URLType" minOccurs="0"/>
 *         &lt;element name="DescriptionURL" type="{http://www.opengis.net/context}URLType" minOccurs="0"/>
 *         &lt;element name="ContactInformation" type="{http://www.opengis.net/context}ContactInformationType" minOccurs="0"/>
 *         &lt;element name="Extension" type="{http://www.opengis.net/context}ExtensionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeneralType", propOrder = {
    "window",
    "boundingBox",
    "title",
    "keywordList",
    "_abstract",
    "logoURL",
    "descriptionURL",
    "contactInformation",
    "extension"
})
public class GeneralType {

    @XmlElement(name = "Window")
    protected WindowType window;
    @XmlElement(name = "BoundingBox", required = true)
    protected BoundingBoxType boundingBox;
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "KeywordList")
    protected KeywordListType keywordList;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "LogoURL")
    protected URLType logoURL;
    @XmlElement(name = "DescriptionURL")
    protected URLType descriptionURL;
    @XmlElement(name = "ContactInformation")
    protected ContactInformationType contactInformation;
    @XmlElement(name = "Extension")
    protected ExtensionType extension;

    /**
     * Gets the value of the window property.
     * 
     * @return
     *     possible object is
     *     {@link WindowType }
     *     
     */
    public WindowType getWindow() {
        return window;
    }

    /**
     * Sets the value of the window property.
     * 
     * @param value
     *     allowed object is
     *     {@link WindowType }
     *     
     */
    public void setWindow(WindowType value) {
        this.window = value;
    }

    /**
     * Gets the value of the boundingBox property.
     * 
     * @return
     *     possible object is
     *     {@link BoundingBoxType }
     *     
     */
    public BoundingBoxType getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoundingBoxType }
     *     
     */
    public void setBoundingBox(BoundingBoxType value) {
        this.boundingBox = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the keywordList property.
     * 
     * @return
     *     possible object is
     *     {@link KeywordListType }
     *     
     */
    public KeywordListType getKeywordList() {
        return keywordList;
    }

    /**
     * Sets the value of the keywordList property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeywordListType }
     *     
     */
    public void setKeywordList(KeywordListType value) {
        this.keywordList = value;
    }

    /**
     * Gets the value of the abstract property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Sets the value of the abstract property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Gets the value of the logoURL property.
     * 
     * @return
     *     possible object is
     *     {@link URLType }
     *     
     */
    public URLType getLogoURL() {
        return logoURL;
    }

    /**
     * Sets the value of the logoURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link URLType }
     *     
     */
    public void setLogoURL(URLType value) {
        this.logoURL = value;
    }

    /**
     * Gets the value of the descriptionURL property.
     * 
     * @return
     *     possible object is
     *     {@link URLType }
     *     
     */
    public URLType getDescriptionURL() {
        return descriptionURL;
    }

    /**
     * Sets the value of the descriptionURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link URLType }
     *     
     */
    public void setDescriptionURL(URLType value) {
        this.descriptionURL = value;
    }

    /**
     * Gets the value of the contactInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ContactInformationType }
     *     
     */
    public ContactInformationType getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets the value of the contactInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactInformationType }
     *     
     */
    public void setContactInformation(ContactInformationType value) {
        this.contactInformation = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *     
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *     
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

}
