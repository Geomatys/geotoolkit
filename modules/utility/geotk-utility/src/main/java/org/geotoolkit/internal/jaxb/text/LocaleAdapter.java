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
package org.geotoolkit.internal.jaxb.text;

import java.util.Locale;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.resources.Locales;


/**
 * JAXB adapter for {@link Locale}, in order to integrate the value in a element
 * complying with ISO-19139 standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.04
 *
 * @since 2.5
 * @module
 */
public final class LocaleAdapter extends XmlAdapter<LocaleAdapter, Locale> {
    /**
     * The locale value.
     */
    private Locale locale;

    /**
     * Empty constructor for JAXB only.
     */
    public LocaleAdapter() {
    }

    /**
     * Builds an adapter for {@link Locale}.
     *
     * @param locale The locale to marshall.
     */
    private LocaleAdapter(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Returns the date matching with the metadata value. This method is systematically
     * called at marshalling time by JAXB.
     *
     * @return The locale to marshal.
     */
    @XmlElement(name = "CharacterString")
    public String getLocale() {
        return Locales.getLanguage(locale);
    }

    /**
     * Sets the value for the metadata locale. This method is systematically called at
     * unmarshalling time by JAXB.
     *
     * @param language The unmarshalled locale.
     */
    public void setLocale(String language) {
        if (language != null) {
            language = language.trim();
            if (language.length() != 0) {
                locale = Locales.parse(language);
                return;
            }
        }
        locale = null;
    }

    /**
     * Converts the locale read from a XML stream to the object containing the value.
     * JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@linkplain Locale locale} which represents the metadata value.
     */
    @Override
    public Locale unmarshal(final LocaleAdapter value) {
        if (value == null) {
            return null;
        }
        return value.locale;
    }

    /**
     * Converts the {@linkplain Locale locale} to the object to be marshalled in a
     * XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The locale value.
     * @return The adapter for the given locale.
     */
    @Override
    public LocaleAdapter marshal(final Locale value) {
        if (value == null) {
            return null;
        }
        return new LocaleAdapter(value);
    }
}
