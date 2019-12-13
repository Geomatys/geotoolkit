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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 * use of File is discouraged
 *                 * !!should format be an enum? how hard is this on CMR if we make a change here later?
 *
 *               | DIF 9              | ECHO 10           | UMM            | DIF 10            | Notes     |
 *               | ------------------ | ----------------- | -------------- | ----------------- | --------- |
 *               | Multimedia_Sample  |         -         |       -        | Multimedia_Sample | No change |
 *
 *
 *
 * <p>Classe Java pour MultimediaSampleType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="MultimediaSampleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="File" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Format" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Caption" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultimediaSampleType", propOrder = {
    "file",
    "url",
    "format",
    "caption",
    "description"
})
public class MultimediaSampleType {

    @XmlElement(name = "File")
    protected String file;
    @XmlElement(name = "URL")
    protected String url;
    @XmlElement(name = "Format")
    protected String format;
    @XmlElement(name = "Caption")
    protected String caption;
    @XmlElement(name = "Description")
    protected String description;

    public MultimediaSampleType() {

    }

    public MultimediaSampleType(String file, String description, String format) {
        this.description = description;
        this.file = file;
        this.format = format;
    }

    /**
     * Obtient la valeur de la propriété file.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFile() {
        return file;
    }

    /**
     * Définit la valeur de la propriété file.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFile(String value) {
        this.file = value;
    }

    /**
     * Obtient la valeur de la propriété url.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getURL() {
        return url;
    }

    /**
     * Définit la valeur de la propriété url.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setURL(String value) {
        this.url = value;
    }

    /**
     * Obtient la valeur de la propriété format.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFormat() {
        return format;
    }

    /**
     * Définit la valeur de la propriété format.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Obtient la valeur de la propriété caption.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Définit la valeur de la propriété caption.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCaption(String value) {
        this.caption = value;
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

}
