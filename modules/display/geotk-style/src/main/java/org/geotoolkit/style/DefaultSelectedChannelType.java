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

import org.opengis.style.ContrastEnhancement;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of GeoAPI SelectedChannelType.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSelectedChannelType implements SelectedChannelType{

    private final String name;
    
    private final ContrastEnhancement enhance;
    
    /**
     * Create a default immutable Selected channel type.
     * 
     * @param name : can not be null
     * @param enchance : if null will be replaced by default description.
     */
    public DefaultSelectedChannelType(String name, ContrastEnhancement enhance){
        if(name == null){
            throw new NullPointerException("Name can not be null");
        }
        this.name = name;
        this.enhance = (enhance == null) ? DEFAULT_CONTRAST_ENHANCEMENT : enhance;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getChannelName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ContrastEnhancement getContrastEnhancement() {
        return enhance;
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

        DefaultSelectedChannelType other = (DefaultSelectedChannelType) obj;

        return this.name.equals(other.name)
                && this.enhance.equals(other.enhance);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return name.hashCode() + enhance.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[SelectedChannelType : Name=");
        builder.append(name.toString());
        builder.append(" Enhancement=");
        builder.append(enhance.toString());
        builder.append(']');
        return builder.toString();
    }
}
