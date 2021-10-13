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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 *         &lt;element name="PremiseName" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                 &lt;attribute name="TypeOccurrence">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                       &lt;enumeration value="Before"/>
 *                       &lt;enumeration value="After"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="PremiseLocation">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;choice>
 *             &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
 *             &lt;element name="PremiseNumberRange">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                     &lt;sequence>
 *                       &lt;element name="PremiseNumberRangeFrom">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;sequence>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
 *                               &lt;/sequence>
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="PremiseNumberRangeTo">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;sequence>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
 *                                 &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
 *                               &lt;/sequence>
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                     &lt;/sequence>
 *                     &lt;attribute name="RangeType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                     &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                     &lt;attribute name="Separator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                     &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                     &lt;attribute name="IndicatorOccurence">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                           &lt;enumeration value="Before"/>
 *                           &lt;enumeration value="After"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/attribute>
 *                     &lt;attribute name="NumberRangeOccurence">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *                           &lt;enumeration value="BeforeName"/>
 *                           &lt;enumeration value="AfterName"/>
 *                           &lt;enumeration value="BeforeType"/>
 *                           &lt;enumeration value="AfterType"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/attribute>
 *                   &lt;/restriction>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *           &lt;/choice>
 *         &lt;/choice>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BuildingName" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}BuildingNameType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="SubPremise" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}SubPremiseType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="Firm" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}FirmType" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="MailStop" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}MailStopType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalCode" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Premise" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="PremiseDependency" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="PremiseDependencyType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="PremiseThoroughfareConnector" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
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
    "premiseName",
    "premiseLocation",
    "premiseNumber",
    "premiseNumberRange",
    "premiseNumberPrefix",
    "premiseNumberSuffix",
    "buildingName",
    "subPremise",
    "firm",
    "mailStop",
    "postalCode",
    "premise",
    "any"
})
@XmlRootElement(name = "Premise")
public class Premise {

