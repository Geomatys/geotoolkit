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
 *         &lt;element ref="{http://www.opengis.net/gml}description"/>
 *         &lt;element name="date" type="{http://www.opengis.net/swe/1.0}timeIso8601" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}contact" minOccurs="0"/>
 *         &lt;element name="format" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}onlineResource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "description",
    "date",
    "contact",
    "format",
    "onlineResource"
})
@XmlRootElement(name = "Document")
public class Document {

    @XmlElement(namespace = "http://www.opengis.net/gml", required = true)
    private StringOrRefType description;
    private String date;
    private Contact contact;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String format;
    private List<OnlineResource> onlineResource;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String version;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public Document() {

    }

    public Document(String description) {
        this.description = new StringOrRefType(description);
    }

    public Document(String description, String format, List<OnlineResource> onlineResource) {
        this.description = new StringOrRefType(description);
        this.format      = format;
        this.onlineResource = onlineResource;
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
     * Person who is responsible for the document
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Person who is responsible for the document
    */
    public void setContact(Contact value) {
        this.contact = value;
    }

    /**
     * Gets the value of the format property.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Points to the actual document corresponding to that version Gets the value of the onlineResource property.
     */
    public List<OnlineResource> getOnlineResource() {
        if (onlineResource == null) {
            onlineResource = new ArrayList<OnlineResource>();
        }
        return this.onlineResource;
    }

    public void setOnlineResource(OnlineResource onlineResource) {
        if (this.onlineResource == null) {
            this.onlineResource = new ArrayList<OnlineResource>();
        }
        this.onlineResource.add(onlineResource);
    }

    public void setOnlineResource(List<OnlineResource> onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     */
    public void setVersion(String value) {
        this.version = value;
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Document) {
            final Document that = (Document) object;
            return Utilities.equals(this.contact,        that.contact)        &&
                   Utilities.equals(this.date,           that.date)           &&
                   Utilities.equals(this.description,    that.description)    &&
                   Utilities.equals(this.format,         that.format)         &&
                   Utilities.equals(this.id,             that.id)             &&
                   Utilities.equals(this.onlineResource, that.onlineResource) &&
                   Utilities.equals(this.version,         that.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 89 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 89 * hash + (this.contact != null ? this.contact.hashCode() : 0);
        hash = 89 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 89 * hash + (this.onlineResource != null ? this.onlineResource.hashCode() : 0);
        hash = 89 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Document]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (contact != null) {
            sb.append("contact: ").append(contact).append('\n');
        }
        if (onlineResource != null) {
            sb.append("onlineResource:").append('\n');
            for (OnlineResource k : onlineResource) {
                sb.append("onlineResource: ").append(k).append('\n');
            }
        }
        if (date != null) {
            sb.append("date: ").append(date).append('\n');
        }
        if (description != null) {
            sb.append("description: ").append(description).append('\n');
        }
        if (format != null) {
            sb.append("format: ").append(format).append('\n');
        }
        if (version != null) {
            sb.append("version: ").append(version).append('\n');
        }
        return sb.toString();
    }


}
