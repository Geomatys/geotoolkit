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
 * Abstract implementation of {@link TransactionRequest}, which defines the
 * parameters for a transaction request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class AbstractTransaction extends AbstractRequest implements TransactionRequest {
    /**
     * Default logger for all GetRecords requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetRecords.class);

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractTransaction(final String serverURL, final String version){
        super(serverURL);
        this.version = version;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException("Transaction requests do not support KVP encoding.");
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        // TODO: implement me
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
