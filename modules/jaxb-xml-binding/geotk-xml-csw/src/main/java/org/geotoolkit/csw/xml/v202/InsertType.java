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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * 
 * Submits one or more records to the catalogue. 
 * The representation is defined by the application profile. 
 * The handle attribute may be included to specify a local identifier for the action 
 * (it must be unique within the context of the transaction).
 *          
 * 
 * <p>Java class for InsertType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertType", propOrder = {
    "any"
})
public class InsertType {

    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String typeName;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String handle;

    /**
     * An empty constructor used by JAXB.
     */
    public InsertType() {}
    
    /**
     * Build a new Insert request with the specified objects to insert.
     */
    public InsertType(Object... objects) {
        any = new ArrayList<Object>();
        for (Object obj: objects) {
            any.add(obj);
        }
    }
    
    /**
     * Gets the value of the any property.
     * (unmodifiable)
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the typeName property.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets the value of the handle property.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InsertType) {
            final InsertType that = (InsertType) object;
            return Utilities.equals(this.any,      that.any)    &&
                   Utilities.equals(this.handle,   that.handle) &&
                   Utilities.equals(this.typeName, that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 47 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 47 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[InsertType]").append('\n');

        if (any != null) {
            s.append("any: ").append('\n');
            for (Object o : any) {
                s.append(o).append('\n');
            }
        }
        if (handle != null) {
            s.append("handle: ").append(handle).append('\n');
        }
        if (typeName != null) {
            s.append("typeName: ").append(typeName).append('\n');
        }
        return s.toString();
    }
}
