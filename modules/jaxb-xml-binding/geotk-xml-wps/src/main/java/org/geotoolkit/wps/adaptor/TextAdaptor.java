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
import org.geotoolkit.wps.xml.v200.ComplexDataType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TextAdaptor extends ComplexAdaptor<String> {

    private static final String ENC_UTF8 = "UTF-8";
    private static final String MIME_TYPE = "text/plain";

    private final String mimeType;
    private final String encoding;
    private final String schema;

    public TextAdaptor(String mimeType, String encoding, String schema) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.schema = schema;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Class getValueClass() {
        return String.class;
    }

    @Override
    public InputType toWPS1Input(String candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataInputType toWPS2Input(String candidate) throws UnconvertibleObjectException {

        final ComplexDataType cdt = new ComplexDataType();
        cdt.getContent().add(new org.geotoolkit.wps.xml.v200.Format(encoding, mimeType, schema, null));
        cdt.getContent().add(candidate);

        final Data data = new Data();
        data.getContent().add(cdt);

        final DataInputType dit = new DataInputType();
        dit.setData(data);
        return dit;
    }

    @Override
    public String fromWPS1Input(OutputDataType candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String fromWPS2Input(DataOutputType candidate) throws UnconvertibleObjectException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;
            if (!ENC_UTF8.equalsIgnoreCase(encoding)) return null;

            return new TextAdaptor(mimeType, encoding, schema);
        }

    }

}
