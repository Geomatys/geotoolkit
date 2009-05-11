/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter.binarylogic;

import java.util.List;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

/**
 * Immutable "And" filter.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultAnd extends AbstractBinaryLogicOperator implements And {

    public DefaultAnd(List<Filter> filters) {
        super(filters);
    }

    public DefaultAnd(Filter filter1, Filter filter2){
        super(filter1,filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object object) {
        for (final Filter filter : filters) {
            if(!filter.evaluate(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("And{");
        for(Filter f : filters){
            sb.append(f.toString());
        }
        sb.append("}");
        return sb.toString();
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
        final AbstractBinaryLogicOperator other = (AbstractBinaryLogicOperator) obj;
        if (this.filters != other.filters && !this.filters.equals(other.filters)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.filters.hashCode();
        return hash;
    }

}
