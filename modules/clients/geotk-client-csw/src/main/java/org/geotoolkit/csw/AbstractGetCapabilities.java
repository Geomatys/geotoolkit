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
import org.geotoolkit.client.AbstractRequest;


/**
 * Abstract get capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest {
    /**
     * The version of the CSW service.
     */
    protected final String version;

    protected AbstractGetCapabilities(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "CSW");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);
        return super.getURL();
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
