/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2024, Geomatys
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
package org.geotoolkit.wps.converters.outputs.complex;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * A converter to transform a File into ComplexOutput data for wps ExecuteResponse query.
 *
 * @author Guilhem Legal (Geomatys).
 */
public class PathToComplexConverter extends AbstractComplexOutputConverter<Path> {

    private static PathToComplexConverter INSTANCE;

    private PathToComplexConverter() {
    }

    public static synchronized PathToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PathToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Path> getSourceClass() {
        return Path.class;
    }

    /**
     * Convert a file into Data object, according to the specifications given in params parameter.
     *
     * @param source The file to convert.
     * @param params The parameters used for conversion (Mime-Type/encoding). If null, mime is set to application/octet-stream, and encoding to base64
     * @return
     * @throws UnconvertibleObjectException
     */
    @Override
    public Data convert(Path source, Map<String, Object> params) throws UnconvertibleObjectException {
        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (params == null) {
            throw new UnconvertibleObjectException("Mandatory parameters are missing.");
        }

        final Object tmpMime = params.get(MIME);
        final String mime;
        final String encoding;
        if (tmpMime instanceof String) {
            mime = (String) tmpMime;

            final Object tmpEncoding = params.get(ENCODING);
            if (tmpEncoding instanceof String)
                encoding = (String) tmpEncoding;
            else
                encoding = null;
        } else {
            mime = WPSMimeType.APP_OCTET.val();
            encoding = WPSEncoding.BASE64.getValue();
        }

        final Data complex = new Data();
        complex.setMimeType(mime);
        complex.setEncoding(encoding);

        //Plain text
        if (mime.startsWith("text")) {
            //XML is special case, we try to find an associate schema.
            if (mime.contains("xml") || mime.contains("gml")) {
                String schemaLocation = source.toAbsolutePath().toString();
                File ogrSchema = new File(schemaLocation);
                // If we find a schema, we ensure it's location is public before giving it.
                if (ogrSchema.exists()) {
                    Object tmpDirValue = params.get(TMP_DIR_PATH);
                    String tmpDir;
                    if (tmpDirValue instanceof URI) {
                        tmpDir = ((URI) params.get(TMP_DIR_PATH)).toString();
                    } else if (tmpDirValue instanceof String) {
                        tmpDir = (String) params.get(TMP_DIR_PATH);
                    } else {
                        throw new UnconvertibleObjectException("Unexpected type for " + TMP_DIR_PATH + " parameter.");
                    }
                    String tmpURL = (String) params.get(TMP_DIR_URL);
                    if (tmpDir == null || tmpURL == null) {
                        throw new UnconvertibleObjectException("Mandatory parameters are missing.");
                    }
                    if (!schemaLocation.contains(tmpDir)) {
                        String schemaName = source.getFileName().toString().replace("\\.[a-z]ml", "").concat(".xsd");
                        Path schemaDest = Paths.get(tmpDir, schemaName);
                        try {
                            IOUtilities.copy(source, schemaDest);
                            schemaLocation = schemaDest.toAbsolutePath().toString();
                        } catch (IOException e) {
                            throw new UnconvertibleObjectException("Unexpected error on schema copy.", e);
                        }
                    }
                    complex.setSchema(schemaLocation.replace(tmpDir, tmpURL));
                }
            }
            // CData needed because files could contain problematic characters.
            complex.getContent().add("<![CDATA[");
            complex.getContent().add(source);
            complex.getContent().add("]]>");
        } else {
            //If no text format, We'll put it as a base64 object.
            if (encoding == null || !encoding.equals(WPSEncoding.BASE64.getValue())) {
                throw new UnconvertibleObjectException("Encoding should be in Base64 for complex request.");
            }

            try (InputStream stream = Files.newInputStream(source)) {
                byte[] barray = stream.readAllBytes();
                complex.getContent().add(Base64.getEncoder().encodeToString(barray));
            } catch (Exception ex) {
                throw new UnconvertibleObjectException(ex.getMessage(), ex);
            }
        }

        return complex;
    }
}
