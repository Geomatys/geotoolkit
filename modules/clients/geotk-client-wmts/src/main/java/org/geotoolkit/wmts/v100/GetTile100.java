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
package org.geotoolkit.wmts.v100;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wmts.AbstractGetTile;


/**
 * Implementation for the GetMap request version 1.0.0.
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class GetTile100 extends AbstractGetTile {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public GetTile100(final String serverURL, final ClientSecurity security, Integer timeout){
        super(serverURL,"1.0.0", security, timeout);
    }

    @Override
    public URL getURL() throws MalformedURLException {
        if(resourceUrl==null){
            return super.getURL();
        }else{
            prepareParameters();
            //use template url
            String url = resourceUrl;
            //replace all parameters
            for (Map.Entry<String,String> entry : requestParameters.entrySet()) {
                url = url.replaceAll("(?i)"+Pattern.quote("{"+entry.getKey()+"}"), entry.getValue());
            }
            return new URL(url);
        }
    }

}
