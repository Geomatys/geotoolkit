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
package org.geotoolkit.se.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContrastEnhancementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContrastEnhancementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/se}Normalize"/>
 *           &lt;element ref="{http://www.opengis.net/se}Histogram"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/se}GammaValue" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContrastEnhancementType", propOrder = {
    "normalize",
    "histogram",
    "gammaValue"
})
public class ContrastEnhancementType {

    @XmlElement(name = "Normalize")
    protected NormalizeType normalize;
    @XmlElement(name = "Histogram")
    protected HistogramType histogram;
    @XmlElement(name = "GammaValue")
    protected Double gammaValue;

    /**
     * Gets the value of the normalize property.
     * 
     * @return
     *     possible object is
     *     {@link NormalizeType }
     *     
     */
    public NormalizeType getNormalize() {
        return normalize;
    }

    /**
     * Sets the value of the normalize property.
     * 
     * @param value
     *     allowed object is
     *     {@link NormalizeType }
     *     
     */
    public void setNormalize(NormalizeType value) {
        this.normalize = value;
    }

    /**
     * Gets the value of the histogram property.
     * 
     * @return
     *     possible object is
     *     {@link HistogramType }
     *     
     */
    public HistogramType getHistogram() {
        return histogram;
    }

    /**
     * Sets the value of the histogram property.
     * 
     * @param value
     *     allowed object is
     *     {@link HistogramType }
     *     
     */
    public void setHistogram(HistogramType value) {
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
    public void setGammaValue(Double value) {
        this.gammaValue = value;
    }

}
