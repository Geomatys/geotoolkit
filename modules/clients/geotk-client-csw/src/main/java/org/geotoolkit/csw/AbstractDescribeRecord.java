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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.logging.Logging;


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

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
