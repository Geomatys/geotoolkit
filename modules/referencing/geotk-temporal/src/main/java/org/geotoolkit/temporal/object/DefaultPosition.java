/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.temporal.object;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.OrdinalPosition;
import org.opengis.temporal.Position;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalPosition;
import org.opengis.util.InternationalString;

/**
 * A union class that consists of one of the data types listed as its attributes.
 * Date, Time, and DateTime are basic data types defined in ISO/TS 19103,
 * and may be used for describing temporal positions referenced to the
 * Gregorian calendar and UTC.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
@XmlType(name = "timePosition_Type", propOrder = {
    "date"
})
@XmlRootElement(name = "TimePosition")
public class DefaultPosition implements Position {

    /**
     * this object represents one of the data types listed as : Date, Time, DateTime, and TemporalPosition with its subtypes
     */
    private Object position;

    /**
     * An Empty constructor that will be removed later.
     * This constructor mustn't be used.
     */
    private DefaultPosition() {
//        this.position = null;
    }

    public DefaultPosition(final Date date) {
        this.position = date;
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Position)
     */
    private DefaultPosition(final Position object) {
        if (object != null) {
            this.position = object.getDate();
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultPosition}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultPosition} instance is created using the
     *       {@linkplain #DefaultPosition(Position) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultPosition castOrCopy(final Position object) {
        if (object == null || object instanceof DefaultPosition) {
            return (DefaultPosition) object;
        }
        return new DefaultPosition(object);
    }

    /**
     * This constructor replace the constructor with further DateTime object which will be included in the futur version of jdk (jdk7).
     * example of datetime argument: format specified by the ISO8601 yyyy-mm-DDTHH:MM:SSZ - example : 2003-02-13T12:28:00.000GMT-08:00.
     * @param datetime
     * @throws java.text.ParseException
     */
    public DefaultPosition(final InternationalString datetime) throws ParseException {
        this.position = TemporalUtilities.getDateFromString(datetime.toString());
    }

    /**
     * This constructor set the position property as a TemporalPosition.
     * @param anyOther
     */
    public DefaultPosition(final TemporalPosition anyOther) {
        this.position = anyOther;
    }

    /**
     * {@linkplain org.opengis.temporal.TemporalPosition} and its subtypes shall be used
     * for describing temporal positions referenced to other reference systems, and may be used for
     * temporal positions referenced to any calendar or clock, including the Gregorian calendar and UTC.
     * @return TemporalPosition
     */
    @Override
    public TemporalPosition anyOther() {
        return (this.position instanceof TemporalPosition) ? (TemporalPosition) position : null;
    }

    /**
     * May be used for describing temporal positions in ISO8601 format referenced to the
     * Gregorian calendar and UTC.
     * @return {@linkplain InternationalString}
     * 
     * @todo all subtypes of TemporalPosition must be implemented.
     */
    @Override
    @XmlValue
    public Date getDate() {
        if (this.position instanceof Date) {
            return (Date) position;
        }
        if (this.position instanceof TemporalPosition) {
            if (this.position instanceof JulianDate) {
                return TemporalUtilities.julianToDate((DefaultJulianDate) position);
            }
            if (this.position instanceof DateAndTime) {
                return TemporalUtilities.dateAndTimeToDate((DateAndTime) position);
            }
            if (this.position instanceof CalendarDate) {
                return TemporalUtilities.calendarDateToDate((CalendarDate) position);
            }
            if (this.position instanceof TemporalCoordinate) {
                return TemporalUtilities.temporalCoordToDate((TemporalCoordinate) position);
            }
            if (this.position instanceof OrdinalPosition) {
                return TemporalUtilities.ordinalToDate((OrdinalPosition) position);
            }
        }
        return null;
    }

    /**
     * May be used for describing temporal positions in ISO8601 format referenced to the
     * Gregorian calendar and UTC.
     * @return {@linkplain InternationalString}
     */
    @Override
    public Time getTime() {
        return (this.position instanceof Time) ? (Time) position : null;
    }

    /**
     * May be used for describing temporal positions in ISO8601 format referenced to the
     * Gregorian calendar and UTC.
     * @return {@linkplain InternationalString}
     */
    @Override
    public InternationalString getDateTime() {
        if (this.position instanceof Date) {
            String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
            SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);
            return new SimpleInternationalString(dateFormat.format(position));
        }
        return null;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultPosition) {
            final DefaultPosition that = (DefaultPosition) object;
            return Objects.equals(this.position, that.position);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.position != null ? this.position.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Position:").append('\n');
        if (position != null) {
            s.append("position:").append(position).append('\n');
        }
        return s.toString();
    }
}

