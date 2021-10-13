package org.geotoolkit.jdbc.html;

import java.util.Locale;
import java.util.MissingResourceException;
import org.apache.sis.util.resources.IndexedResourceBundle;

/**
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public final class Bundle extends IndexedResourceBundle {
    /**
     * Resource keys. This class is used when compiling sources, but no dependencies to
     * {@code Keys} should appear in any resulting class files. Since the Java compiler
     * inlines final integer values, using long identifiers will not bloat the constant
     * pools of compiled classes.
     */
    public static final class Keys {
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
     * Constructs a new resource bundle loading data from the given UTF file.
     *
     * @param filename The file or the JAR entry containing resources.
     */
    public Bundle(final java.net.URL filename) {
        super(filename);
    }

    /**
     * Returns resources in the given locale.
     *
     * @param  locale The locale, or {@code null} for the default locale.
     * @return Resources in the given locale.
     * @throws MissingResourceException if resources can't be found.
     */
    public static Bundle getResources(Locale locale) throws MissingResourceException {
        return getBundle(Bundle.class, locale);
    }
}
