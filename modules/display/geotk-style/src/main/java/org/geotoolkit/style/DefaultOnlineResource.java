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
package org.geotoolkit.style;

import java.net.URI;

import org.geotoolkit.util.Utilities;

import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.util.InternationalString;

/**
 * Immutable implementation of GeoAPI Online Resource.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultOnlineResource implements OnLineResource{
    
    private final URI href;
    
    private final String protocol;
    
    private final String profil;
    
    private final String title;
    
    private final InternationalString desc;
    
    private final OnLineFunction function;
        
    
    /**
     * Create a default immutable OnlineResource.
     * 
     * @param uri
     */
    public DefaultOnlineResource(URI uri, String protocol, String profil, String name,
                            InternationalString desc, OnLineFunction function){
        this.href = uri;
        this.desc = desc;
        this.function = function;
        this.title = name;
        this.profil = profil;
        this.protocol = protocol;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public URI getLinkage() {
        return href;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getProtocol() {
        return protocol;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getApplicationProfile() {
        return profil;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return title;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getDescription() {
       return desc;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public OnLineFunction getFunction() {
        return function;
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

        DefaultOnlineResource other = (DefaultOnlineResource) obj;

        return Utilities.equals(this.desc, other.desc)
                && Utilities.equals(this.function, other.function)
                && Utilities.equals(this.title, other.title)
                && Utilities.equals(this.profil, other.profil)
                && Utilities.equals(this.protocol, other.protocol)
                && Utilities.equals(this.href, other.href);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(desc != null) hash *= desc.hashCode();
        if(function != null) hash *= function.hashCode();
        if(title != null) hash *= title.hashCode();
        if(profil != null) hash *= profil.hashCode();
        if(protocol != null) hash *= protocol.hashCode();
        if(href != null) hash *= href.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[OnlineResource : URI=");
        builder.append(href);
        builder.append(']');
        return builder.toString();
    }
}
