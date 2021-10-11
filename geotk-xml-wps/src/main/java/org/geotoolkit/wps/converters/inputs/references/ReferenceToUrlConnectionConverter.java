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

import java.net.URLConnection;
import java.util.Map;
import org.geotoolkit.wps.xml.v200.Reference;;

/**
 * Implementation of ObjectConverter to convert a reference into an URL connection.
 * WARNING: this converter does not download referenced data, just prepares a
 * connection to the given end point.
 *
 * @author Cédric Briançon (Geomatys).
 */
public class ReferenceToUrlConnectionConverter extends AbstractReferenceInputConverter<URLConnection> {
    private static ReferenceToUrlConnectionConverter INSTANCE;

    private ReferenceToUrlConnectionConverter() {
    }

    public static synchronized ReferenceToUrlConnectionConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToUrlConnectionConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<URLConnection> getTargetClass() {
        return URLConnection.class;
    }

    @Override
    public URLConnection convert(Reference source, Map<String, Object> params) throws UnconvertibleObjectException {
        return connect(source);
    }
}
