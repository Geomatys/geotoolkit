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

import org.opengis.metadata.citation.OnlineResource;
import org.opengis.sld.RemoteOWS;
import org.opengis.sld.SLDVisitor;

import static org.geotoolkit.util.ArgumentChecks.*;


/**
 * Default immutable implementation of remoteOWS. thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultRemoteOWS implements RemoteOWS{

    private final String service;
    private final OnlineResource online;
    
    
    DefaultRemoteOWS(final String service, final OnlineResource online){
        ensureNonNull("service", service);
        ensureNonNull("online resource", online);
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
    public OnlineResource getOnlineResource() {
        return online;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final SLDVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {

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
        final StringBuilder builder = new StringBuilder();
        builder.append("[RemoteOWS : Service=");
        builder.append(service);
        builder.append(" OnlineResource=");
        builder.append(online);
        builder.append(']');
        return builder.toString();
    }
    
}
