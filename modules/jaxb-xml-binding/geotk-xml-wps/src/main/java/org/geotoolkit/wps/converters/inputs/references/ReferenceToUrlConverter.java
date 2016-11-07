/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.geotoolkit.wps.xml.Reference;

/**
 * Implementation of ObjectConverter to convert a reference into an URL.
 * WARNING: this converter does not download referenced data, just provides the URL as it.
 *
 * @author Cédric Briançon (Geomatys).
 */
public class ReferenceToUrlConverter extends AbstractReferenceInputConverter<URL> {
    private static ReferenceToUrlConverter INSTANCE;

    private ReferenceToUrlConverter() {
    }

    public static synchronized ReferenceToUrlConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToUrlConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<URL> getTargetClass() {
        return URL.class;
    }

    @Override
    public URL convert(Reference source, Map<String, Object> params) throws UnconvertibleObjectException {
        try {
            return new URL(source.getHref());
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Can't reach the reference data.", ex);
        }
    }
}
