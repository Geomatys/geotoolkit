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
package org.geotoolkit.wcs.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Asks for the GetCoverage response to be expressed in a particular CRS and encoded in a particular format. Can also ask for the response coverage to be stored remotely from the client at a URL, instead of being returned in the operation response. 
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
 *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}GridCRS" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="format" type="{http://www.opengis.net/ows/1.1}MimeType" />
 *       &lt;attribute name="store" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputType", propOrder = {
    "gridCRS"
})
public class OutputType {

    @XmlElement(name = "GridCRS")
    private GridCrsType gridCRS;
    @XmlAttribute
    private String format;
    @XmlAttribute
    private Boolean store;

    /**
     * Empty constructor used by JAXB
     */
    OutputType(){
        
    }
    
    /**
     * Build a new Output for a getCoevrage request.
     */
    public OutputType(final GridCrsType gridCRS, final String format){
        this.store   = false;
        this.gridCRS = gridCRS;
        this.format  = format;
    }
    
    /**
     * Optional definition of the GridCRS in which the GetCoverage response shall be expressed.
     * When this GridCRS is not included,
     * the output shall be in the ImageCRS of the stored image, 
     * as identified in the CoverageDescription. 
     */
    public GridCrsType getGridCRS() {
        return gridCRS;
    }

    /**
     * Gets the value of the format property.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Gets the value of the store property.
     */
    public boolean isStore() {
        if (store == null) {
            return false;
        } else {
            return store;
        }
    }
}
