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
package org.geotoolkit.swe.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractElementCount;

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
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Count"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
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

    public ElementCount(final int value) {
        this.count = new Count();
    }

    /**
     * Gets the value of the count property.
     */
    @Override
    public Count getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     */
    public void setCount(final Count value) {
        this.count = value;
    }

    /**
     * Gets the value of the ref property.
     */
    @Override
    public Object getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     */
    public void setRef(final Object value) {
        this.ref = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ElementCount) {
            final ElementCount  that = (ElementCount) object;
            return Objects.equals(this.count, that.count) &&
                   Objects.equals(this.ref,   that.ref);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 61 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[ElementCount]");
        if (count != null) {
            s.append("count:").append(count).append('\n');
        }
        if (ref != null) {
            s.append("ref:").append(ref).append('\n');
        }
        return s.toString();
    }
}
