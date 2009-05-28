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
 * <p>Java class for LinePlacementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinePlacementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}PerpendicularOffset" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}IsRepeated" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}InitialGap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Gap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}IsAligned" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}GeneralizeLine" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinePlacementType", propOrder = {
    "perpendicularOffset",
    "isRepeated",
    "initialGap",
    "gap",
    "isAligned",
    "generalizeLine"
})
public class LinePlacementType {

    @XmlElement(name = "PerpendicularOffset")
    protected ParameterValueType perpendicularOffset;
    @XmlElement(name = "IsRepeated")
    protected Boolean isRepeated;
    @XmlElement(name = "InitialGap")
    protected ParameterValueType initialGap;
    @XmlElement(name = "Gap")
    protected ParameterValueType gap;
    @XmlElement(name = "IsAligned")
    protected Boolean isAligned;
    @XmlElement(name = "GeneralizeLine")
    protected Boolean generalizeLine;

    /**
     * Gets the value of the perpendicularOffset property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getPerpendicularOffset() {
        return perpendicularOffset;
    }

    /**
     * Sets the value of the perpendicularOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setPerpendicularOffset(ParameterValueType value) {
        this.perpendicularOffset = value;
    }

    /**
     * Gets the value of the isRepeated property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRepeated() {
        return isRepeated;
    }

    /**
     * Sets the value of the isRepeated property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRepeated(Boolean value) {
        this.isRepeated = value;
    }

    /**
     * Gets the value of the initialGap property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getInitialGap() {
        return initialGap;
    }

    /**
     * Sets the value of the initialGap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setInitialGap(ParameterValueType value) {
        this.initialGap = value;
    }

    /**
     * Gets the value of the gap property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getGap() {
        return gap;
    }

    /**
     * Sets the value of the gap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setGap(ParameterValueType value) {
        this.gap = value;
    }

    /**
     * Gets the value of the isAligned property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAligned() {
        return isAligned;
    }

    /**
     * Sets the value of the isAligned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAligned(Boolean value) {
        this.isAligned = value;
    }

    /**
     * Gets the value of the generalizeLine property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGeneralizeLine() {
        return generalizeLine;
    }

    /**
     * Sets the value of the generalizeLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGeneralizeLine(Boolean value) {
        this.generalizeLine = value;
    }

}
