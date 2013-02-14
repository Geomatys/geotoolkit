/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for DataRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="field" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractDataComponentPropertyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *               &lt;/extension>
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
@XmlType(name = "DataRecordType", propOrder = {
    "field"
})
public class DataRecordType extends AbstractDataComponentType implements DataRecord {

    @XmlElement(required = true)
    private List<Field> field;

    public DataRecordType() {
        
    }
    
    public DataRecordType(final String id, final String definition, final boolean fixed, final List<Field> fields) {
        super(id, definition, !fixed);
        this.field = fields;
    }
    
    /**
     * Gets the value of the field property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DataRecordType.Field }
     * 
     */
    @Override
    public List<Field> getField() {
        if (field == null) {
            field = new ArrayList<Field>();
        }
        return this.field;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (field != null) {
            sb.append("fields:\n");
            for (Field f : field) {
                sb.append(f).append('\n');
            }
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataRecordType && super.equals(object)) {
            final DataRecordType that = (DataRecordType) object;

            return Utilities.equals(this.field,    that.field);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + super.hashCode();
        hash = 71 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }
}
