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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Definition of a format, encoding,  schema, and unit-of-measure for an output to be returned from a process. 
 * 
 * <p>Java class for OutputDefinitionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputDefinitionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/1.0.0}DocumentOutputDefinitionType" maxOccurs="unbounded"/>
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
@XmlType(name = "OutputDefinitionsType", propOrder = {
    "output"
})
public class OutputDefinitionsType {

    @XmlElement(name = "Output", required = true)
    protected List<DocumentOutputDefinitionType> output;

    public OutputDefinitionsType() {
        
    }
    
    public OutputDefinitionsType(List<DocumentOutputDefinitionType> output) {
        this.output = output;
    }
    /**
     * Gets the value of the output property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link DocumentOutputDefinitionType }
     * 
     * 
     */
    public List<DocumentOutputDefinitionType> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

}
