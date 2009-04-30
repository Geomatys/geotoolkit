/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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


/**
 * Locale-dependent resources for long descriptions.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.2
 * @module
 */
public final class Descriptions extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.0
     *
     * @since 2.2
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Data distributed over a grid
         */
        public static final int CODEC_GRID = 0;

        /**
         * Matrix in text file
         */
        public static final int CODEC_MATRIX = 1;

        /**
         * Raw binary file
         */
        public static final int CODEC_RAW = 2;

        /**
         * Usage: {0} [OPTION...] [COMMAND] [PARAMETER...]
         */
        public static final int COMMAND_USAGE_$1 = 3;

        /**
         * Are the {0} data installed? Some optional data can be downloaded and installed by running
         * the "{2}" module. The default directory for {0} data is "{1}", but {2} allows to change this
         * setting.
         */
        public static final int DATA_NOT_INSTALLED_$3 = 4;

        /**
         * {0} files have been read successfuly but {1} files can not be read. The failure causes are
         * reported below.
         */
        public static final int ERROR_READING_SOME_FILES_$2 = 5;

        /**
         * No EPSG database has been configured, an embedded javaDB database will be used.
         */
        public static final int NO_EPSG_USE_JAVADB = 8;

        /**
         * Count:       {0}
         * Minimum:     {1}
         * Maximum:     {2}
         * Mean:        {3}
         * RMS: {4}
         * Standard deviation:  {5}
         */
        public static final int STATISTICS_TO_STRING_$6 = 6;

        /**
         * Use "help" to show available commands.
         */
        public static final int USE_HELP_COMMAND = 7;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    Descriptions(final String filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Descriptions getResources(Locale locale) throws MissingResourceException {
        return getBundle(Descriptions.class, locale);
    }

    /**
     * Gets a string for the given key from this resource bundle or one of its parents.
     *
     * @param  key The key for the desired string.
     * @return The string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int key) throws MissingResourceException {
        return getResources(null).getString(key);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}"
     * with values of {@code arg0}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1);
    }

    /**
     * Gets a string for the given key are replace all occurence of "{0}",
     * "{1}", with values of {@code arg0}, {@code arg1}, etc.
     *
     * @param  key The key for the desired string.
     * @param  arg0 Value to substitute to "{0}".
     * @param  arg1 Value to substitute to "{1}".
     * @param  arg2 Value to substitute to "{2}".
     * @return The formatted string for the given key.
     * @throws MissingResourceException If no object for the given key can be found.
     */
    public static String format(final int     key,
                                final Object arg0,
                                final Object arg1,
                                final Object arg2) throws MissingResourceException
    {
        return getResources(null).getString(key, arg0, arg1, arg2);
    }
}
