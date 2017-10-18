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
package org.geotoolkit.wms.v100;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.AbstractGetFeatureInfo;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.wms.xml.WMSVersion;


/**
 * Implementation for the GetFeatureInfo request version 1.1.1.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class GetFeatureInfo100 extends AbstractGetFeatureInfo {

    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetFeatureInfo100(final String serverURL, final ClientSecurity security){
        super(serverURL, WMSVersion.v100.getCode(), security);
    }

    public GetFeatureInfo100(final WebMapClient server, final ClientSecurity security){
        super(server,WMSVersion.v100.getCode(), security);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected String getCRSParameterName() {
        return "SRS";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (columnIndex == null) {
            throw new IllegalArgumentException("X is not defined");
        }
        if (rawIndex == null) {
            throw new IllegalArgumentException("Y is not defined");
        }
        requestParameters.put("X", String.valueOf(columnIndex));
        requestParameters.put("Y", String.valueOf(rawIndex));
        return super.getURL();
    }

}
