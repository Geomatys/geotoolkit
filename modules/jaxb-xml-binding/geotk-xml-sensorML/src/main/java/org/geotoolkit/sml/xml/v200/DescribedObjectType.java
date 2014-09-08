/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.opengis.metadata.constraint.LegalConstraints;


/**
 * <p>Java class for DescribedObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescribedObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="keywords" type="{http://www.opengis.net/sensorml/2.0}KeywordListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="identification" type="{http://www.opengis.net/sensorml/2.0}IdentifierListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="classification" type="{http://www.opengis.net/sensorml/2.0}ClassifierListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="validTime" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;group ref="{http://www.opengis.net/sensorml/2.0}TimeInstantOrPeriod"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="securityConstraints" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="legalConstraints" type="{http://www.isotc211.org/2005/gmd}MD_LegalConstraints_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="characteristics" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/sensorml/2.0}CharacteristicListPropertyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="capabilities" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/sensorml/2.0}CapabilityListPropertyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="contacts" type="{http://www.opengis.net/sensorml/2.0}ContactListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="documentation" type="{http://www.opengis.net/sensorml/2.0}DocumentListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="history" type="{http://www.opengis.net/sensorml/2.0}EventListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribedObjectType", propOrder = {
    "extension",
    "keywords",
    "identification",
    "classification",
    "validTime",
    "securityConstraints",
    "legalConstraints",
    "characteristics",
    "capabilities",
    "contacts",
    "documentation",
    "history"
})
@XmlSeeAlso({
    AbstractProcessType.class,
    ModeType.class
})
public abstract class DescribedObjectType
    extends AbstractFeatureType
{

    protected List<Object> extension;
    protected List<KeywordListPropertyType> keywords;
    protected List<IdentifierListPropertyType> identification;
    protected List<ClassifierListPropertyType> classification;
    protected List<DescribedObjectType.ValidTime> validTime;
    protected List<Object> securityConstraints;
    protected List<LegalConstraints> legalConstraints;
    protected List<DescribedObjectType.Characteristics> characteristics;
    protected List<DescribedObjectType.Capabilities> capabilities;
    protected List<ContactListPropertyType> contacts;
    protected List<DocumentListPropertyType> documentation;
    protected List<EventListPropertyType> history;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    /**
     * Gets the value of the extension property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extension property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtension().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Object>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the keywords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keywords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeywords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeywordListPropertyType }
     * 
     * 
     */
    public List<KeywordListPropertyType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<KeywordListPropertyType>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the identification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierListPropertyType }
     * 
     * 
     */
    public List<IdentifierListPropertyType> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<IdentifierListPropertyType>();
        }
        return this.identification;
    }

    /**
     * Gets the value of the classification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassifierListPropertyType }
     * 
     * 
     */
    public List<ClassifierListPropertyType> getClassification() {
        if (classification == null) {
            classification = new ArrayList<ClassifierListPropertyType>();
        }
        return this.classification;
    }

    /**
     * Gets the value of the validTime property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validTime property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidTime().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescribedObjectType.ValidTime }
     * 
     * 
     */
    public List<DescribedObjectType.ValidTime> getValidTime() {
        if (validTime == null) {
            validTime = new ArrayList<DescribedObjectType.ValidTime>();
        }
        return this.validTime;
    }

    /**
     * Gets the value of the securityConstraints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the securityConstraints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSecurityConstraints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getSecurityConstraints() {
        if (securityConstraints == null) {
            securityConstraints = new ArrayList<Object>();
        }
        return this.securityConstraints;
    }

    /**
     * Gets the value of the legalConstraints property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the legalConstraints property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLegalConstraints().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MDLegalConstraintsPropertyType }
     * 
     * 
     */
    public List<LegalConstraints> getLegalConstraints() {
        if (legalConstraints == null) {
            legalConstraints = new ArrayList<>();
        }
        return this.legalConstraints;
    }

    /**
     * Gets the value of the characteristics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the characteristics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCharacteristics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescribedObjectType.Characteristics }
     * 
     * 
     */
    public List<DescribedObjectType.Characteristics> getCharacteristics() {
        if (characteristics == null) {
            characteristics = new ArrayList<DescribedObjectType.Characteristics>();
        }
        return this.characteristics;
    }

    /**
     * Gets the value of the capabilities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the capabilities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCapabilities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescribedObjectType.Capabilities }
     * 
     * 
     */
    public List<DescribedObjectType.Capabilities> getCapabilities() {
        if (capabilities == null) {
            capabilities = new ArrayList<DescribedObjectType.Capabilities>();
        }
        return this.capabilities;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contacts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContacts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactListPropertyType }
     * 
     * 
     */
    public List<ContactListPropertyType> getContacts() {
        if (contacts == null) {
            contacts = new ArrayList<ContactListPropertyType>();
        }
        return this.contacts;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentListPropertyType }
     * 
     * 
     */
    public List<DocumentListPropertyType> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<DocumentListPropertyType>();
        }
        return this.documentation;
    }

    /**
     * Gets the value of the history property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the history property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventListPropertyType }
     * 
     * 
     */
    public List<EventListPropertyType> getHistory() {
        if (history == null) {
            history = new ArrayList<EventListPropertyType>();
        }
        return this.history;
    }

    /**
     * A tag that identifies the language (e.g. english, french, etc.) for the overall document using a two-letters code as defined by ISO 639-1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}CapabilityListPropertyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Capabilities
        extends CapabilityListPropertyType
    {

        @XmlAttribute(name = "name", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String name;

        /**
         * Gets the value of the name property.
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
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
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
     *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}CharacteristicListPropertyType">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Characteristics
        extends CharacteristicListPropertyType
    {

        @XmlAttribute(name = "name", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String name;

        /**
         * Gets the value of the name property.
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
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
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
     *         &lt;group ref="{http://www.opengis.net/sensorml/2.0}TimeInstantOrPeriod"/>
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
        "timePeriod",
        "timeInstant"
    })
    public static class ValidTime {

        @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml/3.2")
        protected TimePeriodType timePeriod;
        @XmlElement(name = "TimeInstant", namespace = "http://www.opengis.net/gml/3.2")
        protected TimeInstantType timeInstant;

        /**
         * Gets the value of the timePeriod property.
         * 
         * @return
         *     possible object is
         *     {@link TimePeriodType }
         *     
         */
        public TimePeriodType getTimePeriod() {
            return timePeriod;
        }

        /**
         * Sets the value of the timePeriod property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimePeriodType }
         *     
         */
        public void setTimePeriod(TimePeriodType value) {
            this.timePeriod = value;
        }

        /**
         * Gets the value of the timeInstant property.
         * 
         * @return
         *     possible object is
         *     {@link TimeInstantType }
         *     
         */
        public TimeInstantType getTimeInstant() {
            return timeInstant;
        }

        /**
         * Sets the value of the timeInstant property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimeInstantType }
         *     
         */
        public void setTimeInstant(TimeInstantType value) {
            this.timeInstant = value;
        }

    }

}
