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
import org.opengis.style.AnchorPoint;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types AnchorPoint.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultAnchorPoint implements AnchorPoint{

    private Expression anchorX;

    private Expression anchorY;

    /**
     * Create a default immutable AnchorPoint.
     *
     * @param anchorX : if null or Expression.NIL will be replaced by default value.
     * @param anchorY : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultAnchorPoint(final Expression anchorX, final Expression anchorY){
        this.anchorX = (anchorX == null) ? DEFAULT_ANCHOR_POINT_X : anchorX;
        this.anchorY = (anchorY == null) ? DEFAULT_ANCHOR_POINT_Y : anchorY;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getAnchorPointX() {
        return anchorX;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getAnchorPointY() {
        return anchorY;
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

        DefaultAnchorPoint other = (DefaultAnchorPoint) obj;

        return this.anchorX.equals(other.anchorX)
                && this.anchorY.equals(other.anchorY);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return anchorX.hashCode() + 17*anchorY.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[AnchorPoint : X=");
        builder.append(anchorX);
        builder.append(" Y=");
        builder.append(anchorY);
        builder.append(']');
        return builder.toString();
    }
}
