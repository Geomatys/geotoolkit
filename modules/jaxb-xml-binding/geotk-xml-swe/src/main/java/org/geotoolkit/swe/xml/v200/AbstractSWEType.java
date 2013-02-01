/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractSWEType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSWEType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSWEType", propOrder = {
    "extension"
})
@XmlSeeAlso({
    AllowedTokensType.class,
    ComponentType.class,
    BlockType.class,
    AllowedValuesType.class,
    AllowedTimesType.class,
    AbstractEncodingType.class,
    NilValuesType.class,
    AbstractSWEIdentifiableType.class
})
public class AbstractSWEType {

    private List<Object> extension;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public AbstractSWEType() {
        
    }
    
    public AbstractSWEType(final String id) {
        this.id = id;
    }
    
    public AbstractSWEType(final AbstractSWEType that) {
        this.id = that.id;
        if (that.extension != null) {
            this.extension = new ArrayList<Object>(that.extension);
        }
    }
    
    /**
     * Gets the value of the extension property.
     * 
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractSWEType) {
            final AbstractSWEType that = (AbstractSWEType) object;

            return Utilities.equals(this.extension, that.extension) &&
                   Utilities.equals(this.id,        that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.extension != null ? this.extension.hashCode() : 0);
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (id != null) {
            s.append("id=").append(id).append('\n');
        }
        if(extension != null) {
            s.append("extension:\n");
            for (Object ext : extension) {
                s.append(ext).append('\n');
            }
        }
        return s.toString();
    }
}
