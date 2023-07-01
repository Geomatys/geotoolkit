/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.internal;

import java.util.Date;
import org.apache.sis.internal.temporal.DefaultTemporalFactory;

import org.opengis.temporal.Period;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalFactory;
import org.opengis.temporal.PeriodDuration;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.Static;


/**
 * Utilities related to ISO 19108 objects. This class may disappear after we reviewed
 * the GeoAPI-pending temporal interfaces.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @author  Guilhem Legal (Geomatys)
 * @version 3.21
 *
 * @since 3.20
 * @module
 */
public final class TemporalUtilities extends Static {
    /**
     * {@code true} if a warning has already been logged. This is used in order to log at
     * the warning level only once, in order to avoid polluting the logger. All warnings
     * after the first one will be logged at the fine level.
     */
    private static volatile boolean warningLogged;

    /**
     * Do not allow instantiation of this path.
     */
    private TemporalUtilities() {
    }

    /**
     * Creates an instant for the given date using the given factory.
     */
    private static Instant createInstant(final TemporalFactory factory, final Date date) {
        return factory.createInstant(date);
    }

    /**
     * Creates an instant for the given date.
     *
     * @param  time The date for which to create instant.
     * @return The instant.
     */
    public static Instant createInstant(final Date time) {
        return createInstant(DefaultTemporalFactory.provider(), time);
    }

    /**
     * Creates a period for the given begin and end dates. The given arguments can be null if the
     * {@link TemporalFactory#createPosition(Date)} method accepts null dates, which stand for
     * undetermined position.
     *
     * @param  begin The begin date, inclusive.
     * @param  end The end date, inclusive.
     * @return The period.
     */
    public static Period createPeriod(final Date begin, final Date end) {
        final TemporalFactory factory = DefaultTemporalFactory.provider();
        return factory.createPeriod(createInstant(factory, begin), createInstant(factory, end));
    }

    /**
     * Creates a period duration for the given fields. The given arguments can be null if the
     * {@link TemporalFactory#createPeriodDuration()} method accepts null values.
     *
     * @param years the number of years for this period duration.
     * @param months the number of months for this period duration.
     * @param weeks the number of weeks for this period duration.
     * @param days the number of days for this period duration.
     * @param hours the number of hours for this period duration.
     * @param minutes the number of minutes for this period duration.
     * @param seconds the number of seconds for this period duration.
     *
     * @return The period duration.
     *
     * @since 3.21
     */
    public static PeriodDuration createPeriodDuration(final InternationalString years, final InternationalString months, final InternationalString weeks,
            final InternationalString days, final InternationalString hours, final InternationalString minutes, final InternationalString seconds)
    {
        final TemporalFactory factory = DefaultTemporalFactory.provider();
        return factory.createPeriodDuration(years, months, weeks, days, hours, minutes, seconds);
    }
}
