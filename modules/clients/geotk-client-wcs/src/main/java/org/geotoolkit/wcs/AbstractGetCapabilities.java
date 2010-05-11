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
package org.geotoolkit.wcs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.client.AbstractRequest;


/**
 * Abstract implementation of {@link GetCapabilitiesRequest}, which defines the
 * parameters for a GetCapabilities request.
 *
 * @author Olivier Terral (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{
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
    protected AbstractGetCapabilities(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE", "WCS");
        requestParameters.put("REQUEST", "GetCapabilities");
        requestParameters.put("VERSION", version);
        return super.getURL();
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
