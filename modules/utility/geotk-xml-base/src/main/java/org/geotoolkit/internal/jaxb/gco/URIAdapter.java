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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * JAXB adapter wrapping a URI value with a {@code <gco:CharacterString>}
 * element, for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class URIAdapter extends XmlAdapter<GO_CharacterString, URI> {
    /**
     * The adapter on which to delegate the marshalling processes.
     */
    private final CharSequenceAdapter adapter;

    /**
     * Empty constructor for JAXB.
     */
    URIAdapter() {
        adapter = new CharSequenceAdapter();
    }

    /**
     * Creates a new adapter which will use the anchor map from the given adapter.
     *
     * @param adapter The adaptor on which to delegate the work.
     */
    public URIAdapter(final CharSequenceAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Converts a URI read from a XML stream to the object containing the
     * value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  value The adapter for this metadata value.
     * @return An {@link URI} which represents the metadata value.
     * @throws URISyntaxException If the string is not a valid URI.
     */
    @Override
    public URI unmarshal(final GO_CharacterString value) throws URISyntaxException {
        final String text = StringAdapter.toString(adapter.unmarshal(value));
        return (text != null) ? MarshalContext.converters().toURI(text) : null;
    }

    /**
     * Converts a {@link URI} to the object to be marshalled in a XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The URI value.
     * @return The adapter for the given URI.
     */
    @Override
    public GO_CharacterString marshal(final URI value) {
        return (value != null) ? adapter.marshal(value.toString()) : null;
    }
}
