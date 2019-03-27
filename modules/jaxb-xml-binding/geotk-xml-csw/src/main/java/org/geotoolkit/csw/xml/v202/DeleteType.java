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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.Delete;


/**
 *  Deletes one or more catalogue items that satisfy some set of conditions.
 *
 *
 * <p>Java class for DeleteType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DeleteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}Constraint"/>
 *       &lt;/sequence>
 *       &lt;attribute name="typeName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="handle" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteType", propOrder = {
    "constraint"
})
public class DeleteType implements Delete {

    @XmlElement(name = "Constraint", required = true)
    private QueryConstraintType constraint;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String typeName;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String handle;

    public DeleteType() {

    }

    public DeleteType(final String typeName, final QueryConstraintType constraint) {
        this.typeName   = typeName;
        this.constraint = constraint;
    }

    /**
     * Gets the value of the constraint property.
     */
    @Override
    public QueryConstraintType getConstraint() {
        return constraint;
    }

    public void setConstraint(final QueryConstraintType constraint) {
        this.constraint = constraint;
    }

    /**
     * Gets the value of the typeName property.
     */
    @Override
    public QName getTypeName() {
        if (typeName != null) {
            return new QName(typeName);
        }
        return null;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }


    /**
     * Gets the value of the handle property.
     */
    @Override
    public String getHandle() {
        return handle;
    }

    public void setHandle(final String handle) {
        this.handle = handle;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DeleteType) {
            final DeleteType that = (DeleteType) object;
            return Objects.equals(this.constraint, that.constraint) &&
                   Objects.equals(this.handle,     that.handle)     &&
                   Objects.equals(this.typeName,   that.typeName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 19 * hash + (this.typeName != null ? this.typeName.hashCode() : 0);
        hash = 19 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[DeleteType]").append('\n');

        if (constraint != null) {
            s.append("constraint: ").append(constraint).append('\n');
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
