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
 * Abstract implementation of {@link DownloadChangeSetRequest}, which defines the
 * parameters for a download change set request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractDownloadChangeSet extends AbstractRequest implements DownloadChangeSetRequest{

    protected int id = -1;

    public AbstractDownloadChangeSet(final String serverURL, final String subPath){
        super(serverURL, subPath);
    }

    @Override
    public int getChangeSetID() {
        return id;
    }

    @Override
    public void setChangeSetID(final int id) {
        this.id = id;
    }
    
}
