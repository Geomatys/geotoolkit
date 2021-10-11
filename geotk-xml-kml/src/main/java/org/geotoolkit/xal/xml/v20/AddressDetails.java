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
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * <p>Java class for AddressDetails complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AddressDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PostalServiceElements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AddressIdentifier" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="IdentifierType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="EndorsementLineCode" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="KeyLineCode" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="Barcode" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SortingCode" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AddressLatitude" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AddressLatitudeDirection" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AddressLongitude" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AddressLongitudeDirection" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SupplementaryPostalServiceData" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                           &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;any/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="Address">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                   &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="AddressLines" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLinesType"/>
 *           &lt;element name="Country">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *                     &lt;element name="CountryNameCode" maxOccurs="unbounded" minOccurs="0">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                             &lt;attribute name="Scheme" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                           &lt;/restriction>
 *                         &lt;/complexContent>
 *                       &lt;/complexType>
 *                     &lt;/element>
 *                     &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}CountryName" maxOccurs="unbounded" minOccurs="0"/>
 *                     &lt;choice minOccurs="0">
 *                       &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AdministrativeArea"/>
 *                       &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
 *                       &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Thoroughfare"/>
 *                     &lt;/choice>
 *                     &lt;any/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AdministrativeArea"/>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Thoroughfare"/>
 *         &lt;/choice>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *       &lt;attribute name="AddressType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="CurrentStatus" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="ValidFromDate" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="ValidToDate" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="Usage" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="AddressDetailsKey" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressDetails", propOrder = {
    "postalServiceElements",
    "address",
    "addressLines",
    "country",
    "administrativeArea",
    "locality",
    "thoroughfare",
    "any"
})
public class AddressDetails {

    @XmlElement(name = "PostalServiceElements")
    private AddressDetails.PostalServiceElements postalServiceElements;
    @XmlElement(name = "Address")
    private AddressDetails.Address address;
    @XmlElement(name = "AddressLines")
    private AddressLinesType addressLines;
    @XmlElement(name = "Country")
    private AddressDetails.Country country;
    @XmlElement(name = "AdministrativeArea")
    private AdministrativeArea administrativeArea;
    @XmlElement(name = "Locality")
    private Locality locality;
    @XmlElement(name = "Thoroughfare")
    private Thoroughfare thoroughfare;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute(name = "AddressType")
    @XmlSchemaType(name = "anySimpleType")
    private String addressType;
    @XmlAttribute(name = "CurrentStatus")
    @XmlSchemaType(name = "anySimpleType")
    private String currentStatus;
    @XmlAttribute(name = "ValidFromDate")
    @XmlSchemaType(name = "anySimpleType")
    private String validFromDate;
    @XmlAttribute(name = "ValidToDate")
    @XmlSchemaType(name = "anySimpleType")
    private String validToDate;
    @XmlAttribute(name = "Usage")
    @XmlSchemaType(name = "anySimpleType")
    private String usage;
    @XmlAttribute(name = "AddressDetailsKey")
    @XmlSchemaType(name = "anySimpleType")
    private String addressDetailsKey;
    @XmlAttribute(name = "Code")
    @XmlSchemaType(name = "anySimpleType")
    private String code;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the postalServiceElements property.
     *
     * @return
     *     possible object is
     *     {@link AddressDetails.PostalServiceElements }
     *
     */
    public AddressDetails.PostalServiceElements getPostalServiceElements() {
        return postalServiceElements;
    }

