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

import java.util.Arrays;
import org.opengis.style.ChannelSelection;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of GeoAPI ChannelSelection.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultChannelSelection implements ChannelSelection{

    private final SelectedChannelType[] rgb;
    
    private final SelectedChannelType gray;

    
    /**
     * Create a default immutable channel selection.
     *
     */
    public DefaultChannelSelection(SelectedChannelType red, SelectedChannelType green, SelectedChannelType blue){
        rgb = new SelectedChannelType[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;

        this.gray = null;
    }
    
    /**
     * Create a default immutable channel selection.
     *
     * @param gray : can not be null 
     */
    DefaultChannelSelection(SelectedChannelType gray){
        if(gray == null){
            throw new NullPointerException("Gray channel can't be null");
        }

        this.gray = gray;
        this.rgb = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SelectedChannelType[] getRGBChannels() {
        if(rgb != null) return rgb.clone();
        else return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SelectedChannelType getGrayChannel() {
        return gray;
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

        DefaultChannelSelection other = (DefaultChannelSelection) obj;

        return this.gray.equals(other.gray)
                && Arrays.equals(this.rgb, other.rgb);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        if(rgb != null) return 17*rgb.hashCode();
        else return 17*gray.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ChannelSelection : ");
        if(rgb != null){
            builder.append(" RGB=");
            builder.append(rgb.toString());
        }else if(gray != null){
            builder.append(" GRAY=");
            builder.append(gray.toString());
        }
        builder.append("]");
        return builder.toString();
    }


}
