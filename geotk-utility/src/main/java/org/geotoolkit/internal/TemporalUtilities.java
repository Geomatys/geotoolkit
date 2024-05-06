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
import org.apache.sis.pending.temporal.DefaultTemporalFactory;

import org.opengis.temporal.Period;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalFactory;

import org.geotoolkit.lang.Static;


/**
 * Utilities related to ISO 19108 objects. This class may disappear after we reviewed
 * the GeoAPI-pending temporal interfaces.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @author  Guilhem Legal (Geomatys)
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
}