    /**
     * Sets the value of the postalServiceElements property.
     *
     * @param value
     *     allowed object is
     *     {@link AddressDetails.PostalServiceElements }
     *
     */
    public void setPostalServiceElements(final AddressDetails.PostalServiceElements value) {
        this.postalServiceElements = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return
     *     possible object is
     *     {@link AddressDetails.Address }
     *
     */
    public AddressDetails.Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value
     *     allowed object is
     *     {@link AddressDetails.Address }
     *
     */
    public void setAddress(final AddressDetails.Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the addressLines property.
     *
     * @return
     *     possible object is
     *     {@link AddressLinesType }
     *
     */
    public AddressLinesType getAddressLines() {
        return addressLines;
    }

    /**
     * Sets the value of the addressLines property.
     *
     * @param value
     *     allowed object is
     *     {@link AddressLinesType }
     *
     */
    public void setAddressLines(final AddressLinesType value) {
        this.addressLines = value;
    }

    /**
     * Gets the value of the country property.
     *
     * @return
     *     possible object is
     *     {@link AddressDetails.Country }
     *
     */
    public AddressDetails.Country getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     *
     * @param value
     *     allowed object is
     *     {@link AddressDetails.Country }
     *
     */
    public void setCountry(final AddressDetails.Country value) {
        this.country = value;
    }

    /**
     * Gets the value of the administrativeArea property.
     *
     * @return
     *     possible object is
     *     {@link AdministrativeArea }
     *
     */
    public AdministrativeArea getAdministrativeArea() {
        return administrativeArea;
    }

    /**
     * Sets the value of the administrativeArea property.
     *
     * @param value
     *     allowed object is
     *     {@link AdministrativeArea }
     *
     */
    public void setAdministrativeArea(final AdministrativeArea value) {
        this.administrativeArea = value;
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
     * Gets the value of the thoroughfare property.
     *
     * @return
     *     possible object is
     *     {@link Thoroughfare }
     *
     */
    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    /**
     * Sets the value of the thoroughfare property.
     *
     * @param value
     *     allowed object is
     *     {@link Thoroughfare }
     *
     */
    public void setThoroughfare(final Thoroughfare value) {
        this.thoroughfare = value;
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
     * Gets the value of the addressType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * Sets the value of the addressType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddressType(final String value) {
        this.addressType = value;
    }

    /**
     * Gets the value of the currentStatus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the value of the currentStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCurrentStatus(final String value) {
        this.currentStatus = value;
    }

    /**
     * Gets the value of the validFromDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValidFromDate() {
        return validFromDate;
    }

    /**
     * Sets the value of the validFromDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValidFromDate(final String value) {
        this.validFromDate = value;
    }

    /**
     * Gets the value of the validToDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValidToDate() {
        return validToDate;
    }

    /**
     * Sets the value of the validToDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValidToDate(final String value) {
        this.validToDate = value;
    }

    /**
     * Gets the value of the usage property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the value of the usage property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUsage(final String value) {
        this.usage = value;
    }

    /**
     * Gets the value of the addressDetailsKey property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAddressDetailsKey() {
        return addressDetailsKey;
    }

    /**
     * Sets the value of the addressDetailsKey property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAddressDetailsKey(final String value) {
        this.addressDetailsKey = value;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[AddressDetails]\n");
        if (address != null) {
            sb.append("address:").append(address).append('\n');
        }
        if (addressDetailsKey != null) {
            sb.append("addressDetailsKey:").append(addressDetailsKey).append('\n');
        }
        if (addressLines != null) {
            sb.append("addressLines:").append(addressLines).append('\n');
        }
        if (addressType != null) {
            sb.append("addressType:").append(addressType).append('\n');
        }
        if (administrativeArea != null) {
            sb.append("administrativeArea:").append(administrativeArea).append('\n');
        }
        if (any != null) {
            sb.append("any:\n");
            for (Object o : any) {
                sb.append(o).append('\n');
            }
        }
        if (code != null) {
            sb.append("code:").append(code).append('\n');
        }
        if (country != null) {
            sb.append("country:").append(country).append('\n');
        }
        if (currentStatus != null) {
            sb.append("currentStatus:").append(currentStatus).append('\n');
        }
        if (otherAttributes != null) {
            sb.append("otherAttributes:\n");
            for (Entry entry : otherAttributes.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        if (postalServiceElements != null) {
            sb.append("postalServiceElements:").append(postalServiceElements).append('\n');
        }
        if (thoroughfare != null) {
            sb.append("thoroughfare:").append(thoroughfare).append('\n');
        }
        if (usage != null) {
            sb.append("usage:").append(usage).append('\n');
        }
        if (validFromDate != null) {
            sb.append("validFromDate:").append(validFromDate).append('\n');
        }
        if (validToDate != null) {
            sb.append("validToDate:").append(validToDate).append('\n');
        }
        return sb.toString();
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
    public static class Address {

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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[Address]\n");
            if (code != null) {
                sb.append("code:").append(code).append('\n');
            }
            if (content != null) {
                sb.append("content:").append(content).append('\n');
            }
            if (type != null) {
                sb.append("type:").append(type).append('\n');
            }
            if (otherAttributes != null) {
                sb.append("otherAttributes:\n");
                for (Entry entry : otherAttributes.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            return sb.toString();
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
     *         &lt;element name="CountryNameCode" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Scheme" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}CountryName" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;choice minOccurs="0">
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AdministrativeArea"/>
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Locality"/>
     *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Thoroughfare"/>
     *         &lt;/choice>
     *         &lt;any/>
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
        "addressLine",
        "countryNameCode",
        "countryName",
        "administrativeArea",
        "locality",
        "thoroughfare",
        "any"
    })
    public static class Country {

        @XmlElement(name = "AddressLine")
        private List<AddressLine> addressLine;
        @XmlElement(name = "CountryNameCode")
        private List<AddressDetails.Country.CountryNameCode> countryNameCode;
        @XmlElement(name = "CountryName")
        private List<CountryName> countryName;
        @XmlElement(name = "AdministrativeArea")
        private AdministrativeArea administrativeArea;
        @XmlElement(name = "Locality")
        private Locality locality;
        @XmlElement(name = "Thoroughfare")
        private Thoroughfare thoroughfare;
        @XmlAnyElement(lax = true)
        private List<Object> any;
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
         * Gets the value of the countryNameCode property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the countryNameCode property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCountryNameCode().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AddressDetails.Country.CountryNameCode }
         *
         *
         */
        public List<AddressDetails.Country.CountryNameCode> getCountryNameCode() {
            if (countryNameCode == null) {
                countryNameCode = new ArrayList<AddressDetails.Country.CountryNameCode>();
            }
            return this.countryNameCode;
        }

        /**
         * Gets the value of the countryName property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the countryName property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCountryName().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CountryName }
         *
         *
         */
        public List<CountryName> getCountryName() {
            if (countryName == null) {
                countryName = new ArrayList<CountryName>();
            }
            return this.countryName;
        }

        /**
         * Gets the value of the administrativeArea property.
         *
         * @return
         *     possible object is
         *     {@link AdministrativeArea }
         *
         */
        public AdministrativeArea getAdministrativeArea() {
            return administrativeArea;
        }

        /**
         * Sets the value of the administrativeArea property.
         *
         * @param value
         *     allowed object is
         *     {@link AdministrativeArea }
         *
         */
        public void setAdministrativeArea(final AdministrativeArea value) {
            this.administrativeArea = value;
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
         * Gets the value of the thoroughfare property.
         *
         * @return
         *     possible object is
         *     {@link Thoroughfare }
         *
         */
        public Thoroughfare getThoroughfare() {
            return thoroughfare;
        }

        /**
         * Sets the value of the thoroughfare property.
         *
         * @param value
         *     allowed object is
         *     {@link Thoroughfare }
         *
         */
        public void setThoroughfare(final Thoroughfare value) {
            this.thoroughfare = value;
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


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[Country]\n");
            if (addressLine != null) {
                sb.append("addressLine:\n");
                for (AddressLine a : addressLine) {
                    sb.append(a).append('\n');
                }
            }
            if (administrativeArea != null) {
                sb.append("administrativeArea:").append(administrativeArea).append('\n');
            }
            if (any != null) {
                sb.append("any:\n");
                for (Object o : any) {
                    sb.append(o).append('\n');
                }
            }
            if (countryName != null) {
                sb.append("countryName:\n");
                for (CountryName a : countryName) {
                    sb.append(a).append('\n');
                }
            }
            if (countryNameCode != null) {
                sb.append("countryNameCode:\n");
                for (CountryNameCode a : countryNameCode) {
                    sb.append(a).append('\n');
                }
            }
            if (otherAttributes != null) {
                sb.append("otherAttributes:\n");
                for (Entry entry : otherAttributes.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            if (locality != null) {
                sb.append("locality:").append(locality).append('\n');
            }
            if (thoroughfare != null) {
                sb.append("thoroughfare:").append(thoroughfare).append('\n');
            }
            return sb.toString();
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
         *       &lt;attribute name="Scheme" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
        public static class CountryNameCode {

            @XmlValue
            private String content;
            @XmlAttribute(name = "Scheme")
            @XmlSchemaType(name = "anySimpleType")
            private String scheme;
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
             * Gets the value of the scheme property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getScheme() {
                return scheme;
            }

            /**
             * Sets the value of the scheme property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setScheme(final String value) {
                this.scheme = value;
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

             @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[CountryNameCode]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (scheme != null) {
                    sb.append("scheme:").append(scheme).append('\n');
                }
                return sb.toString();
            }
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
     *         &lt;element name="AddressIdentifier" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="IdentifierType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="EndorsementLineCode" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="KeyLineCode" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="Barcode" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SortingCode" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AddressLatitude" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AddressLatitudeDirection" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AddressLongitude" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AddressLongitudeDirection" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SupplementaryPostalServiceData" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;any/>
     *       &lt;/sequence>
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
        "addressIdentifier",
        "endorsementLineCode",
        "keyLineCode",
        "barcode",
        "sortingCode",
        "addressLatitude",
        "addressLatitudeDirection",
        "addressLongitude",
        "addressLongitudeDirection",
        "supplementaryPostalServiceData",
        "any"
    })
    public static class PostalServiceElements {

        @XmlElement(name = "AddressIdentifier")
        private List<AddressDetails.PostalServiceElements.AddressIdentifier> addressIdentifier;
        @XmlElement(name = "EndorsementLineCode")
        private AddressDetails.PostalServiceElements.EndorsementLineCode endorsementLineCode;
        @XmlElement(name = "KeyLineCode")
        private AddressDetails.PostalServiceElements.KeyLineCode keyLineCode;
        @XmlElement(name = "Barcode")
        private AddressDetails.PostalServiceElements.Barcode barcode;
        @XmlElement(name = "SortingCode")
        private AddressDetails.PostalServiceElements.SortingCode sortingCode;
        @XmlElement(name = "AddressLatitude")
        private AddressDetails.PostalServiceElements.AddressLatitude addressLatitude;
        @XmlElement(name = "AddressLatitudeDirection")
        private AddressDetails.PostalServiceElements.AddressLatitudeDirection addressLatitudeDirection;
        @XmlElement(name = "AddressLongitude")
        private AddressDetails.PostalServiceElements.AddressLongitude addressLongitude;
        @XmlElement(name = "AddressLongitudeDirection")
        private AddressDetails.PostalServiceElements.AddressLongitudeDirection addressLongitudeDirection;
        @XmlElement(name = "SupplementaryPostalServiceData")
        private List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> supplementaryPostalServiceData;
        @XmlAnyElement(lax = true)
        private List<Object> any;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Gets the value of the addressIdentifier property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the addressIdentifier property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAddressIdentifier().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AddressDetails.PostalServiceElements.AddressIdentifier }
         *
         *
         */
        public List<AddressDetails.PostalServiceElements.AddressIdentifier> getAddressIdentifier() {
            if (addressIdentifier == null) {
                addressIdentifier = new ArrayList<AddressDetails.PostalServiceElements.AddressIdentifier>();
            }
            return this.addressIdentifier;
        }

        /**
         * Gets the value of the endorsementLineCode property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.EndorsementLineCode }
         *
         */
        public AddressDetails.PostalServiceElements.EndorsementLineCode getEndorsementLineCode() {
            return endorsementLineCode;
        }

        /**
         * Sets the value of the endorsementLineCode property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.EndorsementLineCode }
         *
         */
        public void setEndorsementLineCode(final AddressDetails.PostalServiceElements.EndorsementLineCode value) {
            this.endorsementLineCode = value;
        }

        /**
         * Gets the value of the keyLineCode property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.KeyLineCode }
         *
         */
        public AddressDetails.PostalServiceElements.KeyLineCode getKeyLineCode() {
            return keyLineCode;
        }

        /**
         * Sets the value of the keyLineCode property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.KeyLineCode }
         *
         */
        public void setKeyLineCode(final AddressDetails.PostalServiceElements.KeyLineCode value) {
            this.keyLineCode = value;
        }

        /**
         * Gets the value of the barcode property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.Barcode }
         *
         */
        public AddressDetails.PostalServiceElements.Barcode getBarcode() {
            return barcode;
        }

        /**
         * Sets the value of the barcode property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.Barcode }
         *
         */
        public void setBarcode(final AddressDetails.PostalServiceElements.Barcode value) {
            this.barcode = value;
        }

        /**
         * Gets the value of the sortingCode property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.SortingCode }
         *
         */
        public AddressDetails.PostalServiceElements.SortingCode getSortingCode() {
            return sortingCode;
        }

        /**
         * Sets the value of the sortingCode property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.SortingCode }
         *
         */
        public void setSortingCode(final AddressDetails.PostalServiceElements.SortingCode value) {
            this.sortingCode = value;
        }

        /**
         * Gets the value of the addressLatitude property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitude }
         *
         */
        public AddressDetails.PostalServiceElements.AddressLatitude getAddressLatitude() {
            return addressLatitude;
        }

        /**
         * Sets the value of the addressLatitude property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitude }
         *
         */
        public void setAddressLatitude(final AddressDetails.PostalServiceElements.AddressLatitude value) {
            this.addressLatitude = value;
        }

        /**
         * Gets the value of the addressLatitudeDirection property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitudeDirection }
         *
         */
        public AddressDetails.PostalServiceElements.AddressLatitudeDirection getAddressLatitudeDirection() {
            return addressLatitudeDirection;
        }

        /**
         * Sets the value of the addressLatitudeDirection property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLatitudeDirection }
         *
         */
        public void setAddressLatitudeDirection(final AddressDetails.PostalServiceElements.AddressLatitudeDirection value) {
            this.addressLatitudeDirection = value;
        }

        /**
         * Gets the value of the addressLongitude property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitude }
         *
         */
        public AddressDetails.PostalServiceElements.AddressLongitude getAddressLongitude() {
            return addressLongitude;
        }

        /**
         * Sets the value of the addressLongitude property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitude }
         *
         */
        public void setAddressLongitude(final AddressDetails.PostalServiceElements.AddressLongitude value) {
            this.addressLongitude = value;
        }

        /**
         * Gets the value of the addressLongitudeDirection property.
         *
         * @return
         *     possible object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitudeDirection }
         *
         */
        public AddressDetails.PostalServiceElements.AddressLongitudeDirection getAddressLongitudeDirection() {
            return addressLongitudeDirection;
        }

        /**
         * Sets the value of the addressLongitudeDirection property.
         *
         * @param value
         *     allowed object is
         *     {@link AddressDetails.PostalServiceElements.AddressLongitudeDirection }
         *
         */
        public void setAddressLongitudeDirection(final AddressDetails.PostalServiceElements.AddressLongitudeDirection value) {
            this.addressLongitudeDirection = value;
        }

        /**
         * Gets the value of the supplementaryPostalServiceData property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the supplementaryPostalServiceData property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSupplementaryPostalServiceData().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AddressDetails.PostalServiceElements.SupplementaryPostalServiceData }
         *
         *
         */
        public List<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData> getSupplementaryPostalServiceData() {
            if (supplementaryPostalServiceData == null) {
                supplementaryPostalServiceData = new ArrayList<AddressDetails.PostalServiceElements.SupplementaryPostalServiceData>();
            }
            return this.supplementaryPostalServiceData;
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[PostalServiceElements]\n");
            if (addressIdentifier != null) {
                sb.append("addressIdentifier:\n");
                for (AddressIdentifier o : addressIdentifier) {
                    sb.append(o).append('\n');
                }
            }
            if (supplementaryPostalServiceData != null) {
                sb.append("supplementaryPostalServiceData:\n");
                for (SupplementaryPostalServiceData o : supplementaryPostalServiceData) {
                    sb.append(o).append('\n');
                }
            }

            if (addressLatitude != null) {
                sb.append("addressLatitude:").append(addressLatitude).append('\n');
            }
            if (addressLatitudeDirection != null) {
                sb.append("addressLatitudeDirection:").append(addressLatitudeDirection).append('\n');
            }
            if (addressLongitude != null) {
                sb.append("addressLongitude:").append(addressLongitude).append('\n');
            }
            if (barcode != null) {
                sb.append("barcode:").append(barcode).append('\n');
            }
            if (any != null) {
                sb.append("any:\n");
                for (Object o : any) {
                    sb.append(o).append('\n');
                }
            }
            if (endorsementLineCode != null) {
                sb.append("endorsementLineCode:").append(endorsementLineCode).append('\n');
            }
            if (keyLineCode != null) {
                sb.append("keyLineCode:").append(keyLineCode).append('\n');
            }
            if (sortingCode != null) {
                sb.append("sortingCode:").append(sortingCode).append('\n');
            }
            if (otherAttributes != null) {
                sb.append("otherAttributes:\n");
                for (Entry entry : otherAttributes.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            if (type != null) {
                sb.append("type:").append(type).append('\n');
            }
            return sb.toString();
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
         *       &lt;attribute name="IdentifierType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
        public static class AddressIdentifier {

            @XmlValue
            private String content;
            @XmlAttribute(name = "IdentifierType")
            @XmlSchemaType(name = "anySimpleType")
            private String identifierType;
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
             * Gets the value of the identifierType property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getIdentifierType() {
                return identifierType;
            }

            /**
             * Sets the value of the identifierType property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setIdentifierType(final String value) {
                this.identifierType = value;
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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[AddressIdentifier]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (identifierType != null) {
                    sb.append("identifierType:").append(identifierType).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class AddressLatitude {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[AddressLatitude]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
            }
        }


        /**
         * Specific to postal service
         *
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
        public static class AddressLatitudeDirection {

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
             * Specific to postal service
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
             * Specific to postal service
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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[AddressLatitudeDirection]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class AddressLongitude {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[AddressLongitude]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class AddressLongitudeDirection {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[AddressLongitudeDirection]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class Barcode {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[Barcode]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class EndorsementLineCode {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[EndorsementLineCode]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        public static class KeyLineCode {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[KeyLineCode]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
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
        @XmlType(name = "")
        public static class SortingCode {

            @XmlAttribute(name = "Type")
            @XmlSchemaType(name = "anySimpleType")
            private String type;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            private String code;

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[SortingCode]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                return sb.toString();
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
        public static class SupplementaryPostalServiceData {

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

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[SupplementaryPostalServiceData]\n");
                if (code != null) {
                    sb.append("code:").append(code).append('\n');
                }
                if (content != null) {
                    sb.append("content:").append(content).append('\n');
                }
                if (type != null) {
                    sb.append("type:").append(type).append('\n');
                }
                if (otherAttributes != null) {
                    sb.append("otherAttributes:\n");
                    for (Entry entry : otherAttributes.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
                return sb.toString();
            }
        }

    }

}
