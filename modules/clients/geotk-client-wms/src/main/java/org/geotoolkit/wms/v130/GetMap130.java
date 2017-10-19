/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wms.v130;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.AbstractGetMap;
import org.geotoolkit.wms.WebMapClient;



/**
 * Implementation for the GetMap request version 1.3.0.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GetMap130 extends AbstractGetMap {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetMap130(final String serverURL, final ClientSecurity security){
        super(serverURL,"1.3.0", security);
    }

    public GetMap130(final WebMapClient server, final ClientSecurity security){
        super(server,"1.3.0", security);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected String getCRSParameterName() {
        return "CRS";
    }
}
