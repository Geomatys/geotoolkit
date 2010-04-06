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
import java.util.Collections;
import java.util.List;

import org.geotoolkit.client.AbstractRequest;

/**
 * Abstract implementation of {@link ReadElementsRequest}, which defines the
 * parameters for a get elements request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractReadElements extends AbstractRequest implements ReadElementsRequest{

    protected Class type = null;
    protected List<Long> ids = Collections.EMPTY_LIST;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     */
    protected AbstractReadElements(final String serverURL, String subpath){
        super(serverURL, subpath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<?> getElementType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setElementType(Class<?> clazz) {
        this.type = clazz;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Long> getIds() {
        return ids;
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
