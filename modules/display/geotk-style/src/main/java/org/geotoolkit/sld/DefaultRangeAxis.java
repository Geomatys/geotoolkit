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

import org.opengis.sld.RangeAxis;
import org.opengis.sld.SLDVisitor;

/**
 * Default imumutable range axis, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultRangeAxis implements RangeAxis {

    private final String name;
    private final String value;
    
    /**
     * Default constructor.
     */
    DefaultRangeAxis(String name, String value){
        if(name == null || value == null){
            throw new NullPointerException("Name and value can not be null.");
        }
        this.name = name;
        this.value = value;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getValue() {
        return value;
    }

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

        DefaultRangeAxis other = (DefaultRangeAxis) obj;

        return this.name.equals(other.name)
                && this.value.equals(other.value);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return name.hashCode() + 17*value.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[RangeAxis : Name=");
        builder.append(name);
        builder.append(" Value=");
        builder.append(value);
        builder.append(']');
        return builder.toString();
    }
    
}
