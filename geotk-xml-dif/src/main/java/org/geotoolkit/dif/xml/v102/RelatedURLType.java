/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 * Require at least one, the datacenter (organization) url should be used if no other url can be found
 *                 * Echo uses both OnlineAccessURLs and OnlineResources for this
 *
 *                 | DIF 9            | ECHO 10                                                    |UMM             | DIF 10           | ISO      | Notes
 *                 | ---------------- | ---------------------------------------------------------- |----------------| ---------------- | -------- | --------------------------------|
 *                 | URL_Content_Type | OnlineResource/Type                                        | URLContentType | URL_Content_Type |     ?    | No change                       |
 *                 |         -        |                              -                             | Protocol       |         -        | Protocol | Added for ISO support           |
 *                 | URL              | OnlineResource/URL, OnlineAccessURL/URL                    | URL            | URL              |     ?    |                                 |
 *                 |         -        |                              -                             | Title          | Title            |     ?    | Need to cite the change request |
 *                 | Description      | OnlineAccessURL/URLDescription, OnlineResource/Description | Description    | Description      |     ?    | Can now be formatted            |
 *                 |         -        | OnlineResource/MimeType, OnlineAccessURL/Mimetype          | MimeType       | Mime_Type        |     ?    | No change                       |
 *
 *
 * <p>Classe Java pour RelatedURLType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RelatedURLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="URL_Content_Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}URLContentType" minOccurs="0"/>
 *         &lt;element name="Protocol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="Title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DisplayableTextType" minOccurs="0"/>
 *         &lt;element name="Mime_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelatedURLType", propOrder = {
    "urlContentType",
    "protocol",
    "url",
    "title",
    "description",
    "mimeType"
})
public class RelatedURLType {

    @XmlElement(name = "URL_Content_Type")
    protected URLContentType urlContentType;
    @XmlElement(name = "Protocol")
    protected String protocol;
    @XmlElement(name = "URL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected List<String> url;
    @XmlElement(name = "Title")
    protected String title;
    @XmlElement(name = "Description")
    protected DisplayableTextType description;
    @XmlElement(name = "Mime_Type")
    protected String mimeType;

    public RelatedURLType() {

    }

    public RelatedURLType(URLContentType urlContentType, String protocol, List<String> url, String title, DisplayableTextType description, String mimeType) {
        this.description = description;
        this.mimeType = mimeType;
        this.protocol = protocol;
        this.title = title;
        this.url = url;
        this.urlContentType = urlContentType;
    }

    /**
     * Obtient la valeur de la propriété urlContentType.
     *
     * @return
     *     possible object is
     *     {@link URLContentType }
     *
     */
    public URLContentType getURLContentType() {
        return urlContentType;
    }

    /**
     * Définit la valeur de la propriété urlContentType.
     *
     * @param value
     *     allowed object is
     *     {@link URLContentType }
     *
     */
    public void setURLContentType(URLContentType value) {
        this.urlContentType = value;
    }

    /**
     * Obtient la valeur de la propriété protocol.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Définit la valeur de la propriété protocol.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProtocol(String value) {
        this.protocol = value;
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
     *    getURL().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getURL() {
        if (url == null) {
            url = new ArrayList<>();
        }
        return this.url;
    }

    /**
     * Obtient la valeur de la propriété title.
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
     * Définit la valeur de la propriété title.
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
     * Obtient la valeur de la propriété description.
     *
     * @return
     *     possible object is
     *     {@link DisplayableTextType }
     *
     */
    public DisplayableTextType getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     *
     * @param value
     *     allowed object is
     *     {@link DisplayableTextType }
     *
     */
    public void setDescription(DisplayableTextType value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété mimeType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Définit la valeur de la propriété mimeType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

}
