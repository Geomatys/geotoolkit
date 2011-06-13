/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.v202.DescribeRecordType;
import org.geotoolkit.security.ClientSecurity;

/**
 * Abstract implementation of {@link DescribeRecordRequest}, which defines the
 * parameters for a DescribeRecord request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractDescribeRecord extends AbstractCSWRequest implements DescribeRecordRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String outputFormat = null;
    private String namespace = null;
    private String schemaLanguage = null;
    private QName[] typeNames = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractDescribeRecord(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security);
        this.version = version;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    @Override
    public String getSchemaLanguage() {
        return schemaLanguage;
    }

    @Override
    public QName[] getTypeNames() {
        return typeNames;
    }

    @Override
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setSchemaLanguage(final String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    @Override
    public void setTypeNames(final QName... typeName) {
        this.typeNames = typeName;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.put("SERVICE", "CSW");
        requestParameters.put("REQUEST", "GetRecords");
        requestParameters.put("VERSION", version);

        if (namespace != null) {
            requestParameters.put("NAMESPACE", namespace);
        }
        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT", outputFormat);
        }
        if (schemaLanguage != null) {
            requestParameters.put("SCHEMALANGUAGE", schemaLanguage);
        }
        if (typeNames != null) {
            final StringBuilder sTypeNames = new StringBuilder();
            for (int i=0; i<typeNames.length; i++) {
                final QName q = typeNames[i];
                sTypeNames.append(q.toString());
                if (i < typeNames.length - 1) {
                    sTypeNames.append(',');
                }
            }
            requestParameters.put("TYPENAME", sTypeNames.toString());
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final DescribeRecordType describeXml = new DescribeRecordType("CSW", version,
                    Arrays.asList(typeNames), outputFormat, schemaLanguage);
            marsh.marshal(describeXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (POOL != null && marsh != null) {
                POOL.release(marsh);
            }
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
}
