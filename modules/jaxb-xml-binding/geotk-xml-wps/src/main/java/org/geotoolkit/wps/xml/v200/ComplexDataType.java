/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.ComplexDataTypeDescription;
import org.w3c.dom.Element;


/**
 * <p>Java class for ComplexDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ComplexDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescriptionType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComplexDataType", propOrder = {
    "any"
})
public class ComplexDataType extends DataDescriptionType implements org.geotoolkit.wps.xml.ComplexDataType, ComplexDataTypeDescription {

    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public ComplexDataType() {
        
    }
    
    public ComplexDataType(List<Format> format) {
        super(format);
    }
    
    /**
     * Gets the value of the any property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     */
    @Override
    public List<Object> getContent() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

}
