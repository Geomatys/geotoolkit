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

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.util.Utilities;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI Line symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLineSymbolizer extends AbstractSymbolizer implements LineSymbolizer{

    private final Stroke stroke;
    
    private final Expression offset;
    
    /**
     * Create a default immutable Line symbolizer.
     * 
     * @param stroke : if null will be replaced by default value.
     * @param offset : if null or Expression.NIL will be replaced by default value.
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    public DefaultLineSymbolizer(Stroke stroke, Expression offset, Unit<?> uom, String geom,
            String name, Description desc){
        super(uom, geom, name, desc);
        this.stroke = (stroke == null) ? DEFAULT_STROKE : stroke;
        this.offset = (offset == null || offset == NIL) ? DEFAULT_LINE_OFFSET : offset;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getPerpendicularOffset() {
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

        DefaultLineSymbolizer other = (DefaultLineSymbolizer) obj;

        return this.stroke.equals(other.stroke)
                && this.desc.equals(other.desc)
                && this.offset.equals(other.offset)
                && this.uom.equals(other.uom)
                && Utilities.equals(this.geom, other.geom)
                && Utilities.equals(this.name, other.name);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = stroke.hashCode();
        hash *= offset.hashCode();
        hash *= uom.hashCode();
        hash *= desc.hashCode();
        if(geom != null) hash *= geom.hashCode();
        if(name != null) hash *= name.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Line Symbolizer : Stroke=");
        if(stroke != null){
            builder.append(stroke.toString());
        }
        builder.append(" Offset=");
        builder.append(offset.toString());
        if(uom != null){
            builder.append(" Unit=");
            builder.append(uom.toString());
        }
        if(geom != null){
            builder.append(" Geometry=");
            builder.append(geom.toString());
        }
        builder.append(']');
        return builder.toString();
    }
}
