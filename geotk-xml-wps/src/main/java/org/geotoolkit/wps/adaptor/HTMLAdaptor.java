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
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HTMLAdaptor extends ComplexAdaptor<String> {

    private static final String ENC_UTF8 = "UTF-8";
    private static final String MIME_TYPE = "text/html";

    private final String mimeType;
    private final String encoding;
    private final String schema;

    public HTMLAdaptor(String mimeType, String encoding, String schema) {
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
    public DataInput toWPS2Input(String candidate) throws UnconvertibleObjectException {

        final ComplexData cdt = new ComplexData();
        cdt.getContent().add(new org.geotoolkit.wps.xml.v200.Format(encoding, mimeType, schema, null));
        cdt.getContent().add(candidate);

        final Data data = new Data();
        data.getContent().add(cdt);

        final DataInput dit = new DataInput();
        dit.setData(data);
        return dit;
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;
            if (encoding != null && !ENC_UTF8.equalsIgnoreCase(encoding)) return null;

            return new HTMLAdaptor(mimeType, encoding, schema);
        }

    }

}
