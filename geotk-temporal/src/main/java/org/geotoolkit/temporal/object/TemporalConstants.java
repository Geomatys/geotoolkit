/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

/**
 * Temporal constants.
 *
 * Note: consider using {@link java.time.temporal.ChronoUnit#getDuration()} instead.
 *
 * @author Johann sorel (Geomatys)
 */
public final class TemporalConstants {
    /**
     * The number of millisecond in one year.
     */
    public static final long YEAR_MS = 31536000000L;
    /**
     * The number of millisecond in one month.
     */
    public static final long MONTH_MS = 2628000000L;
    /**
     * The number of millisecond in one week.
     */
    public static final long WEEK_MS = 604800000L;
    /**
     * The number of millisecond in one day.
     */
    public static final long DAY_MS = 86400000L;
    /**
     * The number of millisecond in one hour.
     */
    public static final long HOUR_MS = 3600000L;
    /**
     * The number of millisecond in one minute.
     */
    public static final long MINUTE_MS = 60000L;
    /**
     * The number of millisecond in one second.
     */
    public static final long SECOND_MS = 1000L;

    private TemporalConstants() {
    }
}
