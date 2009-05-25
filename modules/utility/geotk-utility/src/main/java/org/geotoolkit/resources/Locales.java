/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.XArrays;
import static org.geotoolkit.util.Utilities.hashMapCapacity;


/**
 * Utilities methods working with {@link Locale} instances.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
@Static
public final class Locales {
    /**
     * A read-only map for canonicalizing the locales. Filled on class
     * initialization in order to avoid the need for synchronization.
     */
    private static final Map<Locale,Locale> POOL;
    static {
        final Locale[] locales = Locale.getAvailableLocales();
        POOL = new HashMap<Locale,Locale>(hashMapCapacity(locales.length));
        for (final Locale lc : locales) {
            POOL.put(lc, lc);
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
        locales = XArrays.resize(locales, count);
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
}
