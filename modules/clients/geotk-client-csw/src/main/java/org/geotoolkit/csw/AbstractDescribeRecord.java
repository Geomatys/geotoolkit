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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.csw.xml.v202.DescribeRecordType;
import org.geotoolkit.ebrim.xml.EBRIMClassesContext;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link DescribeRecordRequest}, which defines the
 * parameters for a DescribeRecord request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractDescribeRecord extends AbstractRequest implements DescribeRecordRequest {
    /**
     * Default logger for all DescribeRecord requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractDescribeRecord.class);

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String outputFormat = null;
    private String namespace = null;
    private String schemaLanguage = null;
    private String typeName = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractDescribeRecord(final String serverURL, final String version){
        super(serverURL);
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
    public String getTypeName() {
        return typeName;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setSchemaLanguage(String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    @Override
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
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
        if (typeName != null) {
            requestParameters.put("TYPENAME", typeName);
        }

        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getSOAPResponse() throws IOException {
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        MarshallerPool pool = null;
        Marshaller marsh = null;
        try {
            pool = new MarshallerPool(EBRIMClassesContext.getAllClasses());
            marsh = pool.acquireMarshaller();
            final DescribeRecordType describeXml = new DescribeRecordType("CSW", version,
                    Collections.singletonList(new QName(typeName)), outputFormat, schemaLanguage);
            marsh.marshal(describeXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (pool != null && marsh != null) {
                pool.release(marsh);
            }
        }
        stream.close();
        return conec.getInputStream();
    }
}
