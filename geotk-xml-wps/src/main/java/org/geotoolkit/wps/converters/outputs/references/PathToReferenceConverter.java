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
package org.geotoolkit.wps.converters.outputs.references;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Reference;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 * @author Guilhem Legal (Geomatys).
 */
public class PathToReferenceConverter extends AbstractReferenceOutputConverter<Path> {

    private static PathToReferenceConverter INSTANCE;

    private PathToReferenceConverter(){
    }

    public static synchronized PathToReferenceConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PathToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Path> getSourceClass() {
        return Path.class;
    }

    @Override
    public Reference convert(Path source, Map<String, Object> params) throws UnconvertibleObjectException {

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
        final String tmpDir = getTemproraryDirectoryPath(params);
        final String tmpDirUrl = (String) params.get(TMP_DIR_URL);
        try {
            // if the source is already in the temp folder
            String absPath = source.toAbsolutePath().toString();
            if (absPath.startsWith(tmpDir)) {
                reference.setHref(absPath.replace(tmpDir, tmpDirUrl));

            // else we create a link from the source file in temp dir
            } else {
                final Path target = targetDirectory.resolve(source);
                Files.createSymbolicLink(target, source);
                //IOUtilities.copy(source.toPath(),target);
                String suffix = getRelativeLocation(target, tmpDir);
                reference.setHref(tmpDirUrl + "/" + suffix);
            }
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Error during moving file to output directory.", ex);
        }

        return reference;
    }
}
