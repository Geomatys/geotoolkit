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
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.util.logging.Logging;


/**
 * Abstract implementation of {@link GetRecordByIdRequest}, which defines the
 * parameters for a GetRecordById request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetRecordById extends AbstractRequest implements GetRecordByIdRequest {
    /**
     * Default logger for all GetRecordById requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetRecordById.class);

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private ElementSetType elementSetName = null;
    private String id = null;
    private String outputFormat = null;
    private String outputSchema = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetRecordById(final String serverURL, final String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public ElementSetType getElementSetName() {
        return elementSetName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    @Override
    public void setElementSetName(ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (id == null) {
            throw new IllegalArgumentException("The parameter \"ID\" is not defined");
        }

        requestParameters.put("SERVICE", "CSW");
        requestParameters.put("REQUEST", "GetRecords");
        requestParameters.put("VERSION", version);
        requestParameters.put("ID",      id);

        if (elementSetName != null) {
            requestParameters.put("ELEMENTSETNAME", elementSetName.value());
        }
        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT", outputFormat);
        }
        if (outputSchema != null) {
            requestParameters.put("OUTPUTSCHEMA", outputSchema);
        }

        return super.getURL();
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
