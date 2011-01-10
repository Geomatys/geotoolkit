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
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sld}Normalize"/>
 *           &lt;element ref="{http://www.opengis.net/sld}Histogram"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sld}GammaValue" minOccurs="0"/>
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
@XmlType(name = "", propOrder = {
    "normalize",
    "histogram",
    "gammaValue"
})
@XmlRootElement(name = "ContrastEnhancement")
public class ContrastEnhancement {

    @XmlElement(name = "Normalize")
    protected Normalize normalize;
    @XmlElement(name = "Histogram")
    protected Histogram histogram;
    @XmlElement(name = "GammaValue")
    protected Double gammaValue;

    /**
     * Gets the value of the normalize property.
     * 
     * @return
     *     possible object is
     *     {@link Normalize }
     *     
     */
    public Normalize getNormalize() {
        return normalize;
    }

    /**
     * Sets the value of the normalize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Normalize }
     *     
     */
    public void setNormalize(final Normalize value) {
        this.normalize = value;
    }

    /**
     * Gets the value of the histogram property.
     * 
     * @return
     *     possible object is
     *     {@link Histogram }
     *     
     */
    public Histogram getHistogram() {
        return histogram;
    }

    /**
     * Sets the value of the histogram property.
     * 
     * @param value
     *     allowed object is
     *     {@link Histogram }
     *     
     */
    public void setHistogram(final Histogram value) {
        this.histogram = value;
    }

    /**
     * Gets the value of the gammaValue property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGammaValue() {
        return gammaValue;
    }

    /**
     * Sets the value of the gammaValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGammaValue(final Double value) {
        this.gammaValue = value;
    }

}
