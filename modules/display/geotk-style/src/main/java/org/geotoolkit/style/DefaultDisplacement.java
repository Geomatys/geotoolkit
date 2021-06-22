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
import org.opengis.style.Displacement;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types displacement.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultDisplacement implements Displacement{

    private final Expression dispX;

    private final Expression dispY;

    /**
     * Create a default immutable displacement.
     *
     * @param dispX : if null or Expression.NIL will be replaced by default value.
     * @param dispY : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultDisplacement(final Expression dispX, final Expression dispY){
        this.dispX = (dispX == null) ? DEFAULT_DISPLACEMENT_X : dispX;
        this.dispY = (dispY == null) ? DEFAULT_DISPLACEMENT_Y : dispY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getDisplacementX() {
        return dispX;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getDisplacementY() {
        return dispY;
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

        DefaultDisplacement other = (DefaultDisplacement) obj;

        return this.dispX.equals(other.dispX)
                && this.dispY.equals(other.dispY);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return dispX.hashCode() + 17*dispY.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Displacement : X=");
        builder.append(dispX);
        builder.append(" Y=");
        builder.append(dispY);
        builder.append(']');
        return builder.toString();
    }
}
