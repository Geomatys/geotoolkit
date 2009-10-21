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
package org.geotoolkit.filter.sort;

import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Immutable "sort by".
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSortBy implements SortBy{

    private final PropertyName property;
    private final SortOrder order;

    public DefaultSortBy(PropertyName property, SortOrder order) {
        if(property == null || order == null){
            throw new NullPointerException("Property and sort order can not be null.");
        }
        this.property = property;
        this.order = order;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyName getPropertyName() {
        return property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SortOrder getSortOrder() {
        return order;
    }

    @Override
    public String toString() {
        return new StringBuilder("SortBy{")
                .append(property).append(',')
                .append(order).append('}')
                .toString();
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
        final DefaultSortBy other = (DefaultSortBy) obj;
        if (this.property != other.property && (this.property == null || !this.property.equals(other.property))) {
            return false;
        }
        if (this.order != other.order && (this.order == null || !this.order.equals(other.order))) {
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
        hash = 83 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 83 * hash + (this.order != null ? this.order.hashCode() : 0);
        return hash;
    }

}
