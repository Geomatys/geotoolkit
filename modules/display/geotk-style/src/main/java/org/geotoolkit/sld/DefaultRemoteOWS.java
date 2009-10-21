/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld;

import org.opengis.metadata.citation.OnLineResource;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.SLDVisitor;


/**
 * Default immutable implementation of remoteOWS. thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultRemoteOWS implements RemoteOWS{

    private final String service;
    private final OnLineResource online;
    
    
    DefaultRemoteOWS(String service, OnLineResource online){
        if(service == null || online == null){
            throw new NullPointerException("Service and online resource can not be null.");
        }
        this.service = service;
        this.online = online;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getService() {
        return service;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OnLineResource getOnlineResource() {
        return online;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(SLDVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultRemoteOWS other = (DefaultRemoteOWS) obj;

        return this.service.equals(other.service)
                && this.online.equals(other.online);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return service.hashCode() + 17*online.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[RemoteOWS : Service=");
        builder.append(service.toString());
        builder.append(" OnlineResource=");
        builder.append(online.toString());
        builder.append(']');
        return builder.toString();
    }
    
}
