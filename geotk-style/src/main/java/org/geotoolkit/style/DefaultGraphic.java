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
import java.util.Objects;

import org.apache.sis.util.internal.UnmodifiableArrayList;

import org.opengis.filter.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types Graphic.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultGraphic implements Graphic{

    protected final List<GraphicalSymbol> symbols;

    protected final Expression opacity;

    protected final Expression size;

    protected final Expression rotation;

    protected final AnchorPoint anchor;

    protected final Displacement disp;

    /**
     * Create a default immutable Graphic.
     *
     * @param symbols : can be null, if null or empty, the default mark will be added.
     * @param opacity : if null or Expression.NIL will be replaced by default value.
     * @param size : if null will be replaced by Expression.NIL,
     * if NIL the size of the graphicalSymbol is used
     * @param rotation : if null or Expression.NIL will be replaced by default value.
     * @param anchor : if null will be replaced by default value.
     * @param disp : if null will be replaced by default value.
     */
    public DefaultGraphic(final List<GraphicalSymbol> symbols, final Expression opacity, final Expression size,
            final Expression rotation, final AnchorPoint anchor, final Displacement disp){

        if(symbols == null || symbols.isEmpty()){
            this.symbols = Collections.singletonList((GraphicalSymbol)DEFAULT_GRAPHICAL_SYMBOL);
        }else{
            final GraphicalSymbol[] rep = symbols.toArray(new GraphicalSymbol[symbols.size()]);
            this.symbols = UnmodifiableArrayList.wrap(rep);
        }

        this.opacity  = (opacity  == null) ? DEFAULT_GRAPHIC_OPACITY : opacity;
        this.rotation = (rotation == null) ? DEFAULT_GRAPHIC_ROTATION : rotation;

        //size is special since if it null, we must use the graphicsymbol original size
        this.size = size;

        this.anchor = (anchor == null) ? DEFAULT_ANCHOR_POINT : anchor;
        this.disp = (disp == null) ? DEFAULT_DISPLACEMENT : disp;
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
    public Expression getSize() {
        return size;
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
    public Object accept(final StyleVisitor visitor, final Object extraData) {
        return visitor.visit(this,extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<GraphicalSymbol> graphicalSymbols() {
        return symbols;
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
    public int hashCode() {
        int hash = symbols.hashCode();
        if(size != null) hash += size.hashCode();
        hash += opacity.hashCode();
        hash += rotation.hashCode();
        hash += anchor.hashCode();
        hash += disp.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Graphic : Opacity=").append(opacity);
        builder.append(" Size=").append(size);
        builder.append(" Rotation=").append(rotation);
        builder.append(" Anchor=").append(anchor);
        builder.append(" Displacement=").append(disp);
        builder.append(']');
        return builder.toString();
    }
}
