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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractSWEIdentifiableType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSWEIdentifiableType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEType">
 *       &lt;sequence>
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSWEIdentifiableType", propOrder = {
    "identifier",
    "label",
    "description"
})
@XmlSeeAlso({
    DataStreamType.class,
    AbstractDataComponentType.class
})
public class AbstractSWEIdentifiableType extends AbstractSWEType {

    @XmlSchemaType(name = "anyURI")
    private String identifier;
    private String label;
    private String description;

    public AbstractSWEIdentifiableType() {
        
    }
    
    public AbstractSWEIdentifiableType(final String id) {
        super(id);
    }
    
    public AbstractSWEIdentifiableType(final AbstractSWEIdentifiableType that) {
        super(that);
        this.description = that.description;
        this.identifier  = that.identifier;
        this.label       = that.label;
    }
    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public void setName(final String name) {
        //do nothing
    }
    
    public String getName() {
        return null;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractSWEIdentifiableType && super.equals(object)) {
            final AbstractSWEIdentifiableType that = (AbstractSWEIdentifiableType) object;

            return Utilities.equals(this.description, that.description) &&
                   Utilities.equals(this.label,       that.label) &&
                   Utilities.equals(this.identifier,  that.identifier);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 47 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 47 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (description != null) {
            s.append("description=").append(description).append('\n');
        }
        if (label != null) {
            s.append("label=").append(label).append('\n');
        }
        if (identifier != null) {
            s.append("identifier=").append(identifier).append('\n');
        }
        return s.toString();
    }
}
