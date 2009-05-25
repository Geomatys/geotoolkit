/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter wrapping a URI value with a {@code <gco:CharacterString>}
 * element, for ISO-19139 compliance.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class URINameAdapter extends XmlAdapter<URINameAdapter, URI> {
    /**
     * The URI value.
     */
    @XmlElement(name = "CharacterString")
    public String uri;

    /**
     * Empty constructor for JAXB only.
     */
    public URINameAdapter() {
    }

    /**
     * Builds an adapter for {@link URI}.
     *
     * @param uri The URI to marshall.
     */
    private URINameAdapter(final String uri) {
        this.uri = uri;
    }

    /**
     * Converts a URI read from a XML stream ro the object containing the
     * value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return An {@link URI} which represents the metadata value.
     * @throws URISyntaxException If the string is not a valid URI.
     */
    @Override
    public URI unmarshal(final URINameAdapter value) throws URISyntaxException {
        if (value == null) {
            return null;
        }
        return new URI(value.uri);
    }

    /**
     * Converts a {@link URI} to the object to be marshalled in a XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The URI value.
     * @return The adapter for the given URI.
     */
    @Override
    public URINameAdapter marshal(final URI value) {
        if (value == null) {
            return null;
        }
        return new URINameAdapter(value.toString());
    }
}
