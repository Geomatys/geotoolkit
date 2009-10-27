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

import javax.swing.Icon;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ExternalMark;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of GeoAPI External mark.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultExternalMark implements ExternalMark{

    private final OnlineResource online;
    
    private final Icon icon;
    
    private final String format;
    
    private final int index;
    
    /**
     * Create a default immutable external mark.
     * 
     * @param online : only one between online and icon can be defined
     * @param format : can not be null
     * @param index : can be null
     */
    public DefaultExternalMark(OnlineResource online, String format, int index){
        if( online == null || format == null ){
            throw new IllegalArgumentException("Online resource and format can not be null");
        }
        
        this.online = online;
        this.icon = null;
        this.format = format;
        this.index = index;
    }
    
    
    /**
     * Create a default immutable external mark.
     * 
     * @param icon : can not be null
     * @param format : can not be null
     * @param index : can be null
     */
    DefaultExternalMark(Icon icon){
        if( icon == null){
            throw new IllegalArgumentException("Icon can not be null");
        }
        
        this.online = null;
        this.icon = icon;
        this.format = null;
        this.index = 0;
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
    public int getMarkIndex() {
        return index;
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

        DefaultExternalMark other = (DefaultExternalMark) obj;

        return Utilities.equals(this.online, other.online)
                && Utilities.equals(this.icon, other.icon)
                && Utilities.equals(this.format, other.format)
                && Utilities.equals(this.index, other.index);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = index;
        if(format != null) hash += format.hashCode();
        if(online != null) hash += online.hashCode();
        if(icon != null) hash += icon.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ExternalMark : Type=");
        builder.append( (online == null) ? "Inline Mark" : "Online Mark" );
        builder.append(" Format=");
        builder.append((format != null) ? format.toString() : "");
        builder.append(" Index=");
        builder.append(index);
        builder.append(']');
        return builder.toString();
    }
    
}
