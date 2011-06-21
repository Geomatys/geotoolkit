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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.ignrm.parser.TokenParser;
import org.geotoolkit.ignrm.parser.TokenInformationParser;
import org.geotoolkit.security.ClientSecurity;

/**
 * IGN right management server
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IGNRMServer extends AbstractServer {
            
    public IGNRMServer(final URL serverURL){
        this(serverURL,null);
    }
    
    public IGNRMServer(final URL serverURL, final ClientSecurity security){
        super(serverURL, security);
    }
    
    public GetTokenRequest createGetToken(){
        return new GetTokenRequest(this);
    }
    
    public GetConfigRequest createGetConfig(){
        return new GetConfigRequest(this);
    }
    
    public ReleaseTokenRequest createReleaseToken(){
        return new ReleaseTokenRequest(this);
    }
    
    public Token getToken(final String key) throws IOException, XMLStreamException{
        final GetTokenRequest request = createGetToken();
        request.setKey(key);
        
        final InputStream stream = request.getResponseStream();
        final TokenParser parser = new TokenParser(this, key);
        try{            
            parser.setInput(stream);
            return parser.read();
        }finally{
            parser.dispose();
            stream.close();
        }
    }

    public TokenInformation getConfig(final Token token) throws IOException, XMLStreamException {
        final GetConfigRequest request = createGetConfig();
        request.setKey(token.getKey());
        
        final InputStream stream = request.getResponseStream();
        final TokenInformationParser parser = new TokenInformationParser();
        try{            
            parser.setInput(stream);
            return parser.read();
        }finally{
            parser.dispose();
            stream.close();
        }
    }
    
    public void releaseToken(final Token token) throws IOException{
        final ReleaseTokenRequest request = createReleaseToken();
        request.setToken(token);
        
        final InputStream stream = request.getResponseStream();
        try{            
        }finally{
            stream.close();
        }
    }
    
}
