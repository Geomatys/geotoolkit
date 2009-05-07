/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
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

package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.util.Utilities;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleDataRecordType", propOrder = {
    "field"
})
public class SimpleDataRecordType extends AbstractDataRecordType implements SimpleDataRecord {

    private List<AnyScalarPropertyType> field;

    public SimpleDataRecordType() {

    }

    public SimpleDataRecordType(List<AnyScalarPropertyType> field) {
        this.field = field;
    }

    /**
     * Gets the value of the field property.
     */
    public List<AnyScalarPropertyType> getField() {
        if (field == null) {
            field = new ArrayList<AnyScalarPropertyType>();
        }
        return this.field;
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof SimpleDataRecordType && super.equals(object)) {
            final SimpleDataRecordType that = (SimpleDataRecordType) object;
            return Utilities.equals(this.field, that.field);
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
