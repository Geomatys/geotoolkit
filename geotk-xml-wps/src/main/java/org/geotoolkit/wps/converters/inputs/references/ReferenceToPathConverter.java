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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Reference;;

/**
 * Implementation of ObjectConverter to convert a reference into a Path.
 *
 * @author Guilhem Legal (Geomatys).
 */
public final class ReferenceToPathConverter extends AbstractReferenceInputConverter<Path> {

    private static ReferenceToPathConverter INSTANCE;

    private ReferenceToPathConverter() {
    }

    public static synchronized ReferenceToPathConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToPathConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Path> getTargetClass() {
        return Path.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return Path.
     */
    @Override
    public Path convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        Path file;
        try (InputStream in = getInputStreamFromReference(source)) {

            final String fileName = UUID.randomUUID().toString();
            final String suffix = ".tmp";
            //Create a temp file
            file = Files.createTempFile(fileName, suffix);
            Files.copy(in, file);

        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("ReferenceType file invalid input : Malformed url", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("ReferenceType file invalid input : IO", ex);
        }
        return file;
    }
}
