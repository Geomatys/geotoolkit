/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.gmd;

import java.util.Locale;
import java.util.Objects;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;

import org.geotoolkit.resources.Locales;
import org.apache.sis.util.Classes;


/**
 * The {@code <LocalisedCharacterString>} elements nested in a {@code <textGroup>} one.
 * This element contains a string for a given {@linkplain Locale locale}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @see TextGroup
 *
 * @since 2.5
 * @module
 */
final class LocalisedCharacterString {
    /**
     * A prefix to concatenate with the {@linkplain Locale#getISO3Language() language code}
     * in order to get the attribute value specified in ISO-19139 for this elements.
     */
    private static final String LOCALE = "#locale-";

    /**
     * The locale value for this string.
     */
    protected Locale locale;

    /**
     * The text in the locale of this localized string. JAXB uses this field for formatting
     * the {@code <LocalisedCharacterString>} elements in the XML tree at marshalling-time.
     */
    @XmlValue
    protected String text;

    /**
     * Empty constructor only used by JAXB.
     */
    public LocalisedCharacterString() {
    }

    /**
     * Constructs a localized string for the given locale and text.
     *
     * @param locale The string language.
     * @param text The string.
     */
    LocalisedCharacterString(final Locale locale, final String text) {
        this.locale = locale;
        this.text = text;
    }

    /**
     * Returns the locale language, as specified by ISO-19139 for
     * {@code <LocalisedCharacterString>} attribute.
     *
     * @return The current locale.
     */
    @XmlAttribute(name = "locale", required = true)
    public String getLocale() {
        return (locale != null) ? LOCALE.concat(Locales.getLanguage(locale)) : null;
    }

    /**
     * Sets the locale language, using a string formatted as {@code #locale-xx},
     * where {@code xx} are the two letters representing the language.
     *
     * @param localeId The new locale.
     */
    public void setLocale(String localeId) {
        if (localeId != null) {
            localeId = localeId.trim();
            if (!localeId.isEmpty()) {
                locale = Locales.parse(localeId.substring(localeId.indexOf('-') + 1));
                return;
            }
        }
        locale = null;
    }

    /**
     * Returns a hash code value for this string.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        if (locale != null) hash += locale.hashCode();
        if (text   != null) hash = 31*hash + text.hashCode();
        return hash;
    }

    /**
     * Compares this string with the given object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof LocalisedCharacterString)) {
            return false;
        }
        final LocalisedCharacterString that = (LocalisedCharacterString) object;
        return Objects.equals(locale, that.locale) && Objects.equals(text, that.text);
    }

    /**
     * Returns a string representation of this object for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this))
                .append('[').append(getLocale());
        if (text != null) {
            buffer.append(", \"").append(text).append('"');
        }
        return buffer.append(']').toString();
    }
}
