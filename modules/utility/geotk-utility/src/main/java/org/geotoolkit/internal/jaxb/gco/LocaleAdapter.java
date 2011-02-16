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
package org.geotoolkit.internal.jaxb.gco;

import java.util.Locale;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.resources.Locales;


/**
 * JAXB adapter for XML {@code <GO_CharacterString>} element mapped to {@link Locale}.
 * This adapter formats the locale like below:
 *
 * {@preformat xml
 *   <gmd:language>
 *     <gco:CharacterString>eng</gco:CharacterString>
 *   </gmd:language>
 * }
 *
 * For an alternative format, see {@link org.geotoolkit.internal.jaxb.code.LanguageAdapter}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class LocaleAdapter extends XmlAdapter<GO_CharacterString, Locale> {
    /**
     * The adapter on which to delegate the marshalling processes.
     */
    private final CharSequenceAdapter adapter;

    /**
     * Empty constructor for JAXB.
     */
    LocaleAdapter() {
        adapter = new CharSequenceAdapter();
    }

    /**
     * Creates a new adapter which will use the anchor map from the given adapter.
     *
     * @param adapter The adaptor on which to delegate the work.
     */
    public LocaleAdapter(final CharSequenceAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Converts the locale read from a XML stream to the object containing the value.
     * JAXB calls automatically this method at unmarshalling time.
     *
     * @param  value The adapter for this metadata value.
     * @return A {@linkplain Locale locale} which represents the metadata value.
     */
    @Override
    public Locale unmarshal(final GO_CharacterString value) {
        final CharSequence text = adapter.unmarshal(value);
        return (text != null) ? MarshalContext.converters().toLocale(text.toString()) : null;
    }

    /**
     * Converts the {@linkplain Locale locale} to the object to be marshalled in a
     * XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The locale value.
     * @return The adapter for the given locale.
     */
    @Override
    public GO_CharacterString marshal(final Locale value) {
        return (value != null) ? adapter.marshal(Locales.getLanguage(value)) : null;
    }
}
