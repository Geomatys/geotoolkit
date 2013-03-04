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
package org.geotoolkit.internal.image.io;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Localized;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.SpatialImageWriter;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.util.logging.Logging;


/**
 * Utilities methods for emitting warnings.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08
 * @module
 */
public final class Warnings extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Warnings() {
    }

    /**
     * Logs the given record to the given object, which may or may not be an instance
     * of {@link WarningProducer}.
     *
     * @param  target The object where to log the message, or {@code null}.
     * @param  record The message to log.
     * @return {@code true} if the message has been sent to at least one warning listener.
     */
    public static boolean log(final Object target, final LogRecord record) {
        if (target instanceof WarningProducer) {
            return ((WarningProducer) target).warningOccurred(record);
        } else {
            record.setLoggerName(WarningProducer.LOGGER.getName());
            WarningProducer.LOGGER.log(record);
            return false;
        }
    }

    /**
     * Convenience method for logging a warning from the given exception.
     * We put the name of the exception class in the message only if the exception does
     * not provide a localized message, or that message is made of only one word.
     *
     * @param  plugin The object invoking this method, or {@code null}.
     * @param  level  The logging level, or {@code null} for the default one.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  exception The exception to log.
     */
    public static void log(final WarningProducer plugin, Level level,
            final Class<?> caller, final String method, final Exception exception)
    {
        String message = exception.getLocalizedMessage();
        if (message == null || ((message = message.trim()).indexOf(' ') < 0)) {
            final String word = message;
            message = Classes.getShortClassName(exception);
            if (word != null) {
                message = message + ": " + word;
            }
        }
        log(plugin, level, caller, method, message);
    }

    /**
     * Convenience method for logging a warning from the given message.
     *
     * @param  plugin The object invoking this method, or {@code null}.
     * @param  level  The logging level, or {@code null} for the default one.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  message The message to log.
     */
    public static void log(final WarningProducer plugin, Level level,
            final Class<?> caller, final String method, final String message)
    {
        if (level == null) {
            level = Level.WARNING;
        }
        final LogRecord record = new LogRecord(level, message);
        if (plugin != null) {
            record.setSourceClassName(caller.getCanonicalName());
            record.setSourceMethodName(method);
            plugin.warningOccurred(record);
        } else {
            Logging.log(caller, method, record);
        }
    }

    /**
     * Convenience method for logging a warning from the given method.
     *
     * @param  plugin The object invoking this method, or {@code null}.
     * @param  level  The logging level, or {@code null} for the default one.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  key The key from the error resource bundle to use for creating a message.
     * @param  arguments The arguments to be used together with the key for building the message.
     * @throws ClassCastException If the given plugin is not an {@link SpatialImageReader}
     *         or {@link SpatialImageWriter}.
     */
    public static void log(final WarningProducer plugin, Level level,
            final Class<?> caller, final String method, final int key, final Object... arguments)
    {
        if (level == null) {
            level = Level.WARNING;
        }
        final LogRecord record = Errors.getResources(plugin != null ? plugin.getLocale() : null)
                .getLogRecord(level, key, arguments);
        if (plugin != null) {
            record.setSourceClassName(caller.getCanonicalName());
            record.setSourceMethodName(method);
            plugin.warningOccurred(record);
        } else {
            Logging.log(caller, method, record);
        }
    }

    /**
     * Returns the error message from the given resource key and arguments.
     * The key shall be one of the {@link org.geotoolkit.resources.Errors.Keys} constants.
     * This is used for formatting the message in {@link javax.imageio.IIOException}.
     *
     * @param  plugin The object invoking this method, or {@code null}.
     * @param  key The key from the error resource bundle to use for creating a message.
     * @param  arguments The arguments to be used together with the key for building the message.
     * @return The configured record to log.
     */
    public static String message(final Localized plugin, final int key, final Object... arguments) {
        return Errors.getResources(plugin != null ? plugin.getLocale() : null).getString(key, arguments);
    }
}
