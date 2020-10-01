/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.inputs.references;

import org.apache.sis.util.UnconvertibleObjectException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.geotoolkit.wps.xml.v200.Reference;;

/**
 * Implementation of ObjectConverter to convert a reference into an URI.
 * WARNING: this converter does not download referenced data, just provides the URI as it.
 *
 * @author Guilhem Legal (Geomatys).
 */
public class ReferenceToUriConverter extends AbstractReferenceInputConverter<URI> {
    private static ReferenceToUriConverter INSTANCE;

    private ReferenceToUriConverter() {
    }

    public static synchronized ReferenceToUriConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToUriConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<URI> getTargetClass() {
        return URI.class;
    }

    @Override
    public URI convert(Reference source, Map<String, Object> params) throws UnconvertibleObjectException {
        if (source == null) return null;
        String href = source.getHref();
        if (href == null || (href = href.trim()).isEmpty()) return null;
        try {
            return new URI(href);
        } catch (URISyntaxException ex) {
            throw new UnconvertibleObjectException("Can't reach the reference data.", ex);
        }
    }
}
