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

import org.opengis.filter.expression.Function;
import org.opengis.style.ColorReplacement;
import org.opengis.style.StyleVisitor;

/**
 * Immutable implementation of GeoAPI ColorReplacement.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultColorReplacement implements ColorReplacement{

    private final Function recode;

    /**
     * Create a default immutable color replacement.
     *
     * @param recode : can not be null.
     */
    public DefaultColorReplacement(final Function recode){
        if(recode == null){
            throw new NullPointerException("A color replacement must define a function");
        }
        this.recode = recode;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function getRecoding() {
        return recode;
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

        DefaultColorReplacement other = (DefaultColorReplacement) obj;

        return this.recode.equals(other.recode);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return recode.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[ColorReplacement : Function=");
        builder.append(recode);
        builder.append(']');
        return builder.toString();
    }

}
