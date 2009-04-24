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
package org.geotoolkit.wcs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.CodeType;


/**
 * Asks for the GetCoverage response to be expressed in a particular Coordinate Reference System (crs) and encoded in a particular format. 
 * 
 * <p>Java class for OutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crs" type="{http://www.opengis.net/gml}CodeType" minOccurs="0"/>
 *         &lt;element name="format" type="{http://www.opengis.net/gml}CodeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputType", propOrder = {
    "crs",
    "format"
})
public class OutputType {

    private CodeType crs;
    @XmlElement(required = true)
    private CodeType format;

    /**
     * An empty constructor used by JAXB
     */
    OutputType(){
        
    }
    
    /**
     * Build a new Output Type with the specified format and response CRS
     */
    public OutputType(String format, String crs) {
        this.format = new CodeType(format);
        this.crs = new CodeType(crs);
    }
    
    /**
     * Gets the value of the crs property.
     */
    public CodeType getCrs() {
        return crs;
    }

    /**
     * Gets the value of the format property.
     */
    public CodeType getFormat() {
        return format;
    }
}
