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

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.util.InternationalString;
import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * JAXB adapter for XML {@code <GO_CharacterString>} element mapped to {@link String}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class StringAdapter extends XmlAdapter<GO_CharacterString, String> {
    /**
     * The adapter on which to delegate the marshalling processes.
     */
    private final CharSequenceAdapter adapter;

    /**
     * Empty constructor for JAXB.
     */
    StringAdapter() {
        adapter = new CharSequenceAdapter();
    }

    /**
     * Creates a new adapter which will use the anchor map from the given adapter.
     *
     * @param adapter The adaptor on which to delegate the work.
     */
    public StringAdapter(final CharSequenceAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Returns a string representation of the given character sequence. If the given
     * sequence is an instance of {@link InternationalString}, then the locale from
     * the current unmashalling context is used in order to get a string.
     *
     * @param text The text for which to get a string representation, or {@code null}.
     * @return The string representation of the given text, or {@code null}.
     *
     * @since 3.17
     */
    public static String toString(final CharSequence text) {
        if (text != null) {
            if (text instanceof InternationalString) {
                return ((InternationalString) text).toString(MarshalContext.getLocale());
            }
            return text.toString();
        }
        return null;
    }

    /**
     * Converts a string read from a XML stream to the object containing
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@link String} which represents the metadata value.
     */
    @Override
    public String unmarshal(final GO_CharacterString value) {
        return toString(adapter.unmarshal(value));
    }

    /**
     * Converts a {@linkplain String string} to the object to be marshalled in a
     * XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The string value.
     * @return The adapter for this string.
     */
    @Override
    public GO_CharacterString marshal(final String value) {
        return adapter.marshal(value);
    }
}
