/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.logging.*;
import net.jcip.annotations.ThreadSafe;
import org.geotoolkit.io.X364;


/**
 * A formatter writing log messages on a single line. Compared to {@link SimpleFormatter}, this
 * formatter uses only one line per message instead of two. For example a message formatted by
 * {@code MonolineFormatter} looks like:
 *
 * {@preformat text
 *     FINE   A log message logged with level FINE from the "org.geotoolkit.util" logger.
 * }
 *
 * By default, {@code MonolineFormatter} displays only the level and the message. Additional
 * fields can be formatted if {@link #setTimeFormat} or {@link #setSourceFormat} methods are
 * invoked with a non-null argument. The format can also be set from the
 * {@code jre/lib/logging.properties} file. For example, user can cut and paste the following
 * properties into {@code logging.properties}:
 *
 * {@preformat text
 *     ###########################################################################
 *     # Properties for the Geotoolkit.org's MonolineFormatter.
 *     # By default, the monoline formatter display only the level
 *     # and the message. Additional fields can be specified here:
 *     #
 *     #  time:   If set, writes the time elapsed since the initialization.
 *     #          The argument specifies the output pattern. For example, the
 *     #          pattern "HH:mm:ss.SSSS" displays the hours, minutes, seconds
 *     #          and milliseconds.
 *     #
 *     #  source: If set, writes the source logger name or the source class name.
 *     #          Valid argument values are "none", "logger:short", "logger:long",
 *     #          "class:short" and "class:long".
 *     ###########################################################################
 *     org.geotoolkit.util.logging.MonolineFormatter.time = HH:mm:ss.SSS
 *     org.geotoolkit.util.logging.MonolineFormatter.source = class:short
 * }
 *
 * The example below sets the {@code MonolineFormatter} for the whole system with level
 * {@code FINE} and {@code "Cp850"} page encoding (which is appropriate for some DOS
 * console on Windows).
 *
 * {@preformat text
 *     java.util.logging.ConsoleHandler.formatter = org.geotoolkit.util.logging.MonolineFormatter
 *     java.util.logging.ConsoleHandler.encoding = Cp850
 *     java.util.logging.ConsoleHandler.level = FINE
 * }
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.18
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.util.logging.MonolineFormatter}.
 */
@ThreadSafe
public class MonolineFormatter extends org.apache.sis.util.logging.MonolineFormatter {
    /**
     * Constructs a default {@code MonolineFormatter}. Formatters are often associated to
     * a particular handler. If this handler is known, giving it at construction time can
     * help this formatter to configure itself. This handler is only a hint - no reference
     * to this handler will be kept; it could even be the wrong handler without breaking
     * this formatter. The output may just be a little bit less "nice".
     *
     * @param handler The handler to be used with this formatter, or {@code null} if unknown.
     */
    public MonolineFormatter(final Handler handler) {
        super(handler);
        /*
         * Configures this formatter according the properties, if any.
         */
        final LogManager manager = LogManager.getLogManager();
        final String classname = MonolineFormatter.class.getName();
        try {
            setTimeFormat(manager.getProperty(classname + ".time"));
        } catch (IllegalArgumentException exception) {
            // Can't use the logging framework, since we are configuring it.
            // Display the exception name only, not the trace.
            System.err.println(exception);
        }
        try {
            setSourceFormat(manager.getProperty(classname + ".source"));
        } catch (IllegalArgumentException exception) {
            System.err.println(exception);
        }
    }

    /**
     * Sets the color to use for the given level. This method should be invoked only if this
     * formatter is associated to a {@link Handler} writing to an ANSI X3.64 compatible terminal.
     *
     * @param level The level for which to set a new color.
     * @param color The new color, or {@code null} if none.
     *
     * @since 3.00
     */
    public synchronized void setLevelColor(final Level level, final X364 color) {
        String name = null;
        if (color != null) {
            name = org.apache.sis.internal.util.X364.valueOf(color.name()).color;
        }
        setLevelColor(level, name);
    }

    /**
     * Clears all colors setting. If this formatter was inserting X3.64 escape sequences
     * for colored output, invoking this method will force the formatting of plain text.
     *
     * @since 3.00
     */
    public synchronized void clearLevelColors() {
        resetLevelColors(false);
    }

    /**
     * Setups a {@code MonolineFormatter} for the specified logger and its children. This method
     * searches for all instances of {@link ConsoleHandler} using the {@link SimpleFormatter}. If
     * such instances are found, they are replaced by a single instance of {@code MonolineFormatter}.
     * If no such {@link ConsoleHandler} are found, then a new one is created with a new
     * {@code MonolineFormatter}.
     * <p>
     * In addition, this method can set the handler levels. If the level is non-null, then every
     * {@link Handler}s using the monoline formatter may be set to the specified level. Whatever
     * the given level is used or not depends on current configuration. The choice is based on
     * heuristic rules that may change in any future version. Developers are encouraged to avoid
     * non-null level except for debugging purpose, since a user trying to configure his logging
     * properties file may find confusing to see his setting ignored.
     *
     * @param  logger The base logger to apply the change on.
     * @param  level The desired level, or {@code null} if no level should be set.
     * @return The registered {@code MonolineFormatter}, or {@code null} if the registration failed.
     *         If non-null, the formatter output can be configured using the {@link #setTimeFormat}
     *         and {@link #setSourceFormat} methods.
     */
    public static org.apache.sis.util.logging.MonolineFormatter configureConsoleHandler(final Logger logger, final Level level) {
        return install(logger, level);
    }
}
