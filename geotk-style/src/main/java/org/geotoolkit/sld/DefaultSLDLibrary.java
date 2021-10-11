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
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.SLDVisitor;
import org.opengis.sld.StyledLayerDescriptor;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Default immutable implementation of SLD library. thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultSLDLibrary implements SLDLibrary{

    private final OnlineResource online;
    private StyledLayerDescriptor sld = null;


    DefaultSLDLibrary(final OnlineResource online){
        ensureNonNull("online resource", online);
        this.online = online;
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
    public StyledLayerDescriptor getSLD() {
        //TODO parse the online resource
        return sld;
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

        DefaultSLDLibrary other = (DefaultSLDLibrary) obj;

        return this.online.equals(other.online);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 17*online.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[SLDLibrary : Online=");
        builder.append(online);
        builder.append(']');
        return builder.toString();
    }

}
