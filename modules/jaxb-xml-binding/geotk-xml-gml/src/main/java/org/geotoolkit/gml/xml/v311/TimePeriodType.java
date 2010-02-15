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

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for TimePeriodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimePeriodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractTimeGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="beginPosition" type="{http://www.opengis.net/gml}TimePositionType"/>
 *           &lt;element name="begin" type="{http://www.opengis.net/gml}TimeInstantPropertyType"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="endPosition" type="{http://www.opengis.net/gml}TimePositionType"/>
 *           &lt;element name="end" type="{http://www.opengis.net/gml}TimeInstantPropertyType"/>
 *         &lt;/choice>
 *         &lt;group ref="{http://www.opengis.net/gml}timeLength" minOccurs="0"/>
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
@XmlType(name = "TimePeriodType", propOrder = {
    "beginPosition",
    "begin",
    "endPosition",
    "end",
    "duration",
    "timeInterval"
})
public class TimePeriodType extends AbstractTimeGeometricPrimitiveType {

    protected TimePositionType beginPosition;
    protected TimeInstantPropertyType begin;
    protected TimePositionType endPosition;
    protected TimeInstantPropertyType end;
    protected Duration duration;
    protected TimeIntervalLengthType timeInterval;

    /**
     * Empty constructor used by JAXB.
     */
    TimePeriodType(){}

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(TimePositionType beginPosition, TimePositionType endPosition){
        this.beginPosition = beginPosition;
        this.endPosition   = endPosition;
    }

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(String beginValue, String endValue){
        this.beginPosition = new TimePositionType(beginValue);
        this.endPosition   = new TimePositionType(endValue);
    }

    /**
     * Build a new Time period bounded by the begin and with the end position "now".
     */
    public TimePeriodType(TimePositionType beginPosition){
        this.beginPosition = beginPosition;
        this.endPosition   = new TimePositionType(TimeIndeterminateValueType.NOW);
    }

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(String beginValue){
        this.beginPosition = new TimePositionType(beginValue);
        this.endPosition   = new TimePositionType(TimeIndeterminateValueType.NOW);
    }

    /**
     * Build a new Time period bounded by an indeterminate time at begin.
     */
    public TimePeriodType(TimeIndeterminateValueType indeterminateBegin, TimePositionType endPosition){
        this.beginPosition = new TimePositionType(indeterminateBegin);
        this.endPosition   = endPosition;
    }

    /**
     * Gets the value of the beginPosition property.
     * 
     * @return
     *     possible object is
     *     {@link TimePositionType }
     *     
     */
    public TimePositionType getBeginPosition() {
        return beginPosition;
    }

    /**
     * Sets the value of the beginPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *     
     */
    public void setBeginPosition(TimePositionType value) {
        this.beginPosition = value;
    }

    /**
     * Sets the value of the beginPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setBeginPosition(Date value) {
        this.beginPosition = new TimePositionType(value);
    }

    /**
     * Sets the value of the beginPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setBeginPosition(TimeIndeterminateValueType value) {
        this.beginPosition = new TimePositionType(value);
    }

    /**
     * Gets the value of the begin property.
     * 
     * @return
     *     possible object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public TimeInstantPropertyType getBegin() {
        return begin;
    }

    /**
     * Sets the value of the begin property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public void setBegin(TimeInstantPropertyType value) {
        this.begin = value;
    }

    /**
     * Gets the value of the endPosition property.
     * 
     * @return
     *     possible object is
     *     {@link TimePositionType }
     *     
     */
    public TimePositionType getEndPosition() {
        return endPosition;
    }

    /**
     * Sets the value of the endPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *     
     */
    public void setEndPosition(TimePositionType value) {
        this.endPosition = value;
    }

    /**
     * Sets the value of the endPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setEndPosition(Date value) {
        this.endPosition = new TimePositionType(value);
    }

    /**
     * Sets the value of the beginPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setEndPosition(TimeIndeterminateValueType value) {
        this.endPosition = new TimePositionType(value);
    }

    /**
     * Gets the value of the end property.
     * 
     * @return
     *     possible object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public TimeInstantPropertyType getEnd() {
        return end;
    }

    /**
     * Sets the value of the end property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeInstantPropertyType }
     *     
     */
    public void setEnd(TimeInstantPropertyType value) {
        this.end = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setDuration(Duration value) {
        this.duration = value;
    }

    /**
     * Gets the value of the timeInterval property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalLengthType }
     *     
     */
    public TimeIntervalLengthType getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the value of the timeInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeIntervalLengthType }
     *     
     */
    public void setTimeInterval(TimeIntervalLengthType value) {
        this.timeInterval = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimePeriodType)) {
            return false;
        }
        final TimePeriodType that = (TimePeriodType) object;

        return Utilities.equals(this.begin,         that.begin)         &&
               Utilities.equals(this.beginPosition, that.beginPosition) &&
               Utilities.equals(this.duration,      that.duration)      &&
               Utilities.equals(this.endPosition,   that.endPosition)   &&
               Utilities.equals(this.timeInterval,  that.timeInterval)  &&
               Utilities.equals(this.end,           that.end);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.beginPosition != null ? this.beginPosition.hashCode() : 0);
        hash = 37 * hash + (this.begin != null ? this.begin.hashCode() : 0);
        hash = 37 * hash + (this.endPosition != null ? this.endPosition.hashCode() : 0);
        hash = 37 * hash + (this.end != null ? this.end.hashCode() : 0);
        hash = 37 * hash + (this.duration != null ? this.duration.hashCode() : 0);
        hash = 37 * hash + (this.timeInterval != null ? this.timeInterval.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        char lineSeparator = '\n';
        StringBuilder s = new StringBuilder("TimePeriod:").append(lineSeparator);
        if (begin != null) {
            s.append("begin:").append(begin).append(lineSeparator);
        }
        if (end != null) {
            s.append("end  :").append(end).append(lineSeparator);
        }
        if (beginPosition != null) {
            s.append("beginPosition :").append(beginPosition).append(lineSeparator);
        }
        if (endPosition != null) {
            s.append("endPosition   :").append(endPosition);
        }
        if (duration != null) {
            s.append(lineSeparator).append("duration:").append(duration);
        }
        if (timeInterval != null) {
            s.append(lineSeparator).append("timeInterval:").append(timeInterval).append(lineSeparator);
        }

        return s.toString();
    }
}
