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

import org.opengis.filter.Expression;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types halo.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultHalo implements Halo{

    private final Fill fill;

    private final Expression radius;

    /**
     * Create a default immutable Halo.
     *
     * @param fill : if null will be replaced by default value.
     * @param radius : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultHalo(final Fill fill, final Expression radius){
        this.fill   = (fill   == null) ? DEFAULT_HALO_FILL   : fill;
        this.radius = (radius == null) ? DEFAULT_HALO_RADIUS : radius;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Fill getFill() {
        return fill;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getRadius() {
        return radius;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final StyleVisitor visitor, final Object extraData) {
        return visitor.visit(this,extraData);
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

        DefaultHalo other = (DefaultHalo) obj;

        return this.fill.equals(other.fill)
                && this.radius.equals(other.radius);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return fill.hashCode() + 17*radius.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Halo : Radius=").append(radius);
        builder.append(" Fill=").append(fill);
        builder.append(']');
        return builder.toString();
    }
}
