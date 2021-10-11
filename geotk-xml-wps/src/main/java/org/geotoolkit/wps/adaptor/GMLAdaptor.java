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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.Reference;
import org.w3c.dom.Document;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLAdaptor extends ComplexAdaptor {

    private static final String[] FEATURE_SCHEMAS = new String[]{
        "http://schemas.opengis.net/gml/3.2.1/feature.xsd",
        "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd",
        "http://schemas.opengis.net/gml/3.1.0/base/feature.xsd"
        };

    private static final String ENC_UTF8 = "UTF-8";
    private static final String ENC_BASE64 = "base64";

    private static final String MIME_TYPE = "text/xml";

    private final String mimeType;
    private final String encoding;
    private final String schema;
    private final String gmlVersion;

    public GMLAdaptor(String mimeType, String encoding, String schema) {
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.schema = schema;

        if (FEATURE_SCHEMAS[0].equalsIgnoreCase(schema)) {
            gmlVersion = "3.2.1";
        } else if (FEATURE_SCHEMAS[1].equalsIgnoreCase(schema)) {
            gmlVersion = "3.1.1";
        } else if (FEATURE_SCHEMAS[2].equalsIgnoreCase(schema)) {
            gmlVersion = "3.1.0";
        } else {
            throw new IllegalArgumentException("Unknown schema version "+schema);
        }
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
        return FeatureSet.class;
    }

    @Override
    public DataInput toWPS2Input(Object candidate) throws UnconvertibleObjectException {
        if(candidate instanceof ReferenceProxy) return super.toWPS2Input(candidate);

        final ComplexData cdt = new ComplexData();
        cdt.getContent().add(new org.geotoolkit.wps.xml.v200.Format(encoding, mimeType, schema, null));


        final JAXPStreamFeatureWriter writer = new JAXPStreamFeatureWriter(gmlVersion, null, null);
        try {
            if (ENC_BASE64.equalsIgnoreCase(encoding)) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                writer.write(candidate, out);
                out.flush();
                String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
                cdt.getContent().add(base64);
            } else if (ENC_UTF8.equalsIgnoreCase(encoding)) {
                final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
                final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
                final Document document = constructeur.newDocument();
                final DOMResult domResult = new DOMResult(document);
                writer.write(candidate, domResult);
                cdt.getContent().add(document.getDocumentElement());
            }
        } catch (IOException | XMLStreamException | DataStoreException | ParserConfigurationException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }

        final Data data = new Data();
        data.getContent().add(cdt);

        final DataInput dit = new DataInput();
        dit.setData(data);
        return dit;
    }

    @Override
    public Object fromWPS2Input(DataOutput candidate) throws UnconvertibleObjectException {
        final Reference ref = candidate.getReference();
        if (ref != null) {
            return WPSConvertersUtils.convertFromReference(ref, FeatureSet.class);
        }
        final Data data = candidate.getData();
        if (data != null) {
            return WPSConvertersUtils.convertFromComplex(gmlVersion, data, FeatureSet.class);
        }
        throw new UnconvertibleObjectException("Niether reference or data is filled.");
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();
            final String schema = format.getSchema();

            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;
            if (encoding != null && !(ENC_UTF8.equalsIgnoreCase(encoding) || ENC_BASE64.equalsIgnoreCase(encoding))) return null;
            if (!ArraysExt.contains(FEATURE_SCHEMAS, schema)) return null;

            return new GMLAdaptor(mimeType, encoding, schema);
        }

    }

}
