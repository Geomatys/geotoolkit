/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wps.adaptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLAdaptor extends ComplexAdaptor<Path> {

    private static final String MIME_TYPE = "application/vnd.google-earth.kml+xml";

    private final String mimeType;

    public KMLAdaptor(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public Class<Path> getValueClass() {
        return Path.class;
    }

    @Override
    public DataInput toWPS2Input(Path candidate) throws UnconvertibleObjectException {
        if(candidate instanceof ReferenceProxy) return super.toWPS2Input(candidate);


        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(candidate);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(),ex);
        }
        final String cdata = new String(bytes, Charset.forName("UTF-8"));
        final DataInput dit = new DataInput();
        final Data data = new Data();
        data.getContent().add("<![CDATA["+cdata+"]]>");
        dit.setData(data);
        return dit;
    }


    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (encoding!=null || schema!=null) return null;
            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;

            return new KMLAdaptor(mimeType);
        }

    }

}
