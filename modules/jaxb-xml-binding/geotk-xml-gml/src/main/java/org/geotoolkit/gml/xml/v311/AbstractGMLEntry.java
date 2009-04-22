/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.AbstractGML;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractGMLType complex type.
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGMLType", propOrder = {
    "description",
    "descriptionReference",
    "name"
})
public abstract class AbstractGMLEntry implements AbstractGML {

    //private List<MetaDataPropertyType> metaDataProperty;
    //private CodeWithAuthorityType identifier;
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
    public AbstractGMLEntry() {}
    
    /**
     *  Simple super constructor to initialise the entry name.
     */
    public AbstractGMLEntry(String id) {
        //super(id);
        this.id = id;
    }
    
    public AbstractGMLEntry(String id, String name, String description, ReferenceEntry descriptionReference) {
        //super(id);
        this.id = id;
        this.name = name;
        this.description = description;
        this.descriptionReference = descriptionReference;
    }

    /**
     * Gets the value of the description property.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the value of the description property.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the value of the descriptionReference property.
     */
    public ReferenceEntry getDescriptionReference() {
        return descriptionReference;
    }


    public String getName() {
        return name;
    }
    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractGMLEntry) {
        final AbstractGMLEntry that = (AbstractGMLEntry) object;
            //TODO fix this problem       
            return Utilities.equals(this.description,          that.description)          &&
                   Utilities.equals(this.descriptionReference, that.descriptionReference);
                   //Utilities.equals(this.id,                   that.id)                   &&
                   //Utilities.equals(this.name,                 that.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]");
        if (id != null) {
            s.append("id = ").append(id).append('\n');
        }
        if (name != null) {
            s.append(" name = ").append(name).append('\n');
        }
        if (description != null) {
            s.append(" description = ").append(description).append('\n');
        }
        if (descriptionReference != null) {
            s.append("description reference = ").append(descriptionReference.toString()).append('\n');
        }
        
        return s.toString();
    }
     
    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *     
     
    public CodeWithAuthorityType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *     
     
    public void setIdentifier(CodeWithAuthorityType value) {
        this.identifier = value;
    }*/
    
    /**
     * Gets the value of the metaDataProperty property.
     * 
     
    public List<MetaDataPropertyType> getMetaDataProperty() {
        if (metaDataProperty == null) {
            metaDataProperty = new ArrayList<MetaDataPropertyType>();
        }
        return this.metaDataProperty;
    }*/

}
