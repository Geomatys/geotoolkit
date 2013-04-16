/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.util.logging;

import java.util.logging.Level;
import java.util.concurrent.TimeUnit;


/**
 * Logging levels for measurements of execution time. Different logging levels - {@link #SLOW},
 * {@link #SLOWER} and {@link #SLOWEST} - are provided in order to log only the events taking
 * more than some time duration. For example the console could log only the slowest events,
 * while a file could log all events considered slow.
 * <p>
 * Every levels defined in this class have a {@linkplain #intValue() value} between the
 * {@link Level#FINE} and {@link Level#CONFIG} values. Consequently performance logging are
 * disabled by default, and enabling them imply enabling configuration logging too. This is
 * done that way because the configuration typically have a significant impact on performance.
 *
 * {@section Enabling performance logging}
 * Performance logging can be enabled in various ways. Among others:
 * <p>
 * <ul>
 *   <li>The {@code $JAVA_HOME/lib/logging.properties} file can be edited in order to log
 *       messages at the {@code FINE} level, at least for the packages of interest.</li>
 *   <li>The {@link Logging#forceMonolineConsoleOutput(Level)} convenience method
 *       can be invoked.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.util.logging.PerformanceLevel}.
 */
@Deprecated
public final class PerformanceLevel {
    /**
     * The level for logging all time measurements, regardless of their duration.
     * The {@linkplain #intValue() value} of this level is 600.
     */
    public static final org.apache.sis.util.logging.PerformanceLevel PERFORMANCE = org.apache.sis.util.logging.PerformanceLevel.PERFORMANCE;

    /**
     * The level for logging relatively slow events. By default, only events having an execution
     * time equals or greater than 0.1 second are logged at this level. However this threshold
     * can be changed by a call to <code>SLOW.{@linkplain #setMinDuration(long, TimeUnit)}</code>.
     */
    public static final org.apache.sis.util.logging.PerformanceLevel SLOW = org.apache.sis.util.logging.PerformanceLevel.SLOW;

    /**
     * The level for logging only events slower than the ones logged at the {@link #SLOW} level.
     * By default, only events having an execution time equals or greater than 1 second are
     * logged at this level. However this threshold can be changed by a call to
     * <code>SLOWER.{@linkplain #setMinDuration(long, TimeUnit)}</code>.
     */
    public static final org.apache.sis.util.logging.PerformanceLevel SLOWER = org.apache.sis.util.logging.PerformanceLevel.SLOWER;

    /**
     * The level for logging only slowest events. By default, only events having an execution
     * time equals or greater than 5 seconds are logged at this level. However this threshold
     * can be changed by a call to <code>SLOWEST.{@linkplain #setMinDuration(long, TimeUnit)}</code>.
     */
    public static final org.apache.sis.util.logging.PerformanceLevel SLOWEST = org.apache.sis.util.logging.PerformanceLevel.SLOWEST;

    private PerformanceLevel() {
    }

    /**
     * Returns the level to use for logging an event of the given duration.
     *
     * @param  duration The event duration.
     * @param  unit The unit of the given duration value.
     * @return The level to use for logging an event of the given duration.
     */
    public static org.apache.sis.util.logging.PerformanceLevel forDuration(long duration, final TimeUnit unit) {
        return org.apache.sis.util.logging.PerformanceLevel.forDuration(duration, unit);
    }
}
