/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractCountRange;


/**
 * <p>Java class for CountRangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CountRangeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSimpleComponentType">
 *       &lt;sequence>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/2.0}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/2.0}IntegerPair" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CountRangeType", propOrder = {
    "constraint",
    "value"
})
public class CountRangeType extends AbstractSimpleComponentType implements AbstractCountRange {

    private AllowedValuesPropertyType constraint;
    @XmlList
    private List<Integer> value;

    public CountRangeType() {
        
    }
    
    public CountRangeType(final String definition, final List<Integer> value) {
        super(null, definition, null);
        this.value = value;
    }
    
    /**
     * Gets the value of the constraint property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedValuesPropertyType }
     *     
     */
    @Override
    public AllowedValuesPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedValuesPropertyType }
     *     
     */
    public void setConstraint(AllowedValuesPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    @Override
    public List<Integer> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return this.value;
    }
}
