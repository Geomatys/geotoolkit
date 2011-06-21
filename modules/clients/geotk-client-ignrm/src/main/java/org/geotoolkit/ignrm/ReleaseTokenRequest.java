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
 * Request to release a token.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ReleaseTokenRequest extends AbstractRequest{
    
    private static final String SUBPATH = "releaseToken";  
    
    private Token token;
    
    public ReleaseTokenRequest(final IGNRMServer server){
        super(server,SUBPATH);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        //access the value directly
        requestParameters.put(token.getName(), token.value);
    }
    
}
