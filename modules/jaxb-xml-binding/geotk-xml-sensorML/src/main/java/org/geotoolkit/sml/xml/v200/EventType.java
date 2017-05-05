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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentPropertyType;
import org.geotoolkit.swe.xml.v200.AbstractSWEIdentifiableType;
import org.opengis.metadata.identification.Keywords;


/**
 * <p>Java class for EventType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EventType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEIdentifiableType">
 *       &lt;sequence>
 *         &lt;element name="keywords" type="{http://www.isotc211.org/2005/gmd}MD_Keywords_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="identification" type="{http://www.opengis.net/sensorml/2.0}IdentifierListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="classification" type="{http://www.opengis.net/sensorml/2.0}ClassifierListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contacts" type="{http://www.opengis.net/sensorml/2.0}ContactListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="documentation" type="{http://www.opengis.net/sensorml/2.0}DocumentListPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="time">
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
 *         &lt;element name="property" type="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="configuration" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractSettings"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="definition" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventType", propOrder = {
    "keywords",
    "identification",
    "classification",
    "contacts",
    "documentation",
    "time",
    "property",
    "configuration"
})
public class EventType
    extends AbstractSWEIdentifiableType
{

    protected List<Keywords> keywords;
    protected List<IdentifierListPropertyType> identification;
    protected List<ClassifierListPropertyType> classification;
    protected List<ContactListPropertyType> contacts;
    protected List<DocumentListPropertyType> documentation;
    @XmlElement(required = true)
    protected EventType.Time time;
    protected List<AbstractDataComponentPropertyType> property;
    protected EventType.Configuration configuration;
    @XmlAttribute(name = "definition")
    @XmlSchemaType(name = "anyURI")
    protected String definition;

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
     * {@link MDKeywordsPropertyType }
     *
     *
     */
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
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
     * Gets the value of the time property.
     *
     * @return
     *     possible object is
     *     {@link EventType.Time }
     *
     */
    public EventType.Time getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     * @param value
     *     allowed object is
     *     {@link EventType.Time }
     *
     */
    public void setTime(EventType.Time value) {
        this.time = value;
    }

    /**
     * Gets the value of the property property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractDataComponentPropertyType }
     *
     *
     */
    public List<AbstractDataComponentPropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<AbstractDataComponentPropertyType>();
        }
        return this.property;
    }

    /**
     * Gets the value of the configuration property.
     *
     * @return
     *     possible object is
     *     {@link EventType.Configuration }
     *
     */
    public EventType.Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     *
     * @param value
     *     allowed object is
     *     {@link EventType.Configuration }
     *
     */
    public void setConfiguration(EventType.Configuration value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the definition property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefinition(String value) {
        this.definition = value;
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
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}AbstractSettings"/>
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
        "abstractSettings"
    })
    public static class Configuration {

        @XmlElementRef(name = "AbstractSettings", namespace = "http://www.opengis.net/sensorml/2.0", type = JAXBElement.class)
        protected JAXBElement<? extends AbstractSettingsType> abstractSettings;

        /**
         * Gets the value of the abstractSettings property.
         *
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link SettingsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSettingsType }{@code >}
         *
         */
        public JAXBElement<? extends AbstractSettingsType> getAbstractSettings() {
            return abstractSettings;
        }

        /**
         * Sets the value of the abstractSettings property.
         *
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link SettingsType }{@code >}
         *     {@link JAXBElement }{@code <}{@link AbstractSettingsType }{@code >}
         *
         */
        public void setAbstractSettings(JAXBElement<? extends AbstractSettingsType> value) {
            this.abstractSettings = value;
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
    public static class Time {

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
