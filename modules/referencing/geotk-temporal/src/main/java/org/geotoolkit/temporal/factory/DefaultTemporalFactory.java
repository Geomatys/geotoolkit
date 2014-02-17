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
 * @module pending
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
    public Calendar createCalendar(final ReferenceIdentifier name, final Extent domainOfValidit) {
        return new DefaultCalendar(name, domainOfValidit);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CalendarDate createCalendarDate(final TemporalReferenceSystem frame,
            final IndeterminateValue indeterminatePosition, final InternationalString calendarEraName,
            final int[] calendarDate) {
        return new DefaultCalendarDate(frame, indeterminatePosition, calendarEraName, calendarDate);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CalendarEra createCalendarEra(final InternationalString name, final InternationalString referenceEvent,
            final CalendarDate referenceDate, final JulianDate julianReference, final Period epochOfUse) {
        return new DefaultCalendarEra(name, referenceEvent, referenceDate, julianReference, epochOfUse);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Clock createClock(final ReferenceIdentifier name, final Extent domainOfValidity,
            final InternationalString referenceEvent, final ClockTime referenceTime, final ClockTime utcReference) {
        return new DefaultClock(name, domainOfValidity, referenceEvent, referenceTime, utcReference);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ClockTime createClockTime(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition,
            final Number[] clockTime) {
        return new DefaultClockTime(frame, indeterminatePosition, clockTime);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DateAndTime createDateAndTime(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition,
            final InternationalString calendarEraName, final int[] calendarDate, final Number[] clockTime) {
        return new DefaultDateAndTime(frame, indeterminatePosition, calendarEraName, calendarDate, clockTime);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IntervalLength createIntervalLenght(final Unit unit, final int radix, final int factor, final int value) {
        return new DefaultIntervalLength(unit, radix, factor, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JulianDate createJulianDate(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition,
            final Number coordinateValue) {
        return new DefaultJulianDate(frame, indeterminatePosition, coordinateValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalEra createOrdinalEra(final InternationalString name, final Date beginning, final Date end,
            final Collection<OrdinalEra> composition) {
        return new DefaultOrdinalEra(name, beginning, end, composition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalPosition createOrdinalPosition(final TemporalReferenceSystem frame,
            final IndeterminateValue indeterminatePosition, final OrdinalEra ordinalPosition) {
        return new DefaultOrdinalPosition(frame, indeterminatePosition, ordinalPosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OrdinalReferenceSystem createOrdinalReferenceSystem(final ReferenceIdentifier name,
            final Extent domainOfValidity, final Collection<OrdinalEra> ordinalEraSequence) {
        return new DefaultOrdinalReferenceSystem(name, domainOfValidity, ordinalEraSequence);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PeriodDuration createPeriodDuration(final InternationalString years, final InternationalString months,
            final InternationalString week, final InternationalString days, final InternationalString hours,
            final InternationalString minutes, final InternationalString seconds) {
        return new DefaultPeriodDuration(years, months, week, days, hours, minutes, seconds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalCoordinate createTemporalCoordinate(final TemporalReferenceSystem frame,
            final IndeterminateValue indeterminatePosition, final Number coordinateValue) {
        return new DefaultTemporalCoordinate(frame, indeterminatePosition, coordinateValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalCoordinateSystem createTemporalCoordinateSystem(final ReferenceIdentifier name,
            final Extent domainOfValidity, final Date origin, final InternationalString interval) {
        return new DefaultTemporalCoordinateSystem(name, domainOfValidity, origin, interval);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalPosition createTemporalPosition(final TemporalReferenceSystem frame,
            final IndeterminateValue indeterminatePosition) {
        return new DefaultTemporalPosition(frame, indeterminatePosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TemporalReferenceSystem createTemporalReferenceSystem(final ReferenceIdentifier name,
            final Extent domainOfValidity) {
        return new DefaultTemporalReferenceSystem(name, domainOfValidity);
    }

}
