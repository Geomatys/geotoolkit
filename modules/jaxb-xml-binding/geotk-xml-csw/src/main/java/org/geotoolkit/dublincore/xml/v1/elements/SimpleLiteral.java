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
package org.geotoolkit.dublincore.xml.v1.elements;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.dublincore.xml.AbstractSimpleLiteral;
import org.geotoolkit.util.Utilities;


/**
 * This is the default type for all of the DC elements. 
 * It defines a complexType SimpleLiteral which permits mixed content but disallows child elements by use of minOcccurs/maxOccurs. 
 * However, this complexType does permit the derivation of other types which would permit child elements. 
 * The scheme attribute may be used as a qualifier to reference an encoding scheme that describes the value domain for a given property.
 *       
 * 
 * <p>Java class for SimpleLiteral complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SimpleLiteral">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="scheme" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleLiteral", propOrder = {
    "content"
})
public class SimpleLiteral extends AbstractSimpleLiteral {

    @XmlMixed
    private List<String> content;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String scheme;
    
     /**
     * An empty constructor used by JAXB
     */
    public SimpleLiteral() {
        
    }
    
    /**
     * Build a new Simple literal
     */
    public SimpleLiteral(final String content) {
        this.content = new ArrayList<String>();
        this.content.add(content);
    }
    
    /**
     * Build a new Simple literal
     */
    public SimpleLiteral(final String scheme, final String content) {
        this.scheme  = scheme;
        this.content = new ArrayList<String>();
        this.content.add(content);
    }
    
    /**
     * Build a new Simple literal
     */
    public SimpleLiteral(final String scheme, final List<String> content) {
        this.scheme  = scheme;
        this.content = content;
    }
    

    /**
     * This is the default type for all of the DC elements. 
     * It defines a complexType SimpleLiteral which permits mixed content but disallows child elements by use of minOcccurs/maxOccurs. 
     * However, this complexType does permit the derivation of other types which would permit child elements. 
     * The scheme attribute may be used as a qualifier to reference an encoding scheme that describes the value domain for a given property.
     *
     * Gets the value of the content property.
     * 
     */
    public List<String> getContent() {
        if (content == null) {
            content = new ArrayList<String>();
        }
        return this.content;
    }

    /**
     * Gets the value of the scheme property.
     * 
    */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the value of the scheme property.
     * 
     */
    public void setScheme(final String value) {
        this.scheme = value;
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SimpleLiteral) {
            final SimpleLiteral that = (SimpleLiteral) object;
            return Utilities.equals(this.content,  that.content)   &&
                   Utilities.equals(this.scheme ,  that.scheme);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 61 * hash + (this.scheme != null ? this.scheme.hashCode() : 0);
        return hash;
    }
    
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        
        if (scheme != null) {
            s.append(scheme).append(':');
        }
        if (content != null && !content.isEmpty()) {
            for (String c : content) {
                s.append(c).append('\n');
            }
            s.deleteCharAt(s.length() -1);
        }
        return s.toString();
    }

}
