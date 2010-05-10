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

import org.geotoolkit.client.Request;
import org.opengis.geometry.Envelope;

/**
 * Request to get GPS trace from the osm server.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GetGPSTraceRequest extends Request{

    /**
     * Envelope of the datas that will be retrieved from the server.
     */
    Envelope getEnvelope();

    /**
     * Envelope of the datas that will be retrieved from the server.
     * @param env : Envelope of asked datas
     */
    void setEnvelope(Envelope env);

    /**
     * Requested page number.
     * @return int >= 0
     */
    int getPage();

    /**
     * OSM only return 5000 points per page.
     * increment this number to retrieve the next page.
     * @param page : requested page number
     */
    void setPage(int page);
    
}
