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
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.gco.StringAdapter;
import org.geotoolkit.internal.jaxb.gco.CharSequenceAdapter;


/**
 * JAXB adapter for XML {@code <GO_CharacterString>} or {@code <LanguageCode>} elements
 * mapped to {@link Locale}. This adapter formats the locale like below:
 *
 * {@preformat xml
 *   <gmd:language>
 *     <gco:CharacterString>eng</gco:CharacterString>
 *   </gmd:language>
 * }
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see org.geotoolkit.internal.jaxb.gmd.LanguageCode
 * @see org.geotoolkit.internal.jaxb.gmd.PT_Locale
 *
 * @since 2.5
 * @module
 */
public final class LocaleAdapter extends XmlAdapter<LanguageCode, Locale> {
    /**
     * The adapter on which to delegate the marshalling processes.
     */
    private final CharSequenceAdapter adapter;

    /**
     * Empty constructor for JAXB.
     */
    private LocaleAdapter() {
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
    public Locale unmarshal(final LanguageCode value) {
        final Locale candidate = LanguageCode.getLocale(value, false);
        if (candidate != null) {
            return candidate;
        }
        final String text = StringAdapter.toString(adapter.unmarshal(value));
        return (text != null) ? MarshalContext.converters().toLocale(text) : null;
    }

    /**
     * Converts the {@linkplain Locale locale} to the object to be marshalled in a
     * XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * {@note Current implementation formats the display name in the English locale. We use the UK
     * variant for consistency with the policy documented in <code>MarshalContext.getLocale()</code>}
     *
     * @param  value The locale value.
     * @return The adapter for the given locale.
     */
    @Override
    public LanguageCode marshal(final Locale value) {
        return LanguageCode.create(value, Locale.UK, // Really fixed to English, see method javadoc.
                ((MarshalContext.getFlags() & MarshalContext.SUBSTITUTE_LANGUAGE) != 0) ? adapter : null);
    }
}
