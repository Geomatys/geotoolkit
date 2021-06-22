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

import org.opengis.filter.Expression;
import org.opengis.style.LinePlacement;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types Line placement.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLinePlacement implements LinePlacement{

    private final Expression offset;

    private final Expression initial;

    private final Expression gap;

    private final boolean repeated;

    private final boolean aligned;

    private final boolean generalize;

    /**
     * Create a default immutable line placement.
     *
     * @param offset : if null or Expression.NIL will be replaced by default value.
     * @param initial : if null or Expression.NIL will be replaced by default value.
     * @param gap : if null or Expression.NIL will be replaced by default value.
     * @param repeated, default is false
     * @param aligned, default is false
     * @param generalize, defualt is false
     */
    public DefaultLinePlacement(final Expression offset, final Expression initial, final Expression gap,
            final boolean repeated, final boolean aligned, final boolean generalize){

        this.offset     = (offset  == null) ? DEFAULT_LINEPLACEMENT_OFFSET : offset;
        this.initial    = (initial == null) ? DEFAULT_LINEPLACEMENT_INITIAL_GAP : initial;
        this.gap        = (gap     == null) ? DEFAULT_LINEPLACEMENT_GAP : gap;
        this.repeated   = repeated;
        this.aligned    = aligned;
        this.generalize = generalize;
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
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean IsAligned() {
        return aligned;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isGeneralizeLine() {
        return generalize;
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

        DefaultLinePlacement other = (DefaultLinePlacement) obj;

        return this.aligned == other.aligned
                && this.generalize == other.generalize
                && this.repeated == other.repeated
                && this.gap.equals(other.gap)
                && this.initial.equals(other.initial)
                && this.offset.equals(other.offset);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = offset.hashCode();
        hash *= gap.hashCode();
        hash *= initial.hashCode();
        hash *= Boolean.valueOf(aligned).hashCode();
        hash *= Boolean.valueOf(generalize).hashCode();
        hash *= Boolean.valueOf(repeated).hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[LinePlacement : Offset=").append(offset);
        builder.append(" InitialGap=").append(initial);
        builder.append(" Gap=").append(gap);
        builder.append(" Repeated=").append(repeated);
        builder.append(" Aligned=").append(aligned);
        builder.append(" Generalize=").append(generalize);
        builder.append(']');
        return builder.toString();
    }
}
