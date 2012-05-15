/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wps.converters.inputs.references;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.converters.inputs.AbstractInputConverter;

/**
 * Implementation of ObjectConverter to convert a reference into a File.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToFileConverter extends AbstractInputConverter {

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
    public Class<? extends Object> getTargetClass() {
        return File.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return File.
     */
    @Override
    public File convert(final Map<String, Object> source) throws NonconvertibleObjectException {

        File file;
        InputStream in = null;
        FileOutputStream out = null;
        try {

            in = (InputStream) source.get(IN_STREAM);
            
            final String fileName = UUID.randomUUID().toString();
            final String suffix = "tmp";
            //Create a temp file
            file = File.createTempFile(fileName, suffix); //TODO create file in WPS temp directory
            out = new FileOutputStream(file);

            //copy
            byte buf[]=new byte[1024];
            int len;
            while((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }
           
            out.flush();

        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException("Reference file invalid input : Malformed url", ex);
        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Reference file invalid input : IO", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                throw new NonconvertibleObjectException("Reference file invalid input : IO", ex);
            }
        }
        return file;
    }
}