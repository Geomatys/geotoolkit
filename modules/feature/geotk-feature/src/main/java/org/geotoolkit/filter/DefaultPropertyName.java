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
import org.opengis.filter.expression.PropertyName;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.filter.binding.Binding;
import org.geotoolkit.filter.binding.Bindings;

/**
 * Immutable property name expression.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultPropertyName extends AbstractExpression implements PropertyName{

    private final String property;

    /**
     * Stores the last accessor returned.
     */
    private Binding lastAccessor;

    public DefaultPropertyName(final String property) {
        ensureNonNull("property name", property);
        this.property = property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getPropertyName() {
        return property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object evaluate(final Object candidate) {

        final Class cs;
        if(candidate == null){
            cs = Object.class;
        }else{
            cs = candidate.getClass();
        }

        Binding cp = lastAccessor;
        if (cp != null && cp.getBindingClass() != Object.class && cp.getBindingClass().isAssignableFrom(cs)) {
            return cp.get( candidate, property, null );
        }

        final Binding accessor = Bindings.getBinding(cs,property);
        if (accessor == null) {
            return null;
        }
        lastAccessor = accessor;
        return accessor.get( candidate, property, null );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return "{"+property+"}";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPropertyName other = (DefaultPropertyName) obj;
        if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.property != null ? this.property.hashCode() : 0);
        return hash;
    }

}
