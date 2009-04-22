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
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


/**
 * References metadata about a quantity, and provides a name for this metadata. (Informative: This element was simplified from the metaDataProperty element in GML 3.0.) 
 * 
 * Human-readable name of the metadata described by associated referenced document. 
 * 
 * <p>Java class for DomainMetadataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DomainMetadataType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute ref="{http://www.opengis.net/ows/1.1}reference"/>
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
public class DomainMetadataType {

    @XmlValue
    private String value;
    @XmlAttribute(namespace = "http://www.opengis.net/ows/1.1")
    @XmlSchemaType(name = "anyURI")
    private String reference;

    /**
     * Empty constructor used by JAXB.
     */
    DomainMetadataType(){
        
    }
    
    /**
     * Build a new Domaint metadata.
     */
    public DomainMetadataType(String value, String reference){
        this.reference = reference;
        this.value     = value;
        
    }
    
    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the reference property.
     */
    public String getReference() {
        return reference;
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
        return Utilities.equals(this.reference, that.reference) &&
               Utilities.equals(this.value,     that.value);
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
