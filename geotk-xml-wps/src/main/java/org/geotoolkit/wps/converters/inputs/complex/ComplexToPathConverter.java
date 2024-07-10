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
package org.geotoolkit.wps.converters.inputs.complex;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.v200.Data;

/**
 * Save the content of a WPS complex input into local file.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ComplexToPathConverter extends AbstractComplexInputConverter<Path> {

    private static ComplexToPathConverter INSTANCE;

    private ComplexToPathConverter() {
    }

    public static synchronized ComplexToPathConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToPathConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Path> getTargetClass() {
        return Path.class;
    }

    @Override
    public Path convert(Data source, Map<String, Object> params) throws UnconvertibleObjectException {

        if(source == null || source.getContent() == null) {
            throw new UnconvertibleObjectException("Mandatory parameter is missing.");
        }

        Path result = null;
        try {
        //Create a temp file
            final String fileName = UUID.randomUUID().toString();
            result = Files.createTempFile(fileName, ".tmp");

            final List<Object> data = source.getContent();
            if (data.size() < 1) {
                throw new UnconvertibleObjectException("There's no available data in this complex content.");
            }
            String rawData = (String) data.get(0);
            if (params != null && params.get(ENCODING) != null && params.get(ENCODING).equals(WPSEncoding.BASE64.getValue())) {

                final byte[] byteData = Base64.getDecoder().decode(rawData);
                if (byteData != null && byteData.length > 0) {
                    try (final ByteArrayInputStream is = new ByteArrayInputStream(byteData)) {
                        IOUtilities.writeStream(is, result);
                    }
                }

            } else {
                if(rawData.startsWith("<![CDATA[") && rawData.endsWith("]]>")) {
                    rawData = rawData.substring(9, rawData.length()-3);
                }
                IOUtilities.writeString(rawData, result);
            }
        } catch (Exception ex) {
            throw new UnconvertibleObjectException(ex);
        }
        return result;
    }
}
