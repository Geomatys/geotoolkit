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
package org.geotoolkit.temporal.factory;

import java.util.Collection;
import java.util.Date;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.Extent;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.Clock;
import org.opengis.temporal.ClockTime;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;
import org.opengis.temporal.IntervalLength;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalPosition;
import org.opengis.temporal.OrdinalReferenceSystem;
import org.opengis.temporal.Period;
import org.opengis.temporal.PeriodDuration;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;
import org.opengis.temporal.TemporalFactory;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import org.apache.sis.util.logging.Logging;


/**
 * Delegates to {@link org.geotoolkit.gml.GMLTemporalFactory} if it exists,
 * or fallback on {@link DefaultTemporalFactory} otherwise.
 *
 * @deprecated Temporary hack to be removed after we merged the two factories into a single one.
 */
@Deprecated
public final class GMLOrDefaultFactory implements TemporalFactory {
    private final TemporalFactory delegate;

    public GMLOrDefaultFactory() {
        TemporalFactory delegate;
        try {
            delegate = (TemporalFactory) Class.forName("org.geotoolkit.gml.GMLTemporalFactory").newInstance();
        } catch (Exception e) {
            Logging.recoverableException(GMLOrDefaultFactory.class, "<init>", e);
            delegate = new DefaultTemporalFactory();
        }
        this.delegate = delegate;
    }

    @Override
    public Calendar createCalendar(Identifier name, Extent domainOfValidity) {
        return delegate.createCalendar(name, domainOfValidity);
    }

    @Override
    public Calendar createCalendar(Identifier name, Extent domainOfValidity, Collection<CalendarEra> referenceFrame, Clock timeBasis) {
        return delegate.createCalendar(name, domainOfValidity, referenceFrame, timeBasis);
    }

    @Override
    public CalendarDate createCalendarDate(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, InternationalString calendarEraName, int[] calendarDate) {
        return delegate.createCalendarDate(frame, indeterminatePosition, calendarEraName, calendarDate);
    }

    @Override
    public CalendarEra createCalendarEra(InternationalString name, InternationalString referenceEvent, CalendarDate referenceDate, JulianDate julianReference, Period epochOfUse) {
        return delegate.createCalendarEra(name, referenceEvent, referenceDate, julianReference, epochOfUse);
    }

    @Override
    public Clock createClock(Identifier name, Extent domainOfValidity, InternationalString referenceEvent, ClockTime referenceTime, ClockTime utcReference) {
        return delegate.createClock(name, domainOfValidity, referenceEvent, referenceTime, utcReference);
    }

    @Override
    public ClockTime createClockTime(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, Number[] clockTime) {
        return delegate.createClockTime(frame, indeterminatePosition, clockTime);
    }

    @Override
    public DateAndTime createDateAndTime(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, InternationalString calendarEraName, int[] calendarDate, Number[] clockTime) {
        return delegate.createDateAndTime(frame, indeterminatePosition, calendarEraName, calendarDate, clockTime);
    }

    @Override
    public Instant createInstant(Date date) {
        return delegate.createInstant(date);
    }

    @Override
    public IntervalLength createIntervalLenght(Unit unit, int radix, int factor, int value) {
        return delegate.createIntervalLenght(unit, radix, factor, value);
    }

    @Override
    public JulianDate createJulianDate(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, Number coordinateValue) {
        return delegate.createJulianDate(frame, indeterminatePosition, coordinateValue);
    }

    @Override
    public OrdinalEra createOrdinalEra(InternationalString name, Date beginning, Date end, Collection<OrdinalEra> member) {
        return delegate.createOrdinalEra(name, beginning, end, member);
    }

    @Override
    public OrdinalPosition createOrdinalPosition(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, OrdinalEra ordinalPosition) {
        return delegate.createOrdinalPosition(frame, indeterminatePosition, ordinalPosition);
    }

    @Override
    public OrdinalReferenceSystem createOrdinalReferenceSystem(Identifier name, Extent domainOfValidity, Collection<OrdinalEra> ordinalEraSequence) {
        return delegate.createOrdinalReferenceSystem(name, domainOfValidity, ordinalEraSequence);
    }

    @Override
    public Period createPeriod(Instant begin, Instant end) {
        return delegate.createPeriod(begin, end);
    }

    @Override
    public PeriodDuration createPeriodDuration(InternationalString years, InternationalString months, InternationalString week, InternationalString days, InternationalString hours, InternationalString minutes, InternationalString seconds) {
        return delegate.createPeriodDuration(years, months, week, days, hours, minutes, seconds);
    }

    @Override
    public TemporalCoordinate createTemporalCoordinate(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition, Number coordinateValue) {
        return delegate.createTemporalCoordinate(frame, indeterminatePosition, coordinateValue);
    }

    @Override
    public TemporalCoordinateSystem createTemporalCoordinateSystem(Identifier name, Extent domainOfValidity, Date origin, Unit<Duration> interval) {
        return delegate.createTemporalCoordinateSystem(name, domainOfValidity, origin, interval);
    }

    @Override
    public TemporalPosition createTemporalPosition(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition) {
        return delegate.createTemporalPosition(frame, indeterminatePosition);
    }

    @Override
    public TemporalReferenceSystem createTemporalReferenceSystem(Identifier name, Extent domainOfValidity) {
        return delegate.createTemporalReferenceSystem(name, domainOfValidity);
    }
}
