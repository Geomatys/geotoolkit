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
package org.geotoolkit.swe.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.DataRecord;


/**
 * <p>Java class for DataRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataRecordType">
 *       &lt;sequence>
 *         &lt;element name="field" type="{http://www.opengis.net/swe/1.0.1}DataComponentPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRecordType", propOrder = {
    "field"
})
public class DataRecordType extends AbstractDataRecordType implements DataRecord {

    private List<DataComponentPropertyType> field;

    public DataRecordType() {

    }

    public DataRecordType(final DataRecord dr) {
        super(dr);
        if (dr != null && dr.getField() != null) {
            this.field = new ArrayList<DataComponentPropertyType>();
            for (DataComponentProperty d : dr.getField()) {
                this.field.add(new DataComponentPropertyType(d));
            }
        }

    }

    public DataRecordType(final String definition, final List<DataComponentPropertyType> field) {
        super(definition);
        this.field = field;
    }
    
    /**
     * Gets the value of the field property.
     */
    public List<DataComponentPropertyType> getField() {
        if (field == null) {
            field = new ArrayList<DataComponentPropertyType>();
        }
        return this.field;
    }

}
