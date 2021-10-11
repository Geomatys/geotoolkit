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
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;


/**
 * <p>Java class for TimeEdgeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeEdgeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimeTopologyPrimitiveType">
 *       &lt;sequence>
 *         &lt;element name="start" type="{http://www.opengis.net/gml/3.2}TimeNodePropertyType"/>
 *         &lt;element name="end" type="{http://www.opengis.net/gml/3.2}TimeNodePropertyType"/>
 *         &lt;element name="extent" type="{http://www.opengis.net/gml/3.2}TimePeriodPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeEdgeType", propOrder = {
    "start",
    "end",
    "extent"
})
public class TimeEdgeType extends AbstractTimeTopologyPrimitiveType implements Serializable {

    @XmlElement(required = true)
    private TimeNodePropertyType start;
    @XmlElement(required = true)
    private TimeNodePropertyType end;
    private TimePeriodPropertyType extent;

    public TimeEdgeType() {

    }

    public TimeEdgeType(final TimeEdgeType that) {
        super(that);
        if (that != null) {
            if (that.end != null) {
                this.end = new TimeNodePropertyType(that.end);
            }
            if (that.extent != null) {
                this.extent = new TimePeriodPropertyType(that.extent);
            }
            if (that.start != null) {
                this.start = new TimeNodePropertyType(that.start);
            }
        }
    }

    /**
     * Gets the value of the start property.
     *
     * @return
     *     possible object is
     *     {@link TimeNodePropertyType }
     *
     */
    public TimeNodePropertyType getStart() {
        return start;
    }

    /**
     * Sets the value of the start property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeNodePropertyType }
     *
     */
    public void setStart(TimeNodePropertyType value) {
        this.start = value;
    }

    /**
     * Gets the value of the end property.
     *
     * @return
     *     possible object is
     *     {@link TimeNodePropertyType }
     *
     */
    public TimeNodePropertyType getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeNodePropertyType }
     *
     */
    public void setEnd(TimeNodePropertyType value) {
        this.end = value;
    }

    /**
     * Gets the value of the extent property.
     *
     * @return
     *     possible object is
     *     {@link TimePeriodPropertyType }
     *
     */
    public TimePeriodPropertyType getExtent() {
        return extent;
    }

    /**
     * Sets the value of the extent property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePeriodPropertyType }
     *
     */
    public void setExtent(TimePeriodPropertyType value) {
        this.extent = value;
    }

    @Override
    public AbstractTimeObjectType getClone() {
        return new TimeEdgeType(this);
    }

}
