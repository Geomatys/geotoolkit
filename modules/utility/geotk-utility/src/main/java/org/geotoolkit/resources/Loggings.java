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
        public static final short AdjustedGridGeometry_1 = 0;

        /**
         * Ambiguity between inverse flattening and semi minor axis length. Using inverse flattening.
         */
        public static final short AmbiguousEllipsoid = 1;

        /**
         * {3,choice,0#Apply|1#Reuse} operation “{1}” on coverage “{0}” with interpolation “{2}”.
         */
        public static final short AppliedOperation_4 = 2;

        /**
         * Resampled coverage “{0}” from coordinate system “{1}” (for an image of size {2}×{3}) to
         * coordinate system “{4}” (image size {5}×{6}). JAI operation is “{7}” with “{9}”
         * interpolation on {8,choice,0#packed|1#geophysics} pixels values. Background value is ({10}).
         */
        public static final short AppliedResample_11 = 3;

        /**
         * Failed to create a coordinate operation from “{0}” authority factory.
         */
        public static final short CantCreateCoordinateOperation_1 = 4;

        /**
         * Failed to create an object for code “{0}”. This entry will be ignored.
         */
        public static final short CantCreateObjectFromCode_1 = 5;

        /**
         * Can’t load a service for category “{0}”. Cause is “{1}”.
         */
        public static final short CantLoadService_2 = 6;

        /**
         * Can’t read “{0}”.
         */
        public static final short CantReadFile_1 = 7;

        /**
         * Can’t register JAI operation “{0}”. Some grid coverage operations may not work.
         */
        public static final short CantRegisterJaiOperation_1 = 8;

        /**
         * Closed the database connection for thread “{0}” after {1} queries.
         */
        public static final short ClosedDatabaseForThread_2 = 9;

        /**
         * Closed the EPSG database connection.
         */
        public static final short ClosedEpsgDatabase = 10;

        /**
         * Connected thread “{0}” to database “{1}”.
         */
        public static final short ConnectedDatabaseForThread_2 = 11;

        /**
         * Connected to EPSG database “{0}” on “{1}”.
         */
        public static final short ConnectedEpsgDatabase_2 = 12;

        /**
         * {0,choice,0#Loading|1#Writing} of {1}:
         * • Coverage name:      {2}
         * • Coverage type(s):   {3}
         * • Coverage size:      {4} pixels
         * • Coverage CRS:       {5}
         * • {0,choice,0#Required transform:|1#Applied transform: } {6}
         * • Elapsed time:       {7} milliseconds.
         */
        public static final short CoverageStore_8 = 13;

        /**
         * Created {0,choice,0#decoder|1#encoder} of class {1}.
         */
        public static final short CreatedCodecOfClass_2 = 14;

        /**
         * Created coordinate operation “{0}” for source CRS “{1}” and target CRS “{2}”.
         */
        public static final short CreatedCoordinateOperation_3 = 15;

        /**
         * Created a new ‘{0}’ object.
         */
        public static final short CreatedObject_1 = 16;

        /**
         * Created serializable image for coverage “{0}” using the “{1}” codec.
         */
        public static final short CreatedSerializableImage_2 = 17;

        /**
         * Creating cached EPSG database version {0}. This operation may take a few minutes...
         */
        public static final short CreatingCachedEpsgDatabase_1 = 18;

        /**
         * Deferred painting for tile ({0},{1}).
         */
        public static final short DeferredTilePainting_2 = 19;

        /**
         * File “{0}” contains values that duplicate previously stored values.
         */
        public static final short DuplicatedContentInFile_1 = 20;

        /**
         * Tile cache capacity exceed maximum heap size ({0} Mb).
         */
        public static final short ExcessiveTileCache_1 = 21;

        /**
         * Factory implementations for category {0}:
         */
        public static final short FactoryImplementations_1 = 22;

        /**
         * {1} ({0} authority) replaces {2} for {3,choice,0#standard|1#XY} axis order.
         */
        public static final short FactoryReplacedForAxisOrder_4 = 23;

        /**
         * Failure in the primary factory: {0} Now trying the fallback factory...
         */
        public static final short FallbackFactory_1 = 24;

        /**
         * Flush the “{0}” cache.
         */
        public static final short FlushCache_1 = 25;

        /**
         * Found {0} reference systems in {1} elements. The most frequent appears {2} time and the less
         * frequent appears {3} times.
         */
        public static final short FoundMismatchedCRS_4 = 42;

        /**
         * Initializing transformation from {0} to {1}.
         */
        public static final short InitializingTransformation_2 = 26;

        /**
         * {0} JDBC driver version {1}.{2}.
         */
        public static final short JdbcDriverVersion_3 = 27;

        /**
         * Loading region x=[{0} … {1}], y=[{2} … {3}] in {4} milliseconds:
         * {5,choice,0#success|1#canceled|2#FAILURE}.
         */
        public static final short LoadingRegion_6 = 28;

        /**
         * No coordinate operation from “{0}” to “{1}” because of mismatched factories.
         */
        public static final short MismatchedCoordinateOperationFactories_2 = 29;

        /**
         * The type of the requested object does not match the “{0}” URN type.
         */
        public static final short MismatchedUrnType_1 = 30;

        /**
         * Native acceleration {1,choice,0#disabled|1#enabled} for “{0}” operation.
         */
        public static final short NativeAccelerationState_2 = 31;

        /**
         * JAI codec {1,choice,0#disabled|1#enabled} for {2,choice,0#reading|1#writing} “{0}” format.
         */
        public static final short NativeCodecState_3 = 32;

        /**
         * Failed to allocate {0} Mb of memory. Trying a smaller memory allocation.
         */
        public static final short RecoverableOutOfMemory_1 = 33;

        /**
         * Registered Geotoolkit.org extensions to JAI operations.
         */
        public static final short RegisteredJaiOperations = 34;

        /**
         * Creates a {1,choice,0#packed|1#geophysics|2#photographic} view of grid coverage “{0}” using
         * operation “{2}”.
         */
        public static final short SampleTranscode_3 = 35;

        /**
         * No column “{0}” has been found in table “{1}”. Value “{2}” will be used instead.
         */
        public static final short TableColumnNotFound_3 = 36;

        /**
         * Temporary file “{0}” has been garbage-collected.
         */
        public static final short TemporaryFileGc_1 = 37;

        /**
         * Unavailable authority factory: {0}
         */
        public static final short UnavailableAuthorityFactory_1 = 38;

        /**
         * Unrecognized scale type: “{0}”. Default to linear.
         */
        public static final short UnrecognizedScaleType_1 = 39;

        /**
         * Using “{0}” as a fallback.
         */
        public static final short UsingFallback_1 = 40;

        /**
         * Using “{0}” as {1} factory.
         */
        public static final short UsingFileAsFactory_2 = 41;
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
