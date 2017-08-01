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
import java.nio.file.Files;
import java.nio.file.Path;
import net.iharder.Base64;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.Format;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInputType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSVAdaptor extends ComplexAdaptor<Path> {

    private static final String MIME_TYPE = "text/csv";

    private final String mimeType;

    public CSVAdaptor(String mimeType) {
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
    public InputType toWPS1Input(Path candidate) throws UnconvertibleObjectException {
        if(candidate instanceof ReferenceProxy) return super.toWPS1Input(candidate);

        return InputType.createComplex("", null, mimeType, null, candidate, null, null);
    }

    @Override
    public DataInputType toWPS2Input(Path candidate) throws UnconvertibleObjectException {
        if(candidate instanceof ReferenceProxy) return super.toWPS2Input(candidate);

        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(candidate);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(),ex);
        }
        final String base64 = Base64.encodeBytes(bytes);
        final DataInputType dit = new DataInputType();
        final Data data = new Data();
        data.setEncoding("base64");
        data.getContent().add(base64);
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

            return new CSVAdaptor(mimeType);
        }

    }

}
