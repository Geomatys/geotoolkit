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

package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.TimeInstantType;
import org.geotoolkit.gml.xml.v311modified.TimePeriodType;
import org.geotoolkit.gml.xml.v311modified.TimePositionType;
import org.geotoolkit.sml.xml.AbstractValidTime;
import org.geotoolkit.util.Utilities;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "timeInstant",
    "timePeriod"
})
@XmlRootElement(name = "validTime")
public class ValidTime implements AbstractValidTime {

    @XmlElement(name = "TimeInstant", namespace = "http://www.opengis.net/gml")
    private TimeInstantType timeInstant;
    @XmlElement(name = "TimePeriod", namespace = "http://www.opengis.net/gml")
    private TimePeriodType timePeriod;

    public ValidTime() {

    }

    public ValidTime(AbstractValidTime time) {
        this.timeInstant = time.getTimeInstant();
        this.timePeriod  = time.getTimePeriod();
    }
    
    public ValidTime(TimeInstantType timeInstant) {
        this.timeInstant = timeInstant;
    }

    public ValidTime(TimePeriodType timePeriod) {
        this.timePeriod = timePeriod;
    }

    public ValidTime(String begin, String end) {
        this.timePeriod = new TimePeriodType(begin, end);
    }

    public ValidTime(String instant) {
        this.timeInstant = new TimeInstantType(new TimePositionType(instant));
    }

    /**
     * Gets the value of the timeInstant property.
     */
    public TimeInstantType getTimeInstant() {
        return timeInstant;
    }

    /**
     * Sets the value of the timeInstant property.
     */
    public void setTimeInstant(TimeInstantType value) {
        this.timeInstant = value;
    }

    /**
     * Gets the value of the timePeriod property.
     */
    public TimePeriodType getTimePeriod() {
        return timePeriod;
    }

    /**
     * Sets the value of the timePeriod property.
     */
    public void setTimePeriod(TimePeriodType value) {
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof ValidTime) {
            final ValidTime that = (ValidTime) object;

            return Utilities.equals(this.timeInstant, that.timeInstant) &&
                   Utilities.equals(this.timePeriod,  that.timePeriod);
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
