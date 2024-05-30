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
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.temporal.Temporal;
import javax.xml.datatype.Duration;
import org.geotoolkit.gml.xml.TimeIndeterminateValueType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.geotoolkit.gml.xml.GMLInstant;
import org.geotoolkit.gml.xml.GMLPeriod;
import org.opengis.temporal.Period;


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
@XmlRootElement(name="TimePeriod")
public class TimePeriodType extends AbstractTimeGeometricPrimitiveType implements GMLPeriod, Serializable {

    private TimePositionType beginPosition;
    private TimeInstantPropertyType begin;
    private TimePositionType endPosition;
    private TimeInstantPropertyType end;
    private Duration duration;
    private TimeIntervalLengthType timeInterval;

    /**
     * Empty constructor used by JAXB.
     */
    public TimePeriodType(){}

    public TimePeriodType(final String id, final Period period) {
        super(id);
        if (period != null) {
            if (period.getBeginning() != null) {
                this.beginPosition = new TimePositionType(period.getBeginning());
            }
            if (period.getEnding() != null) {
                this.endPosition = new TimePositionType(period.getEnding());
            }
        }
    }

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(final String id, final AbstractTimePosition beginPosition, final AbstractTimePosition endPosition) {
        if (beginPosition instanceof TimePositionType) {
            this.beginPosition = (TimePositionType) beginPosition;
        } else if (beginPosition != null && beginPosition.getDate() != null) {
            this.beginPosition = new TimePositionType(beginPosition.getTemporal());
        }
        if (endPosition instanceof TimePositionType) {
            this.endPosition = (TimePositionType) endPosition;
        } else if (endPosition != null && endPosition.getDate() != null) {
            this.endPosition = new TimePositionType(endPosition.getTemporal());
        }
    }

    public TimePeriodType(final String id, final Temporal beginPosition, final Temporal endPosition){
        super(id);
        if (beginPosition != null) {
            this.beginPosition = new TimePositionType(beginPosition);
        }
        if (endPosition != null) {
            this.endPosition = new TimePositionType(endPosition);
        }
    }

    public TimePeriodType(final String id, final GMLInstant beginPosition, final GMLInstant endPosition){
        super(id);
        if (beginPosition != null) {
            this.beginPosition = new TimePositionType(beginPosition);
        }
        if (endPosition != null) {
            this.endPosition = new TimePositionType(endPosition);
        }
    }

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(final String id, final String beginValue, final String endValue){
        super(id);
        this.beginPosition = new TimePositionType(beginValue);
        this.endPosition   = new TimePositionType(endValue);
    }

    /**
     * Build a new Time period bounded by the begin and with the end position "now".
     */
    public TimePeriodType(final AbstractTimePosition beginPosition) {
        if (beginPosition instanceof TimePositionType) {
            this.beginPosition = (TimePositionType) beginPosition;
        } else if (beginPosition != null) {
            this.beginPosition = new TimePositionType(beginPosition);
        }
        this.endPosition   = new TimePositionType(TimeIndeterminateValueType.NOW);
    }

    /**
     * Build a new Time period bounded by the begin and end time specified.
     */
    public TimePeriodType(final String id, final String beginValue){
        super(id);
        this.beginPosition = new TimePositionType(beginValue);
        this.endPosition   = new TimePositionType(TimeIndeterminateValueType.NOW);
    }

    public TimePeriodType(final String id, final Temporal beginValue){
        super(id);
        this.beginPosition = new TimePositionType(beginValue);
        this.endPosition   = new TimePositionType(TimeIndeterminateValueType.NOW);
    }

    /**
     * Build a new Time period bounded by an indeterminate time at begin.
     */
    public TimePeriodType(final TimeIndeterminateValueType indeterminateBegin, final AbstractTimePosition endPosition) {
        this.beginPosition = new TimePositionType(indeterminateBegin);
        if (endPosition instanceof TimePositionType) {
            this.endPosition = (TimePositionType) endPosition;
        } else if (endPosition != null) {
            this.endPosition = new TimePositionType(endPosition.getTemporal());
        }
    }

