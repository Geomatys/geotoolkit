/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opengis.metadata.identification.CharacterSet;

import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.Locales;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.code.MD_CharacterSetCode;


/**
 * JAXB adapter for {@link Locale}, in order to integrate the value in an element respecting
 * the ISO-19139 standard. See package documentation for more information about the handling
 * of {@code CodeList} in ISO-19139.
 * <p>
 * This adapter formats the locale like below:
 *
 * {@preformat xml
 *   <gmd:locale>
 *     <gmd:PT_Locale id="locale-eng">
 *       <gmd:languageCode>
 *         <gmd:LanguageCode codeList="./resources/Codelists.xml#LanguageCode" codeListValue="eng">eng</gmd:LanguageCode>
 *       </gmd:languageCode>
 *       <gmd:country>
 *         <gmd:Country codeList="./resources/Codelists.xml#Country" codeListValue="GB">GB</gmd:Country>
 *       </gmd:country>
 *       <gmd:characterEncoding>
 *         <gmd:MD_CharacterSetCode codeList="./resources/Codelists.xml#MD_CharacterSetCode"
 *                 codeListValue="8859part15">8859part15</gmd:MD_CharacterSetCode>
 *       </gmd:characterEncoding>
 *     </gmd:PT_Locale>
 *   </gmd:locale>
 * }
 *
 * For an alternative (simpler) format, see {@link org.geotoolkit.internal.jaxb.gco.LocaleAdapter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see LanguageCode
 * @see Country
 * @see org.geotoolkit.internal.jaxb.gco.LocaleAdapter
 *
 * @since 3.17
 * @module
 */
public final class PT_Locale extends XmlAdapter<PT_Locale, Locale> {
    /**
     * The attributes wrapped in a {@code "PT_Locale"} element.
     */
    @XmlElement(name = "PT_Locale")
    private Wrapper element;

    /**
     * Wraps the {@code "locale"} attributes in a {@code "PT_Locale"} element.
     */
    @XmlType(name = "PT_Locale", propOrder = { "languageCode", "country", "characterEncoding" })
    private static final class Wrapper {
        /**
         * Empty constructor for JAXB only.
         */
        public Wrapper() {
        }

        /**
         * The language code, or {@code null} if none.
         */
        @XmlElement(required = true)
        LanguageCode languageCode;

        /**
         * The country code, or {@code null} if none.
         */
        @XmlElement
        Country country;

        /**
         * The character encoding. The specification said:
         *
         * <blockquote>Indeed, an XML file can only support data expressed in a single character set,
         * which is generally declared in the XML file header. Having all the localized strings stored
         * in a single XML file would limit the use of a single character set such as UTF-8. In order
         * to avoid this, the {@link LocalisedCharacterString} class is implemented specifically to
         * allow a by-reference containment of the {@link PT_FreeText#textGroup} property, and the
         * {@link PT_LocaleContainer} is the recommended root element to be instantiated in a
         * dedicated XML file. The localized string related to a given locale can be stored in a
         * corresponding locale container (i.e. XML file) and referenced from the
         * {@link PT_FreeText#textGroup} property instances.
         * </blockquote>
         *
         * Current Geotk implementation does not yet support {@code PT_LocaleContainer}.
         */
        @XmlElement(required = true)
        @XmlJavaTypeAdapter(MD_CharacterSetCode.class)
        CharacterSet characterEncoding;

        /**
         * Creates a new wrapper for the given locale.
         */
        Wrapper(final Locale locale) {
            final Locale marshalLocale = MarshalContext.getLocale();
            languageCode = LanguageCode.create(locale, marshalLocale, null);
            country      = Country     .create(locale, marshalLocale, null);
            // The characterEncoding field will be initialized at marshalling time
            // (see the method below).
        }

        /**
         * Invoked by JAXB {@link javax.xml.bind.Marshaller} before this object is marshalled to XML.
         * This method sets the {@link #characterEncoding} to the XML encoding.
         */
        private void beforeMarshal(final Marshaller marshaller) {
            final Object encoding;
            try {
                encoding = marshaller.getProperty(Marshaller.JAXB_ENCODING);
            } catch (PropertyException e) {
                // Should never happen. But if it happen anyway, just let the
                // characterEncoding unitialized: it will not be marshalled.
                Logging.unexpectedException(PT_Locale.class, "beforeMarshal", e);
                return;
            }
            if (encoding instanceof String) {
                characterEncoding = Types.forCodeName(CharacterSet.class, (String) encoding, true);
            }
        }
    }

    /**
     * Empty constructor for JAXB only.
     */
    public PT_Locale() {
    }

    /**
     * Creates a new wrapper for the given locale.
     */
    private PT_Locale(final Locale locale) {
        element = new Wrapper(locale);
    }

    /**
     * Substitutes the locale by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The locale value.
     * @return The adapter for the locale value.
     */
    @Override
    public PT_Locale marshal(final Locale value) {
        return (value != null) ? new PT_Locale(value) : null;
    }

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A locale which represents the metadata value.
     * @throws Exception Should never happen.
     */
    @Override
    public Locale unmarshal(final PT_Locale value) throws Exception {
        if (value != null) {
            final Wrapper element = value.element;
            if (element != null) {
                Locale language = LanguageCode.getLocale(element.languageCode, true);
                Locale country  = Country.getLocale(element.country);
                if (language == null) {
                    language = country;
                } else if (country != null) {
                    // Merge the language and the country in a single Locale instance.
                    final String c = country.getCountry();
                    if (!c.equals(language.getCountry())) {
                        language = Locales.unique(new Locale(language.getLanguage(), c));
                    }
                }
                return language;
            }
        }
        return null;
    }
}
