/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */


package org.geotoolkit.temporal.factory;

import java.util.Collection;
import java.util.Date;
import javax.measure.unit.Unit;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.temporal.object.DefaultCalendarDate;
import org.geotoolkit.temporal.object.DefaultClockTime;
import org.geotoolkit.temporal.object.DefaultDateAndTime;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultIntervalLength;
import org.geotoolkit.temporal.object.DefaultJulianDate;
import org.geotoolkit.temporal.object.DefaultOrdinalPosition;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultPeriodDuration;
import org.geotoolkit.temporal.object.DefaultPosition;
import org.geotoolkit.temporal.object.DefaultTemporalCoordinate;
import org.geotoolkit.temporal.object.DefaultTemporalPosition;
import org.geotoolkit.temporal.reference.DefaultCalendar;
import org.geotoolkit.temporal.reference.DefaultCalendarEra;
import org.geotoolkit.temporal.reference.DefaultClock;
import org.geotoolkit.temporal.reference.DefaultOrdinalEra;
import org.geotoolkit.temporal.reference.DefaultOrdinalReferenceSystem;
import org.geotoolkit.temporal.reference.DefaultTemporalCoordinateSystem;
import org.geotoolkit.temporal.reference.DefaultTemporalReferenceSystem;

import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.ReferenceIdentifier;
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
import org.opengis.temporal.Position;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;
import org.opengis.temporal.TemporalFactory;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DefaultTemporalFactory extends Factory implements TemporalFactory {

    public DefaultTemporalFactory() {
        super();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Period createPeriod(final Instant begin, final Instant end) {
        return new DefaultPeriod(begin, end);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Instant createInstant(final Position instant) {
        return new DefaultInstant(instant);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Position createPosition(final Date position) {
        return new DefaultPosition(position);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Calendar createCalendar(ReferenceIdentifier name, Extent domainOfValidit) {
        return new DefaultCalendar(name, domainOfValidit);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CalendarDate createCalendarDate(TemporalReferenceSystem frame,
            IndeterminateValue indeterminatePosition, InternationalString calendarEraName,
            int[] calendarDate) {
        return new DefaultCalendarDate(frame, indeterminatePosition, calendarEraName, calendarDate);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CalendarEra createCalendarEra(InternationalString name, InternationalString referenceEvent,
            CalendarDate referenceDate, JulianDate julianReference, Period epochOfUse) {
        return new DefaultCalendarEra(name, referenceEvent, referenceDate, julianReference, epochOfUse);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Clock createClock(ReferenceIdentifier name, Extent domainOfValidity,
            InternationalString referenceEvent, ClockTime referenceTime, ClockTime utcReference) {
        return new DefaultClock(name, domainOfValidity, referenceEvent, referenceTime, utcReference);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ClockTime createClockTime(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition,
            Number[] clockTime) {
        return new DefaultClockTime(frame, indeterminatePosition, clockTime);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DateAndTime createDateAndTime(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition,
            InternationalString calendarEraName, int[] calendarDate, Number[] clockTime) {
        return new DefaultDateAndTime(frame, indeterminatePosition, calendarEraName, calendarDate, clockTime);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IntervalLength createIntervalLenght(Unit unit, int radix, int factor, int value) {
        return new DefaultIntervalLength(unit, radix, factor, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JulianDate createJulianDate(TemporalReferenceSystem frame, IndeterminateValue indeterminatePosition,
            Number coordinateValue) {
        return new DefaultJulianDate(frame, indeterminatePosition, coordinateValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalEra createOrdinalEra(InternationalString name, Date beginning, Date end,
            Collection<OrdinalEra> composition) {
        return new DefaultOrdinalEra(name, beginning, end, composition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalPosition createOrdinalPosition(TemporalReferenceSystem frame,
            IndeterminateValue indeterminatePosition, OrdinalEra ordinalPosition) {
        return new DefaultOrdinalPosition(frame, indeterminatePosition, ordinalPosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalReferenceSystem createOrdinalReferenceSystem(ReferenceIdentifier name,
            Extent domainOfValidity, Collection<OrdinalEra> ordinalEraSequence) {
        return new DefaultOrdinalReferenceSystem(name, domainOfValidity, ordinalEraSequence);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PeriodDuration createPeriodDuration(InternationalString years, InternationalString months,
            InternationalString week, InternationalString days, InternationalString hours,
            InternationalString minutes, InternationalString seconds) {
        return new DefaultPeriodDuration(years, months, week, days, hours, minutes, seconds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalCoordinate createTemporalCoordinate(TemporalReferenceSystem frame,
            IndeterminateValue indeterminatePosition, Number coordinateValue) {
        return new DefaultTemporalCoordinate(frame, indeterminatePosition, coordinateValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalCoordinateSystem createTemporalCoordinateSystem(ReferenceIdentifier name,
            Extent domainOfValidity, Date origin, InternationalString interval) {
        return new DefaultTemporalCoordinateSystem(name, domainOfValidity, origin, interval);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalPosition createTemporalPosition(TemporalReferenceSystem frame,
            IndeterminateValue indeterminatePosition) {
        return new DefaultTemporalPosition(frame, indeterminatePosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalReferenceSystem createTemporalReferenceSystem(ReferenceIdentifier name,
            Extent domainOfValidity) {
        return new DefaultTemporalReferenceSystem(name, domainOfValidity);
    }

}
