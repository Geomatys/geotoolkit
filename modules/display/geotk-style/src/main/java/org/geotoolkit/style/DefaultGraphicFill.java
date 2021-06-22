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
import java.util.Objects;

import org.opengis.filter.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of Types Graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultGraphicFill extends DefaultGraphic implements GraphicFill{

    /**
     * Create a default immutable Graphic fill.
     *
     * @param symbols : can not be null and must hold at least one graphicalSymbol
     * @param opacity : if null will be replaced by Expression.NIL
     * @param size : if null will be replaced by Expression.NIL,
     * if null the size od the graphicalSymbol is used
     * @param rotation : if null will be replaced by Expression.NIL
     * @param anchor : can be null, if so renderers shall use the default specification value.
     * @param disp : can be null, if so renderers shall use the default specification value.
     */
    public DefaultGraphicFill(final List<GraphicalSymbol> symbols, final Expression opacity,
            final Expression size, final Expression rotation, final AnchorPoint anchor, final Displacement disp){
        super(symbols,opacity,size,rotation,anchor,disp);
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

        DefaultGraphic other = (DefaultGraphic) obj;

        return this.symbols.equals(other.symbols)
                && Objects.equals(this.size, other.size)
                && this.opacity.equals(other.opacity)
                && this.rotation.equals(other.rotation)
                && Objects.equals(this.anchor,other.anchor)
                && Objects.equals(this.disp,other.disp);

    }


    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "GraphicFill : " + super.toString();
    }

}
