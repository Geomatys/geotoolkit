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

package org.geotoolkit.metalinker;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour metalinkType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="metalinkType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="license" type="{http://www.metalinker.org/}licenseType" minOccurs="0"/>
 *         &lt;element name="publisher" type="{http://www.metalinker.org/}publisherType" minOccurs="0"/>
 *         &lt;element name="releasedate" type="{http://www.metalinker.org/}RFC822DateTime" minOccurs="0"/>
 *         &lt;element name="tags" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="files" type="{http://www.metalinker.org/}filesType"/>
 *       &lt;/all>
 *       &lt;attribute name="origin" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="type" default="static">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="dynamic"/>
 *             &lt;enumeration value="static"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="pubdate" type="{http://www.metalinker.org/}RFC822DateTime" />
 *       &lt;attribute name="refreshdate" type="{http://www.metalinker.org/}RFC822DateTime" />
 *       &lt;attribute name="generator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metalinkType", propOrder = {

})
public class MetalinkType {

    protected String description;
    protected String identity;
    protected LicenseType license;
    protected PublisherType publisher;
    protected String releasedate;
    protected String tags;
    protected String version;
    @XmlElement(required = true)
    protected FilesType files;
    @XmlAttribute(name = "origin")
    @XmlSchemaType(name = "anyURI")
    protected String origin;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "pubdate")
    protected String pubdate;
    @XmlAttribute(name = "refreshdate")
    protected String refreshdate;
    @XmlAttribute(name = "generator")
    protected String generator;

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
     * Obtient la valeur de la propriété identity.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Définit la valeur de la propriété identity.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Obtient la valeur de la propriété license.
     *
     * @return
     *     possible object is
     *     {@link LicenseType }
     *
     */
    public LicenseType getLicense() {
        return license;
    }

    /**
     * Définit la valeur de la propriété license.
     *
     * @param value
     *     allowed object is
     *     {@link LicenseType }
     *
     */
    public void setLicense(LicenseType value) {
        this.license = value;
    }

    /**
     * Obtient la valeur de la propriété publisher.
     *
     * @return
     *     possible object is
     *     {@link PublisherType }
     *
     */
    public PublisherType getPublisher() {
        return publisher;
    }

    /**
     * Définit la valeur de la propriété publisher.
     *
     * @param value
     *     allowed object is
     *     {@link PublisherType }
     *
     */
    public void setPublisher(PublisherType value) {
        this.publisher = value;
    }

    /**
     * Obtient la valeur de la propriété releasedate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReleasedate() {
        return releasedate;
    }

    /**
     * Définit la valeur de la propriété releasedate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReleasedate(String value) {
        this.releasedate = value;
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
     * Obtient la valeur de la propriété version.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété files.
     *
     * @return
     *     possible object is
     *     {@link FilesType }
     *
     */
    public FilesType getFiles() {
        return files;
    }

    /**
     * Définit la valeur de la propriété files.
     *
     * @param value
     *     allowed object is
     *     {@link FilesType }
     *
     */
    public void setFiles(FilesType value) {
        this.files = value;
    }

    /**
     * Obtient la valeur de la propriété origin.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Définit la valeur de la propriété origin.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrigin(String value) {
        this.origin = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "static";
        } else {
            return type;
        }
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété pubdate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPubdate() {
        return pubdate;
    }

    /**
     * Définit la valeur de la propriété pubdate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPubdate(String value) {
        this.pubdate = value;
    }

    /**
     * Obtient la valeur de la propriété refreshdate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRefreshdate() {
        return refreshdate;
    }

    /**
     * Définit la valeur de la propriété refreshdate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRefreshdate(String value) {
        this.refreshdate = value;
    }

    /**
     * Obtient la valeur de la propriété generator.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGenerator() {
        return generator;
    }

    /**
     * Définit la valeur de la propriété generator.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGenerator(String value) {
        this.generator = value;
    }

}
