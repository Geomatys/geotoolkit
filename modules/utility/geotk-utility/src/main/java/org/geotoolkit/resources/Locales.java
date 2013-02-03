/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.Arrays;
import java.util.Locale;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.logging.Logging;
import static org.geotoolkit.util.collection.XCollections.hashMapCapacity;


/**
 * Utilities methods working with {@link Locale} instances.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.04
 *
 * @since 2.4
 * @module
 */
public final class Locales extends Static {
    /**
     * A read-only map for canonicalizing the locales. Filled on class
     * initialization in order to avoid the need for synchronization.
     */
    private static final Map<Locale,Locale> POOL;
    static {
        final Locale[] locales = Locale.getAvailableLocales();
        POOL = new HashMap<>(hashMapCapacity(locales.length));
        for (final Locale lc : locales) {
            POOL.put(lc, lc);
        }
        /*
         * Add static constants replace, which may replace some values which
         * were returned by Locale.getAvailableLocales().
         */
        try {
            final Field[] fields = Locale.class.getFields();
            for (int i=0; i<fields.length; i++) {
                final Field field = fields[i];
                if (Modifier.isStatic(field.getModifiers())) {
                    if (Locale.class.isAssignableFrom(field.getType())) {
                        final Locale toAdd = (Locale) field.get(null);
                        POOL.put(toAdd, toAdd);
                    }
                }
            }
        } catch (ReflectiveOperationException exception) {
            /*
             * Not a big deal if this operation fails (this is actually just an
             * optimization for reducing memory usage). Log a warning and continue.
             */
            Logging.unexpectedException(Locales.class, "unique", exception);
        }
    }

    /**
     * Do not allow instantiation of this class.
     */
    private Locales() {
    }

    /**
     * Returns available languages.
     *
     * @return Available languages.
     *
     * @todo Current implementation returns a hard-coded list.
     *       Future implementations may perform a more intelligent work.
     */
    public static Locale[] getAvailableLanguages() {
        return new Locale[] {
            Locale.ENGLISH,
            Locale.FRENCH,
            Locale.GERMAN
            // TODO: missing constants for SPANISH, PORTUGUES and GREEK
        };
    }

    /**
     * Returns the list of available locales.
     *
     * @return Available locales.
     */
    public static Locale[] getAvailableLocales() {
        final Locale[] languages = getAvailableLanguages();
        Locale[] locales = Locale.getAvailableLocales();
        int count = 0;
        for (int i=0; i<locales.length; i++) {
            final Locale locale = locales[i];
            if (containsLanguage(languages, locale)) {
                locales[count++] = locale;
            }
        }
        locales = ArraysExt.resize(locales, count);
        return locales;
    }

    /**
     * Returns {@code true} if the specified array of locales contains at least
     * one element with the specified language.
     */
    private static boolean containsLanguage(final Locale[] locales, final Locale language) {
        final String code = language.getLanguage();
        for (int i=0; i<locales.length; i++) {
            if (code.equals(locales[i].getLanguage())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the list of available locales formatted as string in the specified locale.
     *
     * @param locale The locale to use for formatting the strings to be returned.
     * @return String descriptions of available locales.
     */
    public static String[] getAvailableLocales(final Locale locale) {
        final Locale[] locales = getAvailableLocales();
        final String[] display = new String[locales.length];
        for (int i=0; i<locales.length; i++) {
            display[i] = locales[i].getDisplayName(locale);
        }
        Arrays.sort(display);
        return display;
    }

    /**
     * Returns a unique instance of the given locale, if one is available.
     * Otherwise returns {@code locale} unchanged.
     *
     * @param  locale The locale to canonicalize.
     * @return A unique instance of the given locale, or {@code locale} if
     *         the given locale is not cached.
     */
    public static Locale unique(final Locale locale) {
        final Locale candidate = POOL.get(locale);
        return (candidate != null) ? candidate : locale;
    }

    /**
     * Parses the given locale. The string is the language code either as the 2 letters or the
     * 3 letters ISO code. It can optionally be followed by the {@code '_'} character and the
     * country code (again either as 2 or 3 letters), optionally followed by {@code '_'} and
     * the variant.
     *
     * @param  code The language code, which may be followed by country code.
     * @return The language for the given code.
     * @throws IllegalArgumentException If the given code doesn't seem to be a valid locale.
     *
     * @since 3.04
     */
    public static Locale parse(final String code) throws IllegalArgumentException {
        final String language, country, variant;
        int ci = code.indexOf('_');
        if (ci < 0) {
            language = code.trim();
            country  = "";
            variant  = "";
        } else {
            language = code.substring(0, ci).trim();
            int vi = code.indexOf('_', ++ci);
            if (vi < 0) {
                country = code.substring(ci).trim();
                variant = "";
            } else {
                country = code.substring(ci, vi).trim();
                variant = code.substring(++vi).trim();
                if (code.indexOf('_', vi) >= 0) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_LANGUAGE_CODE_$1, code));
                }
            }
        }
        final boolean language3 = isThreeLetters(language);
        final boolean country3  = isThreeLetters(country);
        /*
         * Perform a linear scan only if we need to compare some 3-letters ISO code.
         * Otherwise (if every code are 2 letters), it will be faster to create a new
         * locale and check for an existing instance in the hash map.
         */
        if (language3 || country3) {
            String language2 = language;
            String country2  = country;
            for (Locale locale : Locale.getAvailableLocales()) {
                String c = (language3) ? locale.getISO3Language() : locale.getLanguage();
                if (language.equals(c)) {
                    if (country2 == country) { // NOSONAR: really identity comparison.
                        // Remember the 2-letters ISO code in an opportunist way.
                        // If the 2-letters ISO code has been set for the country
                        // as well, we will not change the language code because
                        // it has already been set with the code associated with
                        // the right country.
                        language2 = locale.getLanguage();
                    }
                    c = (country3) ? locale.getISO3Country() : locale.getCountry();
                    if (country.equals(c)) {
                        country2 = locale.getCountry();
                        if (variant.equals(locale.getVariant())) {
                            return unique(locale);
                        }
                    }
                }
            }
            return unique(new Locale(language2, country2, variant));
        }
        return unique(new Locale(language, country, variant));
    }

    /**
     * Returns {@code true} if the following code is 3 letters, or {@code false} if 2 letters.
     */
    private static boolean isThreeLetters(final String code) {
        switch (code.length()) {
            case 0: // fall through
            case 2: return false;
            case 3: return true;
            default: {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_LANGUAGE_CODE_$1, code));
            }
        }
    }

    /**
     * Returns the 3-letters ISO language code if available, or the 2-letters code otherwise.
     *
     * @param  locale The locale for which we want the language.
     * @return The language code, 3 letters if possible or 2 letters otherwise.
     *
     * @since 3.04
     */
    public static String getLanguage(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException e) {
            Logging.recoverableException(Locales.class, "getLanguage", e);
            return locale.getLanguage();
        }
    }
}
