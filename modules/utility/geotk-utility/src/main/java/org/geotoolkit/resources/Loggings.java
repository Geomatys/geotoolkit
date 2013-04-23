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
import java.util.logging.LogRecord;
import java.util.logging.Level;


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
        public static final int ADJUSTED_GRID_GEOMETRY_1 = 0;

        /**
         * Ambiguity between inverse flattening and semi minor axis length. Using inverse flattening.
         */
        public static final int AMBIGUOUS_ELLIPSOID = 1;

        /**
         * {3,choice,0#Apply|1#Reuse} operation “{1}” on coverage “{0}” with interpolation “{2}”.
         */
        public static final int APPLIED_OPERATION_4 = 2;

        /**
         * Resampled coverage “{0}” from coordinate system “{1}” (for an image of size {2}×{3}) to
         * coordinate system “{4}” (image size {5}×{6}). JAI operation is “{7}” with “{9}”
         * interpolation on {8,choice,0#packed|1#geophysics} pixels values. Background value is ({10}).
         */
        public static final int APPLIED_RESAMPLE_11 = 3;

        /**
         * Converted “{0}” from “{1}” to “{2}” units. We assume that this is the expected units for
         * computation purpose.
         */
        public static final int APPLIED_UNIT_CONVERSION_3 = 4;

        /**
         * Caching {0}.
         */
        public static final int CACHING_1 = 5;

        /**
         * Failed to bind a “{0}” entry.
         */
        public static final int CANT_BIND_DATASOURCE_1 = 6;

        /**
         * Failed to create a coordinate operation from “{0}” authority factory.
         */
        public static final int CANT_CREATE_COORDINATE_OPERATION_1 = 7;

        /**
         * Failed to create an object for code “{0}”. This entry will be ignored.
         */
        public static final int CANT_CREATE_OBJECT_FROM_CODE_1 = 8;

        /**
         * Failed to dispose the backing store after timeout.
         */
        public static final int CANT_DISPOSE_BACKING_STORE = 9;

        /**
         * Can’t load a service for category “{0}”. Cause is “{1}”.
         */
        public static final int CANT_LOAD_SERVICE_2 = 10;

        /**
         * Can’t read “{0}”.
         */
        public static final int CANT_READ_FILE_1 = 11;

        /**
         * Can’t register JAI operation “{0}”. Some grid coverage operations may not work.
         */
        public static final int CANT_REGISTER_JAI_OPERATION_1 = 12;

        /**
         * Can’t roll longitude for this {0} projection.
         */
        public static final int CANT_ROLL_LONGITUDE_1 = 13;

        /**
         * Changed the renderer coordinate system. Cause is:
         */
        public static final int CHANGED_COORDINATE_REFERENCE_SYSTEM = 14;

        /**
         * Closed the database connection for thread “{0}” after {1} queries.
         */
        public static final int CLOSED_DATABASE_FOR_THREAD_2 = 64;

        /**
         * Closed the EPSG database connection.
         */
        public static final int CLOSED_EPSG_DATABASE = 15;

        /**
         * Connected thread “{0}” to database “{1}”.
         */
        public static final int CONNECTED_DATABASE_FOR_THREAD_2 = 63;

        /**
         * Connected to EPSG database “{0}” on “{1}”.
         */
        public static final int CONNECTED_EPSG_DATABASE_2 = 16;

        /**
         * {0,choice,0#Loading|1#Writing} of {1}:
         * • Coverage name:      {2}
         * • Coverage type(s):   {3}
         * • Coverage size:      {4} pixels
         * • Coverage CRS:       {5}
         * • {0,choice,0#Required transform:|1#Applied transform: } {6}
         * • Elapsed time:       {7} milliseconds.
         */
        public static final int COVERAGE_STORE_8 = 62;

        /**
         * Created {0,choice,0#decoder|1#encoder} of class {1}.
         */
        public static final int CREATED_CODEC_OF_CLASS_2 = 61;

        /**
         * Created coordinate operation “{0}” for source CRS “{1}” and target CRS “{2}”.
         */
        public static final int CREATED_COORDINATE_OPERATION_3 = 17;

        /**
         * Created a “{0}” entry in the naming system.
         */
        public static final int CREATED_DATASOURCE_ENTRY_1 = 18;

        /**
         * Created a new ‘{0}’ object.
         */
        public static final int CREATED_OBJECT_1 = 65;

        /**
         * Created serializable image for coverage “{0}” using the “{1}” codec.
         */
        public static final int CREATED_SERIALIZABLE_IMAGE_2 = 19;

        /**
         * Creating cached EPSG database version {0}. This operation may take a few minutes...
         */
        public static final int CREATING_CACHED_EPSG_DATABASE_1 = 20;

        /**
         * Deferred painting for tile ({0},{1}).
         */
        public static final int DEFERRED_TILE_PAINTING_2 = 21;

        /**
         * File “{0}” contains values that duplicate previously stored values.
         */
        public static final int DUPLICATED_CONTENT_IN_FILE_1 = 22;

        /**
         * Excessive memory usage.
         */
        public static final int EXCESSIVE_MEMORY_USAGE = 23;

        /**
         * Tile cache capacity exceed maximum heap size ({0} Mb).
         */
        public static final int EXCESSIVE_TILE_CACHE_1 = 24;

        /**
         * Factory implementations for category {0}:
         */
        public static final int FACTORY_IMPLEMENTATIONS_1 = 25;

        /**
         * {1} ({0} authority) replaces {2} for {3,choice,0#standard|1#XY} axis order.
         */
        public static final int FACTORY_REPLACED_FOR_AXIS_ORDER_4 = 26;

        /**
         * Failure in the primary factory: {0} Now trying the fallback factory...
         */
        public static final int FALLBACK_FACTORY_1 = 27;

        /**
         * Flush the “{0}” cache.
         */
        public static final int FLUSH_CACHE_1 = 66;

        /**
         * Found {0} reference systems in {1} elements. The most frequent appears {2} time and the less
         * frequent appears {3} times.
         */
        public static final int FOUND_MISMATCHED_CRS_4 = 28;

        /**
         * Ignored “{0}” hint.
         */
        public static final int HINT_IGNORED_1 = 29;

        /**
         * Initializing transformation from {0} to {1}.
         */
        public static final int INITIALIZING_TRANSFORMATION_2 = 30;

        /**
         * {0} JDBC driver version {1}.{2}.
         */
        public static final int JDBC_DRIVER_VERSION_3 = 31;

        /**
         * Loading datum aliases from “{0}”.
         */
        public static final int LOADING_DATUM_ALIASES_1 = 32;

        /**
         * Loading region x=[{0} … {1}], y=[{2} … {3}] in {4} milliseconds:
         * {5,choice,0#success|1#canceled|2#FAILURE}.
         */
        public static final int LOADING_REGION_6 = 60;

        /**
         * Text were discarded for some locales.
         */
        public static final int LOCALES_DISCARTED = 33;

        /**
         * No coordinate operation from “{0}” to “{1}” because of mismatched factories.
         */
        public static final int MISMATCHED_COORDINATE_OPERATION_FACTORIES_2 = 34;

        /**
         * The type of the requested object does not match the “{0}” URN type.
         */
        public static final int MISMATCHED_URN_TYPE_1 = 35;

        /**
         * Native acceleration {1,choice,0#disabled|1#enabled} for “{0}” operation.
         */
        public static final int NATIVE_ACCELERATION_STATE_2 = 36;

        /**
         * JAI codec {1,choice,0#disabled|1#enabled} for {2,choice,0#reading|1#writing} “{0}” format.
         */
        public static final int NATIVE_CODEC_STATE_3 = 37;

        /**
         * Offscreen rendering failed for layer “{0}”. Fall back on default rendering.
         */
        public static final int OFFSCREEN_RENDERING_FAILED_1 = 38;

        /**
         * Renderer “{0}” painted in {1} seconds.
         */
        public static final int PAINTING_LAYER_2 = 39;

        /**
         * Polygons drawn with {0,number,percent} of available points, reusing {1,number,percent} from
         * the cache (resolution: {2} {3}).
         */
        public static final int POLYGON_CACHE_USE_4 = 40;

        /**
         * Failed to allocate {0} Mb of memory. Trying a smaller memory allocation.
         */
        public static final int RECOVERABLE_OUT_OF_MEMORY_1 = 41;

        /**
         * Log records are redirected to Apache commons logging.
         */
        public static final int REDIRECTED_TO_COMMONS_LOGGING = 42;

        /**
         * Registered Geotoolkit.org extensions to JAI operations.
         */
        public static final int REGISTERED_JAI_OPERATIONS = 43;

        /**
         * Registered RMI services for {0}.
         */
        public static final int REGISTERED_RMI_SERVICES_1 = 44;

        /**
         * Select an image of “{0}” decimated to level {1} of {2}.
         */
        public static final int RESSAMPLING_RENDERED_IMAGE_3 = 45;

        /**
         * Creates a {1,choice,0#packed|1#geophysics|2#photographic} view of grid coverage “{0}” using
         * operation “{2}”.
         */
        public static final int SAMPLE_TRANSCODE_3 = 46;

        /**
         * Layer “{0}” send a repaint event for the whole widget area.
         */
        public static final int SEND_REPAINT_EVENT_1 = 47;

        /**
         * Layer “{0}” send a repaint event for pixels x=[{1}..{2}] and y=[{3}..{4}] in widget area.
         */
        public static final int SEND_REPAINT_EVENT_5 = 48;

        /**
         * No column “{0}” has been found in table “{1}”. Value “{2}” will be used instead.
         */
        public static final int TABLE_COLUMN_NOT_FOUND_3 = 49;

        /**
         * Temporary file “{0}” has been garbage-collected.
         */
        public static final int TEMPORARY_FILE_GC_1 = 50;

        /**
         * Unavailable authority factory: {0}
         */
        public static final int UNAVAILABLE_AUTHORITY_FACTORY_1 = 51;

        /**
         * Attempt to recover from unexpected exception.
         */
        public static final int UNEXPECTED_EXCEPTION = 52;

        /**
         * Unexpected unit “{0}”. Map scale may be inaccurate.
         */
        public static final int UNEXPECTED_UNIT_1 = 53;

        /**
         * Ignoring unknown parameter: “{0}” = {1} {2}.
         */
        public static final int UNKNOWN_PARAMETER_3 = 54;

        /**
         * Can’t handle style of class {0}. Consequently, geometry “{1}” will ignore its style
         * information.
         */
        public static final int UNKNOWN_STYLE_2 = 55;

        /**
         * Unrecognized scale type: “{0}”. Default to linear.
         */
        public static final int UNRECOGNIZED_SCALE_TYPE_1 = 56;

        /**
         * Update the cache for layer “{0}”.
         */
        public static final int UPDATE_RENDERER_CACHE_1 = 57;

        /**
         * Using “{0}” as a fallback.
         */
        public static final int USING_FALLBACK_1 = 58;

        /**
         * Using “{0}” as {1} factory.
         */
        public static final int USING_FILE_AS_FACTORY_2 = 59;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    Loggings(final String filename) {
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
                                   final int key) throws MissingResourceException
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
                                   final int     key,
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
                                   final int     key,
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
                                   final int     key,
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
                                   final int     key,
                                   final Object arg0,
                                   final Object arg1,
                                   final Object arg2,
                                   final Object arg3) throws MissingResourceException
    {
        return getResources(null).getLogRecord(level, key, arg0, arg1, arg2, arg3);
    }
}
