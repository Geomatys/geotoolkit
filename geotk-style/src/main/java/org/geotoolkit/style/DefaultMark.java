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

import java.util.Objects;

import org.opengis.filter.Expression;
import org.opengis.style.ExternalMark;
import org.opengis.style.Fill;
import org.opengis.style.Mark;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types Mark.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMark implements Mark{

    private final Expression wkn;

    private final ExternalMark external;

    private final Fill fill;

    private final Stroke stroke;

    /**
     * Create a default immutable Mark.
     *
     * @param wkn : if null will be replaced by default value.
     * @param fill : if null will be replaced by default value.
     * @param stroke : if null will be replaced by default value.
     */
    public DefaultMark(final Expression wkn, final Fill fill, final Stroke stroke){
        this.wkn      = (wkn    == null) ? DEFAULT_MARK_WKN : wkn;
        this.external = null;
        this.fill     = (fill   == null) ? DEFAULT_FILL : fill;
        this.stroke   = (stroke == null) ? DEFAULT_STROKE : stroke;
    }

    /**
     * Create a default immutable Mark.
     *
     * @param external : if null will be replaced by default value.
     * @param fill : if null will be replaced by default value.
     * @param stroke : if null will be replaced by default value.
     */
    DefaultMark(final ExternalMark external, final Fill fill, final Stroke stroke){
        if(external == null){
            this.wkn = DEFAULT_MARK_WKN;
            this.external = null;
        }else{
            this.wkn = null;
            this.external = external;
        }

        this.fill = fill;
        this.stroke = stroke;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getWellKnownName() {
        return wkn;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ExternalMark getExternalMark() {
        return external;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Fill getFill() {
        return fill;
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

        DefaultMark other = (DefaultMark) obj;

        return Objects.equals(this.external, other.external)
                && Objects.equals(this.wkn, other.wkn)
                && Objects.equals(this.fill, other.fill)
                && Objects.equals(this.stroke, other.stroke);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = fill.hashCode();
        hash *= stroke.hashCode();

        if(wkn != null) hash *= wkn.hashCode();
        if(external != null) hash *= external.hashCode();

        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Mark : Type=");
        builder.append( (wkn != null) ? "WKN:"+wkn : "ExternalMark" );
        builder.append(" Stroke=");
        builder.append(stroke);
        builder.append(" Fill=");
        builder.append(fill);
        builder.append(']');
        return builder.toString();
    }
}
