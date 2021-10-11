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

import org.opengis.sld.Extent;
import org.opengis.sld.SLDVisitor;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Default imumutable extent, thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultExtent implements Extent{

    private final String name;
    private final String value;

    /**
     * Default constructor.
     */
    DefaultExtent(final String name, final String value){
        ensureNonNull("name", name);
        ensureNonNull("value", value);
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
        final StringBuilder builder = new StringBuilder();
        builder.append("[Extent : Name=");
        builder.append(name);
        builder.append(" Value=");
        builder.append(value);
        builder.append(']');
        return builder.toString();
    }

}
