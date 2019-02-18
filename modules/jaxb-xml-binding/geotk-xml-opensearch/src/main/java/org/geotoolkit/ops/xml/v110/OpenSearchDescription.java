/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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


package org.geotoolkit.ops.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ops.xml.OpenSearchResponse;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShortName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Description">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1024"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Url" type="{http://a9.com/-/spec/opensearch/1.1/}url" maxOccurs="unbounded"/>
 *         &lt;element name="Contact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Tags" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="256"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="LongName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="48"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Image" type="{http://a9.com/-/spec/opensearch/1.1/}imageType" minOccurs="0"/>
 *         &lt;element name="Query" type="{http://a9.com/-/spec/opensearch/1.1/}InspireQueryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Developer" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="64"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Attribution" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="256"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SyndicationRight" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="open"/>
 *               &lt;enumeration value="limited"/>
 *               &lt;enumeration value="private"/>
 *               &lt;enumeration value="closed"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AdultContent" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="InputEncoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OutputEncoding" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "shortName",
    "description",
    "url",
    "contact",
    "tags",
    "longName",
    "image",
    "query",
    "developer",
    "attribution",
    "syndicationRight",
    "adultContent",
    "language",
    "inputEncoding",
    "outputEncoding"
})
@XmlRootElement(name = "OpenSearchDescription")
public class OpenSearchDescription implements OpenSearchResponse{

    @XmlElement(name = "ShortName", required = true)
    protected String shortName;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "Url", required = true)
    protected List<Url> url;
    @XmlElement(name = "Contact")
    protected String contact;
    @XmlElement(name = "Tags")
    protected String tags;
    @XmlElement(name = "LongName")
    protected String longName;
    @XmlElement(name = "Image")
    protected ImageType image;
    @XmlElement(name = "Query")
    protected List<InspireQueryType> query;
    @XmlElement(name = "Developer")
    protected String developer;
    @XmlElement(name = "Attribution")
    protected String attribution;
    @XmlElement(name = "SyndicationRight", defaultValue = "open")
    protected String syndicationRight;
    @XmlElement(name = "AdultContent", defaultValue = "false")
    protected String adultContent;
    @XmlElement(name = "Language", defaultValue = "*")
    protected List<String> language;
    @XmlElement(name = "InputEncoding", defaultValue = "UTF-8")
    protected String inputEncoding;
    @XmlElement(name = "OutputEncoding", defaultValue = "UTF-8")
    protected String outputEncoding;

    public OpenSearchDescription() {

    }

    public OpenSearchDescription(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
    }

    /**
     * Obtient la valeur de la propriété shortName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Définit la valeur de la propriété shortName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the url property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the url property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUrl().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Url }
     *
     *
     */
    public List<Url> getUrl() {
        if (url == null) {
            url = new ArrayList<Url>();
        }
        return this.url;
    }

    /**
     * Obtient la valeur de la propriété contact.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContact() {
        return contact;
    }

    /**
     * Définit la valeur de la propriété contact.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContact(String value) {
        this.contact = value;
    }

    /**
     * Obtient la valeur de la propriété tags.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTags() {
        return tags;
    }

    /**
     * Définit la valeur de la propriété tags.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTags(String value) {
        this.tags = value;
    }

    /**
     * Obtient la valeur de la propriété longName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Définit la valeur de la propriété longName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLongName(String value) {
        this.longName = value;
    }

    /**
     * Obtient la valeur de la propriété image.
     *
     * @return
     *     possible object is
     *     {@link ImageType }
     *
     */
    public ImageType getImage() {
        return image;
    }

    /**
     * Définit la valeur de la propriété image.
     *
     * @param value
     *     allowed object is
     *     {@link ImageType }
     *
     */
    public void setImage(ImageType value) {
        this.image = value;
    }

    /**
     * Gets the value of the query property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the query property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuery().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InspireQueryType }
     *
     *
     */
    public List<InspireQueryType> getQuery() {
        if (query == null) {
            query = new ArrayList<InspireQueryType>();
        }
        return this.query;
    }

    /**
     * Obtient la valeur de la propriété developer.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * Définit la valeur de la propriété developer.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDeveloper(String value) {
        this.developer = value;
    }

    /**
     * Obtient la valeur de la propriété attribution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAttribution() {
        return attribution;
    }

    /**
     * Définit la valeur de la propriété attribution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAttribution(String value) {
        this.attribution = value;
    }

    /**
     * Obtient la valeur de la propriété syndicationRight.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSyndicationRight() {
        return syndicationRight;
    }

    /**
     * Définit la valeur de la propriété syndicationRight.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSyndicationRight(String value) {
        this.syndicationRight = value;
    }

    /**
     * Obtient la valeur de la propriété adultContent.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAdultContent() {
        return adultContent;
    }

    /**
     * Définit la valeur de la propriété adultContent.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAdultContent(String value) {
        this.adultContent = value;
    }

    /**
     * Gets the value of the language property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<String>();
        }
        return this.language;
    }

    /**
     * Obtient la valeur de la propriété inputEncoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInputEncoding() {
        return inputEncoding;
    }

    /**
     * Définit la valeur de la propriété inputEncoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInputEncoding(String value) {
        this.inputEncoding = value;
    }

    /**
     * Obtient la valeur de la propriété outputEncoding.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOutputEncoding() {
        return outputEncoding;
    }

    /**
     * Définit la valeur de la propriété outputEncoding.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOutputEncoding(String value) {
        this.outputEncoding = value;
    }

}
