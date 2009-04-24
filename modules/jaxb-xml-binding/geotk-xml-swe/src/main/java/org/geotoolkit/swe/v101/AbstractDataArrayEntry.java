/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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
package org.geotoolkit.swe.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractDataArrayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDataArrayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="elementCount">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/swe/1.0.1}Count"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDataArrayType", propOrder = {
    "elementCount"
})
@XmlSeeAlso({
    DataArrayEntry.class
})
public abstract class AbstractDataArrayEntry extends AbstractDataComponentEntry {

    @XmlElement(required = true)
    private AbstractDataArrayEntry.ElementCount elementCount;

    /**
     * Empty constructor used by JAXB.
     */
    AbstractDataArrayEntry() {
        
    }
    
    /**
     * Build a new Abstract Data array with only the value.
     */
    public AbstractDataArrayEntry(String id, int count) {
        super(id, null, false);
        this.elementCount = new ElementCount(count);
    }

    /**
     * Gets the value of the elementCount property.
     */
    public AbstractDataArrayEntry.ElementCount getElementCount() {
        return elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     */
    public void setElementCount(AbstractDataArrayEntry.ElementCount elementCount) {
        this.elementCount = elementCount;
    }

    /**
     * Sets the value of the elementCount property.
     */
    public void setElementCount(int count) {
        this.elementCount = new ElementCount(count);
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractDataArrayEntry && super.equals(object)) {
            final AbstractDataArrayEntry that = (AbstractDataArrayEntry) object;
            return Utilities.equals(this.elementCount, that.elementCount);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.elementCount != null ? this.elementCount.hashCode() : 0);
        return hash;
    }

    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence minOccurs="0">
     *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Count"/>
     *       &lt;/sequence>
     *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "count"
    })
    public static class ElementCount {

        @XmlElement(name = "Count")
        private Count count;
        @XmlAttribute
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        private Object ref;

        /**
         * Empty constructor used by JAXB.
         */
        ElementCount() {
        
        }
    
       /**
        * Build a new Element count with only the value.
        */
        public ElementCount(int value) {
            this.count = new Count(value);
        }
    
        /**
         * Gets the value of the count property.
         */
        public Count getCount() {
            return count;
        }

        /**
         * Gets the value of the ref property.
         */
        public Object getRef() {
            return ref;
        }

        /**
         * Verify that the object is identical to the specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof ElementCount) {
                final ElementCount that = (ElementCount) object;
                return Utilities.equals(this.count, that.count) &&
                       Utilities.equals(this.ref,   that.ref);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this.count != null ? this.count.hashCode() : 0);
            hash = 23 * hash + (this.ref != null ? this.ref.hashCode() : 0);
            return hash;
        }
    }
}
