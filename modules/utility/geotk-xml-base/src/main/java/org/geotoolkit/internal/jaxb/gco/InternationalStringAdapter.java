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
package org.geotoolkit.internal.jaxb.gco;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;


/**
 * JAXB adapter for XML {@code <GO_CharacterString>} element mapped to {@link InternationalString}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class InternationalStringAdapter extends XmlAdapter<GO_CharacterString, InternationalString> {
    /**
     * The adapter on which to delegate the marshalling processes.
     */
    private final CharSequenceAdapter adapter;

    /**
     * Empty constructor for JAXB.
     */
    InternationalStringAdapter() {
        adapter = new CharSequenceAdapter();
    }

    /**
     * Creates a new adapter which will use the anchor map from the given adapter.
     *
     * @param adapter The adaptor on which to delegate the work.
     */
    public InternationalStringAdapter(final CharSequenceAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Converts an object read from a XML stream to an {@link InternationalString}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param value The adapter for the string value.
     * @return An {@link InternationalString} for the string value.
     */
    @Override
    public InternationalString unmarshal(final GO_CharacterString value) {
        final CharSequence text = adapter.unmarshal(value);
        if (text != null) {
            if (text instanceof InternationalString) {
                return (InternationalString) text;
            }
            return new SimpleInternationalString(text.toString());
        }
        return null;
    }

    /**
     * Converts an {@link InternationalString} to an object to format into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public GO_CharacterString marshal(final InternationalString value) {
        return adapter.marshal(value);
    }
}
