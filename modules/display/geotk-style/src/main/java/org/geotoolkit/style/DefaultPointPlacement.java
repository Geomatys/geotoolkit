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

import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.PointPlacement;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI point placement.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPointPlacement implements PointPlacement{

    private final AnchorPoint anchor;
    
    private final Displacement disp;
    
    private final Expression rotation;
    
    /**
     * Create a default immutable Point placement.
     * 
     * @param anchor : if null will be replaced by default value.
     * @param disp : if null will be replaced by default value.
     * @param rotation : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultPointPlacement(AnchorPoint anchor, Displacement disp, Expression rotation){
        this.anchor = (anchor == null) ? DEFAULT_ANCHOR_POINT : anchor;
        this.disp = (disp == null) ? DEFAULT_DISPLACEMENT : disp;
        this.rotation = (rotation == null || rotation == NIL) ? DEFAULT_POINTPLACEMENT_ROTATION : rotation;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public AnchorPoint getAnchorPoint() {
        return anchor;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Displacement getDisplacement() {
        return disp;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getRotation() {
        return rotation;
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

        DefaultPointPlacement other = (DefaultPointPlacement) obj;

        return this.disp.equals(other.disp)
                && this.anchor.equals(other.anchor)
                && this.rotation.equals(other.rotation);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = disp.hashCode();
        hash *= anchor.hashCode();
        hash *= rotation.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[PointPlacement : Disp=");
        builder.append(disp);
        builder.append(" Anchor=");
        builder.append(anchor);
        builder.append(" Rotation=");
        builder.append(rotation);
        builder.append(']');
        return builder.toString();
    }
}
