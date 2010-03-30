/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.sml.xml.AbstractClassification;
import org.geotoolkit.sml.xml.AbstractContact;
import org.geotoolkit.sml.xml.AbstractDocumentation;
import org.geotoolkit.sml.xml.AbstractEvent;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractKeywords;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.v101.DataComponentPropertyType;


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
 *         &lt;element name="date" type="{http://www.opengis.net/swe/1.0.1}timeIso8601" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}generalInfo" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0.1}references" minOccurs="0"/>
 *         &lt;element name="property" type="{http://www.opengis.net/swe/1.0.1}DataComponentPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "date",
    "description",
    "keywords",
    "identification",
    "classification",
    "contact",
    "documentation",
    "property"
})
@XmlRootElement(name = "Event")
public class Event implements AbstractEvent {

    private String date;
    @XmlElement(namespace = "http://www.opengis.net/gml")
    private StringOrRefType description;
    private List<Keywords> keywords;
    private List<Identification> identification;
    private List<Classification> classification;
    private List<Contact> contact;
    private List<Documentation> documentation;
    private List<DataComponentPropertyType> property;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public Event() {

    }

    public Event(AbstractEvent event) {
        if (event != null) {
            if (event.getClassification() != null) {
                this.classification = new ArrayList<Classification>();
                for (AbstractClassification c : event.getClassification()) {
                    this.classification.add(new Classification(c));
                }
            }
            if (event.getContact() != null) {
                this.contact = new ArrayList<Contact>();
                for (AbstractContact c : event.getContact()) {
                    this.contact.add(new Contact(c));
                }
            }
            if (event.getDocumentation() != null) {
                this.documentation = new ArrayList<Documentation>();
                for (AbstractDocumentation c : event.getDocumentation()) {
                    this.documentation.add(new Documentation(c));
                }
            }
            if (event.getIdentification() != null) {
                this.identification = new ArrayList<Identification>();
                for (AbstractIdentification c : event.getIdentification()) {
                    this.identification.add(new Identification(c));
                }
            }
            if (event.getKeywords() != null) {
                this.keywords = new ArrayList<Keywords>();
                for (AbstractKeywords c : event.getKeywords()) {
                    this.keywords.add(new Keywords(c));
                }
            }
            if (event.getProperty() != null) {
                this.property = new ArrayList<DataComponentPropertyType>();
                for (DataComponentProperty c : event.getProperty()) {
                    this.property.add(new DataComponentPropertyType(c));
                }
            }
            this.date        = event.getDate();
            this.id          = event.getId();
            this.description = event.getDescription();
        }

    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *     
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *     
     */
    public void setDescription(StringOrRefType value) {
        this.description = value;
    }

    /**
     * Gets the value of the keywords property.
     * 
     */
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<Keywords>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the identification property.
     * 
     */
    public List<Identification> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<Identification>();
        }
        return this.identification;
    }

    /**
     * Gets the value of the classification property.
     * 
     */
    public List<Classification> getClassification() {
        if (classification == null) {
            classification = new ArrayList<Classification>();
        }
        return this.classification;
    }

    /**
     * Gets the value of the contact property.
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the documentation property.
     */
    public List<Documentation> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<Documentation>();
        }
        return this.documentation;
    }

    /**
     * Gets the value of the property property.
     */
    public List<DataComponentPropertyType> getProperty() {
        if (property == null) {
            property = new ArrayList<DataComponentPropertyType>();
        }
        return this.property;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
