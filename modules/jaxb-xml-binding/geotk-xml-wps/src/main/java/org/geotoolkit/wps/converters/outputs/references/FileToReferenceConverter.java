/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.FileUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;
import org.geotoolkit.wps.xml.v100.ReferenceType;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class FileToReferenceConverter extends AbstractReferenceOutputConverter<File> {

    private static FileToReferenceConverter INSTANCE;

    private FileToReferenceConverter(){
    }

    public static synchronized FileToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new FileToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<File> getSourceClass() {
        return File.class;
    }

    @Override
    public ReferenceType convert(File source, Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        final WPSIO.IOType ioType = WPSIO.IOType.valueOf((String) params.get(IOTYPE));
        ReferenceType reference;

        if (ioType.equals(WPSIO.IOType.INPUT)) {
            reference = new InputReferenceType();
        } else {
            reference = new OutputReferenceType();
        }

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));
        final File targetDirectory = new File((String) params.get(TMP_DIR_PATH));
        try {
            final File target = new File(targetDirectory, source.getName());
            if(source.getAbsolutePath().startsWith((String) params.get(TMP_DIR_PATH))) {
                reference.setHref(source.getAbsolutePath().replace((String) params.get(TMP_DIR_PATH), (String) params.get(TMP_DIR_URL)));
            } else {
                FileUtilities.copy(source, new File(targetDirectory, source.getName()));
                reference.setHref((String) params.get(TMP_DIR_URL) + "/" +source.getName());
            }
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error during moving file to output directory.", ex);
        }

        return reference;
    }
}
