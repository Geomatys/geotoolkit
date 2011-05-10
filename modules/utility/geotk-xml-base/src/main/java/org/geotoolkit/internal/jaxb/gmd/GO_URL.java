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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.internal.jaxb.MarshalContext;


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
     * The URI as a string. We uses a string in order to allow the user
     * to catch potential error at unmarshalling time.
     */
    @XmlElement(name = "URL")
    private String uri;

    /**
     * Empty constructor for JAXB only.
     */
    public GO_URL() {
    }

    /**
     * Builds an adapter for the given URI.
     *
     * @param value The URI to marshall.
     */
    private GO_URL(final URI value) {
        uri = value.toString();
    }

    /**
     * Converts a URI read from a XML stream to the object which will contains
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@link URI} which represents the metadata value.
     * @throws URISyntaxException if the given value contains an invalid URI.
     */
    @Override
    public URI unmarshal(final GO_URL value) throws URISyntaxException {
        return (value != null) ? MarshalContext.converters().toURI(value.uri) : null;
    }

    /**
     * Converts a {@link URI} to the object to be marshalled in a XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The URI value.
     * @return The adapter for this URI.
     */
    @Override
    public GO_URL marshal(final URI value) {
        return (value != null) ? new GO_URL(value) : null;
    }
}
