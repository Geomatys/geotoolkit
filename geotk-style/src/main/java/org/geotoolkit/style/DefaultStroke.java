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

import java.util.Arrays;
import java.util.Objects;

import org.opengis.filter.Expression;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types Stroke.
 *
 * @author Johann Sorel (Geomatys)
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
    public DefaultStroke(final Expression color, final Expression opacity, final Expression width,
            final Expression join, final Expression cap, final float[] dashes, final Expression offset){

        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }

        this.fill    = null;
        this.stroke  = null;
        this.color   = (color   == null) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width   = (width   == null) ? DEFAULT_STROKE_WIDTH : width;
        this.join    = (join    == null) ? DEFAULT_STROKE_JOIN : join;
        this.cap     = (cap     == null) ? DEFAULT_STROKE_CAP : cap;
        this.dashes  = dashes;
        this.offset  = (offset  == null) ? DEFAULT_STROKE_OFFSET : offset;
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
    DefaultStroke(final GraphicFill fill, final Expression color, final Expression opacity,
            final Expression width, final Expression join, final Expression cap, final float[] dashes, final Expression offset){

        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }

        this.fill    = fill;
        this.stroke  = null;
        this.color   = (color   == null) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width   = (width   == null) ? DEFAULT_STROKE_WIDTH : width;
        this.join    = (join    == null) ? DEFAULT_STROKE_JOIN : join;
        this.cap     = (cap     == null) ? DEFAULT_STROKE_CAP : cap;
        this.dashes  = dashes;
        this.offset  = (offset  == null) ? DEFAULT_STROKE_OFFSET : offset;
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
    DefaultStroke(final GraphicStroke stroke, final Expression color, final Expression opacity,
            final Expression width, final Expression join, final Expression cap, final float[] dashes, final Expression offset){
        if(stroke == null){
            throw new IllegalArgumentException("Stroke can not be null.");
        }

        if(dashes != null && dashes.length < 2){
            throw new IllegalArgumentException("Dashes must have 2 or more fields");
        }

        this.fill    = null;
        this.stroke  = stroke;
        this.color   = (color   == null) ? DEFAULT_STROKE_COLOR : color;
        this.opacity = (opacity == null) ? DEFAULT_STROKE_OPACITY : opacity;
        this.width   = (width   == null) ? DEFAULT_STROKE_WIDTH : width;
        this.join    = (join    == null) ? DEFAULT_STROKE_JOIN : join;
        this.cap     = (cap     == null) ? DEFAULT_STROKE_CAP : cap;
        this.dashes  = dashes;
        this.offset  = (offset  == null) ? DEFAULT_STROKE_OFFSET : offset;
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

        DefaultStroke other = (DefaultStroke) obj;

        return
            Objects.deepEquals(this.dashes, other.dashes)
            && Objects.equals(this.stroke, other.stroke)
            && Objects.equals(this.fill, other.fill)
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
        if(dashes != null) hash *= Arrays.hashCode(dashes);
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
        final StringBuilder builder = new StringBuilder();
        builder.append("[Stroke : Color=").append(color);
        builder.append(" Cap=").append(cap);
        builder.append(" Join=").append(join);
        builder.append(" Offset=").append(offset);
        builder.append(" Opacity=").append(opacity);
        builder.append(" Width=").append(width);
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
            builder.append(fill);
        }
        if(stroke != null){
            builder.append(" Stroke=");
            builder.append(stroke);
        }
        builder.append(']');
        return builder.toString();
    }
}
