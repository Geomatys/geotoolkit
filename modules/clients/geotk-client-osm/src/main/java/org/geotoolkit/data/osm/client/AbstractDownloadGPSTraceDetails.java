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
 * Abstract implementation of {@link DownloadGPSDetail}, which defines the
 * parameters for to download a GPS detail request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractDownloadGPSTraceDetails extends AbstractRequest implements DownloadGPSTraceDetail{

    protected int id = -1;

    public AbstractDownloadGPSTraceDetails(final String serverURL, final String subPath){
        super(serverURL, subPath);
    }

    @Override
    public int getTraceID() {
        return id;
    }

    @Override
    public void setTraceID(final int id) {
        this.id = id;
    }
    
}
