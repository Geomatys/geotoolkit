/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
         * Confirm
         */
        public static final int CONFIRM = 1;

        /**
         * Creating the mosaic
         */
        public static final int CREATING_MOSAIC = 2;

        /**
         * Define pyramid tiling
         */
        public static final int DEFINE_PYRAMID_TILING = 3;

        /**
         * The wizard has now enough informations for creating the mosaic. Press "Finish" to confirm.
         */
        public static final int ENOUGH_INFORMATION = 4;

        /**
         * Geotoolkit.org wizards
         */
        public static final int GEOTK_WIZARDS = 5;

        /**
         * The selected tiles can not make a single mosaic.
         */
        public static final int INVALID_MOSAIC_LAYOUT = 6;

        /**
         * Read a potentially big image (which may be splitted in many tiles at the same resolution)
         * and write a set of smaller tiles of given size and using different subsamplings.
         */
        public static final int MOSAIC_DESC = 7;

        /**
         * Mosaic generator
         */
        public static final int MOSAIC_TITLE = 8;

        /**
         * At least one tile must be selected.
         */
        public static final int NO_SELECTED_TILES = 9;

        /**
         * Remove opaque border
         */
        public static final int REMOVE_OPAQUE_BORDER = 10;

        /**
         * Select source tiles
         */
        public static final int SELECT_SOURCE_TILES = 11;

        /**
         * Select directories and install the NADCON and EPSG data. This setup is optional. If
         * executed, the setting will be remembered for all subsequent Geotoolkit.org usage.
         */
        public static final int SETUP_DESC = 12;

        /**
         * Geotoolkit Setup
         */
        public static final int SETUP_TITLE = 13;

        /**
         * Writing the mosaic
         */
        public static final int WRITING_MOSAIC = 14;
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
