/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.thw.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getAvailableThesauri complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAvailableThesauri">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAvailableThesauri")
@XmlRootElement(name = "GetAvailableThesauri", namespace = "http://ws.geotk.org/")
public class GetAvailableThesauri {

    private String outputFormat;

    private Boolean showDeactivated;
    
    public GetAvailableThesauri() {
        
    }
    
    public GetAvailableThesauri(final String outputFormat,final Boolean showDeactivated) {
        this.outputFormat = outputFormat;
        this.showDeactivated = showDeactivated;
    }
    
    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the showDeactivated
     */
    public Boolean getShowDeactivated() {
        if (showDeactivated == null) {
            return false;
        }
        return showDeactivated;
    }

    /**
     * @param showDeactivated the showDeactivated to set
     */
    public void setShowDeactivated(Boolean showDeactivated) {
        this.showDeactivated = showDeactivated;
    }
}
