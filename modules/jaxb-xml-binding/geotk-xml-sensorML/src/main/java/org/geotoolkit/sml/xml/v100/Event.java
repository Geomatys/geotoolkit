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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.sml.xml.AbstractEvent;
import org.geotoolkit.swe.xml.v100.DataComponentPropertyType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="date" type="{http://www.opengis.net/swe/1.0}timeIso8601" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0}generalInfo" minOccurs="0"/>
 *         &lt;group ref="{http://www.opengis.net/sensorML/1.0}references" minOccurs="0"/>
 *         &lt;element name="property" type="{http://www.opengis.net/swe/1.0}DataComponentPropertyType" maxOccurs="unbounded" minOccurs="0"/>
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
    private String id;

    /**
     * Gets the value of the date property.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the description property.
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     */
    public void setDescription(StringOrRefType value) {
        this.description = value;
    }

    /**
     * Gets the value of the keywords property.
     */
    public List<Keywords> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<Keywords>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the identification property.
     */
    public List<Identification> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<Identification>();
        }
        return this.identification;
    }

    /**
     * Gets the value of the classification property.
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
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Event]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (date != null) {
            sb.append("date: ").append(date).append('\n');
        }
        if (description != null) {
            sb.append("description: ").append(description).append('\n');
        }
        if (classification != null) {
            sb.append("classification:").append('\n');
            for (Classification k : classification) {
                sb.append(k).append('\n');
            }
        }
        if (contact != null) {
            sb.append("contact:").append('\n');
            for (Contact k : contact) {
                sb.append(k).append('\n');
            }
        }
        if (documentation != null) {
            sb.append("documentation:").append('\n');
            for (Documentation k : documentation) {
                sb.append(k).append('\n');
            }
        }
        if (identification != null) {
            sb.append("identification:").append('\n');
            for (Identification k : identification) {
                sb.append(k).append('\n');
            }
        }
        if (keywords != null) {
            sb.append("keywords:").append('\n');
            for (Keywords k : keywords) {
                sb.append(k).append('\n');
            }
        }
        if (property != null) {
            sb.append("property:").append('\n');
            for (DataComponentPropertyType k : property) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Event) {
            final Event that = (Event) object;

            return Utilities.equals(this.classification, that.classification)
                    && Utilities.equals(this.contact, that.contact)
                    && Utilities.equals(this.date, that.date)
                    && Utilities.equals(this.description, that.description)
                    && Utilities.equals(this.documentation, that.documentation)
                    && Utilities.equals(this.id, that.id)
                    && Utilities.equals(this.identification, that.identification)
                    && Utilities.equals(this.keywords, that.keywords)
                    && Utilities.equals(this.property, that.property);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 53 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 53 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        hash = 53 * hash + (this.identification != null ? this.identification.hashCode() : 0);
        hash = 53 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 53 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 53 * hash + (this.documentation != null ? this.documentation.hashCode() : 0);
        hash = 53 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
