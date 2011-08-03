/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PortrayMapRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PortrayMapRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractRequestParametersType">
 *       &lt;sequence>
 *         &lt;element name="Output" type="{http://www.opengis.net/xls}OutputType" maxOccurs="unbounded"/>
 *         &lt;element name="Basemap" type="{http://www.opengis.net/xls}LayerType" minOccurs="0"/>
 *         &lt;element name="Overlay" type="{http://www.opengis.net/xls}OverlayType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PortrayMapRequestType", propOrder = {
    "output",
    "basemap",
    "overlay"
})
public class PortrayMapRequestType extends AbstractRequestParametersType {

    @XmlElement(name = "Output", required = true)
    private List<OutputType> output;
    @XmlElement(name = "Basemap")
    private LayerType basemap;
    @XmlElement(name = "Overlay")
    private List<OverlayType> overlay;

    /**
     * Gets the value of the output property.
     * 
     */
    public List<OutputType> getOutput() {
        if (output == null) {
            output = new ArrayList<OutputType>();
        }
        return this.output;
    }

    /**
     * Gets the value of the basemap property.
     * 
     * @return
     *     possible object is
     *     {@link LayerType }
     *     
     */
    public LayerType getBasemap() {
        return basemap;
    }

    /**
     * Sets the value of the basemap property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayerType }
     *     
     */
    public void setBasemap(LayerType value) {
        this.basemap = value;
    }

    /**
     * Gets the value of the overlay property.
     */
    public List<OverlayType> getOverlay() {
        if (overlay == null) {
            overlay = new ArrayList<OverlayType>();
        }
        return this.overlay;
    }

}
