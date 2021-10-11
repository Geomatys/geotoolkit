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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.csw.xml.Update;


/**
 * Update statements may replace an entire record or only update part of a record:
 *
 * 1) To replace an existing record, include a new instance of the record;
 *
 * 2) To update selected properties of an existing record, include a set of RecordProperty elements.
 * The scope of the update statement is determined by the Constraint element.
 *
 * The 'handle' is a local identifier for the action.
 *
 *
 * <p>Java class for UpdateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UpdateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;any/>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}RecordProperty" maxOccurs="unbounded"/>
 *             &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}Constraint"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
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
@XmlType(name = "UpdateType", propOrder = {
    "any",
    "recordProperty",
    "constraint"
})
public class UpdateType implements Update {

    @XmlAnyElement(lax = true)
    private Object any;
    @XmlElement(name = "RecordProperty")
    private List<RecordPropertyType> recordProperty;
    @XmlElement(name = "Constraint")
    private QueryConstraintType constraint;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String handle;

    public UpdateType() {

    }

    public UpdateType(final Object any, final QueryConstraintType query) {
        this.any        = any;
        this.constraint = query;
    }

    public UpdateType(final List<RecordPropertyType> recordProperty, final QueryConstraintType query) {
        this.recordProperty = recordProperty;
        this.constraint     = query;
    }
    /**
     * Gets the value of the any property.
     */
    @Override
    public Object getAny() {
        return any;
    }

    public void setAny(final Object any) {
        this.any = any;
    }

    /**
     * Gets the value of the recordProperty property.
     * (unmodifiable)
     */
    @Override
    public List<RecordPropertyType> getRecordProperty() {
        if (recordProperty == null) {
            recordProperty = new ArrayList<>();
        }
        return recordProperty;
    }

    @Override
    public Map<String, Object> getRecordPropertyMap() {
        final Map<String, Object> result = new HashMap<>();
        if (recordProperty != null) {
            for (RecordPropertyType rp : recordProperty) {
                result.put(rp.getName(), rp.getValue());
            }
        }
        return result;
    }

    public void setRecordProperty(final List<RecordPropertyType> recordProperty) {
        this.recordProperty = recordProperty;
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
        if (object instanceof UpdateType) {
            final UpdateType that = (UpdateType) object;
            return Objects.equals(this.constraint,     that.constraint) &&
                   Objects.equals(this.handle,         that.handle)     &&
                   Objects.equals(this.any,            that.any)        &&
                   Objects.equals(this.recordProperty, that.recordProperty);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 97 * hash + (this.recordProperty != null ? this.recordProperty.hashCode() : 0);
        hash = 97 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 97 * hash + (this.handle != null ? this.handle.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[UpdateType]").append('\n');

        if (constraint != null) {
            s.append("constraint: ").append(constraint).append('\n');
        }
        if (handle != null) {
            s.append("handle: ").append(handle).append('\n');
        }
        if (any != null) {
            s.append("any: ").append(any).append('\n');
        }
        if (recordProperty != null) {
            s.append("recordProperties: ").append('\n');
            for (RecordPropertyType rp : recordProperty) {
                s.append(rp).append('\n');
            }
        }
        return s.toString();
    }
}
