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
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.ListOfValues;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for ListOfValuesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfValuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
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
@XmlType(name = "ListOfValuesType", propOrder = {
    "value"
})
public class ListOfValuesType implements ListOfValues {

    @XmlElement(name = "Value", required = true)
    private List<String> value;
    
    /**
     * An empty constructor used by JAXB 
     */
     public ListOfValuesType(){
         
     }
     
     /**
      * Build a new List of values
      */
     public ListOfValuesType(List<String> values){
         value  = values;
     }
     
    /**
     * Gets the value of the value property.
     * (unmodifiable)
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return Collections.unmodifiableList(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ListOfValuesType]").append('\n');
        if (value != null) {
            sb.append("values:").append('\n');
            for (String v : value) {
                sb.append(v).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ListOfValuesType) {
            final ListOfValuesType that = (ListOfValuesType) object;

            return  Utilities.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

}
