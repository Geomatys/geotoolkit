/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sampling.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SamplingFeatureComplexPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SamplingFeatureComplexPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sampling/2.0}SamplingFeatureComplex"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SamplingFeatureComplexPropertyType", propOrder = {
    "samplingFeatureComplex"
})
public class SamplingFeatureComplexPropertyType {

    @XmlElement(name = "SamplingFeatureComplex", required = true)
    private SamplingFeatureComplexType samplingFeatureComplex;

    /**
     * Gets the value of the samplingFeatureComplex property.
     *
     * @return
     *     possible object is
     *     {@link SamplingFeatureComplexType }
     *
     */
    public SamplingFeatureComplexType getSamplingFeatureComplex() {
        return samplingFeatureComplex;
    }

    /**
     * Sets the value of the samplingFeatureComplex property.
     *
     * @param value
     *     allowed object is
     *     {@link SamplingFeatureComplexType }
     *
     */
    public void setSamplingFeatureComplex(SamplingFeatureComplexType value) {
        this.samplingFeatureComplex = value;
    }

}
