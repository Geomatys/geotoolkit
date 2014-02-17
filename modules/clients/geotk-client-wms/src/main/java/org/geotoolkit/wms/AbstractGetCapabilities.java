/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;


/**
 * Abstract implementation of {@link GetCapabilitiesRequest}, which defines the
 * parameters for a GetCapabilities request.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{
    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    /**
     * The update sequence string, defining a unique id for the GetCapabilities response.
     */
    protected String updateSequence;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetCapabilities(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUpdateSequence(final String sequence) {
        this.updateSequence = sequence;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);
        if (updateSequence != null && !updateSequence.isEmpty()) {
            requestParameters.put("UPDATESEQUENCE", updateSequence);
        }
        return super.getURL();
    }
}
