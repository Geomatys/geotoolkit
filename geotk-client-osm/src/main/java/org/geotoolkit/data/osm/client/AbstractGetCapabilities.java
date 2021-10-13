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
package org.geotoolkit.data.osm.client;

import org.geotoolkit.client.AbstractRequest;


/**
 * Abstract implementation of {@link GetCapabilitiesRequest}, which defines the
 * parameters for a capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server url.
     */
    protected AbstractGetCapabilities(final OpenStreetMapClient server){
        super(server, "api/capabilities");
    }

    @Override
    protected void prepareParameters() {
    }

}
