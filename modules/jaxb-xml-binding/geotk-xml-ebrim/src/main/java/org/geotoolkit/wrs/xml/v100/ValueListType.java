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
package org.geotoolkit.wrs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Allows complex slot values.
 * 
 * <p>Java class for ValueListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueListType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ValueListType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/cat/wrs/1.0}AnyValue"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueListType", propOrder = {
    "anyValue"
})
public class ValueListType extends org.geotoolkit.ebrim.xml.v300.ValueListType {

    @XmlElement(name = "AnyValue")
    private List<AnyValueType> anyValue;

    /**
     * Gets the value of the anyValue property.
     */
    public List<AnyValueType> getAnyValue() {
        if (anyValue == null) {
            anyValue = new ArrayList<AnyValueType>();
        }
        return this.anyValue;
    }
    
    /**
     * Sets the value of the anyValue property.
     */
    public void setAnyValue(AnyValueType anyValue) {
        if (this.anyValue == null) {
            this.anyValue = new ArrayList<AnyValueType>();
        }
        this.anyValue.add(anyValue);
    }
    
    /**
     * Sets the value of the anyValue property.
     */
    public void setAnyValue(List<AnyValueType> anyValue) {
        this.anyValue = anyValue;
    }

}
