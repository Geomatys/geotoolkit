/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}Filter_Capabilities"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "filterCapabilities"
})
public class FilterCapabilities implements org.geotoolkit.sos.xml.FilterCapabilities {

    @XmlElement(name = "Filter_Capabilities", namespace = "http://www.opengis.net/fes/2.0", required = true)
    private org.geotoolkit.ogc.xml.v200.FilterCapabilities filterCapabilities;

    /**
     * Gets the value of the filterCapabilities property.
     *
     * @return possible object is {@link net.opengis.fes._2.FilterCapabilities }
     *
     */
    public org.geotoolkit.ogc.xml.v200.FilterCapabilities getFilterCapabilities() {
        return filterCapabilities;
    }

    /**
     * Sets the value of the filterCapabilities property.
     *
     * @param value allowed object is
         *     {@link net.opengis.fes._2.FilterCapabilities }
     *
     */
    public void setFilterCapabilities(org.geotoolkit.ogc.xml.v200.FilterCapabilities value) {
        this.filterCapabilities = value;
    }
}
