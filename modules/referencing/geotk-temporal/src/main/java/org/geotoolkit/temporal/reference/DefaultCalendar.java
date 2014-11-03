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
package org.geotoolkit.temporal.reference;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.temporal.object.DefaultCalendarDate;
import org.geotoolkit.temporal.object.DefaultDateAndTime;
import org.geotoolkit.temporal.object.DefaultJulianDate;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.Clock;
import org.opengis.temporal.ClockTime;
import org.opengis.temporal.JulianDate;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.TemporalCoordinateSystem;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 * A discrete temporal reference system that provides a
 * basis for defining temporal position to a resolution of one day.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @version 4.0
 * @since   4.0
 * @see TemporalReferenceSystem
 */
@XmlType(name = "TimeCalendar_Type", propOrder = {
    "referenceFrame"
})
@XmlRootElement(name = "TimeCalendar")
public class DefaultCalendar extends DefaultTemporalReferenceSystem implements Calendar {

    /**
     * The {@linkplain CalendarEra calendar eras} associated with the calendar being described.
     */
    private Collection<CalendarEra> referenceFrame;
    
    /**
     * The {@linkplain Clock time basis} that is use with this calendar to define 
     * temporal position within a calendar day.
     */
    private Clock timeBasis;

    /**
     * Creates a new {@link Calendar} implementation initialize by given parameters.
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalCRS#DefaultTemporalCRS(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS)  super-class constructor}.
     * 
     * @param properties The properties to be given to the coordinate reference system.
     * @param datum The datum.
     * @param cs The coordinate system.
     * @param referenceFrame The {@linkplain CalendarEra calendar eras} associated with the calendar being described.
     * @param timeBasis The {@linkplain Clock time basis} that is use with this calendar to define temporal position within a calendar day.
     * @see DefaultTemporalReferenceSystem#DefaultTemporalReferenceSystem(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS) 
     */
    public DefaultCalendar(Map<String, ?> properties, 
                           Collection<CalendarEra> referenceFrame, Clock timeBasis ) {
        super(properties);
        ArgumentChecks.ensureNonNull("referenceFrame", referenceFrame);
        this.referenceFrame = referenceFrame;
        this.timeBasis      = timeBasis;
    }
    
    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultCalendar() {
        super();
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Calendar to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(Calendar)
     * @throws NullArgumentException if referenceFrame is {@code null}. 
     */
    private DefaultCalendar(final Calendar object) {
        super(object);
        if (object != null) {
            referenceFrame = object.getReferenceFrame();
            ArgumentChecks.ensureNonNull("referenceFrame", referenceFrame);
            timeBasis      = object.getTimeBasis();
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultCalendar}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultCalendar} instance is created using the
     *       {@linkplain #DefaultCalendar(CalendarEra) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultCalendar castOrCopy(final Calendar object) {
        if (object == null || object instanceof DefaultCalendar) {
            return (DefaultCalendar) object;
        }
        return new DefaultCalendar(object);
    }

    /**
     * Converts a {@linkplain CalendarDate date} in this calendar to a
     * {@linkplain JulianDate julian date}.
     * <blockquote><font size="-1">date may be {@code null}, time may be {@code null} but not both.</font></blockquote>
     * 
     * @param date The {@linkplain CalendarDate date} which will be converted, may be {@code null}.
     * @param time The {@linkplain Clock time basis} which will be converted, may be {@code null}.
     * @return {@linkplain CalendarDate date} from this calendar to a {@linkplain JulianDate julian date} convertion.
     */
    @Override
    public JulianDate dateTrans(final CalendarDate calDate, final ClockTime time) {
        JulianDate response;
        if (calDate != null && time != null) {
            DateAndTime dateAndTime = new DefaultDateAndTime(this, calDate.getIndeterminatePosition(), calDate.getCalendarEraName(), calDate.getCalendarDate(), time.getClockTime());
            return dateTrans(dateAndTime);
        }
        GregorianCalendar gc = new GregorianCalendar(-4713, 1, 1);
        gc.set(GregorianCalendar.ERA, GregorianCalendar.BC);
        final int julianGre = 15 + 31 * (10 + 12 * 1582);
        Number coordinateValue = 0;
        
        final Map<String, Object> properties = new HashMap<>();
        final NamedIdentifier name = new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Julian calendar"));
        final Unit<Duration> interval = NonSI.DAY;
        properties.put(IdentifiedObject.NAME_KEY, name);
//        properties.put(TemporalCoordinateSystem.INTERVAL_KEY, interval);
        final TemporalCoordinateSystem refSystem = new DefaultTemporalCoordinateSystem(properties, interval, gc.getTime());
        
//        TemporalCoordinateSystem refSystem = new DefaultTemporalCoordinateSystem(
//                new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Julian calendar")),
//                null, gc.getTime(), new SimpleInternationalString("day"));
        if (calDate != null) {
            int[] cal = calDate.getCalendarDate();
            int year = 0;
            int month = 0;
            int day = 0;
            if (cal.length > 3) {
                throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");
            } else {
                year = cal[0];
                if (cal.length > 0) {
                    month = cal[1];
                }
                if (cal.length > 1) {
                    day = cal[2];
                }
                int julianYear = year;
                if (year < 0) {
                    julianYear++;
                }
                int julianMonth = month;
                if (month > 2) {
                    julianMonth++;
                } else {
                    julianYear--;
                    julianMonth += 13;
                }
                double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001 * julianMonth) + day + 1720995.0);
                if (day + 31 * (month + 12 * year) >= julianGre) {
                    // change over to Gregorian calendar
                    int ja = (int) (0.01 * julianYear);
                    julian += 2 - ja + (0.25 * ja);
                }
                coordinateValue = java.lang.Math.floor(julian);
                response = new DefaultJulianDate(refSystem, null, coordinateValue);
                return response;
            }
        } else if (time != null) {
            Number[] clk = time.getClockTime();
            Number hour = 0;
            Number minute = 0;
            Number second = 0;
            if (clk.length > 3) {
                throw new IllegalArgumentException("The ClockTime Number array is malformed ! see ISO 8601 format.");
            } else {
                hour = clk[0];
                if (clk.length > 0) {
                    minute = clk[1];
                }
                if (clk.length > 1) {
                    second = clk[2];
                }
                double julian = ((hour.doubleValue() - 12) / 24) + (minute.doubleValue() / 1440) + (second.doubleValue() / 86400);
                coordinateValue = julian;
                response = new DefaultJulianDate(refSystem, null, coordinateValue);
                return response;
            }
        } else {
            throw new IllegalArgumentException("the both CalendarDate and ClockTime cannot be null !");
        }
    }

    /**
     * This method is called by the Overrided method dateTrans() which take 2 arguments CalendarDate and ClockTime.
     * @param dateAndTime
     * @return JulianDate
     */
    public JulianDate dateTrans(final DateAndTime dateAndTime) {
        JulianDate response;
        GregorianCalendar gc = new GregorianCalendar(-4713, 1, 1);
        gc.set(GregorianCalendar.ERA, GregorianCalendar.BC);
        final int julianGre = 15 + 31 * (10 + 12 * 1582);
        
        final Map<String, Object> properties = new HashMap<>();
        final NamedIdentifier name = new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Julian calendar"));
//        final InternationalString interval = new SimpleInternationalString("day");
        properties.put(IdentifiedObject.NAME_KEY, name);
//        properties.put(TemporalCoordinateSystem.INTERVAL_KEY, interval);
        final TemporalCoordinateSystem refSystem = new DefaultTemporalCoordinateSystem(properties, NonSI.DAY, gc.getTime());
        
//        TemporalCoordinateSystem refSystem = new DefaultTemporalCoordinateSystem(new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Julian calendar")),
//                null, gc.getTime(), new SimpleInternationalString("day"));
        Number coordinateValue = 0;
        int year = 0, month = 0, day = 0;
        Number hour = 0, minute = 0, second = 0;
        if (dateAndTime == null) {
            throw new IllegalArgumentException("The DateAndTime cannot be null ! ");
        }
        if (dateAndTime.getCalendarDate() != null) {
            int[] cal = dateAndTime.getCalendarDate();
            if (cal.length > 3) {
                throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");
            } else {
                year = cal[0];
                if (cal.length > 0) {
                    month = cal[1];
                }
                if (cal.length > 1) {
                    day = cal[2];
                }
                int julianYear = year;
                if (year < 0) {
                    julianYear++;
                }
                int julianMonth = month;
                if (month > 2) {
                    julianMonth++;
                } else {
                    julianYear--;
                    julianMonth += 13;
                }
                double julian = (java.lang.Math.floor(365.25 * julianYear) + java.lang.Math.floor(30.6001 * julianMonth) + day + 1720995.0);
                if (day + 31 * (month + 12 * year) >= julianGre) {
                    int ja = (int) (0.01 * julianYear);
                    julian += 2 - ja + (0.25 * ja);
                }
                coordinateValue = java.lang.Math.floor(julian);
            }
        }
        if (dateAndTime.getClockTime() != null) {
            Number[] clk = dateAndTime.getClockTime();
            if (clk.length > 3) {
                throw new IllegalArgumentException("The ClockTime Number array is malformed ! see ISO 8601 format.");
            } else {
                hour = clk[0];
                if (clk.length > 0) {
                    minute = clk[1];
                }
                if (clk.length > 1) {
                    second = clk[2];
                }
                double julian = ((hour.doubleValue() - 12) / 24) + (minute.doubleValue() / 1440) + (second.doubleValue() / 86400);
                coordinateValue = coordinateValue.doubleValue() + julian;
            }
        }
        response = new DefaultJulianDate(refSystem, null, coordinateValue);
        return response;
    }

    /**
     * Returns convertion of {@linkplain JulianDate julian date} to a {@linkplain CalendarDate date}
     * in this calendar.
     * 
     * @param julian The {@linkplain JulianDate julian date} which will be converted.
     * @return {@linkplain JulianDate julian date} to a {@linkplain CalendarDate date} convertion.
     */
    @Override
    public CalendarDate julTrans(final JulianDate jdt) {
        if (jdt == null)
            return null;
        
        CalendarDate response = null;

        int JGREG = 15 + 31 * (10 + 12 * 1582);
        int jalpha, ja, jb, jc, jd, je, year, month, day;
        ja = (int) jdt.getCoordinateValue().intValue();
        if (ja >= JGREG) {
            jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
            ja = ja + 1 + jalpha - jalpha / 4;
        }

        jb = ja + 1524;
        jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
        jd = 365 * jc + jc / 4;
        je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;
        if (month > 12) {
            month = month - 12;
        }
        year = jc - 4715;
        if (month > 2) {
            year--;
        }
        if (year <= 0) {
            year--;
        }
        int[] calendarDate = {year, month, day};
        response = new DefaultCalendarDate(this, null, null, calendarDate);
        return response;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object object, ComparisonMode mode) {
        if (object instanceof Calendar) {
            final Calendar that = (Calendar) object;

            return Objects.equals(this.getName(), that.getName()) 
                    && Objects.equals(this.getDomainOfValidity(), that.getDomainOfValidity())
                    && Objects.equals(this.referenceFrame, that.getReferenceFrame()) 
                    &&Objects.equals(this.timeBasis, that.getTimeBasis());
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected long computeHashCode() {
        int hash = this.getName().hashCode() + this.getDomainOfValidity().hashCode();
        hash = 42 * hash + (this.timeBasis != null ? this.timeBasis.hashCode() : 0);
        hash = 37 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n').append("Calendar : ").append('\n');
        if (timeBasis != null) {
            s.append("clock:").append(timeBasis).append('\n');
        }
        if (referenceFrame != null) {
            s.append("basis:").append(referenceFrame).append('\n');
        }
        return super.toString().concat("\n").concat(s.toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @XmlElement(name = "referenceFrame", required = true)
    public Collection<CalendarEra> getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Clock getTimeBasis() {
         return timeBasis;
    }
}
