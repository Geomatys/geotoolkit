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
package org.geotoolkit.xal.xml.v20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AdministrativeAreaName" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="SubAdministrativeArea" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="SubAdministrativeAreaName" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;choice minOccurs="0">
 *                     &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
 *                     &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostOffice"/>
 *                     &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalCode"/>
 *                   &lt;/choice>
 *                   &lt;any/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="UsageType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostOffice"/>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalCode"/>
 *         &lt;/choice>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="UsageType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "administrativeAreaName",
    "subAdministrativeArea",
    "locality",
    "postOffice",
    "postalCode",
    "any"
})
@XmlRootElement(name = "AdministrativeArea")
public class AdministrativeArea {

    @XmlElement(name = "AddressLine")
    private List<AddressLine> addressLine;
    @XmlElement(name = "AdministrativeAreaName")
    private List<AdministrativeArea.AdministrativeAreaName> administrativeAreaName;
    @XmlElement(name = "SubAdministrativeArea")
    private AdministrativeArea.SubAdministrativeArea subAdministrativeArea;
    @XmlElement(name = "Locality")
    private Locality locality;
    @XmlElement(name = "PostOffice")
    private PostOffice postOffice;
    @XmlElement(name = "PostalCode")
    private PostalCode postalCode;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    private String type;
    @XmlAttribute(name = "UsageType")
    @XmlSchemaType(name = "anySimpleType")
    private String usageType;
    @XmlAttribute(name = "Indicator")
    @XmlSchemaType(name = "anySimpleType")
    private String indicator;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the addressLine property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressLine property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressLine().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressLine }
     *
     *
     */
    public List<AddressLine> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<AddressLine>();
        }
        return this.addressLine;
    }

    /**
     * Gets the value of the administrativeAreaName property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the administrativeAreaName property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdministrativeAreaName().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdministrativeArea.AdministrativeAreaName }
     *
     *
     */
    public List<AdministrativeArea.AdministrativeAreaName> getAdministrativeAreaName() {
        if (administrativeAreaName == null) {
            administrativeAreaName = new ArrayList<AdministrativeArea.AdministrativeAreaName>();
        }
        return this.administrativeAreaName;
    }

    /**
     * Gets the value of the subAdministrativeArea property.
     *
     * @return
     *     possible object is
     *     {@link AdministrativeArea.SubAdministrativeArea }
     *
     */
    public AdministrativeArea.SubAdministrativeArea getSubAdministrativeArea() {
        return subAdministrativeArea;
    }

    /**
     * Sets the value of the subAdministrativeArea property.
     *
     * @param value
     *     allowed object is
     *     {@link AdministrativeArea.SubAdministrativeArea }
     *
     */
    public void setSubAdministrativeArea(final AdministrativeArea.SubAdministrativeArea value) {
        this.subAdministrativeArea = value;
    }

    /**
     * Gets the value of the locality property.
     *
     * @return
     *     possible object is
     *     {@link Locality }
     *
     */
    public Locality getLocality() {
        return locality;
    }

    /**
     * Sets the value of the locality property.
     *
     * @param value
     *     allowed object is
     *     {@link Locality }
     *
     */
    public void setLocality(final Locality value) {
        this.locality = value;
    }

    /**
     * Gets the value of the postOffice property.
     *
     * @return
     *     possible object is
     *     {@link PostOffice }
     *
     */
    public PostOffice getPostOffice() {
        return postOffice;
    }

    /**
     * Sets the value of the postOffice property.
     *
     * @param value
     *     allowed object is
     *     {@link PostOffice }
     *
     */
    public void setPostOffice(final PostOffice value) {
        this.postOffice = value;
    }

    /**
     * Gets the value of the postalCode property.
     *
     * @return
     *     possible object is
     *     {@link PostalCode }
     *
     */
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     *
     * @param value
     *     allowed object is
     *     {@link PostalCode }
     *
     */
    public void setPostalCode(final PostalCode value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the usageType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUsageType() {
        return usageType;
    }

    /**
     * Sets the value of the usageType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUsageType(final String value) {
        this.usageType = value;
    }

    /**
     * Gets the value of the indicator property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * Sets the value of the indicator property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndicator(final String value) {
        this.indicator = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    public static class AdministrativeAreaName {

        @XmlValue
        private String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        private String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Gets the value of the content property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the value of the content property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setContent(final String value) {
            this.content = value;
        }

        /**
         * Gets the value of the type property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the code property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the value of the code property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCode(final String value) {
            this.code = value;
        }

        /**
         * Gets a map that contains attributes that aren't bound to any typed property on this class.
         *
         * <p>
         * the map is keyed by the name of the attribute and
         * the value is the string value of the attribute.
         *
         * the map returned by this method is live, and you can add new attribute
         * by updating the map directly. Because of this design, there's no setter.
         *
         *
         * @return
         *     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="SubAdministrativeAreaName" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;choice minOccurs="0">
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostOffice"/>
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalCode"/>
     *         &lt;/choice>
     *         &lt;any/>
     *       &lt;/sequence>
     *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="UsageType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "addressLine",
        "subAdministrativeAreaName",
        "locality",
        "postOffice",
        "postalCode",
        "any"
    })
    public static class SubAdministrativeArea {

        @XmlElement(name = "AddressLine")
        private List<AddressLine> addressLine;
        @XmlElement(name = "SubAdministrativeAreaName")
        private List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> subAdministrativeAreaName;
        @XmlElement(name = "Locality")
        private Locality locality;
        @XmlElement(name = "PostOffice")
        private PostOffice postOffice;
        @XmlElement(name = "PostalCode")
        private PostalCode postalCode;
        @XmlAnyElement(lax = true)
        private List<Object> any;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAttribute(name = "UsageType")
        @XmlSchemaType(name = "anySimpleType")
        private String usageType;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        private String indicator;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Gets the value of the addressLine property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the addressLine property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddressLine().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AddressLine }
         *
         *
         */
        public List<AddressLine> getAddressLine() {
            if (addressLine == null) {
                addressLine = new ArrayList<AddressLine>();
            }
            return this.addressLine;
        }

        /**
         * Gets the value of the subAdministrativeAreaName property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the subAdministrativeAreaName property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSubAdministrativeAreaName().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName }
         *
         *
         */
        public List<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName> getSubAdministrativeAreaName() {
            if (subAdministrativeAreaName == null) {
                subAdministrativeAreaName = new ArrayList<AdministrativeArea.SubAdministrativeArea.SubAdministrativeAreaName>();
            }
            return this.subAdministrativeAreaName;
        }

        /**
         * Gets the value of the locality property.
         *
         * @return
         *     possible object is
         *     {@link Locality }
         *
         */
        public Locality getLocality() {
            return locality;
        }

        /**
         * Sets the value of the locality property.
         *
         * @param value
         *     allowed object is
         *     {@link Locality }
         *
         */
        public void setLocality(final Locality value) {
            this.locality = value;
        }

        /**
         * Gets the value of the postOffice property.
         *
         * @return
         *     possible object is
         *     {@link PostOffice }
         *
         */
        public PostOffice getPostOffice() {
            return postOffice;
        }

        /**
         * Sets the value of the postOffice property.
         *
         * @param value
         *     allowed object is
         *     {@link PostOffice }
         *
         */
        public void setPostOffice(final PostOffice value) {
            this.postOffice = value;
        }

        /**
         * Gets the value of the postalCode property.
         *
         * @return
         *     possible object is
         *     {@link PostalCode }
         *
         */
        public PostalCode getPostalCode() {
            return postalCode;
        }

        /**
         * Sets the value of the postalCode property.
         *
         * @param value
         *     allowed object is
         *     {@link PostalCode }
         *
         */
        public void setPostalCode(final PostalCode value) {
            this.postalCode = value;
        }

        /**
         * Gets the value of the any property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         *
         *
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

        /**
         * Gets the value of the type property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the usageType property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUsageType() {
            return usageType;
        }

        /**
         * Sets the value of the usageType property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUsageType(final String value) {
            this.usageType = value;
        }

        /**
         * Gets the value of the indicator property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getIndicator() {
            return indicator;
        }

        /**
         * Sets the value of the indicator property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setIndicator(final String value) {
            this.indicator = value;
        }

        /**
         * Gets a map that contains attributes that aren't bound to any typed property on this class.
         *
         * <p>
         * the map is keyed by the name of the attribute and
         * the value is the string value of the attribute.
         *
         * the map returned by this method is live, and you can add new attribute
         * by updating the map directly. Because of this design, there's no setter.
         *
         *
         * @return
         *     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }


        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
         *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        public static class SubAdministrativeAreaName {

            @XmlValue
            private String content;
            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            private String type;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            private String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            /**
             * Gets the value of the content property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getContent() {
                return content;
            }

            /**
             * Sets the value of the content property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setContent(final String value) {
                this.content = value;
            }

            /**
             * Gets the value of the type property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getType() {
                return type;
            }

            /**
             * Sets the value of the type property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setType(final String value) {
                this.type = value;
            }

            /**
             * Gets the value of the code property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getCode() {
                return code;
            }

            /**
             * Sets the value of the code property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setCode(final String value) {
                this.code = value;
            }

            /**
             * Gets a map that contains attributes that aren't bound to any typed property on this class.
             *
             * <p>
             * the map is keyed by the name of the attribute and
             * the value is the string value of the attribute.
             *
             * the map returned by this method is live, and you can add new attribute
             * by updating the map directly. Because of this design, there's no setter.
             *
             *
             * @return
             *     always non-null
             */
            public Map<QName, String> getOtherAttributes() {
                return otherAttributes;
            }

        }

    }

}
