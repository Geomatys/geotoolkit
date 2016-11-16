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
package org.geotoolkit.wps.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * List of the Inputs provided as part of the Execute Request. 
 * 
 * <p>Java class for DataInputsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataInputsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/1.0.0}InputType" maxOccurs="unbounded"/>
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
@XmlType(name = "DataInputsType", propOrder = {
    "input"
})
public class DataInputsType {

    @XmlElement(name = "Input", required = true)
    protected List<InputType> input;
    
    public DataInputsType() {
        
    }
    
    public DataInputsType(List<InputType> input) {
        this.input = input;
    }

    /**
     * Gets the value of the input property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link InputType }
     * 
     * 
     * @return 
     */
    public List<InputType> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[DataInputsType]\n");
        if (input != null) {
            sb.append("Inputs:\n");
            for (InputType out : input) {
                sb.append(out).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     *
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DataInputsType) {
            final DataInputsType that = (DataInputsType) object;
            return Objects.equals(this.input, that.input);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.input);
        return hash;
    }
}
