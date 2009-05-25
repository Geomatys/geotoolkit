/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter in order to wrap the string value with a {@code <gco:CharacterString>}
 * element, for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class CharSequenceAdapter extends XmlAdapter<CharacterString, CharSequence> {
    /**
     * Empty constructor for JAXB only.
     */
    public CharSequenceAdapter() {
    }

    /**
     * Converts a string read from a XML stream to the object containing
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@link CharSequence} which represents the metadata value.
     */
    @Override
    public final CharSequence unmarshal(final CharacterString value) {
        if (value == null) {
            return null;
        }
        final Object text = value.text;
        if (text == null || text instanceof CharSequence) {
            return (CharSequence) text;
        }
        return text.toString();
    }

    /**
     * Converts a {@linkplain CharSequence character sequence} to the object to be marshalled
     * in a XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The string value.
     * @return The adapter for this string.
     */
    @Override
    public CharacterString marshal(final CharSequence value) {
        return (value != null) ? new CharacterString(value) : null;
    }
}
