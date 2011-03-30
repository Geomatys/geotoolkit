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
 * <p>Java class for RegistryObjectListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegistryObjectListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ObjectRef" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "RegistryObjectListType", propOrder = {
    "registryObject",
    "objectRef"
})
public class RegistryObjectListType {

    @XmlElement(name = "RegistryObject")
    private List<RegistryObjectType> registryObject;
    @XmlElement(name = "ObjectRef")
    private List<ObjectRefType> objectRef;

    /**
     * Gets the value of the registryObject property.
     */
    public List<RegistryObjectType> getRegistryObject() {
        if (registryObject == null) {
            registryObject = new ArrayList<RegistryObjectType>();
        }
        return this.registryObject;
    }
    
    /**
     * Sets the value of the registryObject property.
     */
    public void setRegistryObject(final RegistryObjectType registryObject) {
        if (this.registryObject == null) {
            this.registryObject = new ArrayList<RegistryObjectType>();
        }
        this.registryObject.add(registryObject);
    }
    
    /**
     * Sets the value of the registryObject property.
     */
    public void setRegistryObject(final List<RegistryObjectType> registryObject) {
        this.registryObject = registryObject;
    }

    /**
     * Gets the value of the objectRef property.
     * 
     */
    public List<ObjectRefType> getObjectRef() {
        if (objectRef == null) {
            objectRef = new ArrayList<ObjectRefType>();
        }
        return this.objectRef;
    }
    
    /**
     * Sets the value of the objectRef property.
     * 
     */
    public void setObjectRef(final List<ObjectRefType> objectRef) {
        this.objectRef = objectRef;
    }
    
     /**
     * Sets the value of the objectRef property.
     * 
     */
    public void setObjectRef(final ObjectRefType objectRef) {
        if (this.objectRef == null) {
            this.objectRef = new ArrayList<ObjectRefType>();
        }
        this.objectRef.add(objectRef);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[RegistryObjectListType]\n");
        if (objectRef != null) {
            sb.append("objectRef:\n");
            for (ObjectRefType o : objectRef) {
                sb.append(o).append('\n');
            }
        }
        if (registryObject != null) {
            sb.append("registryObject:\n");
            for (RegistryObjectType o : registryObject) {
                sb.append(o).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RegistryObjectListType) {
            final RegistryObjectListType that = (RegistryObjectListType) obj;
            return Utilities.equals(this.objectRef,      that.objectRef) &&
                   Utilities.equals(this.registryObject, that.registryObject) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.registryObject != null ? this.registryObject.hashCode() : 0);
        hash = 97 * hash + (this.objectRef != null ? this.objectRef.hashCode() : 0);
        return hash;
    }
}
