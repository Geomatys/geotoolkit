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
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;

/**
 * Implementation of ObjectConverter to convert a {@code String}, {@code Number}, {@code Boolean} into a {@link OutputReferenceType reference}.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class LiteralsToReferenceConverter extends AbstractReferenceOutputConverter {

    private static LiteralsToReferenceConverter INSTANCE;

    private LiteralsToReferenceConverter() {
    }

    public static synchronized LiteralsToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiteralsToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public OutputReferenceType convert(Map<String, Object> source) throws NonconvertibleObjectException {
        
        if (source.get(OUT_TMP_DIR_PATH) == null) {
            throw new NonconvertibleObjectException("The output directory should be defined.");
        }
        
        final Object data = source.get(OUT_DATA);
        
        if (data == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        
        final OutputReferenceType reference = new OutputReferenceType();

        reference.setMimeType("text/plain");
        reference.setEncoding("UTF-8");
        reference.setSchema(null);
        
        final String randomFileName = UUID.randomUUID().toString();
        FileWriter writer = null;
        try {
            //create file
            final File literalFile = new File((String) source.get(OUT_TMP_DIR_PATH), randomFileName);
            writer = new FileWriter(literalFile);
            writer.write(String.valueOf(data));
            writer.flush();
            reference.setHref((String) source.get(OUT_TMP_DIR_URL) + "/" + randomFileName);
            
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Error occure during image writing.", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new NonconvertibleObjectException("Can't close the writer.", ex);
                }
            }
        }
        return reference;
    }
}
