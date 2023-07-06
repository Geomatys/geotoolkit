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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java pour fileType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="fileType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="changelog" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="copyright" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}language" minOccurs="0"/>
 *         &lt;element name="license" type="{http://www.metalinker.org/}licenseType" minOccurs="0"/>
 *         &lt;element name="logo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mimetype" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="multimedia" type="{http://www.metalinker.org/}multimediaType" minOccurs="0"/>
 *         &lt;element name="os" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="publisher" type="{http://www.metalinker.org/}publisherType" minOccurs="0"/>
 *         &lt;element name="relations" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="releasedate" type="{http://www.metalinker.org/}RFC822DateTime" minOccurs="0"/>
 *         &lt;element name="resources" type="{http://www.metalinker.org/}resourcesType"/>
 *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="screenshot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tags" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="upgrade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verification" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="hash" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" default="sha1" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="signature" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="PGP" />
 *                           &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="pieces" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="hash" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="piece">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                                           &lt;minInclusive value="0"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                           &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileType", propOrder = {

})
public class FileType {

    protected String changelog;
    protected String copyright;
    protected String description;
    protected String identity;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String language;
    protected LicenseType license;
    protected String logo;
    protected String mimetype;
    protected MultimediaType multimedia;
    protected String os;
    protected PublisherType publisher;
    protected String relations;
    protected String releasedate;
    @XmlElement(required = true)
    protected ResourcesType resources;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger size;
    protected String screenshot;
    protected String tags;
    protected String upgrade;
    protected FileType.Verification verification;
    protected String version;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Obtient la valeur de la propriété changelog.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getChangelog() {
        return changelog;
    }

    /**
     * Définit la valeur de la propriété changelog.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setChangelog(String value) {
        this.changelog = value;
    }

    /**
     * Obtient la valeur de la propriété copyright.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Définit la valeur de la propriété copyright.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCopyright(String value) {
        this.copyright = value;
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
     * Obtient la valeur de la propriété language.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la valeur de la propriété language.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguage(String value) {
        this.language = value;
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
     * Obtient la valeur de la propriété logo.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Définit la valeur de la propriété logo.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLogo(String value) {
        this.logo = value;
    }

    /**
     * Obtient la valeur de la propriété mimetype.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Définit la valeur de la propriété mimetype.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMimetype(String value) {
        this.mimetype = value;
    }

    /**
     * Obtient la valeur de la propriété multimedia.
     *
     * @return
     *     possible object is
     *     {@link MultimediaType }
     *
     */
    public MultimediaType getMultimedia() {
        return multimedia;
    }

    /**
     * Définit la valeur de la propriété multimedia.
     *
     * @param value
     *     allowed object is
     *     {@link MultimediaType }
     *
     */
    public void setMultimedia(MultimediaType value) {
        this.multimedia = value;
    }

    /**
     * Obtient la valeur de la propriété os.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOs() {
        return os;
    }

    /**
     * Définit la valeur de la propriété os.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOs(String value) {
        this.os = value;
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
     * Obtient la valeur de la propriété relations.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRelations() {
        return relations;
    }

    /**
     * Définit la valeur de la propriété relations.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRelations(String value) {
        this.relations = value;
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
     * Obtient la valeur de la propriété resources.
     *
     * @return
     *     possible object is
     *     {@link ResourcesType }
     *
     */
    public ResourcesType getResources() {
        return resources;
    }

    /**
     * Définit la valeur de la propriété resources.
     *
     * @param value
     *     allowed object is
     *     {@link ResourcesType }
     *
     */
    public void setResources(ResourcesType value) {
        this.resources = value;
    }

    /**
     * Obtient la valeur de la propriété size.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getSize() {
        return size;
    }

    /**
     * Définit la valeur de la propriété size.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setSize(BigInteger value) {
        this.size = value;
    }

    /**
     * Obtient la valeur de la propriété screenshot.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getScreenshot() {
        return screenshot;
    }

    /**
     * Définit la valeur de la propriété screenshot.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setScreenshot(String value) {
        this.screenshot = value;
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
     * Obtient la valeur de la propriété upgrade.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUpgrade() {
        return upgrade;
    }

    /**
     * Définit la valeur de la propriété upgrade.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUpgrade(String value) {
        this.upgrade = value;
    }

    /**
     * Obtient la valeur de la propriété verification.
     *
     * @return
     *     possible object is
     *     {@link FileType.Verification }
     *
     */
    public FileType.Verification getVerification() {
        return verification;
    }

