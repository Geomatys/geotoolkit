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
package org.geotoolkit.wps.converters.outputs.references;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import static org.geotoolkit.wps.converters.WPSObjectConverter.IOTYPE;
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;

/**
 * Implementation of ObjectConverter to convert a {@link URL url} into a {@link OutputReferenceType reference}.
 *
 * @author Thomas Rouby (Geomatys).
 */
public class UrlConnectionToReferenceConverter extends AbstractReferenceOutputConverter<URLConnection> {

    private static UrlConnectionToReferenceConverter INSTANCE;

    private UrlConnectionToReferenceConverter() {}

    public static synchronized UrlConnectionToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UrlConnectionToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<URLConnection> getSourceClass() {
        return URLConnection.class;
    }

    @Override
    public Reference convert(URLConnection source, Map<String, Object> params) throws UnconvertibleObjectException {
        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);

        reference.setHref(source.getURL().toString());
        mapParameters(reference, params);

        return reference;
    }
}
