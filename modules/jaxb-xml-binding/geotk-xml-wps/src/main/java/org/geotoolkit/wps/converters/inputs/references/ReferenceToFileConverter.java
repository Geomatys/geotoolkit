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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.Reference;

/**
 * Implementation of ObjectConverter to convert a reference into a File.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToFileConverter extends AbstractReferenceInputConverter<File> {

    private static ReferenceToFileConverter INSTANCE;

    private ReferenceToFileConverter() {
    }

    public static synchronized ReferenceToFileConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToFileConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<File> getTargetClass() {
        return File.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return File.
     */
    @Override
    public File convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        File file;
        InputStream in = null;
        FileOutputStream out = null;
        try {

            in = getInputStreamFromReference(source);

            final String fileName = UUID.randomUUID().toString();
            final String suffix = ".tmp";
            //Create a temp file
            file = File.createTempFile(fileName, suffix);
            out = new FileOutputStream(file);

            //copy
            byte buf[]=new byte[1024];
            int len;
            while((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }

            out.flush();

        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("Reference file invalid input : Malformed url", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException("Reference file invalid input : IO", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                throw new UnconvertibleObjectException("Reference file invalid input : IO", ex);
            }
        }
        return file;
    }
}