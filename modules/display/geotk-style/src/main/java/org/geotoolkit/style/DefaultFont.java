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

import java.util.Collections;
import java.util.List;

import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Font;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI Font.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFont implements Font{

    private final List<Expression> family;
    
    private final Expression style;
    
    private final Expression weight;
    
    private final Expression size;
    
    /**
     * Create a default immutable Font.
     * 
     * @param family : can be null or empty
     * @param style : if null or Expression.NIL will be replaced by default value.
     * defaults are “normal”, “italic”, and “oblique”
     * @param weight : if null or Expression.NIL will be replaced by default value.
     * defaults are “normal” and “bold”
     * @param size : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultFont(List<Expression> family, Expression style, Expression weight, Expression size){
        
        this.style = (style == null || style == NIL) ? DEFAULT_FONT_STYLE : style;
        this.weight = (weight == null || weight == NIL) ? DEFAULT_FONT_WEIGHT : weight;
        this.size = (size == null || size == NIL) ? DEFAULT_FONT_SIZE : size;
        
        if(family != null && !family.isEmpty()) {
            final Expression[] rep = family.toArray(new Expression[family.size()]);
            this.family = UnmodifiableArrayList.wrap(rep);
        }else{
            this.family = Collections.emptyList();
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Expression> getFamily() {
        return family;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getStyle() {
        return style;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getWeight() {
        return weight;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getSize() {
        return size;
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

        DefaultFont other = (DefaultFont) obj;

        return this.family.equals(other.family)
                && this.size.equals(other.size)
                && this.style.equals(other.style)
                && this.weight.equals(other.weight);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = family.hashCode();
        hash += size.hashCode();
        hash += weight.hashCode();
        hash += style.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Font : family=");
        for(Expression ex : family){
            builder.append(ex.toString() +",");
        }
        builder.append(" Style=");
        builder.append(style.toString());
        builder.append(" Weight=");
        builder.append(weight.toString());
        builder.append(" Size=");
        builder.append(size.toString());
        builder.append(']');
        return builder.toString();
    }
    
}
