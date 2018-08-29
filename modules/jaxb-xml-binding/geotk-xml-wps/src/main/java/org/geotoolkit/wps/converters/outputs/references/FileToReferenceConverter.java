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

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.xml.v200.Reference;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

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
    public Reference convert(File source, Map<String, Object> params) throws UnconvertibleObjectException {

        if (params.get(TMP_DIR_PATH) == null) {
            throw new UnconvertibleObjectException("The output directory should be defined.");
        }

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }

        Reference reference = new Reference();

        reference.setMimeType((String) params.get(MIME));
        reference.setEncoding((String) params.get(ENCODING));
        reference.setSchema((String) params.get(SCHEMA));
        final Path targetDirectory = buildPath(params, null);
        Object tmpDirValue = params.get(TMP_DIR_PATH);
        String tmpDir;
        if (tmpDirValue instanceof URI) {
            tmpDir = ((URI) params.get(TMP_DIR_PATH)).toString();
        } else if (tmpDirValue instanceof String) {
            tmpDir = (String) params.get(TMP_DIR_PATH);
        } else {
            throw new UnconvertibleObjectException("Unexpected type for " + TMP_DIR_PATH + " parameter.");
        }
        try {
            final Path target = targetDirectory.resolve(source.getName());
            if(source.getAbsolutePath().startsWith(tmpDir)) {
                reference.setHref(source.getAbsolutePath().replace(tmpDir, (String) params.get(TMP_DIR_URL)));
            } else {
                IOUtilities.copy(source.toPath(),target);
                reference.setHref((String) params.get(TMP_DIR_URL) + "/" +source.getName());
            }
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error during moving file to output directory.", ex);
        }

        return reference;
    }
}
