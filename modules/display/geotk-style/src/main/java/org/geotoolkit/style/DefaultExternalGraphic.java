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

import java.util.Collection;
import java.util.Collections;
import javax.swing.Icon;

import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.Utilities;

import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of GeoAPI External graphic.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultExternalGraphic implements ExternalGraphic{

    private final OnlineResource resource;
    
    private final Icon icon;
    
    private final String format;
    
    private final Collection<ColorReplacement> replaces;
    
    /**
     * Create a default immutable external graphic.
     * 
     * @param resource : can not be null
     * @param format : can not be null
     * @param replaces : can be null or empty
     */
    public DefaultExternalGraphic(OnlineResource resource, String format, Collection<ColorReplacement> replaces){
        if( resource == null || format == null ){
            throw new IllegalArgumentException("OnlineResource and format can not be null");
        }
        
        this.resource = resource;
        this.icon = null;
        this.format = format;
        
        if(replaces != null && !replaces.isEmpty()) {
            final ColorReplacement[] rep = replaces.toArray(new ColorReplacement[replaces.size()]);
            this.replaces = UnmodifiableArrayList.wrap(rep);
        }else{
            this.replaces = Collections.emptyList();
        }
        
    }
    
    /**
     * Create a default immutable external graphic.
     * 
     * @param icon : can not be null
     * @param format : can not be null
     * @param replaces : can be null
     */
    DefaultExternalGraphic(Icon icon, Collection<ColorReplacement> replaces){
        if( icon == null ){
            throw new IllegalArgumentException("Icon can not be null");
        }
        
        this.resource = null;
        this.icon = icon;
        this.format = null;
        
        if(replaces != null && !replaces.isEmpty()) {
            final ColorReplacement[] rep = replaces.toArray(new ColorReplacement[replaces.size()]);
            this.replaces = UnmodifiableArrayList.wrap(rep);
        }else{
            this.replaces = Collections.emptyList();
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public OnlineResource getOnlineResource() {
        return resource;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Icon getInlineContent() {
        return icon;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<ColorReplacement> getColorReplacements() {
        return replaces;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
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

        DefaultExternalGraphic other = (DefaultExternalGraphic) obj;

        return Utilities.equals(this.resource, other.resource)
                && Utilities.equals(this.icon, other.icon)
                && Utilities.equals(this.format, other.format)
                && Utilities.equals(this.replaces, other.replaces);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = replaces.hashCode();
        if(format != null) hash += format.hashCode();
        if(resource != null) hash += resource.hashCode();
        if(icon != null) hash += icon.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[ExternalGraphic : Type=");
        builder.append( (resource == null) ? "Inline Image" : "Online Image" );
        builder.append(" Format=");
        builder.append((format != null) ?format : "");
        builder.append(']');
        return builder.toString();
    }
    
}
