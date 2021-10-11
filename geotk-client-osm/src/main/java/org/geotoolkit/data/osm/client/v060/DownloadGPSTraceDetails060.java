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

package org.geotoolkit.data.osm.client.v060;

import org.geotoolkit.data.osm.client.AbstractDownloadGPSTraceDetails;
import org.geotoolkit.data.osm.client.OpenStreetMapClient;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DownloadGPSTraceDetails060 extends AbstractDownloadGPSTraceDetails{

    public DownloadGPSTraceDetails060(final OpenStreetMapClient server){
        super(server,"");
    }

    @Override
    protected String getSubPath() {
        return new StringBuilder("/api/0.6/gpx/").append(id).append("/details").toString();
    }

}
