/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.jdbc.html;

import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.apache.sis.util.resources.KeyConstants;


/**
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class Bundle extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     */
    public static final class Keys extends KeyConstants {
        /** The unique instance of key constants handler. */
        static final Keys INSTANCE = new Keys();

        private Keys() {
        }

        /**
         * Auto incremented
         */
        public static final short autoIncrement = 1;

        /**
         * Catalogs
         */
        public static final short catalogs = 2;

        /**
         * Columns
         */
        public static final short cols = 3;

        /**
         * Default value
         */
        public static final short defaultVal = 4;

        /**
         * Remarks
         */
        public static final short desc = 5;

        /**
         * Foreign keys
         */
        public static final short fKeys = 6;

        /**
         * Generated
         */
        public static final short generated = 7;

        /**
         * Imported from
         */
        public static final short importedFrom = 8;

        /**
         * Maximum length (bytes)
         */
        public static final short maxLength = 9;

        /**
         * Name
         */
        public static final short name = 10;

        /**
         * No entry
         */
        public static final short noEntry = 11;

        /**
         * Nullable
         */
        public static final short nullable = 12;

        /**
         * Primary keys
         */
        public static final short pKeys = 13;

        /**
         * refers to
         */
        public static final short refers = 14;

        /**
         * Type
         */
        public static final short type = 15;
    }

    /**
     * Constructs a new resource bundle loading data from
     * the resource file of the same name than this class.
     */
    public Bundle() {
    }

    /**
     * Opens the binary file containing the localized resources to load.
     * This method delegates to {@link Class#getResourceAsStream(String)},
     * but this delegation must be done from the same module than the one
     * that provides the binary file.
     */
    @Override
    protected InputStream getResourceAsStream(final String name) {
        return getClass().getResourceAsStream(name);
     }

    /**
     * Returns the handle for the {@code Keys} constants.
     */
    @Override
    protected KeyConstants getKeyConstants() {
        return Keys.INSTANCE;
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale  the locale, or {@code null} for the default locale.
     * @return resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Bundle getResources(Locale locale) throws MissingResourceException {
        return (Bundle) getBundle(Bundle.class.getName(), nonNull(locale));
    }
}
