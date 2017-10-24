/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.v100;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.AbstractGetMap;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class GetMap100 extends AbstractGetMap {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetMap100(final String serverURL, final ClientSecurity security){
        super(serverURL,WMSVersion.v100.getCode(), security);
    }

    public GetMap100(final WebMapClient server, final ClientSecurity security){
        super(server,WMSVersion.v100.getCode(), security);
    }

    @Override
    protected String getCRSParameterName() {
        return "SRS";
    }
}
