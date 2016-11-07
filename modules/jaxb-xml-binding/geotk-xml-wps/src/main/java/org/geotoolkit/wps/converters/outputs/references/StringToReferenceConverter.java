/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.references;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import static org.geotoolkit.wps.converters.WPSObjectConverter.IOTYPE;
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.WPSXmlFactory;


/**
 * Implementation of ObjectConverter to convert a {@code String} into a {@link Reference reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class StringToReferenceConverter extends AbstractReferenceOutputConverter<String> {

    private static StringToReferenceConverter INSTANCE;

    private StringToReferenceConverter() {
    }

    public static synchronized StringToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StringToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Reference convert(final String source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (!(params.get(TMP_DIR_PATH) instanceof URI)) {
            throw new UnconvertibleObjectException("The output directory should be defined by an URI.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        Reference reference = WPSXmlFactory.buildInOutReference(wpsVersion, ioType);


        final String mime = (params.get(MIME) == null)? "text/plain" : (String) params.get(MIME);
        final String encoding = (params.get(ENCODING) == null)? "UTF-8" : (String) params.get(ENCODING);

        reference.setMimeType(mime);
        reference.setEncoding(encoding);
        reference.setSchema((String) params.get(SCHEMA));

        final String randomFileName = UUID.randomUUID().toString();
        try {
            //create file
            final Path literalFile = Paths.get((URI) params.get(TMP_DIR_PATH)).resolve(randomFileName);
            IOUtilities.writeString(source, literalFile);
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" + randomFileName);

        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error occurs during image writing.", ex);
        }
        return reference;
    }

}