    /**
     * Définit la valeur de la propriété verification.
     *
     * @param value
     *     allowed object is
     *     {@link FileType.Verification }
     *
     */
    public void setVerification(FileType.Verification value) {
        this.verification = value;
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
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }


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
     *         &lt;element name="hash" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" default="sha1" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="signature" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="PGP" />
     *                 &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="pieces" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="hash" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *                           &lt;attribute name="piece">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *                                 &lt;minInclusive value="0"/>
     *                               &lt;/restriction>
     *                             &lt;/simpleType>
     *                           &lt;/attribute>
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *                 &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "hash",
        "signature",
        "pieces"
    })
    public static class Verification {

        protected List<FileType.Verification.Hash> hash;
        protected List<FileType.Verification.Signature> signature;
        protected FileType.Verification.Pieces pieces;

        /**
         * Gets the value of the hash property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the hash property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getHash().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FileType.Verification.Hash }
         *
         *
         */
        public List<FileType.Verification.Hash> getHash() {
            if (hash == null) {
                hash = new ArrayList<FileType.Verification.Hash>();
            }
            return this.hash;
        }

        /**
         * Gets the value of the signature property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the signature property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSignature().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link FileType.Verification.Signature }
         *
         *
         */
        public List<FileType.Verification.Signature> getSignature() {
            if (signature == null) {
                signature = new ArrayList<FileType.Verification.Signature>();
            }
            return this.signature;
        }

        /**
         * Obtient la valeur de la propriété pieces.
         *
         * @return
         *     possible object is
         *     {@link FileType.Verification.Pieces }
         *
         */
        public FileType.Verification.Pieces getPieces() {
            return pieces;
        }

        /**
         * Définit la valeur de la propriété pieces.
         *
         * @param value
         *     allowed object is
         *     {@link FileType.Verification.Pieces }
         *
         */
        public void setPieces(FileType.Verification.Pieces value) {
            this.pieces = value;
        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" default="sha1" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Hash {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "type")
            protected HashType type;

            /**
             * Obtient la valeur de la propriété value.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Obtient la valeur de la propriété type.
             *
             * @return
             *     possible object is
             *     {@link HashType }
             *
             */
            public HashType getType() {
                if (type == null) {
                    return HashType.SHA_1;
                } else {
                    return type;
                }
            }

            /**
             * Définit la valeur de la propriété type.
             *
             * @param value
             *     allowed object is
             *     {@link HashType }
             *
             */
            public void setType(HashType value) {
                this.type = value;
            }

        }


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
         *         &lt;element name="hash" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *                 &lt;attribute name="piece">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
         *                       &lt;minInclusive value="0"/>
         *                     &lt;/restriction>
         *                   &lt;/simpleType>
         *                 &lt;/attribute>
         *               &lt;/extension>
         *             &lt;/simpleContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
         *       &lt;attribute name="type" type="{http://www.metalinker.org/}hashType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "hash"
        })
        public static class Pieces {

            @XmlElement(required = true)
            protected List<FileType.Verification.Pieces.Hash> hash;
            @XmlAttribute(name = "length")
            @XmlSchemaType(name = "nonNegativeInteger")
            protected BigInteger length;
            @XmlAttribute(name = "type")
            protected HashType type;

            /**
             * Gets the value of the hash property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the hash property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getHash().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link FileType.Verification.Pieces.Hash }
             *
             *
             */
            public List<FileType.Verification.Pieces.Hash> getHash() {
                if (hash == null) {
                    hash = new ArrayList<FileType.Verification.Pieces.Hash>();
                }
                return this.hash;
            }

            /**
             * Obtient la valeur de la propriété length.
             *
             * @return
             *     possible object is
             *     {@link BigInteger }
             *
             */
            public BigInteger getLength() {
                return length;
            }

            /**
             * Définit la valeur de la propriété length.
             *
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *
             */
            public void setLength(BigInteger value) {
                this.length = value;
            }

            /**
             * Obtient la valeur de la propriété type.
             *
             * @return
             *     possible object is
             *     {@link HashType }
             *
             */
            public HashType getType() {
                return type;
            }

            /**
             * Définit la valeur de la propriété type.
             *
             * @param value
             *     allowed object is
             *     {@link HashType }
             *
             */
            public void setType(HashType value) {
                this.type = value;
            }


            /**
             * <p>Classe Java pour anonymous complex type.
             *
             * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
             *
             * <pre>
             * &lt;complexType>
             *   &lt;simpleContent>
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
             *       &lt;attribute name="piece">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
             *             &lt;minInclusive value="0"/>
             *           &lt;/restriction>
             *         &lt;/simpleType>
             *       &lt;/attribute>
             *     &lt;/extension>
             *   &lt;/simpleContent>
             * &lt;/complexType>
             * </pre>
             *
             *
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class Hash {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "piece")
                protected BigInteger piece;

                /**
                 * Obtient la valeur de la propriété value.
                 *
                 * @return
                 *     possible object is
                 *     {@link String }
                 *
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * Définit la valeur de la propriété value.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *
                 */
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Obtient la valeur de la propriété piece.
                 *
                 * @return
                 *     possible object is
                 *     {@link BigInteger }
                 *
                 */
                public BigInteger getPiece() {
                    return piece;
                }

                /**
                 * Définit la valeur de la propriété piece.
                 *
                 * @param value
                 *     allowed object is
                 *     {@link BigInteger }
                 *
                 */
                public void setPiece(BigInteger value) {
                    this.piece = value;
                }

            }

        }


        /**
         * <p>Classe Java pour anonymous complex type.
         *
         * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
         *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="PGP" />
         *       &lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Signature {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "type")
            protected String type;
            @XmlAttribute(name = "file")
            protected String file;

            /**
             * Obtient la valeur de la propriété value.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getValue() {
                return value;
            }

            /**
             * Définit la valeur de la propriété value.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setValue(String value) {
                this.value = value;
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
                    return "PGP";
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

        }

    }

}
