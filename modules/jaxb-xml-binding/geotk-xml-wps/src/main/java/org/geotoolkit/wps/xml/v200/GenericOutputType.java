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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of a process Output. 
 * 
 * 
 * In this use, the DescriptionType shall describe a process output.
 * 					
 * 
 * <p>Java class for GenericOutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenericOutputType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}GenericOutputType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericOutputType", propOrder = {
    "output"
})
public class GenericOutputType extends DescriptionType {

    @XmlElement(name = "Output")
    protected List<GenericOutputType> output;

    /**
     * Gets the value of the output property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GenericOutputType }
     * 
     * 
     */
    public List<GenericOutputType> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

}
