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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * List of all the valid values and/or ranges of values for this variable. For numeric variables, signed values shall be ordered from negative infinity to positive infinity. For intervals, the "type" and "semantic" attributes are inherited by children elements, but can be superceded by them. 
 * 
 * <p>Java class for valueEnumBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="valueEnumBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/wcs}interval"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}singleValue"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "valueEnumBaseType", propOrder = {
    "intervalOrSingleValue"
})
@XmlSeeAlso({
    org.geotoolkit.wcs.xml.v100.RangeSubsetType.AxisSubset.class
    //ValueEnumType.class
})
public class ValueEnumBaseType {

    @XmlElements({
        @XmlElement(name = "interval", type = IntervalType.class),
        @XmlElement(name = "singleValue", type = TypedLiteralType.class)
    })
    private List<Object> intervalOrSingleValue;

    /**
     * Gets the value of the intervalOrSingleValue property.
     * (unmodifiable)
     */
    public List<Object> getIntervalOrSingleValue() {
        if (intervalOrSingleValue == null) {
            intervalOrSingleValue = new ArrayList<Object>();
        }
        return Collections.unmodifiableList(intervalOrSingleValue);
    }

}
