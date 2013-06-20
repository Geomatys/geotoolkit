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

import org.apache.sis.util.Locales;
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
 *   <gmd:country>
 *     <gmd:Country codeList="http://(...snip...)" codeListValue="FR">France</gmd:Country>
 *   </gmd:country>
 * }
 *
 * Note that {@code <gco:CharacterString>} can be substituted to the country code.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.17
 * @module
 */
@XmlType(name = "Country_PropertyType")
public final class Country extends GO_CharacterString {
    /**
     * The country using a {@link CodeList}-like format.
     */
    @XmlElement(name = "Country")
    private CodeListProxy proxy;

    /**
     * Empty constructor for JAXB only.
     */
    private Country() {
    }

    /**
     * Builds a {@code <gco:CharacterString>} element.
     * For private use by {@link #create(Locale)} only.
     */
    private Country(final GO_CharacterString code) {
        super(code);
    }

    /**
     * Builds a {@code <Country>} element.
     * For private use by {@link #create(Locale, Locale)} only.
     */
    private Country(final String code, final String text) {
//      proxy = new CodeListProxy("ML_gmxCodelists.xml", "Country", code, text);
    }

    /**
     * Creates a new wrapper for the given locale.
     *
     * @param value         The value to marshall, or {@code null}.
     * @param marshalLocale The locale of the marshaller, or {@code null} for English.
     * @paral anchors       If non-null, marshall the locale as a {@code <gco:CharacterString>} instead
     *                      than {@code <Country>}, using the given anchors if any.
     */
    static Country create(final Locale value, Locale marshalLocale, final CharSequenceAdapter anchors) {
        if (value != null) {
            final String code = value.getCountry();
            if (anchors != null) {
                if (!code.isEmpty()) {
                    final GO_CharacterString string = anchors.marshal(code);
                    if (string != null) {
                        return new Country(string);
                    }
                }
            }
            if (marshalLocale == null) {
                marshalLocale = Locale.UK;
            }
            final String text = value.getDisplayCountry(marshalLocale);
            if (!code.isEmpty() || !text.isEmpty()) {
                return new Country(code, text);
            }
        }
        return null;
    }

    /**
     * Returns the locale for the given country (which may be null), or {@code null} if none.
     *
     * @param value The wrapper for this metadata value.
     * @return A locale which represents the metadata value.
     *
     * @see LanguageCode#getLocale(LanguageCode, boolean)
     */
    static Locale getLocale(final Country value) {
        if (value != null) {
            String code = null;
            if (value.proxy != null) {
                code = value.proxy.codeListValue;
            }
            // If the country was not specified as a code list,
            // look for a simple character string declaration.
            if (code == null) {
                code = value.toString();
            }
            if (code != null && !(code = code.trim()).isEmpty()) {
                return Locales.unique(new Locale("", code));
            }
        }
        return null;
    }
}
