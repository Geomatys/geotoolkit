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

import org.geotoolkit.util.Utilities;

import org.opengis.filter.expression.Expression;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI Stroke.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultStroke implements Stroke{

    private final GraphicFill fill;
    
    private final GraphicStroke stroke;
    
    private final Expression color;
    
    private final Expression opacity;
    
    private final Expression width;
    
    private final Expression join;
    
    private final Expression cap;
    
    private final float[] dashes;
    
    private final Expression offset;
    
    /**
     * Create a default immutable stroke.
     * 
     * @param color : if null or Expression.NIL will be replaced by default value.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param width : if null or Expression.NIL will be replaced by default value.
     * @param join : if null or Expression.NIL will be replaced by default value.
     * @param cap : if null or Expression.NIL will be replaced by default value.
     * @param dashes : can be null, if not null then it must have 2 fields
     * @param offset : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultStroke(Expression color, Expression opacity, Expression width,
            Expression join, Expression cap, float[] dashes, Expression offset){
        
        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }
        
        this.fill = null;
        this.stroke = null;
        this.color = (color == null || color == NIL) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null || opacity == NIL) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width = (width == null || width == NIL) ? DEFAULT_STROKE_WIDTH : width;
        this.join = (join == null || join == NIL) ? DEFAULT_STROKE_JOIN : join;
        this.cap = (cap == null || cap == NIL) ? DEFAULT_STROKE_CAP : cap;
        this.dashes = dashes;
        this.offset = (offset == null || offset == NIL) ? DEFAULT_STROKE_OFFSET : offset;
    }
    
    /**
     * Create a default immutable stroke.
     * 
     * @param fill : only one between fill and stroke can be defined, both can be null
     * @param color : if null or Expression.NIL will be replaced by default value.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param width : if null or Expression.NIL will be replaced by default value.
     * @param join : if null or Expression.NIL will be replaced by default value.
     * @param cap : if null or Expression.NIL will be replaced by default value.
     * @param dashes : can be null, if not null then it must have 2 fields
     * @param offset : if null or Expression.NIL will be replaced by default value.
     */
    DefaultStroke(GraphicFill fill, Expression color, Expression opacity, 
            Expression width, Expression join, Expression cap, float[] dashes, Expression offset){
                
        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }
        
        this.fill = fill;
        this.stroke = null;
        this.color = (color == null || color == NIL) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null || opacity == NIL) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width = (width == null || width == NIL) ? DEFAULT_STROKE_WIDTH : width;
        this.join = (join == null || join == NIL) ? DEFAULT_STROKE_JOIN : join;
        this.cap = (cap == null || cap == NIL) ? DEFAULT_STROKE_CAP : cap;
        this.dashes = dashes;
        this.offset = (offset == null || offset == NIL) ? DEFAULT_STROKE_OFFSET : offset;
    }
    
    
    
    /**
     * Create a default immutable stroke.
     * 
     * @param stroke : only one between fill and stroke can be defined, both can be null
     * @param color : if null or Expression.NIL will be replaced by default value.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param width : if null or Expression.NIL will be replaced by default value.
     * @param join : if null or Expression.NIL will be replaced by default value.
     * @param cap : if null or Expression.NIL will be replaced by default value.
     * @param dashes : can be null, if not null then it must have 2 fields
     * @param offset : if null or Expression.NIL will be replaced by default value.
     */
    DefaultStroke(GraphicStroke stroke, Expression color, Expression opacity, 
            Expression width, Expression join, Expression cap, float[] dashes, Expression offset){
        if(stroke == null){
            throw new IllegalArgumentException("Stroke can not be null.");
        }
        
        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }
        
        this.fill = null;
        this.stroke = stroke;
        this.color = (color == null || color == NIL) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null || opacity == NIL) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width = (width == null || width == NIL) ? DEFAULT_STROKE_WIDTH : width;
        this.join = (join == null || join == NIL) ? DEFAULT_STROKE_JOIN : join;
        this.cap = (cap == null || cap == NIL) ? DEFAULT_STROKE_CAP : cap;
        this.dashes = dashes;
        this.offset = (offset == null || offset == NIL) ? DEFAULT_STROKE_OFFSET : offset;
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
    public GraphicStroke getGraphicStroke() {
        return stroke;
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
    public Expression getWidth() {
        return width;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLineJoin() {
        return join;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLineCap() {
        return cap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float[] getDashArray() {
        return dashes;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getDashOffset() {
        return offset;
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

        DefaultStroke other = (DefaultStroke) obj;

        return 
            Utilities.deepEquals(this.dashes, other.dashes)
            && Utilities.equals(this.stroke, other.stroke)
            && Utilities.equals(this.fill, other.fill)
            && this.color.equals(other.color)
            && this.cap.equals(other.cap)
            && this.join.equals(other.join)
            && this.offset.equals(other.offset)
            && this.opacity.equals(other.opacity)
            && this.width.equals(other.width);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(dashes != null) hash *= dashes.hashCode();
        if(stroke != null) hash *= stroke.hashCode();
        if(fill != null)   hash *= fill.hashCode();
        hash *= color.hashCode();
        hash *= cap.hashCode();
        hash *= join.hashCode();
        hash *= offset.hashCode();
        hash *= opacity.hashCode();
        hash *= width.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Stroke : Color=");
        builder.append(color.toString());
        builder.append(" Cap=");
        builder.append(cap.toString());
        builder.append(" Join=");
        builder.append(join.toString());
        builder.append(" Offset=");
        builder.append(offset.toString());
        builder.append(" Opacity=");
        builder.append(opacity.toString());
        builder.append(" Width=");
        builder.append(width.toString());
        if(dashes != null){
            builder.append(" Dashes=[");
            for(int i=0;i<dashes.length;i++){
                builder.append(dashes[i]);
                if(i != dashes.length-1){
                    builder.append(',');
                }
            }
            builder.append("]");
        }
        if(fill != null){
            builder.append(" Fill=");
            builder.append(fill.toString());
        }
        if(stroke != null){
            builder.append(" Stroke=");
            builder.append(stroke.toString());
        }
        builder.append(']');
        return builder.toString();
    }
}
