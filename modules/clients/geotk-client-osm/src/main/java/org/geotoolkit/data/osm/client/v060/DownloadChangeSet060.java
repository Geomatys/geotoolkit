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

import org.geotoolkit.data.osm.client.AbstractDownloadChangeSet;
import org.geotoolkit.data.osm.client.OpenStreetMapServer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DownloadChangeSet060 extends AbstractDownloadChangeSet{

    public DownloadChangeSet060(final OpenStreetMapServer server){
        super(server,"");
    }

    @Override
    protected String getSubPath() {
        return new StringBuilder("/api/0.6/changeset/").append(id).append("/download").toString();
    }

}
