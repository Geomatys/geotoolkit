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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.geotoolkit.client.AbstractRequest;

/**
 * Abstract implementation of {@link GetDataRequest}, which defines the
 * parameters for a get data request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetChangeSet extends AbstractRequest implements GetChangeSetRequest{

    protected long id = -1;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     */
    protected AbstractGetChangeSet(final String serverURL, final String subpath){
        super(serverURL, subpath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setChangeSetID(final long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getChangeSetID() {
        return id;
    }

}
