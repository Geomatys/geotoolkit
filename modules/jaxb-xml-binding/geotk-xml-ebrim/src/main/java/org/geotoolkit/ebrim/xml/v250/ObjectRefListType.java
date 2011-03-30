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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ObjectRefListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObjectRefListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ObjectRef"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent> 
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectRefListType", propOrder = {
    "objectRef"
})
public class ObjectRefListType {

    @XmlElement(name = "ObjectRef")
    private List<ObjectRefType> objectRef;

    /**
     * Gets the value of the objectRef property.
     */
    public List<ObjectRefType> getObjectRef() {
        if (objectRef == null) {
            objectRef = new ArrayList<ObjectRefType>();
        }
        return this.objectRef;
    }
    
    /**
     * Sets the value of the objectRef property.
     */
    public void setObjectRef(final ObjectRefType ref) {
        if (objectRef == null) {
            objectRef = new ArrayList<ObjectRefType>();
        }
        this.objectRef.add(ref);
    }
    
    /**
     * Sets the value of the objectRef property.
     */
    public void setObjectRef(final List<ObjectRefType> ref) {
        this.objectRef = ref;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ObjectRefListType]\n");
        if (objectRef != null) {
            sb.append("objectRef:");
            for (ObjectRefType ob : objectRef) {
                sb.append(ob).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ObjectRefListType) {
            final ObjectRefListType that = (ObjectRefListType) obj;
            return Utilities.equals(this.objectRef, that.objectRef);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.objectRef != null ? this.objectRef.hashCode() : 0);
        return hash;
    }
}
