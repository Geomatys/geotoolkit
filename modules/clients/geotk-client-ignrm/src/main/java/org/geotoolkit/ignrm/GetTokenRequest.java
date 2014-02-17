/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ignrm;

import org.geotoolkit.client.AbstractRequest;

/**
 * Request to grab a token.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GetTokenRequest extends AbstractRequest{
    
    private static final String SUBPATH = "getToken";    
    private static final String PARAM_KEY = "key";
    private static final String PARAM_OUTPUT = "output";
    
    private String key;
    
    public GetTokenRequest(final IGNRMServer server){
        super(server,SUBPATH);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.put(PARAM_KEY, key);
        requestParameters.put(PARAM_OUTPUT, "xml");
    }
    
}
