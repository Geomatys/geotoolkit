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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;

/**
 * Implementation of ObjectConverter to convert a {@code Boolean} into a {@link OutputReferenceType reference}.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class BooleanToReferenceConverter extends AbstractReferenceOutputConverter<Boolean> {

    private static BooleanToReferenceConverter INSTANCE;

    private BooleanToReferenceConverter() {
    }

    public static synchronized BooleanToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BooleanToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Boolean> getSourceClass() {
        return Boolean.class;
    }

    @Override
    public ReferenceType convert(final Boolean source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        ReferenceType reference = null ;

        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));

        reference.setMimeType("text/plain");
        reference.setEncoding("UTF-8");
        reference.setSchema(null);

        final String randomFileName = UUID.randomUUID().toString();
        FileWriter writer = null;
        try {
            //create file
            final File literalFile = new File((String) params.get(TMP_DIR_PATH), randomFileName);
            writer = new FileWriter(literalFile);
            writer.write(String.valueOf(source));
            writer.flush();
            reference.setHref((String) params.get(TMP_DIR_URL) + "/" + randomFileName);

        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error occure during image writing.", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new UnconvertibleObjectException("Can't close the writer.", ex);
                }
            }
        }
        return reference;
    }

}
