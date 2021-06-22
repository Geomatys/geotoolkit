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
import org.opengis.style.ColorMap;
import org.opengis.style.StyleVisitor;


/**
 * Immutable implementation of Types ColorMap.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultColorMap implements ColorMap{

    private final Expression function;

    /**
     * Create a default immutable color map.
     *
     * @param function : should not be null.
     */
    public DefaultColorMap(final Expression function){
//        if(function == null){
//            throw new NullPointerException("A color map must have a function");
//        }
        this.function = function;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getFunction() {
        return function;
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

        DefaultColorMap other = (DefaultColorMap) obj;

        return Objects.equals(this.function, other.function);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 45465123;
        if(function != null){
            hash*= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[ColorMap : Function=");
        if(function != null){
            builder.append(function);
        }
        builder.append("]");
        return builder.toString();
    }

}
