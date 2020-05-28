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
package org.geotoolkit.wps.converters.outputs.references;

import java.net.URI;
import org.apache.sis.util.UnconvertibleObjectException;

import java.net.URL;
import java.util.Map;
import org.geotoolkit.wps.xml.v200.Reference;

/**
 * Implementation of ObjectConverter to convert a {@link URI uri} into a {@link Reference reference}.
 *
 * @author Thomas Rouby (Geomatys).
 */
public class UriToReferenceConverter extends AbstractReferenceOutputConverter<URI> {

    private static UriToReferenceConverter INSTANCE;

    private UriToReferenceConverter() {}

    public static synchronized UriToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UriToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<URI> getSourceClass() {
        return URI.class;
    }

    @Override
    public Reference convert(URI source, Map<String, Object> params) throws UnconvertibleObjectException {
        final Reference reference = new Reference();
        reference.setHref(source.toString());
        mapParameters(reference, params);

        return reference;
    }
}
