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

import java.util.List;

import org.geotoolkit.util.Utilities;

import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.GraphicStroke;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI Graphic Stroke.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGraphicStroke extends DefaultGraphic implements GraphicStroke{

    private final Expression initial;
    
    private final Expression gap;
    
    /**
     * Create a default immutable Graphic Stroke.
     * 
     * @param symbols : can be null, if null or empty, the default mark will be added.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param size : if null will be replaced by Expression.NIL, 
     * if NIL the size of the graphicalSymbol is used
     * @param rotation : if null or Expression.NIL will be replaced by default value.
     * @param anchor : if null will be replaced by default value.
     * @param disp : if null will be replaced by default value.
     * @param initial : if null or Expression.NIL will be replaced by default value.
     * @param gap : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultGraphicStroke(List<GraphicalSymbol> symbols,
            Expression opacity, 
            Expression size, 
            Expression rotation, 
            AnchorPoint anchor, 
            Displacement disp, 
            Expression initial, 
            Expression gap){
        super(symbols,opacity,size,rotation,anchor,disp);
        
        this.gap = (gap == null || gap == NIL) ? DEFAULT_GRAPHIC_STROKE_GAP : gap;
        this.initial = (initial == null || initial == NIL) ? DEFAULT_GRAPHIC_STROKE_INITIAL_GAP : initial;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getInitialGap() {
        return initial;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getGap() {
        return gap;
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

        DefaultGraphicStroke other = (DefaultGraphicStroke) obj;

        return this.symbols.equals(other.symbols)
                && Utilities.equals(this.size, other.size)
                && this.opacity.equals(other.opacity)
                && this.rotation.equals(other.rotation)
                && this.anchor.equals(other.anchor)
                && this.disp.equals(other.disp)
                && this.initial.equals(other.initial)
                && this.gap.equals(other.gap);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += gap.hashCode();
        hash += initial.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GraphicStroke : ");
        builder.append(super.toString());
        builder.append(" InitialGap=");
        builder.append(initial.toString());
        builder.append(" Gap=");
        builder.append(gap.toString());
        return builder.toString();
    }
}
