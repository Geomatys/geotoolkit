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

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.Format;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.OutputDataType;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ZipAdaptor extends ComplexAdaptor<byte[]> {

    private static final String MIME_TYPE = "application/zip";

    private final String mimeType;

    public ZipAdaptor(String mimeType) {
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
    public Class<byte[]> getValueClass() {
        return byte[].class;
    }

    @Override
    public InputType toWPS1Input(byte[] candidate) throws UnconvertibleObjectException {
        return InputType.createComplex("", null, mimeType, null, candidate, null, null);
    }

    @Override
    public DataInputType toWPS2Input(byte[] candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] fromWPS1Input(OutputDataType candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] fromWPS2Input(DataOutputType candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (encoding!=null || schema!=null) return null;
            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;

            return new ZipAdaptor(mimeType);
        }

    }

}
