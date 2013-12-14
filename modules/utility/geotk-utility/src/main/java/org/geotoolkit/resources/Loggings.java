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
package org.geotoolkit.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.text.MessageFormat;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.apache.sis.util.logging.Logging;


/**
 * Locale-dependent resources for logging messages.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public final class Loggings extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.2
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Grid geometry has been adjusted for coverage “{0}”.
         */
        public static final short ADJUSTED_GRID_GEOMETRY_1 = 0;

        /**
         * Ambiguity between inverse flattening and semi minor axis length. Using inverse flattening.
         */
        public static final short AMBIGUOUS_ELLIPSOID = 1;

        /**
         * {3,choice,0#Apply|1#Reuse} operation “{1}” on coverage “{0}” with interpolation “{2}”.
         */
        public static final short APPLIED_OPERATION_4 = 2;

        /**
         * Resampled coverage “{0}” from coordinate system “{1}” (for an image of size {2}×{3}) to
         * coordinate system “{4}” (image size {5}×{6}). JAI operation is “{7}” with “{9}”
         * interpolation on {8,choice,0#packed|1#geophysics} pixels values. Background value is ({10}).
         */
        public static final short APPLIED_RESAMPLE_11 = 3;

        /**
         * Converted “{0}” from “{1}” to “{2}” units. We assume that this is the expected units for
         * computation purpose.
         */
        public static final short APPLIED_UNIT_CONVERSION_3 = 4;

        /**
         * Caching {0}.
         */
        public static final short CACHING_1 = 5;

        /**
         * Failed to bind a “{0}” entry.
         */
        public static final short CANT_BIND_DATASOURCE_1 = 6;

        /**
         * Failed to create a coordinate operation from “{0}” authority factory.
         */
        public static final short CANT_CREATE_COORDINATE_OPERATION_1 = 7;

        /**
         * Failed to create an object for code “{0}”. This entry will be ignored.
         */
        public static final short CANT_CREATE_OBJECT_FROM_CODE_1 = 8;

        /**
         * Failed to dispose the backing store after timeout.
         */
        public static final short CANT_DISPOSE_BACKING_STORE = 9;

        /**
         * Can’t load a service for category “{0}”. Cause is “{1}”.
         */
        public static final short CANT_LOAD_SERVICE_2 = 10;

        /**
         * Can’t read “{0}”.
         */
        public static final short CANT_READ_FILE_1 = 11;

        /**
         * Can’t register JAI operation “{0}”. Some grid coverage operations may not work.
         */
        public static final short CANT_REGISTER_JAI_OPERATION_1 = 12;

        /**
         * Can’t roll longitude for this {0} projection.
         */
        public static final short CANT_ROLL_LONGITUDE_1 = 13;

        /**
         * Changed the renderer coordinate system. Cause is:
         */
        public static final short CHANGED_COORDINATE_REFERENCE_SYSTEM = 14;

        /**
         * Closed the database connection for thread “{0}” after {1} queries.
         */
        public static final short CLOSED_DATABASE_FOR_THREAD_2 = 15;

        /**
         * Closed the EPSG database connection.
         */
        public static final short CLOSED_EPSG_DATABASE = 16;

        /**
         * Connected thread “{0}” to database “{1}”.
         */
        public static final short CONNECTED_DATABASE_FOR_THREAD_2 = 17;

        /**
         * Connected to EPSG database “{0}” on “{1}”.
         */
        public static final short CONNECTED_EPSG_DATABASE_2 = 18;

        /**
         * {0,choice,0#Loading|1#Writing} of {1}:
         * • Coverage name:      {2}
         * • Coverage type(s):   {3}
         * • Coverage size:      {4} pixels
         * • Coverage CRS:       {5}
         * • {0,choice,0#Required transform:|1#Applied transform: } {6}
         * • Elapsed time:       {7} milliseconds.
         */
        public static final short COVERAGE_STORE_8 = 19;

        /**
         * Created {0,choice,0#decoder|1#encoder} of class {1}.
         */
        public static final short CREATED_CODEC_OF_CLASS_2 = 20;

        /**
         * Created coordinate operation “{0}” for source CRS “{1}” and target CRS “{2}”.
         */
        public static final short CREATED_COORDINATE_OPERATION_3 = 21;

        /**
         * Created a “{0}” entry in the naming system.
         */
        public static final short CREATED_DATASOURCE_ENTRY_1 = 22;

        /**
         * Created a new ‘{0}’ object.
         */
        public static final short CREATED_OBJECT_1 = 23;

        /**
         * Created serializable image for coverage “{0}” using the “{1}” codec.
         */
        public static final short CREATED_SERIALIZABLE_IMAGE_2 = 24;

        /**
         * Creating cached EPSG database version {0}. This operation may take a few minutes...
         */
        public static final short CREATING_CACHED_EPSG_DATABASE_1 = 25;

        /**
         * Deferred painting for tile ({0},{1}).
         */
        public static final short DEFERRED_TILE_PAINTING_2 = 26;

        /**
         * File “{0}” contains values that duplicate previously stored values.
         */
        public static final short DUPLICATED_CONTENT_IN_FILE_1 = 27;

        /**
         * Excessive memory usage.
         */
        public static final short EXCESSIVE_MEMORY_USAGE = 28;

        /**
         * Tile cache capacity exceed maximum heap size ({0} Mb).
         */
        public static final short EXCESSIVE_TILE_CACHE_1 = 29;

        /**
         * Factory implementations for category {0}:
         */
        public static final short FACTORY_IMPLEMENTATIONS_1 = 30;

        /**
         * {1} ({0} authority) replaces {2} for {3,choice,0#standard|1#XY} axis order.
         */
        public static final short FACTORY_REPLACED_FOR_AXIS_ORDER_4 = 31;

        /**
         * Failure in the primary factory: {0} Now trying the fallback factory...
         */
        public static final short FALLBACK_FACTORY_1 = 32;

        /**
         * Flush the “{0}” cache.
         */
        public static final short FLUSH_CACHE_1 = 33;

        /**
         * Found {0} reference systems in {1} elements. The most frequent appears {2} time and the less
         * frequent appears {3} times.
         */
        public static final short FOUND_MISMATCHED_CRS_4 = 34;

        /**
         * Ignored “{0}” hint.
         */
        public static final short HINT_IGNORED_1 = 35;

        /**
         * Initializing transformation from {0} to {1}.
         */
        public static final short INITIALIZING_TRANSFORMATION_2 = 36;

        /**
         * {0} JDBC driver version {1}.{2}.
         */
        public static final short JDBC_DRIVER_VERSION_3 = 37;

        /**
         * Loading datum aliases from “{0}”.
         */
        public static final short LOADING_DATUM_ALIASES_1 = 38;

        /**
         * Loading region x=[{0} … {1}], y=[{2} … {3}] in {4} milliseconds:
         * {5,choice,0#success|1#canceled|2#FAILURE}.
         */
        public static final short LOADING_REGION_6 = 39;

        /**
         * Text were discarded for some locales.
         */
        public static final short LOCALES_DISCARTED = 40;

        /**
         * No coordinate operation from “{0}” to “{1}” because of mismatched factories.
         */
        public static final short MISMATCHED_COORDINATE_OPERATION_FACTORIES_2 = 41;

        /**
         * The type of the requested object does not match the “{0}” URN type.
         */
        public static final short MISMATCHED_URN_TYPE_1 = 42;

        /**
         * Native acceleration {1,choice,0#disabled|1#enabled} for “{0}” operation.
         */
        public static final short NATIVE_ACCELERATION_STATE_2 = 43;

        /**
         * JAI codec {1,choice,0#disabled|1#enabled} for {2,choice,0#reading|1#writing} “{0}” format.
         */
        public static final short NATIVE_CODEC_STATE_3 = 44;

        /**
         * Offscreen rendering failed for layer “{0}”. Fall back on default rendering.
         */
        public static final short OFFSCREEN_RENDERING_FAILED_1 = 45;

        /**
         * Renderer “{0}” painted in {1} seconds.
         */
        public static final short PAINTING_LAYER_2 = 46;

        /**
         * Polygons drawn with {0,number,percent} of available points, reusing {1,number,percent} from
         * the cache (resolution: {2} {3}).
         */
        public static final short POLYGON_CACHE_USE_4 = 47;

        /**
         * Failed to allocate {0} Mb of memory. Trying a smaller memory allocation.
         */
        public static final short RECOVERABLE_OUT_OF_MEMORY_1 = 48;

        /**
         * Log records are redirected to Apache commons logging.
         */
        public static final short REDIRECTED_TO_COMMONS_LOGGING = 49;

        /**
         * Registered Geotoolkit.org extensions to JAI operations.
         */
        public static final short REGISTERED_JAI_OPERATIONS = 50;

        /**
         * Registered RMI services for {0}.
         */
        public static final short REGISTERED_RMI_SERVICES_1 = 51;

        /**
         * Select an image of “{0}” decimated to level {1} of {2}.
         */
        public static final short RESSAMPLING_RENDERED_IMAGE_3 = 52;

        /**
         * Creates a {1,choice,0#packed|1#geophysics|2#photographic} view of grid coverage “{0}” using
         * operation “{2}”.
         */
        public static final short SAMPLE_TRANSCODE_3 = 53;

        /**
         * Layer “{0}” send a repaint event for the whole widget area.
         */
        public static final short SEND_REPAINT_EVENT_1 = 54;

        /**
         * Layer “{0}” send a repaint event for pixels x=[{1}..{2}] and y=[{3}..{4}] in widget area.
         */
        public static final short SEND_REPAINT_EVENT_5 = 55;

        /**
         * No column “{0}” has been found in table “{1}”. Value “{2}” will be used instead.
         */
        public static final short TABLE_COLUMN_NOT_FOUND_3 = 56;

        /**
         * Temporary file “{0}” has been garbage-collected.
         */
        public static final short TEMPORARY_FILE_GC_1 = 57;

        /**
         * Unavailable authority factory: {0}
         */
        public static final short UNAVAILABLE_AUTHORITY_FACTORY_1 = 58;

        /**
         * Attempt to recover from unexpected exception.
         */
        public static final short UNEXPECTED_EXCEPTION = 59;

        /**
         * Unexpected unit “{0}”. Map scale may be inaccurate.
         */
        public static final short UNEXPECTED_UNIT_1 = 60;

        /**
         * Ignoring unknown parameter: “{0}” = {1} {2}.
         */
        public static final short UNKNOWN_PARAMETER_3 = 61;

        /**
         * Can’t handle style of class {0}. Consequently, geometry “{1}” will ignore its style
         * information.
         */
        public static final short UNKNOWN_STYLE_2 = 62;

        /**
         * Unrecognized scale type: “{0}”. Default to linear.
         */
        public static final short UNRECOGNIZED_SCALE_TYPE_1 = 63;

        /**
         * Update the cache for layer “{0}”.
         */
        public static final short UPDATE_RENDERER_CACHE_1 = 64;

        /**
         * Using “{0}” as a fallback.
         */
        public static final short USING_FALLBACK_1 = 65;

        /**
         * Using “{0}” as {1} factory.
         */
        public static final short USING_FILE_AS_FACTORY_2 = 66;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Loggings(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Loggings getResources(Locale locale) throws MissingResourceException {
        return getBundle(Loggings.class, locale);
    }

    /**
     * Gets a log record for the given key from this resource bundle or one of its parents.
     *
     * @param  level The log record level.
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static LogRecord format(final Level level,
                                   final short key) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key);
    }

    /**
     * Gets a log record for the given key. Replaces all occurrence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  level The log record level.
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static LogRecord format(final Level level,
                                   final short  key,
                                   final Object arg0) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key, arg0);
    }

    /**
     * Gets a log record for the given key. Replaces all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  level The log record level.
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static LogRecord format(final Level level,
                                   final short  key,
                                   final Object arg0,
                                   final Object arg1) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key, arg0, arg1);
    }

    /**
     * Gets a log record for the given key. Replaces all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  level The log record level.
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static LogRecord format(final Level level,
                                   final short  key,
                                   final Object arg0,
                                   final Object arg1,
                                   final Object arg2) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key, arg0, arg1, arg2);
    }

    /**
     * Gets a log record for the given key. Replaces all occurrence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  level The log record level.
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @param  arg3 Value to substitute to "{3}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static LogRecord format(final Level level,
                                   final short  key,
                                   final Object arg0,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key, arg0, arg1, arg2, arg3);
    }

    /**
     * Localizes and formats the message string from a log record. This method performs a work
     * similar to {@link java.util.logging.Formatter#formatMessage}, except that the work will be
     * delegated to {@link #getString(int, Object)} if the {@linkplain LogRecord#getResourceBundle
     * record resource bundle} is an instance of {@code IndexedResourceBundle}.
     *
     * @param  record The log record to format.
     * @return The formatted message.
     */
    public static String format(final LogRecord record) {
        String message = record.getMessage();
        final ResourceBundle resources = record.getResourceBundle();
        if (resources instanceof IndexedResourceBundle) {
            int key = -1;
            try {
                key = Integer.parseInt(message);
            } catch (NumberFormatException e) {
                 unexpectedException(e);
            }
            if (key >= 0) {
                final Object[] parameters = record.getParameters();
                return ((IndexedResourceBundle) resources).getString((short) key, parameters);
            }
        }
        if (resources != null) {
            try {
                message = resources.getString(message);
            } catch (MissingResourceException e) {
                unexpectedException(e);
            }
            final Object[] parameters = record.getParameters();
            if (parameters != null && parameters.length != 0) {
                final int offset = message.indexOf('{');
                if (offset >= 0 && offset < message.length()-1) {
                    // Uses a more restrictive check than Character.isDigit(char)
                    final char c = message.charAt(offset);
                    if (c>='0' && c<='9') try {
                        return MessageFormat.format(message, parameters);
                    } catch (IllegalArgumentException e) {
                        unexpectedException(e);
                    }
                }
            }
        }
        return message;
    }

    /**
     * Invoked when an unexpected exception occurred in the {@link #format} method.
     */
    private static void unexpectedException(final RuntimeException exception) {
        Logging.unexpectedException(IndexedResourceBundle.class, "format", exception);
    }
}
