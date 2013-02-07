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
package org.geotoolkit.gml.xml.v311;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Position;


/**
 * Omit back-pointers begunBy, endedBy.
 * 
 * <p>Java class for TimeInstantType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeInstantType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractTimeGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}timePosition"/>
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
@XmlType(name = "TimeInstantType", propOrder = {
    "timePosition"
})
public class TimeInstantType extends AbstractTimeGeometricPrimitiveType implements Instant, Serializable {

    @XmlElement(required = true)
    private TimePositionType timePosition;

    /**
     * Empty constructor used by JAXB.
     */
    TimeInstantType(){}

    /**
     * Build a new time instant with the specified timeposition.
     */
    public TimeInstantType(final Position timePosition) {
        if (timePosition instanceof TimePositionType) {
            this.timePosition = (TimePositionType) timePosition;
        } else if (timePosition != null) {
            this.timePosition = new TimePositionType(timePosition.getDate());
        }
    }
    
    public TimeInstantType(final String timePosition) {
        this.timePosition = new TimePositionType(timePosition);
    }

    /**
     * Gets the value of the timePosition property.
     * 
     * @return
     *     possible object is
     *     {@link TimePositionType }
     *     
     */
    public TimePositionType getTimePosition() {
        return timePosition;
    }

    /**
     * Sets the value of the timePosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *     
     */
    public void setTimePosition(final TimePositionType value) {
        this.timePosition = value;
    }

    @Override
    public Position getPosition() {
        return timePosition;
    }
    
    public void setPosition(final Position value) {
        if (value instanceof TimePositionType) {
            this.timePosition = (TimePositionType)value;
        } else if (value != null) {
            this.timePosition = new TimePositionType(value);
        } else {
            this.timePosition = null;
        }
    }

    public long getTime() {
        if (this.timePosition != null && this.timePosition.getDate() != null) {
            return this.timePosition.getDate().getTime();
        }
        return -1;
    }
    
    @Override
    public Collection<Period> getBegunBy() {
        return null;
    }

    @Override
    public Collection<Period> getEndedBy() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TimeInstantType]").append('\n');
        if (timePosition != null) {
            sb.append("timePosition:").append(timePosition).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode cm) {
        if (object == this) {
            return true;
        }
        if (object instanceof TimeInstantType) {
            final TimeInstantType that = (TimeInstantType) object;
            return  Utilities.equals(this.timePosition, that.timePosition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        return hash;
    }
}
