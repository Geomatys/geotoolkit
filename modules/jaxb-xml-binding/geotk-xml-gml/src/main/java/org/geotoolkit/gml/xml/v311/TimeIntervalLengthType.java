/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package org.geotoolkit.gml.xml.v311;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * This type extends the built-in xsd:decimal simple type to allow floating-point 
 *       values for temporal length. According to  the ISO 11404 model you have to use 
 *       positiveInteger together with appropriate values for radix and factor. The 
 *       resolution of the time interval is to one radix ^(-factor) of the specified 
 *       time unit (e.g. unit="second", radix="10", factor="3" specifies a resolution 
 *       of milliseconds). It is a subtype of TimeDurationType.
 * 
 * <p>Java class for TimeIntervalLengthType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeIntervalLengthType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>decimal">
 *       &lt;attribute name="unit" use="required" type="{http://www.opengis.net/gml}TimeUnitType" />
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
public class TimeIntervalLengthType {

    @XmlValue
    private BigDecimal value;
    @XmlAttribute(required = true)
    private String unit;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger radix;
    @XmlAttribute
    private BigInteger factor;

    /**
     * Gets the value of the value property.
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Gets the value of the unit property.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Gets the value of the radix property.
     */
    public BigInteger getRadix() {
        return radix;
    }

    /**
     * Gets the value of the factor property.
     */
    public BigInteger getFactor() {
        return factor;
    }
}
