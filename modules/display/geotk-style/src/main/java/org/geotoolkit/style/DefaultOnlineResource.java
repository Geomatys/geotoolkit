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
import java.util.Objects;

import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.util.InternationalString;
import org.apache.sis.util.iso.SimpleInternationalString;

/**
 * Immutable implementation of Types Online Resource.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultOnlineResource implements OnlineResource{

    private final URI href;

    private final String protocol;

    private final String profil;

    private final InternationalString title;

    private final InternationalString desc;

    private final OnLineFunction function;


    /**
     * Create a default immutable OnlineResource.
     */
    public DefaultOnlineResource(final URI uri, final String protocol, final String profil, final String name,
                            final InternationalString desc, final OnLineFunction function){
        this.href = uri;
        this.desc = desc;
        this.function = function;
        this.title = (name != null) ? new SimpleInternationalString(name) : null;
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
    public String getProtocolRequest() {
        return null;
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
    public InternationalString getName() {
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
    public boolean equals(final Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultOnlineResource other = (DefaultOnlineResource) obj;

        return Objects.equals(this.desc, other.desc)
                && Objects.equals(this.function, other.function)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.profil, other.profil)
                && Objects.equals(this.protocol, other.protocol)
                && Objects.equals(this.href, other.href);

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
        final StringBuilder builder = new StringBuilder();
        builder.append("[OnlineResource : URI=");
        builder.append(href);
        builder.append(']');
        return builder.toString();
    }
}
