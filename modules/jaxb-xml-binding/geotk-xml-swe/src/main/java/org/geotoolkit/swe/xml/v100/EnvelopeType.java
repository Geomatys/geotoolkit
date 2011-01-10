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
import org.geotoolkit.swe.xml.AbstractEnvelope;


/**
 * <p>Java class for EnvelopeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnvelopeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractVectorType">
 *       &lt;sequence>
 *         &lt;element name="time" type="{http://www.opengis.net/swe/1.0}TimeRangePropertyType" minOccurs="0"/>
 *         &lt;element name="lowerCorner" type="{http://www.opengis.net/swe/1.0}VectorPropertyType"/>
 *         &lt;element name="upperCorner" type="{http://www.opengis.net/swe/1.0}VectorPropertyType"/>
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
@XmlType(name = "EnvelopeType", propOrder = {
    "time",
    "lowerCorner",
    "upperCorner"
})
public class EnvelopeType extends AbstractVectorType implements AbstractEnvelope {

    private TimeRangePropertyType time;
    @XmlElement(required = true)
    private VectorPropertyType lowerCorner;
    @XmlElement(required = true)
    private VectorPropertyType upperCorner;

    public EnvelopeType() {

    }

    public EnvelopeType(final AbstractEnvelope env) {
        super(env);
        if (env != null) {
            if (env.getTime() != null) {
                this.time = new TimeRangePropertyType(env.getTime());
            }
            if (env.getLowerCorner() != null) {
                this.lowerCorner = new VectorPropertyType(env.getLowerCorner());
            }
            if (env.getUpperCorner() != null) {
                this.upperCorner = new VectorPropertyType(env.getUpperCorner());
            }
        }
    }
    
    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link TimeRangePropertyType }
     *     
     */
    public TimeRangePropertyType getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeRangePropertyType }
     *     
     */
    public void setTime(final TimeRangePropertyType value) {
        this.time = value;
    }

    /**
     * Gets the value of the lowerCorner property.
     * 
     * @return
     *     possible object is
     *     {@link VectorPropertyType }
     *     
     */
    public VectorPropertyType getLowerCorner() {
        return lowerCorner;
    }

    /**
     * Sets the value of the lowerCorner property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorPropertyType }
     *     
     */
    public void setLowerCorner(final VectorPropertyType value) {
        this.lowerCorner = value;
    }

    /**
     * Gets the value of the upperCorner property.
     * 
     * @return
     *     possible object is
     *     {@link VectorPropertyType }
     *     
     */
    public VectorPropertyType getUpperCorner() {
        return upperCorner;
    }

    /**
     * Sets the value of the upperCorner property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorPropertyType }
     *     
     */
    public void setUpperCorner(final VectorPropertyType value) {
        this.upperCorner = value;
    }

}
