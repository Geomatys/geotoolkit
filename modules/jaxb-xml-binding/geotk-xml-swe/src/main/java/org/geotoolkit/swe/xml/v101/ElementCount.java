/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractElementCount;
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
public class ElementCount implements AbstractElementCount {

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

    
    public ElementCount(final AbstractElementCount value) {
        if (value != null) {
            if (value.getCount() != null) {
                this.count = new Count(value.getCount());
            }
            this.ref = value.getRef();
        }
    }

    /**
     * Build a new Element count with only the value.
     */
    public ElementCount(final int value) {
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
            return Utilities.equals(this.count, that.count)
                    && Utilities.equals(this.ref, that.ref);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ElementCount]\n");
        if (count != null) {
            sb.append("count:").append(count).append('\n');
        }
        if (ref != null) {
            sb.append("ref:").append(ref).append('\n');
        }
        return sb.toString();
    }
}
