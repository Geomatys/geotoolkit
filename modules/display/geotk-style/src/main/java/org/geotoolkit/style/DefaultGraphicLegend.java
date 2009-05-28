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
import org.opengis.style.GraphicLegend;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of GeoAPI Graphic.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGraphicLegend extends DefaultGraphic implements GraphicLegend{

    /**
     * Create a default immutable Graphic legend.
     * 
     * @param symbols : can be null, if null or empty, the default mark will be added.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param size : if null will be replaced by Expression.NIL, 
     * if NIL the size of the graphicalSymbol is used
     * @param rotation : if null or Expression.NIL will be replaced by default value.
     * @param anchor : if null will be replaced by default value.
     * @param disp : if null will be replaced by default value.
     */
    public DefaultGraphicLegend(List<GraphicalSymbol> symbols, Expression opacity, Expression size,
            Expression rotation, AnchorPoint anchor, Displacement disp){
        super(symbols,opacity,size,rotation,anchor,disp);
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

        DefaultGraphic other = (DefaultGraphic) obj;

        return this.symbols.equals(other.symbols)
                && Utilities.equals(this.size, other.size)
                && this.opacity.equals(other.opacity)
                && this.rotation.equals(other.rotation)
                && Utilities.equals(this.anchor,other.anchor)
                && Utilities.equals(this.disp,other.disp);

    }
    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "GraphicLegend : "+super.toString();
    }

}
