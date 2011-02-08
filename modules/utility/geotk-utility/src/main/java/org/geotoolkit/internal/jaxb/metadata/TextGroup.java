/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.metadata;

import java.util.Set;
import java.util.Locale;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.converter.Classes;


/**
 * A set of strings localized in different languages. This adapter represents the
 * {@code <gmd:textGroup>} element defined for embedded translations in ISO-19139
 * standard. See {@link FreeText} class javadoc for an example.
 * <p>
 * If a localized string has a {@code null} locale, then this string will not be
 * included in this text group because that string should be already included in
 * the {@code <gco:CharacterString>} element of the parent {@link FreeText}  (at
 * least in default behavior - actually the above may not be true anymore if the
 * marshaller {@link org.geotoolkit.xml.XML#LOCALE} property has been set).
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see LocalisedCharacterString
 *
 * @since 2.5
 * @module
 */
final class TextGroup {
    /**
     * The set of {@linkplain LocalisedCharacterString localised string}.
     * JAXB uses this field at marshalling-time in order to wrap {@code N}
     * {@code <LocalisedCharacterString>} elements inside a single {@code <textGroup>} element.
     */
    @XmlElementWrapper(name = "textGroup")
    @XmlElement(name = "LocalisedCharacterString")
    protected LocalisedCharacterString[] localised;

    /**
     * Empty constructor only used by JAXB.
     */
    public TextGroup() {
    }

    /**
     * Constructs a {@linkplain TextGroup text group} from a {@link DefaultInternationalString}.
     * Note that the element with {@code null} locale, if any, is ignored (see class javadoc).
     *
     * @param text The international string.
     */
    TextGroup(final DefaultInternationalString text) {
        final Set<Locale> locales = text.getLocales();
        int n = locales.size();
        if (locales.contains(null)) {
            n--;
        }
        localised = new LocalisedCharacterString[n];
        int i=0;
        for (final Locale locale : locales) {
            if (locale != null) {
                localised[i++] = new LocalisedCharacterString(locale, text.toString(locale));
            }
        }
    }

    /**
     * Returns a string representation of this text group for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        if (localised != null) {
            final String lineSeparator = System.getProperty("line.separator", "\n");
            for (LocalisedCharacterString string : localised) {
                buffer.append(lineSeparator).append("  ").append(string);
            }
        }
        return buffer.append(']').toString();
    }
}
