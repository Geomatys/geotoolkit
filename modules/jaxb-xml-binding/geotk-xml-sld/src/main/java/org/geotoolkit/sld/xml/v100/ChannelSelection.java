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
package org.geotoolkit.sld.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{http://www.opengis.net/sld}RedChannel"/>
 *           &lt;element ref="{http://www.opengis.net/sld}GreenChannel"/>
 *           &lt;element ref="{http://www.opengis.net/sld}BlueChannel"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.opengis.net/sld}GrayChannel"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "redChannel",
    "greenChannel",
    "blueChannel",
    "grayChannel"
})
@XmlRootElement(name = "ChannelSelection")
public class ChannelSelection {

    @XmlElement(name = "RedChannel")
    protected SelectedChannelType redChannel;
    @XmlElement(name = "GreenChannel")
    protected SelectedChannelType greenChannel;
    @XmlElement(name = "BlueChannel")
    protected SelectedChannelType blueChannel;
    @XmlElement(name = "GrayChannel")
    protected SelectedChannelType grayChannel;

    /**
     * Gets the value of the redChannel property.
     * 
     * @return
     *     possible object is
     *     {@link SelectedChannelType }
     *     
     */
    public SelectedChannelType getRedChannel() {
        return redChannel;
    }

    /**
     * Sets the value of the redChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectedChannelType }
     *     
     */
    public void setRedChannel(SelectedChannelType value) {
        this.redChannel = value;
    }

    /**
     * Gets the value of the greenChannel property.
     * 
     * @return
     *     possible object is
     *     {@link SelectedChannelType }
     *     
     */
    public SelectedChannelType getGreenChannel() {
        return greenChannel;
    }

    /**
     * Sets the value of the greenChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectedChannelType }
     *     
     */
    public void setGreenChannel(SelectedChannelType value) {
        this.greenChannel = value;
    }

    /**
     * Gets the value of the blueChannel property.
     * 
     * @return
     *     possible object is
     *     {@link SelectedChannelType }
     *     
     */
    public SelectedChannelType getBlueChannel() {
        return blueChannel;
    }

    /**
     * Sets the value of the blueChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectedChannelType }
     *     
     */
    public void setBlueChannel(SelectedChannelType value) {
        this.blueChannel = value;
    }

    /**
     * Gets the value of the grayChannel property.
     * 
     * @return
     *     possible object is
     *     {@link SelectedChannelType }
     *     
     */
    public SelectedChannelType getGrayChannel() {
        return grayChannel;
    }

    /**
     * Sets the value of the grayChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectedChannelType }
     *     
     */
    public void setGrayChannel(SelectedChannelType value) {
        this.grayChannel = value;
    }

}
