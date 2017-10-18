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
package org.geotoolkit.wms.v130;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.AbstractGetFeatureInfo;
import org.geotoolkit.wms.WebMapClient;


/**
 * Implementation for the GetFeatureInfo request version 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GetFeatureInfo130 extends AbstractGetFeatureInfo {

    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetFeatureInfo130(final String serverURL, final ClientSecurity security){
        super(serverURL,"1.3.0", security);
    }

    public GetFeatureInfo130(final WebMapClient server, final ClientSecurity security){
        super(server,"1.3.0", security);
    }

    /**
     * {@inheritDoc }
     */

    @Override
    protected String getCRSParameterName() {
        return "CRS";
    }

    @Override
    public URL getURL() throws MalformedURLException {
        if (columnIndex == null) {
            throw new IllegalArgumentException("I is not defined");
        }
        if (rawIndex == null) {
            throw new IllegalArgumentException("J is not defined");
        }
        requestParameters.put("I", String.valueOf(columnIndex));
        requestParameters.put("J", String.valueOf(rawIndex));
        return super.getURL();
    }
}
