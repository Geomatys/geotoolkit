/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for StoredQueryListItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoredQueryListItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ReturnFeatureType" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoredQueryListItemType", propOrder = {
    "title",
    "returnFeatureType"
})
public class StoredQueryListItemType {

    @XmlElement(name = "Title")
    private List<Title> title;
    @XmlElement(name = "ReturnFeatureType", required = true)
    private List<QName> returnFeatureType;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String id;

    public StoredQueryListItemType() {
        
    }

    public StoredQueryListItemType(final String id, final List<Title> title, final List<QName> returnFeatureType) {
        this.id = id;
        this.returnFeatureType = returnFeatureType;
        this.title = title;
    }
    
    /**
     * Gets the value of the title property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Title }
     * 
     */
    public List<Title> getTitle() {
        if (title == null) {
            title = new ArrayList<Title>();
        }
        return this.title;
    }

    /**
     * Gets the value of the returnFeatureType property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     */
    public List<QName> getReturnFeatureType() {
        if (returnFeatureType == null) {
            returnFeatureType = new ArrayList<QName>();
        }
        return this.returnFeatureType;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[StoredQueryListItemType]\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (title != null) {
           sb.append("title: ").append('\n');
           for (Title a : title) {
                sb.append(a).append('\n');
           }
        }
        if (returnFeatureType != null) {
           sb.append("returnFeatureType: ").append('\n');
           for (QName a : returnFeatureType) {
                sb.append(a).append('\n');
           }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof StoredQueryListItemType) {
            final StoredQueryListItemType that = (StoredQueryListItemType) object;
            return Objects.equals(this.id,                that.id) &&
                   Objects.equals(this.returnFeatureType, that.returnFeatureType) &&
                   Objects.equals(this.title,             that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 13 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 13 * hash + (this.returnFeatureType != null ? this.returnFeatureType.hashCode() : 0);
        return hash;
    }
}
