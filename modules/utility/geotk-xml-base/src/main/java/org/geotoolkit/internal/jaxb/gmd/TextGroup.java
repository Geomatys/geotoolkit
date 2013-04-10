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
import javax.xml.bind.annotation.XmlElement;
import org.apache.sis.util.Classes;


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
 * <p>
 * The {@code TextGroup} name suggest that this object can contains many localized
 * strings. It was done that way prior Geotk 3.17. However it appears that despite
 * its name, {@code TextGroup} shall always contains exactly 1 localized strings
 * and the whole {@code TextGroup} element shall be repeated for each additional
 * languages. Geotk 3.17 uses the ISO 19139 compliant form is used for marshalling,
 * but accepts both forms during unmarshalling.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see LocalisedCharacterString
 * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-152">GEOTK-152</a>
 *
 * @since 2.5
 * @module
 */
final class TextGroup {
    /**
     * The set of {@linkplain LocalisedCharacterString localized string}.
     * JAXB uses this field at marshalling-time in order to wrap {@code N}
     * {@code <LocalisedCharacterString>} elements inside a single {@code <textGroup>} element.
     * <p>
     * In ISO 19139 compliant documents, the length of this array shall be exactly 1.
     * However Geotk allows arbitrary length for compatibility and convenience reasons.
     * See GEOTK-152 for examples.
     */
    @XmlElement(name = "LocalisedCharacterString")
    protected LocalisedCharacterString[] localized;

    /**
     * Empty constructor only used by JAXB.
     */
    public TextGroup() {
    }

    /**
     * Constructs a {@linkplain TextGroup text group} for a single locale. This constructor
     * put exactly one string in the {@code TextGroup}, as required by ISO 19139. However
     * it would be possible to declare an other constructor allowing the more compact form
     * (the one used before GEOTK-152 fix) if there is a need for that in the future.
     *
     * @param locale The string language.
     * @param text The string.
     */
    TextGroup(final Locale locale, final String text) {
        localized = new LocalisedCharacterString[] {
            new LocalisedCharacterString(locale, text)
        };
    }

    /**
     * Returns a string representation of this text group for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this));
        if (localized != null) {
            final String lineSeparator = System.lineSeparator();
            for (LocalisedCharacterString string : localized) {
                buffer.append(lineSeparator).append("  ").append(string);
            }
        }
        return buffer.append(']').toString();
    }
}
