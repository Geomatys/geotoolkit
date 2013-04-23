/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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


/**
 * Locale-dependent resources for wizard messages.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public final class Wizards extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     *
     * @author Cédric Briançon (Geomatys)
     * @version 3.03
     *
     * @since 3.03
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Calculation in progress...
         */
        public static final int CALCULATION_PROGESSING = 0;

        /**
         * <html><blockquote>The database has been created. Check the connection parameters in the
         * preferences menu. Make sure that the data file directory and the time zone are set to the
         * proper values.</blockquote></html>
         */
        public static final int COVERAGE_DATABASE_CREATED = 1;

        /**
         * Creates the tables for the "coverages" schema in an initially empty database.
         */
        public static final int COVERAGE_DATABASE_DESC = 2;

        /**
         * <html><b>Notes:</b><ul><li>Roles shall be created only once for a given server. If a
         * database has already been created on the "<code>{0}</code>" server with the same roles,
         * uncheck the "<cite>Create roles</cite>" option.</li><li>A single EPSG database (<a
         * href="http://www.epsg.org">http://www.epsg.org</a>) is sufficient and can be shared by
         * different Coverages database. However having multiple copies is okay.</li></ul></html>
         */
        public static final int COVERAGE_DATABASE_NOTES_1 = 3;

        /**
         * New coverage database
         */
        public static final int COVERAGE_DATABASE_TITLE = 4;

        /**
         * Copy the EPSG database
         */
        public static final int CREATE_EPSG = 5;

        /**
         * Create the “{0}” and “{1}” roles
         */
        public static final int CREATE_ROLES_2 = 6;

        /**
         * Creating the mosaic
         */
        public static final int CREATING_MOSAIC = 7;

        /**
         * Creating the {0} schema.
         */
        public static final int CREATING_SCHEMA_1 = 8;

        /**
         * A server and a database must be specified.
         */
        public static final int DATABASE_REQUIRED = 9;

        /**
         * Define pyramid tiling
         */
        public static final int DEFINE_PYRAMID_TILING = 10;

        /**
         * The wizard has now enough informations for creating the mosaic. Press "Finish" to confirm.
         */
        public static final int ENOUGH_INFORMATION = 11;

        /**
         * Geotoolkit.org web site
         */
        public static final int GEOTK_SITE = 12;

        /**
         * Geotoolkit.org wizards
         */
        public static final int GEOTK_WIZARDS = 13;

        /**
         * The selected tiles can not make a single mosaic.
         */
        public static final int INVALID_MOSAIC_LAYOUT = 14;

        /**
         * Read a potentially big image (which may be splitted in many tiles at the same resolution)
         * and write a set of smaller tiles of given size and using different subsamplings.
         */
        public static final int MOSAIC_DESC = 15;

        /**
         * Mosaic generator
         */
        public static final int MOSAIC_TITLE = 16;

        /**
         * At least one tile must be selected.
         */
        public static final int NO_SELECTED_TILES = 17;

        /**
         * <html>Specify the directory which contain the <code>postgis.sql</code> and
         * <code>spatial_ref_sys.sql</code> PostGIS files. If the database is hosted on a remote
         * server, make sure that the files specified below are identical to the files on the
         * server.</html>
         */
        public static final int POSTGIS_DIRECTORY = 18;

        /**
         * Remove opaque border
         */
        public static final int REMOVE_OPAQUE_BORDER = 19;

        /**
         * Select source tiles
         */
        public static final int SELECT_SOURCE_TILES = 20;

        /**
         * Select directories and install the NADCON and EPSG data. This setup is optional. If
         * executed, the setting will be remembered for all subsequent Geotoolkit.org usage.
         */
        public static final int SETUP_DESC = 21;

        /**
         * Geotoolkit.org Setup
         */
        public static final int SETUP_TITLE = 22;

        /**
         * Set as the default {0} database
         */
        public static final int SET_AS_DEFAULT_1 = 23;

        /**
         * <html>Use the <cite>Preferences</cite> menu for specifying an existing database,<br>or use
         * the <cite>New coverage database</cite> menu for creating a new database.</html>
         */
        public static final int UNSPECIFIED_COVERAGES_DATABASE = 24;

        /**
         * Writing the mosaic
         */
        public static final int WRITING_MOSAIC = 25;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    Wizards(final String filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Wizards getResources(Locale locale) throws MissingResourceException {
        return getBundle(Wizards.class, locale);
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
}
