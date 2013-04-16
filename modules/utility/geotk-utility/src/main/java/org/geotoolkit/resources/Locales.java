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
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.xml.ValueConverter#toLanguageCode}.
 */
@Deprecated
public final class Locales extends Static {
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
        return org.apache.sis.util.Locales.SIS.getAvailableLanguages();
    }

    /**
     * Returns the list of available locales.
     *
     * @return Available locales.
     */
    public static Locale[] getAvailableLocales() {
        return org.apache.sis.util.Locales.SIS.getAvailableLocales();
    }

    /**
     * Returns the list of available locales formatted as string in the specified locale.
     *
     * @param locale The locale to use for formatting the strings to be returned.
     * @return String descriptions of available locales.
     */
    public static String[] getAvailableLocales(final Locale locale) {
        return org.apache.sis.util.Locales.SIS.getAvailableLocales(locale);
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
        return org.apache.sis.util.Locales.unique(locale);
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
        return org.apache.sis.util.Locales.parse(code);
    }

    /**
     * Returns the 3-letters ISO language code if available, or the 2-letters code otherwise.
     *
     * @param  locale The locale for which we want the language.
     * @return The language code, 3 letters if possible or 2 letters otherwise.
     *
     * @since 3.04
     *
     * @deprecated Moved to {@link org.apache.sis.xml.ValueConverter#toLanguageCode}.
     */
    @Deprecated
    public static String getLanguage(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException e) {
            Logging.recoverableException(Locales.class, "getLanguage", e);
            return locale.getLanguage();
        }
    }
}
