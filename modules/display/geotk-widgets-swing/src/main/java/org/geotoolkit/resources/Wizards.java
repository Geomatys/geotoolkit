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
import org.apache.sis.util.resources.IndexedResourceBundle;


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
        public static final short CalculationProgessing = 0;

        /**
         * <html><blockquote>The database has been created. Check the connection parameters in the
         * preferences menu. Make sure that the data file directory and the time zone are set to the
         * proper values.</blockquote></html>
         */
        public static final short CoverageDatabaseCreated = 1;

        /**
         * Creates the tables for the "coverages" schema in an initially empty database.
         */
        public static final short CoverageDatabaseDesc = 2;

        /**
         * <html><b>Notes:</b><ul><li>Roles shall be created only once for a given server. If a
         * database has already been created on the "<code>{0}</code>" server with the same roles,
         * uncheck the "<cite>Create roles</cite>" option.</li><li>A single EPSG database (<a
         * href="http://www.epsg.org">http://www.epsg.org</a>) is sufficient and can be shared by
         * different Coverages database. However having multiple copies is okay.</li></ul></html>
         */
        public static final short CoverageDatabaseNotes_1 = 3;

        /**
         * New coverage database
         */
        public static final short CoverageDatabaseTitle = 4;

        /**
         * Copy the EPSG database
         */
        public static final short CreateEpsg = 5;

        /**
         * Create the “{0}” and “{1}” roles
         */
        public static final short CreateRoles_2 = 6;

        /**
         * Creating the mosaic
         */
        public static final short CreatingMosaic = 7;

        /**
         * Creating the {0} schema.
         */
        public static final short CreatingSchema_1 = 8;

        /**
         * A server and a database must be specified.
         */
        public static final short DatabaseRequired = 9;

        /**
         * Define pyramid tiling
         */
        public static final short DefinePyramidTiling = 10;

        /**
         * The wizard has now enough informations for creating the mosaic. Press "Finish" to confirm.
         */
        public static final short EnoughInformation = 11;

        /**
         * Geotoolkit.org web site
         */
        public static final short GeotkSite = 12;

        /**
         * Geotoolkit.org wizards
         */
        public static final short GeotkWizards = 13;

        /**
         * The selected tiles can not make a single mosaic.
         */
        public static final short InvalidMosaicLayout = 14;

        /**
         * Read a potentially big image (which may be splitted in many tiles at the same resolution)
         * and write a set of smaller tiles of given size and using different subsamplings.
         */
        public static final short MosaicDesc = 15;

        /**
         * Mosaic generator
         */
        public static final short MosaicTitle = 16;

        /**
         * At least one tile must be selected.
         */
        public static final short NoSelectedTiles = 17;

        /**
         * <html>Specify the directory which contain the <code>postgis.sql</code> and
         * <code>spatial_ref_sys.sql</code> PostGIS files. If the database is hosted on a remote
         * server, make sure that the files specified below are identical to the files on the
         * server.</html>
         */
        public static final short PostgisDirectory = 18;

        /**
         * Remove opaque border
         */
        public static final short RemoveOpaqueBorder = 19;

        /**
         * Select source tiles
         */
        public static final short SelectSourceTiles = 20;

        /**
         * Set as the default {0} database
         */
        public static final short SetAsDefault_1 = 21;

        /**
         * Select directories and install the NADCON and EPSG data. This setup is optional. If
         * executed, the setting will be remembered for all subsequent Geotoolkit.org usage.
         */
        public static final short SetupDesc = 22;

        /**
         * Geotoolkit.org Setup
         */
        public static final short SetupTitle = 23;

        /**
         * <html>Use the <cite>Preferences</cite> menu for specifying an existing database,<br>or use
         * the <cite>New coverage database</cite> menu for creating a new database.</html>
         */
        public static final short UnspecifiedCoveragesDatabase = 24;

        /**
         * Writing the mosaic
         */
        public static final short WritingMosaic = 25;
    }

    /**
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Wizards(final java.net.URL filename) {
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
    public static String format(final short key) throws MissingResourceException {
        return getResources(null).getString(key);
    }
}
