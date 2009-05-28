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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.DataArrayProperty;
import org.geotoolkit.util.Utilities;


/**
 * DataArray is a data-type so usually appears "by value" rather than by reference.
 * 
 * <p>Java class for DataArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}DataArray"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataArrayPropertyType", propOrder = {
    "dataArray"
})
public class DataArrayPropertyType implements DataArrayProperty {

    @XmlElementRef(name = "DataArray", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends DataArrayEntry> dataArray;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    DataArrayPropertyType() {
        
    }
    
    /**
     * Build a new Array Property type.
     */
    public DataArrayPropertyType(DataArrayEntry dataArray) {
        this.dataArray = factory.createDataArray(dataArray);
    }
    
    /**
     * Gets the value of the dataArray property.
     */
    public DataArrayEntry getDataArray() {
        if (dataArray != null) {
            return dataArray.getValue(); 
        }
        return null;
    }
    
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataArrayPropertyType) {
            final DataArrayPropertyType that = (DataArrayPropertyType) object;
            if (this.dataArray == null && that.dataArray == null) {
                return true;
            } else if (this.dataArray != null && that.dataArray != null) {
                return Utilities.equals(this.dataArray.getValue(), that.dataArray.getValue());
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.dataArray != null ? this.dataArray.hashCode() : 0);
        return hash;
    }
    
    /**
     * Return a string representing the dataArray.
     */
    @Override
    public String toString() {
        StringBuilder s    = new StringBuilder();
        char lineSeparator = '\n';
        if (dataArray != null)  {
            s.append("[DataArrayPropertyType] array:");
            s.append(dataArray.getValue()).append(lineSeparator);
        }
        return s.toString();
    }
}
