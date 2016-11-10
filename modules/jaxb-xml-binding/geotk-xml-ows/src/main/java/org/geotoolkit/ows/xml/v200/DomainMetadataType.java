/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.ows.xml.DomainMetadata;


/**
 * References metadata about a quantity, and provides a name
 *       for this metadata. (Informative: This element was simplified from the
 *       metaDataProperty element in GML 3.0.)
 * 
 * Human-readable name of the metadata described by
 *           associated referenced document.
 * 
 * <p>Java class for DomainMetadataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainMetadataType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute ref="{http://www.opengis.net/ows/2.0}reference"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainMetadataType", propOrder = {
    "value"
})
public class DomainMetadataType implements DomainMetadata {

    @XmlValue
    private String value;
    @XmlAttribute(namespace = "http://www.opengis.net/ows/2.0")
    @XmlSchemaType(name = "anyURI")
    private String reference;

    DomainMetadataType(){
        
    }
    
    public DomainMetadataType(final DomainMetadataType that){
        if (that != null) {
            this.reference = that.reference;
            this.value     = that.value;
        }
    }
    
    /**
     * Build a new Domaint metadata.
     */
    public DomainMetadataType(final String value, final String reference){
        this.reference = reference;
        this.value     = value;
        
    }
    
    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Override
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DomainMetadataType) {
        final DomainMetadataType that = (DomainMetadataType) object;
        return Objects.equals(this.reference, that.reference) &&
               Objects.equals(this.value,     that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 29 * hash + (this.reference != null ? this.reference.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "class: DomainMetadataType value=" + value + " reference=" + reference;
        
    }
}
