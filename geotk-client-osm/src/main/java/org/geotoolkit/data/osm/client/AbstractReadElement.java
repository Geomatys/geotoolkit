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
 * Abstract implementation of {@link ReadElementRequest}, which defines the
 * parameters for a get element request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractReadElement extends AbstractRequest implements ReadElementRequest{

    protected OSMType type = null;
    protected long id = -1;
    protected int version = -1;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server url.
     */
    protected AbstractReadElement(final OpenStreetMapClient server, final String subpath){
        super(server, subpath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OSMType getElementType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setElementType(final OSMType clazz) {
        this.type = clazz;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }

}
