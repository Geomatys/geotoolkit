/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.filter.expression.DefaultExpression;
import org.geotoolkit.filter.expression.ExpressionType;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultPropertyName extends DefaultExpression implements PropertyName{

    private final String property;

    public DefaultPropertyName(String property) {
        super(ExpressionType.ATTRIBUTE);
        if(property == null){
            throw new NullPointerException("Property name can not be null");
        }
        this.property = property;
    }

    @Override
    public String getPropertyName() {
        return property;
    }

    @Override
    public Object evaluate(Object candidate) {
        PropertyAccessor accessor = getAccessor(candidate.getClass(),property, null);
    	if (accessor == null) {
            return null;
    	}

    	return accessor.get( candidate, property, null );
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public boolean equals(Object obj) {
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.property != null ? this.property.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return property;
    }

}