    public TimePeriodType(final AbstractTimePosition beginPosition, final TimeIndeterminateValueType indeterminateEnd){
        this.endPosition = new TimePositionType(indeterminateEnd);
        if (beginPosition instanceof TimePositionType) {
            this.beginPosition = (TimePositionType) beginPosition;
        } else if (beginPosition != null) {
            this.beginPosition = new TimePositionType(beginPosition.getTemporal());
        }
    }

    /**
     * Build a new Time period with a duration.
     */
    public TimePeriodType(final Duration duration) {
        this.duration = duration;
    }

    /**
     * Build a new Time period with a duration.
     */
    public TimePeriodType(final Period period) {
        if (period != null) {
            if (period.getBeginning() != null) {
                this.beginPosition = new TimePositionType(period.getBeginning());
            }
            if (period.getEnding() != null) {
                this.endPosition = new TimePositionType(period.getEnding());
            }
        }
    }

    /**
     * Gets the value of the beginPosition property.
     *
     * @return
     *     possible object is
     *     {@link TimePositionType }
     */
    @Override
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
    public void setBeginPosition(final TimePositionType value) {
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
    public void setBeginPosition(final Date value) {
        this.beginPosition = new TimePositionType(value.toInstant());
    }

    /**
     * Sets the value of the beginPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setBeginPosition(final TimeIndeterminateValueType value) {
        this.beginPosition = new TimePositionType(value);
    }

    public void setBeginPosition(final TimeInstantType value) {
        if (value != null) {
            this.beginPosition = value.getTimePosition();
        }
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
    public void setBegin(final TimeInstantPropertyType value) {
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
    @Override
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
    public void setEndPosition(final TimePositionType value) {
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
    public void setEndPosition(final Date value) {
        this.endPosition = new TimePositionType(value.toInstant());
    }

    /**
     * Sets the value of the beginPosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setEndPosition(final TimeIndeterminateValueType value) {
        this.endPosition = new TimePositionType(value);
    }

    public void setEndPosition(final TimeInstantType value) {
        if (value != null) {
            this.endPosition = value.getTimePosition();
        }
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
    public void setEnd(final TimeInstantPropertyType value) {
        this.end = value;
    }

    @Override
    public Temporal getBeginning() {
        if (begin != null) {
            return begin.getTimeInstant().getTemporal();
        } else if (beginPosition != null) {
            return new TimeInstantType(beginPosition).getTemporal();
        }
        return null;
    }

    public void setBeginning(final Temporal instant) {
        if (instant != null) {
            this.beginPosition = new TimePositionType(instant);
        }
    }

    @Override
    public Temporal getEnding() {
        if (end != null) {
            return end.getTimeInstant().getTemporal();
        } else if (endPosition != null) {
            return new TimeInstantType(endPosition).getTemporal();
        }
        return null;
    }

    public void setEnding(final Temporal instant) {
        if (instant != null) {
            this.endPosition = new TimePositionType(instant);
        }
    }

    public long getTime() {
        final long b;
        if (beginPosition != null && beginPosition.getDate() != null) {
            b = beginPosition.getDate().getTime();
        } else if (begin != null &&  begin.getTimeInstant() != null &&
                   begin.getTimeInstant().getTimePosition() != null &&
                   begin.getTimeInstant().getTimePosition().getDate() != null) {
            b = begin.getTimeInstant().getTimePosition().getDate().getTime();
        } else {
            return -1;
        }
        final long e;
        if (endPosition != null && endPosition.getDate() != null) {
            e = endPosition.getDate().getTime();
        } else if (end != null &&  end.getTimeInstant() != null &&
                   end.getTimeInstant().getTimePosition() != null &&
                   end.getTimeInstant().getTimePosition().getDate() != null) {
            e = end.getTimeInstant().getTimePosition().getDate().getTime();
        } else {
            return -1;
        }
        return e - b;
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
    public void setDuration(final Duration value) {
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
    public void setTimeInterval(final TimeIntervalLengthType value) {
        this.timeInterval = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof TimePeriodType)) {
            return false;
        }
        final TimePeriodType that = (TimePeriodType) object;

        return Objects.equals(this.begin,         that.begin)         &&
               Objects.equals(this.beginPosition, that.beginPosition) &&
               Objects.equals(this.duration,      that.duration)      &&
               Objects.equals(this.endPosition,   that.endPosition)   &&
               Objects.equals(this.timeInterval,  that.timeInterval)  &&
               Objects.equals(this.end,           that.end);
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
