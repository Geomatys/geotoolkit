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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.Utilities;
import org.opengis.temporal.Position;
import org.opengis.temporal.TemporalPosition;
import org.opengis.util.InternationalString;


/**
 * Direct representation of a temporal position.
 *       Indeterminate time values are also allowed, as described in ISO 19108. The indeterminatePosition
 *       attribute can be used alone or it can qualify a specific value for temporal position (e.g. before
 *       2002-12, after 1019624400).
 *       For time values that identify position within a calendar, the calendarEraName attribute provides
 *       the name of the calendar era to which the date is referenced (e.g. the Meiji era of the Japanese calendar).
 *
 * <p>Java class for TimePositionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimePositionType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml>TimePositionUnion">
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *       &lt;attribute name="calendarEraName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="indeterminatePosition" type="{http://www.opengis.net/gml}TimeIndeterminateValueType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimePositionType", propOrder = {
    "value"
})
public class TimePositionType implements Position, Serializable {

    @XmlValue
    private String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String frame;
    @XmlAttribute
    private String calendarEraName;
    @XmlAttribute
    private TimeIndeterminateValueType indeterminatePosition;

    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * empty constructor used by JAXB.
     */
    TimePositionType() {}

    /**
     * build a simple Timposition with only a value.
     *
     * @param value a date.
     */
    public TimePositionType(final String value){
        this.value = value;
    }

    public TimePositionType(final Position value){
        synchronized (formatter) {
            this.value = formatter.format(value.getDate());
        }
    }

    /**
     * build a simple Timposition with an indeterminate value.
     *
     */
    public TimePositionType(final TimeIndeterminateValueType indeterminatePosition){
        this.indeterminatePosition = indeterminatePosition;
        value = "";
    }

    /**
     * build a simple Timposition with only a value from a timestamp.
     *
     * @param value a date.
     */
    public TimePositionType(final Timestamp time){
        this.value = time.toString();
    }

    /**
     * build a simple Timposition with only a value from a timestamp.
     *
     * @param value a date.
     */
    public TimePositionType(final Date time){
        synchronized (formatter) {
            this.value = formatter.format(time);
        }
    }

    /**
     * The simple type gml:TimePositionUnion is a union of XML Schema simple types
     * which instantiate the subtypes for temporal position described in ISO 19108.
     * An ordinal era may be referenced via URI.
     * A decimal value may be used to indicate the distance from the scale origin .
     * time is used for a position that recurs daily (see ISO 19108:2002 5.4.4.2).
     * Finally, calendar and clock forms that support the representation of time in systems based on years,
     * months, days, hours, minutes and seconds, in a notation following ISO 8601,
     * are assembled by gml:CalDate Gets the value of the value property.
     *
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValue(Date value) {
        synchronized (formatter) {
            this.value = formatter.format(value);
        }
    }

    /**
     * Gets the value of the frame property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFrame() {
        return frame;
    }

    /**
     * Sets the value of the frame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFrame(final String value) {
        this.frame = value;
    }

    /**
     * Gets the value of the calendarEraName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCalendarEraName() {
        return calendarEraName;
    }

    /**
     * Sets the value of the calendarEraName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCalendarEraName(final String value) {
        this.calendarEraName = value;
    }

    /**
     * Gets the value of the indeterminatePosition property.
     *
     * @return
     *     possible object is
     *     {@link TimeIndeterminateValueType }
     *
     */
    public TimeIndeterminateValueType getIndeterminatePosition() {
        return indeterminatePosition;
    }

    /**
     * Sets the value of the indeterminatePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeIndeterminateValueType }
     *
     */
    public void setIndeterminatePosition(final TimeIndeterminateValueType value) {
        this.indeterminatePosition = value;
    }

    @Override
    public TemporalPosition anyOther() {
        return null;
    }

    @Override
    public Date getDate() {
        if (value != null && !value.isEmpty()) {
            try {
                synchronized (formatter) {
                    return formatter.parse(value);
                }
            } catch (ParseException ex) {
                Logger.getLogger(TimePositionType.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        return null;
    }

    @Override
    public Time getTime() {
        return Time.valueOf(value);
    }

    @Override
    public InternationalString getDateTime() {
        return new SimpleInternationalString(value);
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TimePositionType) {
            final TimePositionType that = (TimePositionType) object;
            return Utilities.equals(this.calendarEraName,       that.calendarEraName)       &&
                   Utilities.equals(this.frame,                 that.frame)                 &&
                   Utilities.equals(this.indeterminatePosition, that.indeterminatePosition) &&
                   Utilities.equals(this.value,                 that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + (this.calendarEraName != null ? this.calendarEraName.hashCode() : 0);
        hash = 97 * hash + (this.frame != null ? this.frame.hashCode() : 0);
        hash = 97 * hash + (this.indeterminatePosition != null ? this.indeterminatePosition.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        try {

            if (calendarEraName != null) {
                s.append("calendarEraName:").append(calendarEraName).append('\n');
            }
            if (frame != null) {
                s.append("frame:").append(frame).append('\n');
            }
            if (indeterminatePosition != null) {
                s.append("indeterminatePosition:").append(indeterminatePosition.value()).append('\n');
            }

            final SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy HH:mm:ss z");
            final Date date;
            synchronized (formatter) {
                date = formatter.parse(value);
            }
            s.append(sdf.format(date));

        } catch (ParseException ex) {
            Logger.getLogger(TimePositionType.class.getName()).log(Level.WARNING, null, ex);
        }

        return s.toString();
    }

}
