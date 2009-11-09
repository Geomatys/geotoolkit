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
package org.geotoolkit.se.xml.vext;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.se.xml.v110.ThreshholdsBelongToType;

/**
 * <p>Java class for RasterSymbolizerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RasterSymbolizerType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}SymbolizerType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Geometry" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Opacity" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ChannelSelection" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}OverlapBehavior" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ColorMap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ContrastEnhancement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ShadedRelief" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}ImageOutline" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PatternSymbolizerType", propOrder = {
    "channel",
    "range"
})
public class PatternSymbolizerType extends SymbolizerType {

    @XmlElement(name = "Channel")
    protected ParameterValueType channel;
    @XmlElementRef(name = "Range", namespace = "http://www.opengis.net/se", type = JAXBElement.class)
    protected List<JAXBElement<RangeType>> range;
    @XmlAttribute
    protected ThreshholdsBelongToType threshholdsBelongTo;

    /**
     * Gets the value of the channelSelection property.
     * 
     * @return
     *     possible object is
     *     {@link ChannelSelectionType }
     *     
     */
    public ParameterValueType getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channelSelection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChannelSelectionType }
     *     
     */
    public void setChannel(ParameterValueType value) {
        this.channel = value;
    }

    /**
     * Gets the value of the threshholdsBelongTo property.
     *
     * @return
     *     possible object is
     *     {@link ThreshholdsBelongToType }
     *
     */
    public ThreshholdsBelongToType getThreshholdsBelongTo() {
        return threshholdsBelongTo;
    }

    /**
     * Sets the value of the threshholdsBelongTo property.
     *
     * @param value
     *     allowed object is
     *     {@link ThreshholdsBelongToType }
     *
     */
    public void setThreshholdsBelongTo(ThreshholdsBelongToType value) {
        this.threshholdsBelongTo = value;
    }

    public List<JAXBElement<RangeType>> getRange() {
        if (range == null) {
            range = new ArrayList<JAXBElement<RangeType>>();
        }
        return this.range;
    }

}
