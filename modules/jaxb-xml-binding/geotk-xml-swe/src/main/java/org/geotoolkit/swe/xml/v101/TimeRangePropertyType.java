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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractTimeRangeProperty;


/**
 * TimeRange is a data-type so usually appears "by value" rather than by reference.
 * 
 * <p>Java class for TimeRangePropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeRangePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}TimeRange"/>
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
@XmlType(name = "TimeRangePropertyType", propOrder = {
    "timeRange"
})
public class TimeRangePropertyType implements AbstractTimeRangeProperty {

    @XmlElement(name = "TimeRange", required = true)
    private TimeRange timeRange;

    public TimeRangePropertyType() {

    }

    public TimeRangePropertyType(AbstractTimeRangeProperty tr) {
        if (tr != null && tr.getTimeRange() != null) {
            this.timeRange = new TimeRange(tr.getTimeRange());
        }
    }

    /**
     * Gets the value of the timeRange property.
     */
    public TimeRange getTimeRange() {
        return timeRange;
    }

    /**
     * Sets the value of the timeRange property.
     * 
     */
    public void setTimeRange(TimeRange value) {
        this.timeRange = value;
    }

}
