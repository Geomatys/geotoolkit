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
 *         &lt;element ref="{http://www.opengis.net/sld}LATEST_ON_TOP"/>
 *         &lt;element ref="{http://www.opengis.net/sld}EARLIEST_ON_TOP"/>
 *         &lt;element ref="{http://www.opengis.net/sld}AVERAGE"/>
 *         &lt;element ref="{http://www.opengis.net/sld}RANDOM"/>
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
    "latestontop",
    "earliestontop",
    "average",
    "random"
})
@XmlRootElement(name = "OverlapBehavior")
public class OverlapBehavior {

    @XmlElement(name = "LATEST_ON_TOP")
    protected LATESTONTOP latestontop;
    @XmlElement(name = "EARLIEST_ON_TOP")
    protected EARLIESTONTOP earliestontop;
    @XmlElement(name = "AVERAGE")
    protected AVERAGE average;
    @XmlElement(name = "RANDOM")
    protected RANDOM random;

    /**
     * Gets the value of the latestontop property.
     * 
     * @return
     *     possible object is
     *     {@link LATESTONTOP }
     *     
     */
    public LATESTONTOP getLATESTONTOP() {
        return latestontop;
    }

    /**
     * Sets the value of the latestontop property.
     * 
     * @param value
     *     allowed object is
     *     {@link LATESTONTOP }
     *     
     */
    public void setLATESTONTOP(final LATESTONTOP value) {
        this.latestontop = value;
    }

    /**
     * Gets the value of the earliestontop property.
     * 
     * @return
     *     possible object is
     *     {@link EARLIESTONTOP }
     *     
     */
    public EARLIESTONTOP getEARLIESTONTOP() {
        return earliestontop;
    }

    /**
     * Sets the value of the earliestontop property.
     * 
     * @param value
     *     allowed object is
     *     {@link EARLIESTONTOP }
     *     
     */
    public void setEARLIESTONTOP(final EARLIESTONTOP value) {
        this.earliestontop = value;
    }

    /**
     * Gets the value of the average property.
     * 
     * @return
     *     possible object is
     *     {@link AVERAGE }
     *     
     */
    public AVERAGE getAVERAGE() {
        return average;
    }

    /**
     * Sets the value of the average property.
     * 
     * @param value
     *     allowed object is
     *     {@link AVERAGE }
     *     
     */
    public void setAVERAGE(final AVERAGE value) {
        this.average = value;
    }

    /**
     * Gets the value of the random property.
     * 
     * @return
     *     possible object is
     *     {@link RANDOM }
     *     
     */
    public RANDOM getRANDOM() {
        return random;
    }

    /**
     * Sets the value of the random property.
     * 
     * @param value
     *     allowed object is
     *     {@link RANDOM }
     *     
     */
    public void setRANDOM(final RANDOM value) {
        this.random = value;
    }

}
