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
package org.geotoolkit.sml.xml.v101;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.sml.xml.AbstractValidTime;
import org.apache.sis.util.ComparisonMode;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/gml}TimeInstant"/>
 *         &lt;element ref="{http://www.opengis.net/gml}TimePeriod"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "timeInstant",
    "timePeriod"
})
@XmlRootElement(name = "ValidTime")
public class ValidTime extends SensorObject implements AbstractValidTime {

    @XmlElement(name = "TimeInstant", namespace = "http://www.opengis.net/gml")
    private TimeInstantType timeInstant;
    @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml")
    private TimePeriodType timePeriod;

    public ValidTime() {

    }

    public ValidTime(final AbstractValidTime time) {
        if (time != null) {
            this.timeInstant = time.getTimeInstant();
            this.timePeriod  = time.getTimePeriod();
        }
    }

    public ValidTime(final TimeInstantType timeInstant) {
        this.timeInstant = timeInstant;
    }

    public ValidTime(final TimePeriodType timePeriod) {
        this.timePeriod = timePeriod;
    }

    public ValidTime(final String begin, final String end) {
        this.timePeriod = new TimePeriodType(begin, end);
    }

    public ValidTime(final String instant) {
        this.timeInstant = new TimeInstantType(new TimePositionType(instant));
    }

    /**
     * Gets the value of the timeInstant property.
     *
     * @return
     *     possible object is
     *     {@link TimeInstantType }
     *
     */
    public TimeInstantType getTimeInstant() {
        return timeInstant;
    }

    /**
     * Sets the value of the timeInstant property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeInstantType }
     *
     */
    public void setTimeInstant(final TimeInstantType value) {
        this.timeInstant = value;
    }

    /**
     * Gets the value of the timePeriod property.
     *
     * @return
     *     possible object is
     *     {@link TimePeriodType }
     *
     */
    public TimePeriodType getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the value of the timePeriod property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePeriodType }
     *
     */
    public void setTimePeriod(final TimePeriodType value) {
        this.timePeriod = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ValidTime]").append("\n");
        if (timeInstant != null) {
            sb.append("timeInstant: ").append(timeInstant).append('\n');
        }
        if (timePeriod != null) {
            sb.append("timePeriod: ").append(timePeriod).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof ValidTime) {
            final ValidTime that = (ValidTime) object;

            return Objects.equals(this.timeInstant, that.timeInstant) &&
                   Objects.equals(this.timePeriod,  that.timePeriod);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.timeInstant != null ? this.timeInstant.hashCode() : 0);
        hash = 97 * hash + (this.timePeriod != null ? this.timePeriod.hashCode() : 0);
        return hash;
    }
}
