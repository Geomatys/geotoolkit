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

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;

import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;

/**
 * Immutable property name expression.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPropertyName extends AbstractExpression implements PropertyName{

    private final String property;

    /**
     * Stores the last accessor returned.
     */
    private Entry<Class,PropertyAccessor> lastAccessor;

    public DefaultPropertyName(final String property) {
        if(property == null){
            throw new NullPointerException("Property name can not be null");
        }
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
        Entry<Class,PropertyAccessor> copy = lastAccessor;
        if (copy != null && copy.getKey().equals(candidate.getClass())) {
            final PropertyAccessor access = copy.getValue();
            if (access != null) {
                return access.get( candidate, property, null );
            }
            return null;
        }

        final PropertyAccessor accessor = Accessors.getAccessor(candidate.getClass(),property, null);
        copy = new SimpleEntry<Class, PropertyAccessor>(candidate.getClass(),accessor);
        lastAccessor = copy;
    	if (accessor == null) {
            return null;
    	}

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
        return property;
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
