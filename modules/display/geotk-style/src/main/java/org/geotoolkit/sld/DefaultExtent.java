/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sld;

import org.opengis.sld.Extent;
import org.opengis.sld.SLDVisitor;

/**
 * Default imumutable extent, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultExtent implements Extent{

    private final String name;
    private final String value;
    
    /**
     * Default constructor.
     */
    DefaultExtent(String name, String value){
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

        DefaultExtent other = (DefaultExtent) obj;

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
        StringBuilder builder = new StringBuilder();
        builder.append("[Extent : Name=");
        builder.append(name.toString());
        builder.append(" Value=");
        builder.append(value.toString());
        builder.append(']');
        return builder.toString();
    }
    
}
