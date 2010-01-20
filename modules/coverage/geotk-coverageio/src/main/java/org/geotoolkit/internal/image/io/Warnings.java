/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.image.io;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.SpatialImageWriter;


/**
 * Utilities methods for emitting warnings. The proper place for those methods should be
 * {@link org.geotoolkit.image.io.SpatialImageReader} but we don't make them part of the
 * public API.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
@Static
public final class Warnings {
    /**
     * Do not allow instantiation of this class.
     */
    private Warnings() {
    }

    /**
     * Sends the given record to the given image reader or writer.
     *
     * @param  plugin The {@link SpatialImageReader} or {@link SpatialImageWriter} where to log.
     * @param  record The record to log.
     * @throws ClassCastException If the given plugin is not an {@link SpatialImageReader}
     *         or {@link SpatialImageWriter}.
     */
    private static void log(final Localized plugin, final LogRecord record) {
        if (plugin instanceof SpatialImageReader) {
            ((SpatialImageReader) plugin).warningOccurred(record);
        } else {
            ((SpatialImageWriter) plugin).warningOccurred(record);
        }
    }

    /**
     * Convenience method for logging a warning from the given exception.
     * We put the name of the exception class in the message only if the exception does
     * not provide a localized message, or that message is made of only one word.
     *
     * @param  plugin The {@link SpatialImageReader} or {@link SpatialImageWriter} invoking this method.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  exception The exception to log.
     * @throws ClassCastException If the given plugin is not an {@link SpatialImageReader}
     *         or {@link SpatialImageWriter}.
     */
    public static void log(final Localized plugin, final Class<?> caller, final String method,
            final Exception exception)
    {
        String message = exception.getLocalizedMessage();
        if (message == null || ((message = message.trim()).indexOf(' ') < 0)) {
            final String word = message;
            message = Classes.getShortClassName(exception);
            if (word != null) {
                message = message + ": " + word;
            }
        }
        log(plugin, caller, method, message);
    }

    /**
     * Convenience method for logging a warning from the given message.
     *
     * @param  plugin The {@link SpatialImageReader} or {@link SpatialImageWriter} invoking this method.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  message The message to log.
     * @throws ClassCastException If the given plugin is not an {@link SpatialImageReader}
     *         or {@link SpatialImageWriter}.
     */
    public static void log(final Localized plugin, final Class<?> caller, final String method,
            final String message)
    {
        final LogRecord record = new LogRecord(Level.WARNING, message);
        record.setSourceClassName(caller.getName());
        record.setSourceMethodName(method);
        log(plugin, record);
    }

    /**
     * Convenience method for logging a warning from the given method.
     *
     * @param  plugin The {@link SpatialImageReader} or {@link SpatialImageWriter} invoking this method.
     * @param  caller The public class which is invoking this method.
     * @param  method The public method which is invoking this method.
     * @param  key The key from the error resource bundle to use for creating a message.
     * @param  arguments The arguments to be used together with the key for building the message.
     * @throws ClassCastException If the given plugin is not an {@link SpatialImageReader}
     *         or {@link SpatialImageWriter}.
     */
    public static void log(final Localized plugin, final Class<?> caller,
            final String method, final int key, final Object... arguments)
    {
        final LogRecord record = Errors.getResources(plugin.getLocale())
                .getLogRecord(Level.WARNING, key, arguments);
        record.setSourceClassName(caller.getName());
        record.setSourceMethodName(method);
        log(plugin, record);
    }

    /**
     * Returns the error message from the given resource key and arguments.
     * The key shall be one of the {@link Errors.Key} constants. This is used
     * for formatting the message in {@link IIOException}.
     *
     * @param  plugin The {@code SpatialImageReader} or {@code SpatialImageWriter} invoking this method.
     * @param  key The key from the error resource bundle to use for creating a message.
     * @param  arguments The arguments to be used together with the key for building the message.
     * @return The configured record to log.
     */
    public static String message(final Localized plugin, final int key, final Object... arguments) {
        return Errors.getResources(plugin.getLocale()).getString(key, arguments);
    }
}
