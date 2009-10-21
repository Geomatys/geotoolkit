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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NormalizedCurveType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NormalizedCurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataRecordType">
 *       &lt;sequence>
 *         &lt;element name="inputGain" type="{http://www.opengis.net/swe/1.0}QuantityPropertyType" minOccurs="0"/>
 *         &lt;element name="inputBias" type="{http://www.opengis.net/swe/1.0}QuantityPropertyType" minOccurs="0"/>
 *         &lt;element name="outputGain" type="{http://www.opengis.net/swe/1.0}QuantityPropertyType" minOccurs="0"/>
 *         &lt;element name="outputBias" type="{http://www.opengis.net/swe/1.0}QuantityPropertyType" minOccurs="0"/>
 *         &lt;element name="interpolationMethod" type="{http://www.opengis.net/swe/1.0}CategoryPropertyType" minOccurs="0"/>
 *         &lt;element name="extrapolationMethod" type="{http://www.opengis.net/swe/1.0}CategoryPropertyType" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.opengis.net/swe/1.0}CurvePropertyType"/>
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
@XmlType(name = "NormalizedCurveType", propOrder = {
    "inputGain",
    "inputBias",
    "outputGain",
    "outputBias",
    "interpolationMethod",
    "extrapolationMethod",
    "function"
})
public class NormalizedCurveType  extends AbstractDataRecordType {

    private QuantityPropertyType inputGain;
    private QuantityPropertyType inputBias;
    private QuantityPropertyType outputGain;
    private QuantityPropertyType outputBias;
    private CategoryPropertyType interpolationMethod;
    private CategoryPropertyType extrapolationMethod;
    @XmlElement(required = true)
    private CurvePropertyType function;

    /**
     * Gets the value of the inputGain property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityPropertyType }
     *     
     */
    public QuantityPropertyType getInputGain() {
        return inputGain;
    }

    /**
     * Sets the value of the inputGain property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityPropertyType }
     *     
     */
    public void setInputGain(QuantityPropertyType value) {
        this.inputGain = value;
    }

    /**
     * Gets the value of the inputBias property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityPropertyType }
     *     
     */
    public QuantityPropertyType getInputBias() {
        return inputBias;
    }

    /**
     * Sets the value of the inputBias property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityPropertyType }
     *     
     */
    public void setInputBias(QuantityPropertyType value) {
        this.inputBias = value;
    }

    /**
     * Gets the value of the outputGain property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityPropertyType }
     *     
     */
    public QuantityPropertyType getOutputGain() {
        return outputGain;
    }

    /**
     * Sets the value of the outputGain property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityPropertyType }
     *     
     */
    public void setOutputGain(QuantityPropertyType value) {
        this.outputGain = value;
    }

    /**
     * Gets the value of the outputBias property.
     * 
     * @return
     *     possible object is
     *     {@link QuantityPropertyType }
     *     
     */
    public QuantityPropertyType getOutputBias() {
        return outputBias;
    }

    /**
     * Sets the value of the outputBias property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityPropertyType }
     *     
     */
    public void setOutputBias(QuantityPropertyType value) {
        this.outputBias = value;
    }

    /**
     * Gets the value of the interpolationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryPropertyType }
     *     
     */
    public CategoryPropertyType getInterpolationMethod() {
        return interpolationMethod;
    }

    /**
     * Sets the value of the interpolationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryPropertyType }
     *     
     */
    public void setInterpolationMethod(CategoryPropertyType value) {
        this.interpolationMethod = value;
    }

    /**
     * Gets the value of the extrapolationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link CategoryPropertyType }
     *     
     */
    public CategoryPropertyType getExtrapolationMethod() {
        return extrapolationMethod;
    }

    /**
     * Sets the value of the extrapolationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategoryPropertyType }
     *     
     */
    public void setExtrapolationMethod(CategoryPropertyType value) {
        this.extrapolationMethod = value;
    }

    /**
     * Gets the value of the function property.
     * 
     * @return
     *     possible object is
     *     {@link CurvePropertyType }
     *     
     */
    public CurvePropertyType getFunction() {
        return function;
    }

    /**
     * Sets the value of the function property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurvePropertyType }
     *     
     */
    public void setFunction(CurvePropertyType value) {
        this.function = value;
    }

}
