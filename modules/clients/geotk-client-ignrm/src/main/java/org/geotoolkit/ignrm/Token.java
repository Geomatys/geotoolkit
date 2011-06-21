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
import javax.xml.stream.XMLStreamException;

/**
 * Token represent an authentification for IGN server.
 * The token key and value must be added on each request send to the server.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class Token {
    
    private static int TIME_MARGIN = 30; //seconds
    
    private final IGNRMServer server;
    private final String key;
    private final String name;
    String value;
    
    private TokenInformation information;
    private long lastUpdate;

    public Token(IGNRMServer server, String key, String name, String value) {
        this.server = server;
        this.key = key;
        this.name = name;
        this.value = value;
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * @return the key used to authentify
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @return the name of the token for the url parameters.
     */
    public String getName() {
        return name;
    }

    /**
     * The token value is automaticly updated when needed.
     * Always call getValue() at the last moment possible.
     * After this method is called the value is valid for at least 30seconds.
     * 
     * @return the value of the token
     * @throws IOException
     * @throws XMLStreamException 
     */
    public String getValue() throws IOException, XMLStreamException {
        //check if the token value is obsolete, if so get a new one.
        final TokenInformation info = getInformation();
        final long currentTime = System.currentTimeMillis();
        if( ((currentTime-lastUpdate)/1000d) > (info.getTokenTimeOut()-TIME_MARGIN) ){
            //token has expired, get a new value
            release();
            refresh();
        }
        
        return value;
    }

    /**
     * @return The IGN RM server
     */
    public IGNRMServer getServer() {
        return server;
    }

    /**
     * 
     * @return TokenInformation
     * @throws IOException
     * @throws XMLStreamException 
     */
    public TokenInformation getInformation() throws IOException, XMLStreamException {
        if(information == null){
            information = server.getConfig(this);
        }
        
        return information;
    }
        
    /**
     * Force refreshing the token value.
     * 
     * @throws IOException
     * @throws XMLStreamException 
     */
    public void refresh() throws IOException, XMLStreamException{
        final Token newVal = server.getToken(key);
        lastUpdate = newVal.lastUpdate;
        value = newVal.value;
    }
    
    /**
     * Send a query to the server to release this token.
     * 
     * @throws IOException 
     */
    public void release() throws IOException{
        server.releaseToken(this);
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    @Override
    public String toString() {
        try {
            return getValue();
        } catch (IOException ex) {
            return super.toString();
        } catch (XMLStreamException ex) {
            return super.toString();
        }
    }
    
}
