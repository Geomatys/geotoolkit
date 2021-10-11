/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for TimeIntervalLengthType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeIntervalLengthType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>decimal">
 *       &lt;attribute name="unit" use="required" type="{http://www.opengis.net/gml/3.2}TimeUnitType" />
 *       &lt;attribute name="radix" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="factor" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeIntervalLengthType", propOrder = {
    "value"
})
public class TimeIntervalLengthType implements Serializable{

    @XmlValue
    private BigDecimal value;
    @XmlAttribute(required = true)
    private String unit;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer radix;
    @XmlAttribute
    private Integer factor;

    public TimeIntervalLengthType() {

    }

    public TimeIntervalLengthType(final TimeIntervalLengthType that) {
        if (that != null) {
            this.factor = that.factor;
            this.radix  = that.radix;
            this.unit   = that.unit;
            this.value  = that.value;
        }
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the unit property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * Gets the value of the radix property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getRadix() {
        return radix;
    }

    /**
     * Sets the value of the radix property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setRadix(Integer value) {
        this.radix = value;
    }

    /**
     * Gets the value of the factor property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getFactor() {
        return factor;
    }

    /**
     * Sets the value of the factor property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setFactor(Integer value) {
        this.factor = value;
    }

}
