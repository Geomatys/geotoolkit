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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.apache.sis.util.ComparisonMode;


/**
 * <p>Java class for SimpleDataRecordType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SimpleDataRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataRecordType">
 *       &lt;sequence>
 *         &lt;element name="field" type="{http://www.opengis.net/swe/1.0}AnyScalarPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleDataRecordType", propOrder = {
    "field"
})
public class SimpleDataRecordType extends AbstractDataRecordType implements SimpleDataRecord {

    /**
     * The datablock identifier containing this data record. still needed ?
     */
    @XmlTransient
    private String blockId;

    private List<AnyScalarPropertyType> field;

    public SimpleDataRecordType() {

    }

    public SimpleDataRecordType(final String blockId, final String id, final String definition, final boolean fixed, final List<AnyScalarPropertyType> field) {
        super(id, definition, fixed);
        this.blockId = blockId;
        this.field = field;
    }

    public SimpleDataRecordType(final SimpleDataRecord record) {
        super(record);
        if (record != null && record.getField() != null) {
            this.field = new ArrayList<AnyScalarPropertyType>();
            for (AnyScalar a : record.getField()) {
                this.field.add(new AnyScalarPropertyType(a));
            }
        }

    }

    /**
     * Gets the value of the field property.
     */
    @Override
    public List<AnyScalarPropertyType> getField() {
        if (field == null) {
            field = new ArrayList<AnyScalarPropertyType>();
        }
        return this.field;
    }

    public void setField(final List<AnyScalarPropertyType> field) {
        this.field = field;
    }

    public void setField(final AnyScalarPropertyType field) {
        if (this.field == null) {
            this.field = new ArrayList<AnyScalarPropertyType>();
        }
        this.field.add(field);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (field != null) {
            s.append("fields:").append('\n');
            for (AnyScalarPropertyType f : field) {
                s.append("field:").append(f).append('\n');
            }
        }
        return s.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SimpleDataRecordType && super.equals(object, mode)) {
            final SimpleDataRecordType that = (SimpleDataRecordType) object;
            return Objects.equals(this.field, that.field);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }

}
