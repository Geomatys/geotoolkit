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
package org.geotoolkit.internal.jaxb.gmd;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB adapter wrapping a URI in a {@code <gmd:URL>} element, for ISO-19139 compliance.
 * Note that while this object is called {@code "URL"}, we actually use the {@link URI}
 * Java object.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class GO_URL extends XmlAdapter<GO_URL, URI> {
    /**
     * The URI value.
     */
    @XmlElement(name = "URL")
    public URI url;

    /**
     * Empty constructor for JAXB only.
     */
    public GO_URL() {
    }

    /**
     * Builds an adapter for the given {@link URL}.
     *
     * @param url The URI to marshall, as a URL.
     */
    private GO_URL(final URI url) {
        this.url = url;
    }

    /**
     * Converts a URI read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@link URI} which represents the metadata value.
     * @throws URISyntaxException if the given value contains an invalid URL.
     */
    @Override
    public URI unmarshal(final GO_URL value) throws URISyntaxException {
        return (value != null) ? value.url : null;
    }

    /**
     * Converts a {@link URI} to the object to be marshalled in a XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The URI value.
     * @return The adapter for this URI.
     * @throws IllegalArgumentException If the given URI is not absolute.
     */
    @Override
    public GO_URL marshal(final URI value) throws IllegalArgumentException {
        return (value != null) ? new GO_URL(value) : null;
    }
}
