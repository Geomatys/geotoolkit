/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractRequest implements Request{

    protected final String serverURL;
    protected final Map<String,String> requestParameters = new HashMap<String,String>();

    protected AbstractRequest(String serverURL){

        if(serverURL.contains("?")){
            this.serverURL = serverURL;
        }else{
            this.serverURL = serverURL + "?";
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        final StringBuilder sb = new StringBuilder();
        final List<String> keys = new ArrayList<String>(requestParameters.keySet());
        
        sb.append(serverURL);
        
        if(!requestParameters.isEmpty()){

            if(!(serverURL.endsWith("?") || serverURL.endsWith("&"))){
                sb.append("&");
            }

            String key = keys.get(0);
            sb.append(noSpaces(key)).append('=').append(noSpaces(requestParameters.get(key)));
            
            for(int i=1,n=keys.size();i<n;i++){
                key = keys.get(i);
                sb.append('&').append(noSpaces(key)).append('=').append(noSpaces(requestParameters.get(key)));
            }
        }

        return new URL(sb.toString());
    }

    private static String noSpaces(String str){
        return str.replaceAll(" ", "%20");
    }

}
