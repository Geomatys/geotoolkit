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
     *
     * @author Martin Desruisseaux (IRD)
     */
    public static final class Keys {
        private Keys() {
        }

        /**
         * Auto incremented
         */
        public static final short autoIncrement = 0;

        /**
         * Catalogs
         */
        public static final short catalogs = 1;

        /**
         * Columns
         */
        public static final short cols = 2;

        /**
         * Default value
         */
        public static final short defaultVal = 3;

        /**
         * Remarks
         */
        public static final short desc = 4;

        /**
         * Foreign keys
         */
        public static final short fKeys = 5;

        /**
         * Generated
         */
        public static final short generated = 6;

        /**
         * Imported from
         */
        public static final short importedFrom = 7;

        /**
         * Maximum length (bytes)
         */
        public static final short maxLength = 8;

        /**
         * Name
         */
        public static final short name = 9;

        /**
         * No entry
         */
        public static final short noEntry = 10;

        /**
         * Nullable
         */
        public static final short nullable = 11;

        /**
         * Primary keys
         */
        public static final short pKeys = 12;

        /**
         * refers to
         */
        public static final short refers = 13;

        /**
         * Type
         */
        public static final short type = 14;
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
