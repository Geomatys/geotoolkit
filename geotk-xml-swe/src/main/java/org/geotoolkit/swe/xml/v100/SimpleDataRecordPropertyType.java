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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.SimpleDataRecordProperty;


/**
 * SimpleDataRecord is a data-type so usually appears "by value" rather than by reference.
 *
 * <p>Java class for SimpleDataRecordPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SimpleDataRecordPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}SimpleDataRecord"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleDataRecordPropertyType", propOrder = {
    "simpleDataRecord"
})
public class SimpleDataRecordPropertyType implements SimpleDataRecordProperty {

    @XmlElement(name = "SimpleDataRecord", required = true)
    private SimpleDataRecordType simpleDataRecord;

    public SimpleDataRecordPropertyType() {

    }

    public SimpleDataRecordPropertyType(final SimpleDataRecordProperty sd) {
       if (sd != null && sd.getSimpleDataRecord() != null) {
           this.simpleDataRecord = new SimpleDataRecordType(sd.getSimpleDataRecord());
       }
    }

    /**
     * Gets the value of the simpleDataRecord property.
     */
    public SimpleDataRecordType getSimpleDataRecord() {
        return simpleDataRecord;
    }

    /**
     * Sets the value of the simpleDataRecord property.
     */
    public void setSimpleDataRecord(final SimpleDataRecordType value) {
        this.simpleDataRecord = value;
    }

}