    @XmlElement(name = "AddressLine")
    private List<AddressLine> addressLine;
    @XmlElement(name = "PremiseName")
    private List<Premise.PremiseName> premiseName;
    @XmlElement(name = "PremiseLocation")
    private Premise.PremiseLocation premiseLocation;
    @XmlElement(name = "PremiseNumber")
    private List<PremiseNumber> premiseNumber;
    @XmlElement(name = "PremiseNumberRange")
    private Premise.PremiseNumberRange premiseNumberRange;
    @XmlElement(name = "PremiseNumberPrefix")
    private List<PremiseNumberPrefix> premiseNumberPrefix;
    @XmlElement(name = "PremiseNumberSuffix")
    private List<PremiseNumberSuffix> premiseNumberSuffix;
    @XmlElement(name = "BuildingName")
    private List<BuildingNameType> buildingName;
    @XmlElement(name = "SubPremise")
    private List<SubPremiseType> subPremise;
    @XmlElement(name = "Firm")
    private FirmType firm;
    @XmlElement(name = "MailStop")
    private MailStopType mailStop;
    @XmlElement(name = "PostalCode")
    private PostalCode postalCode;
    @XmlElement(name = "Premise")
    private Premise premise;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    private String type;
    @XmlAttribute(name = "PremiseDependency")
    @XmlSchemaType(name = "anySimpleType")
    private String premiseDependency;
    @XmlAttribute(name = "PremiseDependencyType")
    @XmlSchemaType(name = "anySimpleType")
    private String premiseDependencyType;
    @XmlAttribute(name = "PremiseThoroughfareConnector")
    @XmlSchemaType(name = "anySimpleType")
    private String premiseThoroughfareConnector;
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
     * Gets the value of the premiseName property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiseName property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiseName().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Premise.PremiseName }
     *
     *
     */
    public List<Premise.PremiseName> getPremiseName() {
        if (premiseName == null) {
            premiseName = new ArrayList<Premise.PremiseName>();
        }
        return this.premiseName;
    }

    /**
     * Gets the value of the premiseLocation property.
     *
     * @return
     *     possible object is
     *     {@link Premise.PremiseLocation }
     *
     */
    public Premise.PremiseLocation getPremiseLocation() {
        return premiseLocation;
    }

    /**
     * Sets the value of the premiseLocation property.
     *
     * @param value
     *     allowed object is
     *     {@link Premise.PremiseLocation }
     *
     */
    public void setPremiseLocation(final Premise.PremiseLocation value) {
        this.premiseLocation = value;
    }

    /**
     * Gets the value of the premiseNumber property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiseNumber property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiseNumber().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiseNumber }
     *
     *
     */
    public List<PremiseNumber> getPremiseNumber() {
        if (premiseNumber == null) {
            premiseNumber = new ArrayList<PremiseNumber>();
        }
        return this.premiseNumber;
    }

    /**
     * Gets the value of the premiseNumberRange property.
     *
     * @return
     *     possible object is
     *     {@link Premise.PremiseNumberRange }
     *
     */
    public Premise.PremiseNumberRange getPremiseNumberRange() {
        return premiseNumberRange;
    }

    /**
     * Sets the value of the premiseNumberRange property.
     *
     * @param value
     *     allowed object is
     *     {@link Premise.PremiseNumberRange }
     *
     */
    public void setPremiseNumberRange(final Premise.PremiseNumberRange value) {
        this.premiseNumberRange = value;
    }

    /**
     * Gets the value of the premiseNumberPrefix property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiseNumberPrefix property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiseNumberPrefix().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiseNumberPrefix }
     *
     *
     */
    public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
        if (premiseNumberPrefix == null) {
            premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
        }
        return this.premiseNumberPrefix;
    }

    /**
     * Gets the value of the premiseNumberSuffix property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the premiseNumberSuffix property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPremiseNumberSuffix().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PremiseNumberSuffix }
     *
     *
     */
    public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
        if (premiseNumberSuffix == null) {
            premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
        }
        return this.premiseNumberSuffix;
    }

    /**
     * Gets the value of the buildingName property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the buildingName property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBuildingName().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BuildingNameType }
     *
     *
     */
    public List<BuildingNameType> getBuildingName() {
        if (buildingName == null) {
            buildingName = new ArrayList<BuildingNameType>();
        }
        return this.buildingName;
    }

    /**
     * Gets the value of the subPremise property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subPremise property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubPremise().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubPremiseType }
     *
     *
     */
    public List<SubPremiseType> getSubPremise() {
        if (subPremise == null) {
            subPremise = new ArrayList<SubPremiseType>();
        }
        return this.subPremise;
    }

    /**
     * Gets the value of the firm property.
     *
     * @return
     *     possible object is
     *     {@link FirmType }
     *
     */
    public FirmType getFirm() {
        return firm;
    }

    /**
     * Sets the value of the firm property.
     *
     * @param value
     *     allowed object is
     *     {@link FirmType }
     *
     */
    public void setFirm(final FirmType value) {
        this.firm = value;
    }

    /**
     * Gets the value of the mailStop property.
     *
     * @return
     *     possible object is
     *     {@link MailStopType }
     *
     */
    public MailStopType getMailStop() {
        return mailStop;
    }

    /**
     * Sets the value of the mailStop property.
     *
     * @param value
     *     allowed object is
     *     {@link MailStopType }
     *
     */
    public void setMailStop(final MailStopType value) {
        this.mailStop = value;
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
     * Gets the value of the premise property.
     *
     * @return
     *     possible object is
     *     {@link Premise }
     *
     */
    public Premise getPremise() {
        return premise;
    }

    /**
     * Sets the value of the premise property.
     *
     * @param value
     *     allowed object is
     *     {@link Premise }
     *
     */
    public void setPremise(final Premise value) {
        this.premise = value;
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
     * Gets the value of the premiseDependency property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPremiseDependency() {
        return premiseDependency;
    }

    /**
     * Sets the value of the premiseDependency property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPremiseDependency(final String value) {
        this.premiseDependency = value;
    }

    /**
     * Gets the value of the premiseDependencyType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPremiseDependencyType() {
        return premiseDependencyType;
    }

    /**
     * Sets the value of the premiseDependencyType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPremiseDependencyType(final String value) {
        this.premiseDependencyType = value;
    }

    /**
     * Gets the value of the premiseThoroughfareConnector property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPremiseThoroughfareConnector() {
        return premiseThoroughfareConnector;
    }

    /**
     * Sets the value of the premiseThoroughfareConnector property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPremiseThoroughfareConnector(final String value) {
        this.premiseThoroughfareConnector = value;
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
    public static class PremiseLocation {

        @XmlValue
        private String content;
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
     *       &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="TypeOccurrence">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
     *             &lt;enumeration value="Before"/>
     *             &lt;enumeration value="After"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
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
    public static class PremiseName {

        @XmlValue
        private String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAttribute(name = "TypeOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String typeOccurrence;
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
         * Gets the value of the typeOccurrence property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getTypeOccurrence() {
            return typeOccurrence;
        }

        /**
         * Sets the value of the typeOccurrence property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setTypeOccurrence(final String value) {
            this.typeOccurrence = value;
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
     *         &lt;element name="PremiseNumberRangeFrom">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="PremiseNumberRangeTo">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
     *                   &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="RangeType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Separator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *       &lt;attribute name="IndicatorOccurence">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
     *             &lt;enumeration value="Before"/>
     *             &lt;enumeration value="After"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="NumberRangeOccurence">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
     *             &lt;enumeration value="BeforeName"/>
     *             &lt;enumeration value="AfterName"/>
     *             &lt;enumeration value="BeforeType"/>
     *             &lt;enumeration value="AfterType"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "premiseNumberRangeFrom",
        "premiseNumberRangeTo"
    })
    public static class PremiseNumberRange {

        @XmlElement(name = "PremiseNumberRangeFrom", required = true)
        private Premise.PremiseNumberRange.PremiseNumberRangeFrom premiseNumberRangeFrom;
        @XmlElement(name = "PremiseNumberRangeTo", required = true)
        private Premise.PremiseNumberRange.PremiseNumberRangeTo premiseNumberRangeTo;
        @XmlAttribute(name = "RangeType")
        @XmlSchemaType(name = "anySimpleType")
        private String rangeType;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        private String indicator;
        @XmlAttribute(name = "Separator")
        @XmlSchemaType(name = "anySimpleType")
        private String separator;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAttribute(name = "IndicatorOccurence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String indicatorOccurence;
        @XmlAttribute(name = "NumberRangeOccurence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String numberRangeOccurence;

        /**
         * Gets the value of the premiseNumberRangeFrom property.
         *
         * @return
         *     possible object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeFrom }
         *
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeFrom getPremiseNumberRangeFrom() {
            return premiseNumberRangeFrom;
        }

        /**
         * Sets the value of the premiseNumberRangeFrom property.
         *
         * @param value
         *     allowed object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeFrom }
         *
         */
        public void setPremiseNumberRangeFrom(final Premise.PremiseNumberRange.PremiseNumberRangeFrom value) {
            this.premiseNumberRangeFrom = value;
        }

        /**
         * Gets the value of the premiseNumberRangeTo property.
         *
         * @return
         *     possible object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeTo }
         *
         */
        public Premise.PremiseNumberRange.PremiseNumberRangeTo getPremiseNumberRangeTo() {
            return premiseNumberRangeTo;
        }

        /**
         * Sets the value of the premiseNumberRangeTo property.
         *
         * @param value
         *     allowed object is
         *     {@link Premise.PremiseNumberRange.PremiseNumberRangeTo }
         *
         */
        public void setPremiseNumberRangeTo(final Premise.PremiseNumberRange.PremiseNumberRangeTo value) {
            this.premiseNumberRangeTo = value;
        }

        /**
         * Gets the value of the rangeType property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRangeType() {
            return rangeType;
        }

        /**
         * Sets the value of the rangeType property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRangeType(final String value) {
            this.rangeType = value;
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
         * Gets the value of the separator property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getSeparator() {
            return separator;
        }

        /**
         * Sets the value of the separator property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setSeparator(final String value) {
            this.separator = value;
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
         * Gets the value of the indicatorOccurence property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getIndicatorOccurence() {
            return indicatorOccurence;
        }

        /**
         * Sets the value of the indicatorOccurence property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setIndicatorOccurence(final String value) {
            this.indicatorOccurence = value;
        }

        /**
         * Gets the value of the numberRangeOccurence property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getNumberRangeOccurence() {
            return numberRangeOccurence;
        }

        /**
         * Sets the value of the numberRangeOccurence property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setNumberRangeOccurence(final String value) {
            this.numberRangeOccurence = value;
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
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
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
            "premiseNumberPrefix",
            "premiseNumber",
            "premiseNumberSuffix"
        })
        public static class PremiseNumberRangeFrom {

            @XmlElement(name = "AddressLine")
            private List<AddressLine> addressLine;
            @XmlElement(name = "PremiseNumberPrefix")
            private List<PremiseNumberPrefix> premiseNumberPrefix;
            @XmlElement(name = "PremiseNumber", required = true)
            private List<PremiseNumber> premiseNumber;
            @XmlElement(name = "PremiseNumberSuffix")
            private List<PremiseNumberSuffix> premiseNumberSuffix;

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
             * Gets the value of the premiseNumberPrefix property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumberPrefix property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumberPrefix().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumberPrefix }
             *
             *
             */
            public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
                if (premiseNumberPrefix == null) {
                    premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
                }
                return this.premiseNumberPrefix;
            }

            /**
             * Gets the value of the premiseNumber property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumber property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumber().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumber }
             *
             *
             */
            public List<PremiseNumber> getPremiseNumber() {
                if (premiseNumber == null) {
                    premiseNumber = new ArrayList<PremiseNumber>();
                }
                return this.premiseNumber;
            }

            /**
             * Gets the value of the premiseNumberSuffix property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumberSuffix property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumberSuffix().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumberSuffix }
             *
             *
             */
            public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
                if (premiseNumberSuffix == null) {
                    premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
                }
                return this.premiseNumberSuffix;
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
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberPrefix" maxOccurs="unbounded" minOccurs="0"/>
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumber" maxOccurs="unbounded"/>
         *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PremiseNumberSuffix" maxOccurs="unbounded" minOccurs="0"/>
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
            "premiseNumberPrefix",
            "premiseNumber",
            "premiseNumberSuffix"
        })
        public static class PremiseNumberRangeTo {

            @XmlElement(name = "AddressLine")
            private List<AddressLine> addressLine;
            @XmlElement(name = "PremiseNumberPrefix")
            private List<PremiseNumberPrefix> premiseNumberPrefix;
            @XmlElement(name = "PremiseNumber", required = true)
            private List<PremiseNumber> premiseNumber;
            @XmlElement(name = "PremiseNumberSuffix")
            private List<PremiseNumberSuffix> premiseNumberSuffix;

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
             * Gets the value of the premiseNumberPrefix property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumberPrefix property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumberPrefix().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumberPrefix }
             *
             *
             */
            public List<PremiseNumberPrefix> getPremiseNumberPrefix() {
                if (premiseNumberPrefix == null) {
                    premiseNumberPrefix = new ArrayList<PremiseNumberPrefix>();
                }
                return this.premiseNumberPrefix;
            }

            /**
             * Gets the value of the premiseNumber property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumber property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumber().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumber }
             *
             *
             */
            public List<PremiseNumber> getPremiseNumber() {
                if (premiseNumber == null) {
                    premiseNumber = new ArrayList<PremiseNumber>();
                }
                return this.premiseNumber;
            }

            /**
             * Gets the value of the premiseNumberSuffix property.
             *
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the premiseNumberSuffix property.
             *
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPremiseNumberSuffix().add(newItem);
             * </pre>
             *
             *
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link PremiseNumberSuffix }
             *
             *
             */
            public List<PremiseNumberSuffix> getPremiseNumberSuffix() {
                if (premiseNumberSuffix == null) {
                    premiseNumberSuffix = new ArrayList<PremiseNumberSuffix>();
                }
                return this.premiseNumberSuffix;
            }

        }

    }

}
