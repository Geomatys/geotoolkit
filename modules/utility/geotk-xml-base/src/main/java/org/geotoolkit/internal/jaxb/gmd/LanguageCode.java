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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.geotoolkit.resources.Locales;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.apache.sis.internal.jaxb.gmd.CodeListProxy;
import org.geotoolkit.internal.jaxb.gco.GO_CharacterString;
import org.geotoolkit.internal.jaxb.gco.CharSequenceAdapter;


/**
 * JAXB wrapper for {@link Locale}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 * <p>
 * This adapter formats the locale like below:
 *
 * {@preformat xml
 *   <gmd:language>
 *     <gmd:LanguageCode codeList="http://(...snip...)" codeListValue="eng">English</gmd:LanguageCode>
 *   </gmd:language>
 * }
 *
 * Note that {@code <gco:CharacterString>} can be substituted to the language code.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
@XmlType(name = "LanguageCode_PropertyType")
public final class LanguageCode extends GO_CharacterString {
    /**
     * The language using a {@link CodeList}-like format.
     */
    @XmlElement(name = "LanguageCode")
    private CodeListProxy proxy;

    /**
     * Empty constructor for JAXB only.
     */
    private LanguageCode() {
    }

    /**
     * Builds a {@code <gco:CharacterString>} element.
     * For private use by {@link #create(Locale)} only.
     */
    private LanguageCode(final GO_CharacterString code) {
        super(code);
    }

    /**
     * Builds a {@code <LanguageCode>} element.
     * For private use by {@link #create(Locale, Locale)} only.
     */
    private LanguageCode(final String code, final String text) {
//      proxy = new CodeListProxy("ML_gmxCodelists.xml", "LanguageCode", code, text);
    }

    /**
     * Creates a new wrapper for the given locale.
     *
     * @param value         The value to marshall, or {@code null}.
     * @param marshalLocale The locale of the marshaller, or {@code null} for English.
     * @paral anchors       If non-null, marshall the locale as a {@code <gco:CharacterString>} instead
     *                      than {@code <LanguageCode>}, using the given anchors if any.
     */
    static LanguageCode create(final Locale value, Locale marshalLocale, final CharSequenceAdapter anchors) {
        if (value != null) {
            final String code = Locales.getLanguage(value);
            if (anchors != null) {
                if (!code.isEmpty()) {
                    final GO_CharacterString string = anchors.marshal(code);
                    if (string != null) {
                        return new LanguageCode(string);
                    }
                }
            }
            if (marshalLocale == null) {
                marshalLocale = Locale.UK;
            }
            final String text = value.getDisplayName(marshalLocale);
            if (!code.isEmpty() || !text.isEmpty()) {
                return new LanguageCode(code, text);
            }
        }
        return null;
    }

    /**
     * Returns the locale for the given language (which may be null), or {@code null} if none.
     *
     * @param value The wrapper for this metadata value.
     * @param useCharSequence Whatever this method should fallback on the
     *        {@code gco:CharacterString} element if no value were specified for the
     *        {@code gml:LanguageCode} element.
     * @return A locale which represents the metadata value.
     *
     * @see Country#getLocale(Country)
     */
    static Locale getLocale(final LanguageCode value, final boolean useCharSequence) {
        if (value != null) {
            if (value.proxy != null) {
                String code = value.proxy.codeListValue;
                if (code != null && !(code = code.trim()).isEmpty()) {
                    return org.apache.sis.util.Locales.parse(code);
                }
            }
            if (useCharSequence) {
                final String text = value.toString();
                if (text != null) {
                    return MarshalContext.converters().toLocale(text);
                }
            }
        }
        return null;
    }
}
