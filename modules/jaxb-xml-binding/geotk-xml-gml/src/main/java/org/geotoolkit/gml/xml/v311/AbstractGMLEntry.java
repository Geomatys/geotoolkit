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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.AbstractGML;


/**
 * All complexContent GML elements are directly or indirectly derived from this abstract supertype 
 * 	to establish a hierarchy of GML types that may be distinguished from other XML types by their ancestry. 
 * 	Elements in this hierarchy may have an ID and are thus referenceable.
 * 
 * <p>Java class for AbstractGMLType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGMLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/gml}StandardObjectProperties"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGMLType", propOrder = {
    //"metaDataProperty",
    "description",
    "descriptionReference",
    "name"
})
@XmlSeeAlso({
    ArrayType.class,
    AbstractTimeObjectType.class,
    DefinitionType.class,
    BagType.class,
    AbstractGeometryType.class,
    AbstractFeatureEntry.class
})
public abstract class AbstractGMLEntry implements AbstractGML {

    //protected List<MetaDataPropertyType> metaDataProperty;
    private String description;
    private ReferenceEntry descriptionReference;
    private String name;
    @XmlAttribute(namespace = "http://www.opengis.net/gml", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    /**
     *  Empty constructor used by JAXB.
     */
    protected AbstractGMLEntry() {}

    /**
     *  Simple super constructor to initialise the entry name.
     */
    public AbstractGMLEntry(String id) {
        this.id = id;
    }

    public AbstractGMLEntry(String id, String name, String description, ReferenceEntry descriptionReference) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.descriptionReference = descriptionReference;
    }

    /**
     * Gets the value of the description property.    
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.    
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the description reference property.    
     */
    public ReferenceEntry getDescriptionReference() {
        return descriptionReference;
    }

    /**
     * Sets the value of the description reference property.    
     */
    public void setDescription(ReferenceEntry value) {
        this.descriptionReference = value;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
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
    @Override
    public void setId(String value) {
        this.id = value;
    }

    /**
     * @todo fix the id problem.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractGMLEntry other = (AbstractGMLEntry) obj;
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.descriptionReference != other.descriptionReference && (this.descriptionReference == null || !this.descriptionReference.equals(other.descriptionReference))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
//        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
//            return false;
//        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 41 * hash + (this.descriptionReference != null ? this.descriptionReference.hashCode() : 0);
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
//        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder('[').append(this.getClass().getName()).append(']').append('\n');
        if (id != null)
            sb.append("id:").append(id).append('\n');
        if (name != null)
            sb.append("name:").append(name).append('\n');
        if (description != null)
            sb.append("description:").append(description).append('\n');
        if (descriptionReference != null)
            sb.append("description reference:").append(descriptionReference).append('\n');
        return sb.toString();
    }

}
