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
package org.geotoolkit.filter.binarylogic;

import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

/**
 * Immutable "And" filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultAnd extends AbstractBinaryLogicOperator implements And {

    public DefaultAnd(final List<Filter> filters) {
        super(filters);
    }

    public DefaultAnd(final Filter filter1, final Filter filter2){
        super(filter1,filter2);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        for (Filter filter : filterArray) {
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
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return Trees.toString("And", filters);
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
