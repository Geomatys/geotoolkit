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
package org.geotoolkit.style;

import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;
import org.opengis.style.GraphicFill;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI Fill.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFill implements Fill{

    private final GraphicFill fill;
    
    private final Expression color;
    
    private final Expression opacity;
    
    /**
     * Create a default immutable Fill.
     * 
     * @param fill : can be bull, if null the color attribut will be used for rendering.
     * @param color : if null or Expression.NIL will be replaced by default value.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultFill(GraphicFill fill, Expression color, Expression opacity){
        this.fill = fill;
        this.color = (color == null || color == NIL) ? DEFAULT_FILL_COLOR : color;
        this.opacity = (opacity == null || opacity == NIL) ? DEFAULT_FILL_OPACITY : opacity;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public GraphicFill getGraphicFill() {
        return fill;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getColor() {
        return color;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getOpacity() {
        return opacity;
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

        DefaultFill other = (DefaultFill) obj;

        return Utilities.equals(this.color, other.color)
                && Utilities.equals(this.fill, this.fill)
                && Utilities.equals(this.opacity, other.opacity);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(color != null) hash += color.hashCode();
        if(fill != null) hash += fill.hashCode();
        if(opacity != null) hash += opacity.hashCode();
        
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Fill : Type=");
        builder.append( (fill == null) ? "Color:"+color.toString() : "GraphicFill" );
        builder.append(" Opacity=");
        builder.append(opacity.toString());
        builder.append(']');
        return builder.toString();
    }
    
}
