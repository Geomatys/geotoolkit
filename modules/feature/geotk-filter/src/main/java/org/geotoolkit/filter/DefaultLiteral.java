/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter;

import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

/**
 * Immutable generic Literal.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLiteral<T> extends AbstractExpression implements Literal{

    private final T value;

    public DefaultLiteral(T value) {
        this.value = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T evaluate(Object feature) {
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return value == null ? "NULL" : value.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultLiteral<T> other = (DefaultLiteral<T>) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

}
