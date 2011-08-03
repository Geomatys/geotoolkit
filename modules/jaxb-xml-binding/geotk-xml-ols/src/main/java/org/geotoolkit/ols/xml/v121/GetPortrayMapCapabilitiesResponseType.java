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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPortrayMapCapabilitiesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPortrayMapCapabilitiesResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element name="AvailableSRS" type="{http://www.opengis.net/xls}AvailableSRSType"/>
 *         &lt;element name="AvailableLayers" type="{http://www.opengis.net/xls}AvailableLayersType"/>
 *         &lt;element name="AvailableFormats" type="{http://www.opengis.net/xls}AvailableFormatsType"/>
 *         &lt;element name="AvailableStyles" type="{http://www.opengis.net/xls}AvailableStylesType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPortrayMapCapabilitiesResponseType", propOrder = {
    "availableSRS",
    "availableLayers",
    "availableFormats",
    "availableStyles"
})
public class GetPortrayMapCapabilitiesResponseType extends AbstractResponseParametersType {

    @XmlElement(name = "AvailableSRS", required = true)
    private AvailableSRSType availableSRS;
    @XmlElement(name = "AvailableLayers", required = true)
    private AvailableLayersType availableLayers;
    @XmlElement(name = "AvailableFormats", required = true)
    private AvailableFormatsType availableFormats;
    @XmlElement(name = "AvailableStyles", required = true)
    private AvailableStylesType availableStyles;

    /**
     * Gets the value of the availableSRS property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableSRSType }
     *     
     */
    public AvailableSRSType getAvailableSRS() {
        return availableSRS;
    }

    /**
     * Sets the value of the availableSRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableSRSType }
     *     
     */
    public void setAvailableSRS(AvailableSRSType value) {
        this.availableSRS = value;
    }

    /**
     * Gets the value of the availableLayers property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableLayersType }
     *     
     */
    public AvailableLayersType getAvailableLayers() {
        return availableLayers;
    }

    /**
     * Sets the value of the availableLayers property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableLayersType }
     *     
     */
    public void setAvailableLayers(AvailableLayersType value) {
        this.availableLayers = value;
    }

    /**
     * Gets the value of the availableFormats property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableFormatsType }
     *     
     */
    public AvailableFormatsType getAvailableFormats() {
        return availableFormats;
    }

    /**
     * Sets the value of the availableFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableFormatsType }
     *     
     */
    public void setAvailableFormats(AvailableFormatsType value) {
        this.availableFormats = value;
    }

    /**
     * Gets the value of the availableStyles property.
     * 
     * @return
     *     possible object is
     *     {@link AvailableStylesType }
     *     
     */
    public AvailableStylesType getAvailableStyles() {
        return availableStyles;
    }

    /**
     * Sets the value of the availableStyles property.
     * 
     * @param value
     *     allowed object is
     *     {@link AvailableStylesType }
     *     
     */
    public void setAvailableStyles(AvailableStylesType value) {
        this.availableStyles = value;
    }

}
