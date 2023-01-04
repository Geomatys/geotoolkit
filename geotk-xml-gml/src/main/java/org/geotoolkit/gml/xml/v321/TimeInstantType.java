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
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.geotoolkit.gml.xml.GMLInstant;
import org.opengis.temporal.TemporalPosition;


/**
 * <p>Java class for TimeInstantType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeInstantType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimeGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}timePosition"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "timePosition"
})
@XmlRootElement(name = "TimeInstant")
public class TimeInstantType extends AbstractTimeGeometricPrimitiveType implements GMLInstant, Serializable{

    @XmlElement(required = true)
    private TimePositionType timePosition;

    /**
     * Empty constructor used by JAXB.
     */
    TimeInstantType(){}

    /**
     * Build a new time instant with the specified timeposition.
     */
    public TimeInstantType(final AbstractTimePosition timePosition) {
        if (timePosition instanceof AbstractTimePosition) {
            this.timePosition = (TimePositionType) timePosition;
        } else if (timePosition != null) {
            this.timePosition = new TimePositionType(timePosition);
        }
    }

    public TimeInstantType(final String id, final String timePosition) {
       super(id);
       this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final String timePosition) {
       this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final Date timePosition) {
       this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final String id, final Date timePosition) {
        super(id);
       this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final TimeInstantType that) {
        super(that);
        if (that != null && that.timePosition != null) {
            this.timePosition = new TimePositionType(that.timePosition);
        }
    }

    /**
     * Gets the value of the timePosition property.
     *
     * @return
     *     possible object is
     *     {@link TimePositionType }
     *
     */
    @Override
    public TimePositionType getTimePosition() {
        return timePosition;
    }

    @Override
    public Date getDate() {
        return (timePosition != null) ? timePosition.getDate() : null;
    }

    public void setPosition(final AbstractTimePosition value) {
        if (value instanceof TimePositionType tp) {
            this.timePosition = tp;
        } else if (value != null) {
            this.timePosition = new TimePositionType(value.getDate());
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

    /**
     * Sets the value of the timePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setTimePosition(TimePositionType value) {
        this.timePosition = value;
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
            return  Objects.equals(this.timePosition, that.timePosition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        return hash;
    }

    @Override
    public AbstractTimeObjectType getClone() {
        return new TimeInstantType(this);
    }

    @Override
    public TemporalPosition getTemporalPosition() {
        return null;
    }
}
